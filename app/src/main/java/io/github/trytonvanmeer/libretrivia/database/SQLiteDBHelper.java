package io.github.trytonvanmeer.libretrivia.database;

import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import io.github.trytonvanmeer.libretrivia.util.TypeUtil;

public class SQLiteDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "Trivia_Local";

    /* Custom Questions Table Declarations*/

    // Table name
    public static final String QUES_TABLE_NAME = "custom_questions";
    // Id of the question
    public static final String QUES_COLUMN_ID = "_id";
    // The question paragraph
    public static final String QUES_COLUMN_QUESTION = "question";
    // The category
    public static final String QUES_COLUMN_CATEGORY = "category";
    // The difficulty rating, ranging from 0-2
    public static final String QUES_COLUMN_DIFFICULTY = "difficulty";
    // The type of the question, aka T/F (type 0) or MC (type 1) question
    public static final String QUES_COLUMN_TYPE = "type";
    // The four possible answers if multiple choice
    public static final String QUES_COLUMN_A1 = "a1";
    public static final String QUES_COLUMN_A2 = "a2";
    public static final String QUES_COLUMN_A3 = "a3";
    public static final String QUES_COLUMN_A4 = "a4";


    public SQLiteDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create Custom Questions Table
        sqLiteDatabase.execSQL("CREATE TABLE " + QUES_TABLE_NAME + " (" +
                QUES_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                QUES_COLUMN_QUESTION + " TEXT, " +
                QUES_COLUMN_CATEGORY + " TEXT, " +
                QUES_COLUMN_DIFFICULTY + " INT, " +
                QUES_COLUMN_TYPE + " INT, " +
                QUES_COLUMN_A1 + " TEXT, " +
                QUES_COLUMN_A2 + " TEXT, " +
                QUES_COLUMN_A3 + " TEXT, " +
                QUES_COLUMN_A4 + " TEXT " +
                " )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + QUES_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    /* insertCustomQuestion
     * A method used to insert a new custom question into the stored local database
     * Sample Calls to insert a custom question:
     *      myDb.insertCustomQuestion("Testing Question 1", "General Knowledge", TypeUtil.HARD, TypeUtil.TF_TYPE, new ArrayList<String>(Arrays.asList("False")));
     *      myDb.insertCustomQuestion("Testing Question 2", "General Knowledge", TypeUtil.EASY, TypeUtil.MC_TYPE, new ArrayList<String>(Arrays.asList("Dogs", "Cats", "Antelope", "Birds")));
     * @param String question - The question being asked
     * @param String category - The category of the question
     * @param Integer difficulty - The difficulty level the question is to be assigned, 0=EASY, 1=MEDIUM, 2=HARD
     * @param Integer type - 0=T/F, 1=MC
     * @param ArrayList<String> answers - Array of possible question answers. If True/False question, answer is in element 0 of arraylist as "True" or "False"
     * @return boolean result - false on failure, true on successful save
     */
    public boolean insertCustomQuestion(String question, String category, Integer difficulty, Integer type, ArrayList<String> answers) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues;
        long result = -1;

        if (type == TypeUtil.TF_TYPE) {
            contentValues = new ContentValues();
            contentValues.put(QUES_COLUMN_QUESTION, question);
            contentValues.put(QUES_COLUMN_CATEGORY, category);
            contentValues.put(QUES_COLUMN_DIFFICULTY, difficulty);
            contentValues.put(QUES_COLUMN_TYPE, type);
            contentValues.put(QUES_COLUMN_A1, answers.get(0));
            result = db.insert(QUES_TABLE_NAME, null, contentValues);
        } else if (type == TypeUtil.MC_TYPE) {
            contentValues = new ContentValues();
            contentValues.put(QUES_COLUMN_QUESTION, question);
            contentValues.put(QUES_COLUMN_CATEGORY, category);
            contentValues.put(QUES_COLUMN_DIFFICULTY, difficulty);
            contentValues.put(QUES_COLUMN_TYPE, type);
            contentValues.put(QUES_COLUMN_A1, answers.get(0));
            contentValues.put(QUES_COLUMN_A2, answers.get(1));
            contentValues.put(QUES_COLUMN_A3, answers.get(2));
            contentValues.put(QUES_COLUMN_A4, answers.get(3));
            result = db.insert(QUES_TABLE_NAME, null, contentValues);
        }

        if (result == TypeUtil.RETURN_ERROR) {
            return false;
        } else {
            return true;
        }
    }

    /* getAllCustomQuestions
     * Returns series of strings corresponding to all rows in Custom Question Table
     * @return Cursor res - Cursor containing all custom questions
     */
    public Cursor getAllCustomQuestions() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * from " + QUES_TABLE_NAME, null);
        return res;
    }

    /* getSpecificCustomQuestions
     * Returns series of strings corresponding to category and/or difficulty column
     * @param String category - The category of question to be matched, can be null
     * @param int difficulty - the difficulty of question to be matched, can be null
     * @return Cursor res - Cursor containing all custom questions
     */
    public Cursor getSpecificCustomQuestions(String category, Integer difficulty, Boolean randomize) {
        SQLiteDatabase db = this.getWritableDatabase();
        String rawQuery = null;
        Cursor res;

        // If both null just get all questions in database, equivalent to getAllCustomQuestions
        if (difficulty == null && category == null) {
            rawQuery = "SELECT * from " + QUES_TABLE_NAME;
        // If difficulty sent as null, pick any difficulty
        } else if (difficulty == null) {
            rawQuery = "SELECT * from "
                    + QUES_TABLE_NAME + " WHERE "
                    + QUES_COLUMN_CATEGORY
                    + " = '" + category + "'";
            Log.d("SQLiteDBHelper", "Option 1: " + rawQuery);

        // If category sent as null, pick any category
        } else if (category == null) {
            rawQuery = "SELECT * from "
                    + QUES_TABLE_NAME + " WHERE "
                    + QUES_COLUMN_DIFFICULTY
                    + " = " + difficulty;
            Log.d("SQLiteDBHelper", "Option 2: " + rawQuery);
        // otherwise use both parameters
        } else {
            rawQuery = "SELECT * from "
                    + QUES_TABLE_NAME + " WHERE "
                    + QUES_COLUMN_CATEGORY
                    + " = '" + category + "'" + " AND "
                    + QUES_COLUMN_DIFFICULTY
                    + " = " + difficulty;

            Log.d("SQLiteDBHelper", "Option 3: " + rawQuery);
        }

        // Randomize quetions so quizzes arent all the same
        if (randomize) { rawQuery = rawQuery + " ORDER BY random()";}

        res = db.rawQuery(rawQuery, null);
        return res;
    }

    public String getQuestionID(String question){
        SQLiteDatabase db = this.getWritableDatabase();
        String id = "";
        Cursor res;

        res = db.rawQuery("SELECT * from "
                + QUES_TABLE_NAME + " WHERE "
                + QUES_COLUMN_QUESTION
                + " = " + "\"" + question + "\"", null);

        while(res.moveToNext()){

            int index = res.getColumnIndexOrThrow("_id");
            id = res.getString(index);

        }

        return id;

    }

    /* updateCustomQuestion
     * @depracated
     * Update a particular custom question based on id value
     * @param String id - The id of the question to be updated
     * @param String question - The question being asked
     * @param String category - The category of the question
     * @param int difficulty - The difficulty level the question is to be assigned
     * @return boolean result - false on failure, true on successful save
     */
    /*public boolean updateCustomQuestion(String id, String question, String category, Integer difficulty) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(QUES_COLUMN_ID, id);
        contentValues.put(QUES_COLUMN_QUESTION, question);
        contentValues.put(QUES_COLUMN_CATEGORY, category);
        contentValues.put(QUES_COLUMN_DIFFICULTY, difficulty);
        db.update(QUES_TABLE_NAME, contentValues, "ID = ?", new String[] {id});
        return true;
    }*/

    /* readCustomQuestions
     * Delete a custom question based on id value
     * @return Integer - Returns the number of rows deleted
     */
    public Integer deleteCustomQuestions(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(QUES_TABLE_NAME, QUES_COLUMN_ID + " = ?", new String[] {id});
    }

    /* deleteAllCustomQuestions
     * Deletes all custom questions in the database
     * @return Integer - Returns the number of rows deleted
     */
    public Integer deleteAllCustomQuestions() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(QUES_TABLE_NAME, null, null);
    }


}
