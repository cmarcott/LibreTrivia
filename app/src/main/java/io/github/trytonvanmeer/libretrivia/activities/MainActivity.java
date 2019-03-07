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

//The setup screen for the game ("Classic mode" setup)
public class MainActivity extends BaseActivity {
    @BindView(R.id.button_play)
    Button buttonPlay;
    @BindView(R.id.button_create)
    Button buttonCreate;
    @BindView(R.id.spinner_number)
    Spinner spinnerNumber;
    @BindView(R.id.spinner_category)
    Spinner spinnerCategory;
    @BindView(R.id.spinner_difficulty)
    Spinner spinnerDifficulty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //handler for starting the game
        buttonPlay.setOnClickListener(v -> {
            //read from input fields
            int amount = (int) spinnerNumber.getSelectedItem();
            TriviaCategory category = (TriviaCategory) spinnerCategory.getSelectedItem();
            TriviaDifficulty difficulty = (TriviaDifficulty) spinnerDifficulty.getSelectedItem();

            //Create the query for the openTDB database
            TriviaQuery query = new TriviaQuery.Builder(amount)
                    .category(category)
                    .difficulty(difficulty)
                    .build();

            //start the trivia game activity, passing it the query
            Intent intent = new Intent(getApplicationContext(), TriviaGameActivity.class);
            intent.putExtra(TriviaGameActivity.EXTRA_TRIVIA_QUERY, query);
            startActivity(intent);
        });


        //set up input fields
        buttonCreate.setOnClickListener(v -> {
            Log.d("CREATE QUESTION","Switching to create question.");
            Intent intent = new Intent(getApplicationContext(), QuestionCreateActivity.class);
            startActivity(intent);
        });



        Integer[] numbers = new Integer[50];
        for (int i = 0; i < 50; ) {
            numbers[i] = ++i;
        }
        spinnerNumber.setAdapter(
                new ArrayAdapter<>(
                        this, android.R.layout.simple_list_item_1, numbers)
        );
        spinnerNumber.setSelection(9);

        spinnerCategory.setAdapter(
                new ArrayAdapter<>(
                        this, android.R.layout.simple_list_item_1, TriviaCategory.values()));

        spinnerDifficulty.setAdapter(
                new ArrayAdapter<>(
                        this, android.R.layout.simple_list_item_1, TriviaDifficulty.values()));
    }
}
