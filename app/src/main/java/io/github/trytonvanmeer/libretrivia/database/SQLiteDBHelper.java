package io.github.trytonvanmeer.libretrivia.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
    // The type of the question, aka T/F or MC type question
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
                QUES_COLUMN_DIFFICULTY + " INT " + " )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + QUES_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    /* insertCustomQuestion
     * A method used to insert a new custom question into the stored local database
     * @param String question - The question being asked
     * @param String category - The category of the question
     * @param int difficulty - The difficulty level the question is to be assigned
     * @return boolean result - false on failure, true on successful save
     */
    public boolean insertCustomQuestion(String question, String category, Integer difficulty, String type, String answers) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(QUES_COLUMN_QUESTION, question);
        contentValues.put(QUES_COLUMN_CATEGORY, category);
        contentValues.put(QUES_COLUMN_DIFFICULTY, difficulty);
        long result = db.insert(QUES_TABLE_NAME, null, contentValues);

        if (result == -1) {
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
    public Cursor getSpecificCustomQuestions(String category, Integer difficulty) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res;
        // If difficulty sent as null, pick any difficulty
        if (difficulty == null) {
            res = db.rawQuery("SELECT * from "
                    + QUES_TABLE_NAME + " WHERE "
                    + QUES_COLUMN_CATEGORY
                    + " = " + category, null);
        // If category sent as null, pick any category
        } else if (category == null) {
            res = db.rawQuery("SELECT * from "
                    + QUES_TABLE_NAME + " WHERE "
                    + QUES_COLUMN_DIFFICULTY
                    + " = " + difficulty, null);
        // otherwise use both parameters
        } else {
            res = db.rawQuery("SELECT * from "
                    + QUES_TABLE_NAME + " WHERE "
                    + QUES_COLUMN_CATEGORY
                    + " = " + category + " AND "
                    + QUES_COLUMN_DIFFICULTY
                    + " = " + difficulty, null);
        }
        return res;
    }

    /* updateCustomQuestion
     * Update a particular custom question based on id value
     * @param String id - The id of the question to be updated
     * @param String question - The question being asked
     * @param String category - The category of the question
     * @param int difficulty - The difficulty level the question is to be assigned
     * @return boolean result - false on failure, true on successful save
     */
    public boolean updateCustomQuestion(String id, String question, String category, Integer difficulty) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(QUES_COLUMN_ID, id);
        contentValues.put(QUES_COLUMN_QUESTION, question);
        contentValues.put(QUES_COLUMN_CATEGORY, category);
        contentValues.put(QUES_COLUMN_DIFFICULTY, difficulty);
        db.update(QUES_TABLE_NAME, contentValues, "ID = ?", new String[] {id});
        return true;
    }

    /* readCustomQuestions
     * Delete a custom question based on id value
     * @return Integer - Returns the number of rows deleted
     */
    public Integer deleteCustomQuestions(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(QUES_TABLE_NAME, "ID = ?", new String[] {id});
    }


}
