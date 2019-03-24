package io.github.trytonvanmeer.libretrivia.activities;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class QuestionViewActivity extends BaseActivity {

    @BindView(R.id.spinner_filter_cat)
    Spinner spinnerFilterCat;
    @BindView(R.id.spinner_filter_diff)
    Spinner spinnerFilterDiff;


    private RecyclerView recyclerView;
    private List<TriviaQuestion> questionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_view);
        ButterKnife.bind(this);

        // Fill the category spinner.

        spinnerFilterCat.setAdapter(
                new ArrayAdapter<>(
                        this, android.R.layout.simple_list_item_1, TriviaCategory.values()));

        // Fill the difficulty spinner.
        List<String> diffArray =  new ArrayList<String>();
        diffArray.add("Any");
        diffArray.add("Easy");
        diffArray.add("Medium");
        diffArray.add("Hard");

        ArrayAdapter<String> diffAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, diffArray);

        diffAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilterDiff.setAdapter(diffAdapter);

        // Create action listener for category change.
        spinnerFilterCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String catstr = spinnerFilterCat.getSelectedItem().toString();
                String diffstr = spinnerFilterDiff.getSelectedItem().toString();

                listSpecific(catstr, diffstr);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do something else
            }

        });

        spinnerFilterDiff.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String catstr = spinnerFilterCat.getSelectedItem().toString();
                String diffstr = spinnerFilterDiff.getSelectedItem().toString();

                listSpecific(catstr, diffstr);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do something else
            }

        });

        listAll();



    }

    public void listSpecific(String passCat, String passDif){
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

            if((t.getCategory().name.toLowerCase().equals(passCat.toLowerCase()) || (passCat.equals("Any"))) && ((t.getDifficulty().name.toLowerCase().equals(passDif.toLowerCase()) || passDif.equals("Any")))){ // Avert your eyes from this crime. I had to do it.
                questionList.add(t);
            }
        }

        // Create adapter.
        QuestionCardView adapter = new QuestionCardView(this, questionList);
        recyclerView.setAdapter(adapter);
    }

    public void listAll(){
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
