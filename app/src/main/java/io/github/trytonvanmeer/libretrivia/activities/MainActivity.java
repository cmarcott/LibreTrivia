package io.github.trytonvanmeer.libretrivia.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;
import androidx.appcompat.app.AppCompatDelegate;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.trytonvanmeer.libretrivia.R;
import io.github.trytonvanmeer.libretrivia.database.SQLiteDBHelper;
import io.github.trytonvanmeer.libretrivia.trivia.TriviaCategory;
import io.github.trytonvanmeer.libretrivia.trivia.TriviaDifficulty;
import io.github.trytonvanmeer.libretrivia.trivia.TriviaQuery;
import io.github.trytonvanmeer.libretrivia.util.TypeUtil;

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
            Intent intent = new Intent (getApplicationContext(), ShareActivity.class);
            startActivity(intent);
        });
    }
}
