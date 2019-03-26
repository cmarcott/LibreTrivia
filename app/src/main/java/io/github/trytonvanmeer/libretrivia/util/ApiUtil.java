package io.github.trytonvanmeer.libretrivia.util;

import android.database.Cursor;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import io.github.trytonvanmeer.libretrivia.activities.BaseActivity;
import io.github.trytonvanmeer.libretrivia.exceptions.NoTriviaResultsException;
import io.github.trytonvanmeer.libretrivia.trivia.TriviaQuery;
import io.github.trytonvanmeer.libretrivia.trivia.TriviaQuestion;
import io.github.trytonvanmeer.libretrivia.trivia.TriviaQuestionBoolean;
import io.github.trytonvanmeer.libretrivia.trivia.TriviaQuestionMultiple;
import io.github.trytonvanmeer.libretrivia.trivia.TriviaType;

//utilities for executing a query to openTDB
public class ApiUtil {

    //reads the results generated from the query
    private static String readStream(InputStream in) throws IOException {
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in), 1000);

        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            builder.append(line);
        }

        in.close();
        return builder.toString();
    }

    //executes the query
    private static String GET(String query) throws IOException {
        String response;

        //open connection
        URL url = new URL(query);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        //read response
        try {
            InputStream in = new BufferedInputStream(connection.getInputStream());
            response = readStream(in);
        } finally {
            connection.disconnect();
        }

        return response;
    }

    //Executes the query
    public static String GET(TriviaQuery query) throws IOException {
        return GET(query.toString());
    }

    //Executes the query
    public static String LocalGET(TriviaQuery query) throws IOException {

        String difficulty = query.getDifficultyString();
        String category = query.getCategoryString();

        if (query.getCategory().equals("Any")) {
            category = null;
        }

        Integer diff = null;
        switch (difficulty) {
            case "Easy":
                diff = TypeUtil.EASY;
                break;
            case "Medium":
                diff = TypeUtil.MEDIUM;
                break;
            case "Hard":
                diff = TypeUtil.HARD;
                break;
        }

        Cursor customQuestions = BaseActivity.myDb.getSpecificCustomQuestions(category, diff, true);
        String json = convertCursorToJSON(customQuestions, Integer.parseInt(query.getAmount()));

        return json;

    }

    public static String convertCursorToJSON(Cursor c, Integer expectedQuestions) throws IOException {

        JSONObject response = new JSONObject();
        JSONArray results = new JSONArray();

        int qCount = 0;

        try {
            if (c.getCount() < expectedQuestions) {
                Log.w("ApiUtil", "Not enough questions found for specified criteria - " + c.getCount() + " questions found but expected " + expectedQuestions);
                response.put("response_code", 1);
                return response.toString();
            } else {
                while(c.moveToNext() && qCount < expectedQuestions) {

                    int index;
                    index = c.getColumnIndexOrThrow("question");
                    String que = c.getString(index);
                    index = c.getColumnIndexOrThrow("category");
                    String cat = c.getString(index);
                    index = c.getColumnIndexOrThrow("difficulty");
                    String dif = c.getString(index);
                    index = c.getColumnIndexOrThrow("type");
                    String typ = c.getString(index);
                    index = c.getColumnIndexOrThrow("a1");
                    String a1 = c.getString(index);
                    index = c.getColumnIndexOrThrow("a2");
                    String a2 = c.getString(index);
                    index = c.getColumnIndexOrThrow("a3");
                    String a3 = c.getString(index);
                    index = c.getColumnIndexOrThrow("a4");
                    String a4 = c.getString(index);

                    Log.d("Q_VIEW", "READING QUESTION FROM DB\n" + que + "\n" +
                            cat + "\n" +
                            dif + "\n" +
                            typ + "\n" +
                            a1 + "\n" +
                            a2 + "\n" +
                            a3 + "\n" +
                            a4 + "\n");

                    JSONObject question = new JSONObject();
                    if (Integer.parseInt(typ) == TypeUtil.MC_TYPE) {

                        // Add question M/C attributes
                        question.put("category", cat);
                        question.put("type", TypeUtil.convertTypeToString(Integer.parseInt(typ)).toLowerCase());
                        question.put("question", que);
                        question.put("difficulty", TypeUtil.convertDifficultyToString(Integer.parseInt(dif)).toLowerCase());
                        question.put("correct_answer", a1);

                        // Add incorrect answers subarray
                        JSONArray incorrect_answers = new JSONArray();
                        incorrect_answers.put(a2);
                        incorrect_answers.put(a3);
                        incorrect_answers.put(a4);

                        // Add to question object
                        question.put("incorrect_answers", incorrect_answers);

                    } else {

                        // Add question T/F attributes
                        question.put("category", cat);
                        question.put("type", TypeUtil.convertTypeToString(Integer.parseInt(typ)).toLowerCase());
                        question.put("question", que);
                        question.put("difficulty", TypeUtil.convertDifficultyToString(Integer.parseInt(dif)).toLowerCase());
                        question.put("correct_answer", a1);

                        // Add incorrect answers subarray (In this case only either true or false)
                        JSONArray incorrect_answers = new JSONArray();
                        incorrect_answers.put(TypeUtil.returnOppositeBooleanAnswer(a1));

                        // Add to question object
                        question.put("incorrect_answers", incorrect_answers);
                    }

                    qCount++;
                    results.put(question);
                }

                response.put("response_code", 0);
                response.put("results", results);

                Log.d("ApiUtil", "JSON string: " + response.toString());
                return response.toString();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            throw new IOException();
        }
    }

    //parses the response into a list of questions
    public static ArrayList<TriviaQuestion> jsonToQuestionArray(String json) throws NoTriviaResultsException {
        JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();

        if (jsonObject.get("response_code").getAsInt() == 1) {
            throw new NoTriviaResultsException();
        }

        //split the questions into a JSON array
        JsonArray jsonArray = jsonObject.getAsJsonArray("results");

        ArrayList<TriviaQuestion> questions = new ArrayList<>();

        //read each question in the array
        for (JsonElement element : jsonArray) {
            JsonObject object = element.getAsJsonObject();
            TriviaType type = TriviaType.get(object.get("type").getAsString());

            if (type == TriviaType.MULTIPLE) {
                questions.add(TriviaQuestionMultiple.fromJson(object));
            } else {
                questions.add(TriviaQuestionBoolean.fromJson(object));
            }
        }

        return questions;
    }
}
