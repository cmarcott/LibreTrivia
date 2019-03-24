package io.github.trytonvanmeer.libretrivia.util;

import android.util.Log;

public class TypeUtil {

    // True/False type question
    public static final int TF_TYPE = 0;

    // Multiple Choice type question
    public static final int MC_TYPE = 1;

    // Easy difficulty level
    public static final int EASY = 0;

    // Medium difficulty level
    public static final int MEDIUM = 1;

    // Hard difficulty level
    public static final int HARD = 3;

    // Error Return
    public static final int RETURN_ERROR = -1;


    // Helper function to convert types to JSON acceptable string
    public static String convertTypeToString(Integer type) {
        String typ = null;
        switch (type) {
            case TF_TYPE:
                typ = "boolean";
                break;
            case MC_TYPE:
                typ = "multiple";
                break;
        }

        return typ;
    }

    public static String returnOppositeBooleanAnswer(String a1) {
        if (a1.toLowerCase().equals("false")) {
            return "True";
        } else if (a1.toLowerCase().equals("true")) {
            return "False";
        }
        Log.w("TypeUtil", "No Match found, returning null result: " + a1);
        return null;
    }

    // Helper functions to convert difficulty types to needed format
    public static Integer convertDifficultyToInt(String difficulty) {
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
        return diff;
    }

    public static String convertDifficultyToString(Integer difficulty) {
        String diff = null;
        switch (difficulty) {
            case 0:
                diff = "Easy";
                break;
            case 1:
                diff = "Medium";
                break;
            case 2:
                diff = "Hard";
                break;
        }
        return diff;
    }


}
