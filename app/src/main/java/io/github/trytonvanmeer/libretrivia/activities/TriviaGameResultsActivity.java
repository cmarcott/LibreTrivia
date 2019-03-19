package io.github.trytonvanmeer.libretrivia.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.trytonvanmeer.libretrivia.R;
import io.github.trytonvanmeer.libretrivia.trivia.TriviaGame;

//Shows the score after the quiz is finished
public class TriviaGameResultsActivity extends BaseActivity {
    static final String EXTRA_TRIVIA_GAME = "extra_trivia_game";

    @BindView(R.id.text_results_correct)
    TextView textResultsCorrect;
    @BindView(R.id.text_results_wrong)
    TextView textResultsWrong;
    @BindView(R.id.text_results_total)
    TextView textResultsTotal;
    @BindView(R.id.text_high_score)
    TextView textHighScore;
    @BindView(R.id.text_new_high_score)
    TextView textNewHighScore;
    @BindView(R.id.button_return_to_menu)
    Button buttonReturnToMenu;

    //Initialization
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trivia_game_results);
        ButterKnife.bind(this);

        //get data passed from the game activity
        Bundle bundle = getIntent().getExtras();
        TriviaGame game = (TriviaGame) bundle.get(EXTRA_TRIVIA_GAME);

        int correctTotal = 0;

        for (boolean result : game.getResults()) {
            if (result) {
                correctTotal++;
            }
        }

        float highScore = 0;//get from DB
        float currentScore = ((float) correctTotal) / game.getQuestionsCount() * 100;

        //display
        textResultsCorrect.setText(String.valueOf(correctTotal));
        textResultsWrong.setText(String.valueOf(game.getQuestionsCount() - correctTotal));
        textResultsTotal.setText(String.valueOf(game.getQuestionsCount()));


        if (currentScore > highScore) {
            textHighScore.setText(currentScore + "%");
        } else {
            textHighScore.setText(highScore + "%");
            textNewHighScore.setText("");
        }

        //try to send high score to DB
        
        
        

        //Listener for return button
        buttonReturnToMenu.setOnClickListener(v -> finish());
    }
}
