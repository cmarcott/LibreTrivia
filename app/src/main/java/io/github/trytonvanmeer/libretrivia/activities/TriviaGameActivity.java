package io.github.trytonvanmeer.libretrivia.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.trytonvanmeer.libretrivia.R;
import io.github.trytonvanmeer.libretrivia.exceptions.NoTriviaResultsException;
import io.github.trytonvanmeer.libretrivia.fragments.TriviaGameErrorFragment;
import io.github.trytonvanmeer.libretrivia.fragments.TriviaQuestionFragment;
import io.github.trytonvanmeer.libretrivia.interfaces.IDownloadTriviaQuestionReceiver;
import io.github.trytonvanmeer.libretrivia.trivia.TriviaGame;
import io.github.trytonvanmeer.libretrivia.trivia.TriviaQuery;
import io.github.trytonvanmeer.libretrivia.trivia.TriviaQuestion;
import io.github.trytonvanmeer.libretrivia.util.ApiUtil;
import io.github.trytonvanmeer.libretrivia.util.SoundUtil;

//Gets and runs through the questions for the quiz
public class TriviaGameActivity extends BaseActivity
        implements IDownloadTriviaQuestionReceiver { //this means it is able to recieve questions from openTDB
    static final String EXTRA_TRIVIA_QUERY = "extra_trivia_query";
    private final String STATE_TRIVIA_GAME = "state_trivia_game";

    private TriviaGame game;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.trivia_status_bar)
    LinearLayout triviaStatusBar;
    @BindView(R.id.text_question_category)
    TextView textViewQuestionCategory;
    @BindView(R.id.text_question_difficulty)
    TextView textViewQuestionDifficulty;
    @BindView(R.id.text_question_progress)
    TextView textViewQuestionProgress;

     //initialization
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trivia_game);
        ButterKnife.bind(this);

        if (savedInstanceState != null) {
            //reload state if we had navigated away and back
            this.game = (TriviaGame) savedInstanceState.getSerializable(STATE_TRIVIA_GAME);
        } else {
            //if this is a new game and not something we're re-entering, we need setup
            Bundle bundle = getIntent().getExtras();
            assert bundle != null;
            //get the query passed from MainActivity
            TriviaQuery query = (TriviaQuery) bundle.get(EXTRA_TRIVIA_QUERY);
            this.game = new TriviaGame(query.getCategory(), query.getDifficulty());

            progressBar.setVisibility(View.VISIBLE);

            //Get the trivia questions in asynchronous task
            DownloadTriviaQuestionsTask task = new DownloadTriviaQuestionsTask();
            task.setReceiver(this);
            task.execute(query);
        }
    }

     //takes care of saving if we navigate away
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(STATE_TRIVIA_GAME, this.game);
    }

     //what to do if phone back button is pressed
    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame_trivia_game);

        if (fragment instanceof TriviaGameErrorFragment) {
            //If we're on the error screen, simply go back to MainActivity
            super.onBackPressed();
        } else {
             //Confirm exiting if in the middle of the game. On yes, go back to MainActivity, else remain in the quiz
            new AlertDialog.Builder(this)
                    .setTitle(R.string.ui_quit_game)
                    .setMessage(R.string.ui_quit_game_msg)
                    .setPositiveButton(android.R.string.yes, (dialog, which) ->
                            TriviaGameActivity.super.onBackPressed())
                    .setNegativeButton(android.R.string.no, (dialog, which) -> {
                    })
                    .show();
        }
    }

    //Executed when the query returns.  Implementation for IDownloadTriviaQuestionReciever
    public void onTriviaQuestionsDownloaded(String json) {
        if (json == null) {
            //Opens an error page
            onNetworkError();
            return;
        } else {
            try {
                //set up game from the questions returned
                this.game.setQuestions(ApiUtil.jsonToQuestionArray(json));
            } catch (NoTriviaResultsException e) {
                //opens an error page
                onNoTriviaResults();
                return;
            }
        }

        // Setup game layout
        progressBar.setVisibility(View.GONE);
        triviaStatusBar.setVisibility(View.VISIBLE);
        updateStatusBar();
        updateTriviaQuestion();
    }

    //Updates question#, category, difficulty when moving to next question
    private void updateStatusBar() {
        //get data
        String progress = getResources().getString(R.string.ui_question_progress,
                game.getQuestionProgress(), game.getQuestionsCount());

        String category = (game.getCurrentQuestion().getCategory() != null)
                ? game.getCurrentQuestion().getCategory().toString() : "";

        String difficulty = game.getCurrentQuestion().getDifficulty().toString();
        
        //display
        textViewQuestionProgress.setText(progress);
        textViewQuestionCategory.setText(category);
        textViewQuestionDifficulty.setText(difficulty);
    }

    //Update to the next question
    private void updateTriviaQuestion() {
        Fragment fragment = TriviaQuestionFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_trivia_game, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    //Display an error message for if there is an error while trying to get data from openTDB
    private void onNetworkError() {
        String msg = getResources().getString(R.string.error_network);
        Fragment errorFragment = TriviaGameErrorFragment.newInstance(msg);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame_trivia_game, errorFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

    //Display an error message for if the query completed properly but returned with no questions
    private void onNoTriviaResults() {
        //String msg = getResources().getString(R.string.error_no_trivia_results);
        //Fragment errorFragment = TriviaGameErrorFragment.newInstance(msg);

        //FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        //ft.replace(R.id.frame_trivia_game, errorFragment);
        //ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        //ft.commit();

        new AlertDialog.Builder(this)
                .setTitle("Not enough questions")
                .setMessage("There weren't enough questions matching your filters. Please ask for fewer questions or broaden your game settings.")
                .setPositiveButton("OK", (dialog, which) ->
                        TriviaGameActivity.super.onBackPressed())
                .show();
    }

    //Asks the user if they wish to continue with the game if they have fewer questions than they wanted


    public TriviaQuestion getCurrentQuestion() {
        return this.game.getCurrentQuestion();
    }

    //Handler for clicking any answer
    public void onAnswerClick(Button answer, Button correctAnswer) {
        //get user guess
        boolean guess = game.nextQuestion(answer.getText().toString());

        //colour green if correct, red if wrong
        final int green = getResources().getColor(R.color.colorAccentGreen);
        final int red = getResources().getColor(R.color.colorAccentRed);
        int color = guess ? green : red;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ColorStateList stateList = ColorStateList.valueOf(color);
            answer.setBackgroundTintList(stateList);

            if (!guess) {
                final ColorStateList greenStateList = ColorStateList.valueOf(green);
                correctAnswer.setBackgroundTintList(greenStateList);
            }
        } else {
            answer.getBackground().getCurrent().setColorFilter(
                    new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));

            if (!guess)
                correctAnswer.getBackground().getCurrent().setColorFilter(
                        new PorterDuffColorFilter(green, PorterDuff.Mode.MULTIPLY));
        }

        //send appropriate sound to be output
        SoundUtil.playSound(this, guess ?
                SoundUtil.SOUND_ANSWER_CORRECT : SoundUtil.SOUND_ANSWER_WRONG);

        //delays for a while so the correctness of the answer can be observed
        new Handler().postDelayed(() -> {
            if (game.isDone()) {
                //open the results activity if there are no more questions
                Intent intent = new Intent(getApplicationContext(), TriviaGameResultsActivity.class);
                intent.putExtra(TriviaGameResultsActivity.EXTRA_TRIVIA_GAME, game);
                startActivity(intent);
                finish();
            } else {
                //otherwise move to the next question
                updateStatusBar();
                updateTriviaQuestion();
            }
        }, 500); //delay value
    }

    //Takes care of internet communication in a background thread
    private static class DownloadTriviaQuestionsTask extends AsyncTask<TriviaQuery, Integer, String> {
        private IDownloadTriviaQuestionReceiver receiver;

        //executes the query
        @Override
        protected String doInBackground(TriviaQuery... query) {
            String json;
            try {
                if (BaseActivity.isCustomGame) {
                    json = ApiUtil.LocalGET(query[0]);
                } else {
                    json = ApiUtil.GET(query[0]);
                }
            } catch (IOException e) {
                return null;
            }
            return json;
        }

        //calls the reciever's handler to deal with the query result
        @Override
        protected void onPostExecute(String json) {
            receiver.onTriviaQuestionsDownloaded(json);
        }

        //designates what will receive the query results
        private void setReceiver(IDownloadTriviaQuestionReceiver receiver) {
            this.receiver = receiver;
        }
    }
}
