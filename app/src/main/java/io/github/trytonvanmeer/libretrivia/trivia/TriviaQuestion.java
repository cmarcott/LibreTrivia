package io.github.trytonvanmeer.libretrivia.trivia;

import java.io.Serializable;

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

    public abstract boolean checkAnswer(String guess);
}
