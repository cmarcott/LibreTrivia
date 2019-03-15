package io.github.trytonvanmeer.libretrivia.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.github.trytonvanmeer.libretrivia.R;
import io.github.trytonvanmeer.libretrivia.trivia.TriviaCategory;
import io.github.trytonvanmeer.libretrivia.trivia.TriviaDifficulty;
import io.github.trytonvanmeer.libretrivia.trivia.TriviaQuestion;
import io.github.trytonvanmeer.libretrivia.trivia.TriviaQuestionBoolean;
import io.github.trytonvanmeer.libretrivia.trivia.TriviaQuestionMultiple;
import io.github.trytonvanmeer.libretrivia.util.QuestionCardView;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class QuestionViewActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private List<TriviaQuestion> questionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_view);

        // Get the recycler view from xml.
        recyclerView = (RecyclerView) findViewById(R.id.question_recycler_view);

        // Possibly not needed.
        recyclerView.setHasFixedSize(true);

        // Unsure of what this does. Don't remove, though.
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create a fake list of fake questions for testing. Use DB in future.

        questionList = new ArrayList<>();

        // Populate question list from SQLLiteDBHelper.
        Cursor c = BaseActivity.myDb.getAllCustomQuestions();

        while(c.moveToNext()){

            int index;
            index = c.getColumnIndexOrThrow("question");
            String que = c.getString(index);
            index = c.getColumnIndexOrThrow("category");
            String dis = c.getString(index);
            index = c.getColumnIndexOrThrow("difficulty");
            String dif = c.getString(index);
            index = c.getColumnIndexOrThrow("type");
            String typ = c.getString(index);
            index = c.getColumnIndexOrThrow("a1");
            String a1 = c.getString(index);
            index = c.getColumnIndexOrThrow("a2");
            String a2 = c.getString(index);
            index = c.getColumnIndexOrThrow("a3");
            String a3 = c.getString(index);
            index = c.getColumnIndexOrThrow("a4");
            String a4 = c.getString(index);

            Log.d("Q_VIEW", "READING QUESTION FROM DB\n" + que + "\n" +
                    dis + "\n" +
                    dif + "\n" +
                    typ + "\n" +
                    a1 + "\n" +
                    a2 + "\n" +
                    a3 + "\n" +
                    a4 + "\n");

            // Type 1 is multiple choice, I think?

            TriviaQuestion t;

            // Determine category from display name.

            TriviaCategory cat = TriviaCategory.ANY;

            for(TriviaCategory i : TriviaCategory.values()){
                if(i.name.equals(dis)){
                    cat = i;
                }
            }

            // The is really hacky. May the lord forgive me.
            int idif = Integer.parseInt(dif);
            if(idif != 3){
                idif ++;
            }

            if(typ.equals("1")){
                String[] arr = new String[3];
                arr[0] = a2;
                arr[1] = a3;
                arr[2] = a4;
                t = new TriviaQuestionMultiple(cat,TriviaDifficulty.values()[idif],que,a1,arr);
            }else{
                t = new TriviaQuestionBoolean(cat,TriviaDifficulty.values()[idif],que,Boolean.parseBoolean(a1));
            }

            questionList.add(t);

        }

        // Create adapter.
        QuestionCardView adapter = new QuestionCardView(this, questionList);
        recyclerView.setAdapter(adapter);


    }
}
