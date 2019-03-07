package io.github.trytonvanmeer.libretrivia.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

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
