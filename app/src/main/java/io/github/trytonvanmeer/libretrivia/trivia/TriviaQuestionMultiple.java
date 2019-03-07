package io.github.trytonvanmeer.libretrivia.trivia;

import android.text.Html;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

//Multiple choice question
public class TriviaQuestionMultiple extends TriviaQuestion {
    private final String correctAnswer;
    private final String[] incorrectAnswers;

    public TriviaQuestionMultiple(TriviaCategory category, TriviaDifficulty difficulty,
                                  String question, String correctAnswer, String[] incorrectAnswers) {
        super(category, difficulty, question);

        this.correctAnswer = correctAnswer;
        this.incorrectAnswers = incorrectAnswers;
    }

    //merges and returns correct and wrong answers
    public String[] getAnswerList() {
        return new String[]{correctAnswer,
                incorrectAnswers[0],
                incorrectAnswers[1],
                incorrectAnswers[2]};
    }

    //check if user guess is correct
    @Override
    public boolean checkAnswer(String guess) {
        return this.correctAnswer.equals(guess);
    }

    //parses a question from a JSON object
    public static TriviaQuestionMultiple fromJson(JsonObject json) {
        TriviaCategory category = TriviaCategory.get(json.get("category").getAsString());
        TriviaDifficulty difficulty = TriviaDifficulty.get(json.get("difficulty").getAsString());
        String question = Html.fromHtml(json.get("question").getAsString()).toString();
        String correctAnswer = Html.fromHtml(json.get("correct_answer").getAsString()).toString();

        JsonArray incorrectAnswersJson = json.get("incorrect_answers").getAsJsonArray();
        String[] incorrectAnswers = new String[]{
                Html.fromHtml(incorrectAnswersJson.get(0).getAsString()).toString(),
                Html.fromHtml(incorrectAnswersJson.get(1).getAsString()).toString(),
                Html.fromHtml(incorrectAnswersJson.get(2).getAsString()).toString()
        };

        return new TriviaQuestionMultiple(
                category, difficulty, question, correctAnswer, incorrectAnswers);
    }
}
