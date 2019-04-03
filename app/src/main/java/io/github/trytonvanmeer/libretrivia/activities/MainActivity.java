package io.github.trytonvanmeer.libretrivia.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.trytonvanmeer.libretrivia.R;

//The landing screen to choose game modes
public class MainActivity extends BaseActivity {
    @BindView(R.id.button_classic_mode)
    Button buttonClassicMode;
    @BindView(R.id.button_custom_mode)
    Button buttonCustomMode;
    @BindView (R.id.button_high_score)
    Button buttonHighScores;
    @BindView (R.id.button_share)
    Button buttonShare;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        buttonClassicMode.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ClassicModeActivity.class);
            startActivity(intent);
        });

        buttonCustomMode.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), CustomModeActivity.class);
            startActivity(intent);
        });

        buttonHighScores.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), HighScoresActivity.class);
            startActivity(intent);
        });
        buttonShare.setOnClickListener(v -> {
            Intent intent = new Intent (getApplicationContext(), QuestionViewActivity.class);
            startActivity(intent);
        });
    }
}
