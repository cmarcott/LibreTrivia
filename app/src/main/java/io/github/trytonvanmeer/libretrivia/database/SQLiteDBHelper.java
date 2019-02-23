package io.github.trytonvanmeer.libretrivia.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteDBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "Local_Data";

    /* Highscores Table */
    public static final String QUESTIONS_TABLE_NAME = "custom_questions";
    public static final String QUESTIONS_COLUMN_ID = "_id";
    public static final String QUESTIONS_COLUMN_QUESTION = "question";
    public static final String QUESTIONS_COLUMN_CATEGORY = "category";
    public static final String QUESTIONS_COLUMN_DIFFICULTY = "difficulty";

    public SQLiteDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + QUESTIONS_TABLE_NAME + " (" +
                QUESTIONS_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                QUESTIONS_COLUMN_QUESTION + " TEXT, " +
                QUESTIONS_COLUMN_CATEGORY + " TEXT, " +
                QUESTIONS_COLUMN_DIFFICULTY + " INT " + " )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + QUESTIONS_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public boolean addData(String table, String item) {
        SQLiteDatabase db = this.getWritableDatabase();
        return true;
    }

    public boolean readData(String query) {
        return true;
    }
}
