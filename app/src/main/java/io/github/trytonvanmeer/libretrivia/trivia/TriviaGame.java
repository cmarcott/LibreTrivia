package io.github.trytonvanmeer.libretrivia.trivia;

import java.io.Serializable;
import java.util.List;

//holds questions, results, state of a trivia game
public class TriviaGame implements Serializable {
    private int currentQuestion;
    private boolean[] results;
    private List<TriviaQuestion> questions;
    private TriviaCategory category;
    private TriviaDifficulty difficulty;

    public TriviaGame(TriviaCategory category, TriviaDifficulty difficulty) {
        this.currentQuestion = 0;
        this.category = category;
        this.difficulty = difficulty;
    }

    public void setQuestions(List<TriviaQuestion> questions) {
        this.questions = questions;
        this.results = new boolean[questions.size()];
    }

    public TriviaCategory getCategory(){
        return this.category;
    }

    public TriviaDifficulty getDifficulty(){
        return this.difficulty;
    }

    public TriviaQuestion getCurrentQuestion() {
        return this.questions.get(currentQuestion);
    }

     //get current question number
    public int getQuestionProgress() {
        return this.currentQuestion + 1;
    }

    //get # of questions in the quiz
    public int getQuestionsCount() {
        return this.questions.size();
    }

    public boolean[] getResults() {
        return this.results;
    }

    //check answer, store result, move to next question
    public boolean nextQuestion(String guess) {
        TriviaQuestion question = getCurrentQuestion();
        boolean answer = question.checkAnswer(guess);

        results[currentQuestion] = answer;
        currentQuestion++;

        return answer;
    }

     //returns true iff there are no more questions to answer
    public boolean isDone() {
        return (this.currentQuestion == questions.size());
    }
}
