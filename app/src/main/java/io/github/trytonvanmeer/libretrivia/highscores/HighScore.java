package io.github.trytonvanmeer.libretrivia.highscores;

public class HighScore {
    private float score;
    private String category;
    private String difficulty;
    private int quizLength;

    public HighScore(float score, String category, String difficulty, int quizLength) {
        this.score = score;
        this.category = category;
        this.difficulty = difficulty;
        this.quizLength = quizLength;
    }

    public float getScore() {
      return score;
    }

    public String getCategory() {
        return category;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public int getQuizLength() {
        return quizLength;
    }
}
