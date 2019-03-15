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

//Custom Mode game setup screen
public class CustomModeActivity extends BaseActivity {
    @BindView(R.id.button_create_question)
    Button buttonCreateQuestion;
    @BindView(R.id.button_view_question)
    Button buttonViewQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_mode);
        ButterKnife.bind(this);

        // Set up input fields.
        buttonCreateQuestion.setOnClickListener(v -> {
            Log.d("CREATE QUESTION","Switching to create question.");
            Intent intent = new Intent(getApplicationContext(), QuestionCreateActivity.class);
            startActivity(intent);
        });

        buttonViewQuestion.setOnClickListener(v -> {
            Log.d("CREATE QUESTION","Switching to view question.");
            Intent intent = new Intent(getApplicationContext(), QuestionViewActivity.class);
            startActivity(intent);
        });


    }
}
