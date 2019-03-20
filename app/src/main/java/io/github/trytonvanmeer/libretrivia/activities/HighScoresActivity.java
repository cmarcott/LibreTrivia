package io.github.trytonvanmeer.libretrivia.activities;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.ButterKnife;
import io.github.trytonvanmeer.libretrivia.R;
import io.github.trytonvanmeer.libretrivia.highscores.HSCardView;
import io.github.trytonvanmeer.libretrivia.highscores.HighScore;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class HighScoresActivity extends BaseActivity {



    private RecyclerView recyclerView;
    private List<HighScore> hsList;//TODO create class

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hs_view);
        ButterKnife.bind(this);

        listAll();



    }

    public void listAll(){
        // Get the recycler view from xml.
        recyclerView = (RecyclerView) findViewById(R.id.hs_recycler_view);

        // Possibly not needed.
        recyclerView.setHasFixedSize(true);

        // Unsure of what this does. Don't remove, though.
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        hsList = new ArrayList<HighScore>();

        // Populate question list from SQLLiteDBHelper.
        Cursor c = BaseActivity.myDb.getAllHighScores();

        while(c.moveToNext()){

            int index;
            index = c.getColumnIndexOrThrow("score");
            float score = c.getFloat(index);
            index = c.getColumnIndexOrThrow("category");
            String cat = c.getString(index);
            index = c.getColumnIndexOrThrow("difficulty");
            String dif = c.getString(index);
            index = c.getColumnIndexOrThrow("quizLength");
            int length = c.getInt(index);

            Log.d("Q_VIEW", "READING QUESTION FROM DB\n" + score + "\n" +
                    cat + "\n" +
                    dif + "\n" +
                    length + "\n");

            HighScore h = new HighScore(score, cat, dif, length);

            hsList.add(h);
        }

        // Create adapter.
        HSCardView adapter = new HSCardView(this, hsList);
        recyclerView.setAdapter(adapter);
    }
}
