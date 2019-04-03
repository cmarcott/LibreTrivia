package io.github.trytonvanmeer.libretrivia.trivia;

import android.util.Log;

import java.io.Serializable;
import java.util.Comparator;

//superclass for MC and T/F questions
public abstract class TriviaQuestion implements Serializable {
    private final TriviaCategory category;
    private final TriviaDifficulty difficulty;

    private final String question;

    TriviaQuestion(TriviaCategory category, TriviaDifficulty difficulty, String question) {
        this.category = category;
        this.difficulty = difficulty;

        this.question = question;
    }

    public TriviaCategory getCategory() {
        return this.category;
    }

    public TriviaDifficulty getDifficulty() {
        return this.difficulty;
    }

    public String getQuestion() {
        return this.question;
    }

    // Comparator for sorting the list of TriviaQuestions by question text.
    public static Comparator<TriviaQuestion> QuestionTextComparator = (q1, q2) -> {
        String QuestionText1 = q1.getQuestion().toUpperCase();
        String QuestionText2 = q2.getQuestion().toUpperCase();
        Log.d("Q_SORT", "Compare Question");
        Log.d("Q_SORT", "\tgetQuestion(): " + QuestionText1);
        Log.d("Q_SORT", "\tgetQuestion(): " + QuestionText2);
        // Ascending order
        return QuestionText1.compareTo(QuestionText2);
    };

    // Comparator for sorting the list of TriviaQuestions by category text.
    public static Comparator<TriviaQuestion> CategoryComparator = (q1, q2) -> {
        String CategoryText1 = q1.getCategory().toString().toUpperCase();
        String CategoryText2 = q2.getCategory().toString().toUpperCase();
        Log.d("Q_SORT", "Compare Category");
        Log.d("Q_SORT", "\tgetCategory().toString: " + CategoryText1);
        Log.d("Q_SORT", "\tgetCategory().toString: " + CategoryText2);
        // Ascending order
        return CategoryText1.compareTo(CategoryText2);
    };

    // Comparator for sorting the list of TriviaQuestions by difficulty text.
    public static Comparator<TriviaQuestion> DifficultyComparator = (q1, q2) -> {
        String DifficultyText1 = q1.getDifficulty().toString().toUpperCase();
        String DifficultyText2 = q2.getDifficulty().toString().toUpperCase();
        Log.d("Q_SORT", "Compare Difficulty");
        Log.d("Q_SORT", "\tgetDifficulty().toString: " + DifficultyText1);
        Log.d("Q_SORT", "\tgetDifficulty().toString: " + DifficultyText2);
        // Ascending order
        return DifficultyText1.compareTo(DifficultyText2);
    };

    public abstract boolean checkAnswer(String guess);
}
