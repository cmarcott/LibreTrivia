package io.github.trytonvanmeer.libretrivia.activities;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.trytonvanmeer.libretrivia.R;
import io.github.trytonvanmeer.libretrivia.fragments.QuestionCreateFragment;
import io.github.trytonvanmeer.libretrivia.trivia.TriviaCategory;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class QuestionCreateActivity extends BaseActivity {

    @BindView(R.id.spinner_type)
    Spinner spinnerType;
    @BindView(R.id.spinner_create_category)
    public Spinner spinnerCategory;
    @BindView(R.id.spinner_create_difficulty)
    public Spinner spinnerDiff;
    @BindView(R.id.question_text)
    public EditText questionEditText;

    public static boolean isMultiple;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_create);
        ButterKnife.bind(this);


        // Fill the type spinner.
        List<String> typeArray =  new ArrayList<String>();
        typeArray.add("Multiple Choice");
        typeArray.add("True False");

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, typeArray);

        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(typeAdapter);

        // Fill the difficulty spinner.
        List<String> diffArray =  new ArrayList<String>();
        diffArray.add("Easy");
        diffArray.add("Medium");
        diffArray.add("Hard");

        ArrayAdapter<String> diffAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, diffArray);

        diffAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDiff.setAdapter(diffAdapter);

        // Listener for when the spinner is changed.
        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                setFragment();
                Log.d("Q_CREATE",spinnerType.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        // Fill the category spinner.
        spinnerCategory.setAdapter(
                new ArrayAdapter<>(
                        this, android.R.layout.simple_list_item_1, TriviaCategory.values()));

        setFragment();

    }

    /**
     * Function to set the correct question creation fragment.
     * Called in onCreate() and whenever typeSpinner's selected item is changed.
     *
     * @author Christopher Oehler
     * @since 2019-02-24
     */
    private void setFragment(){

        // Determine the current spinner setting.
        String type = spinnerType.getSelectedItem().toString();

        // Set exposed boolean so that QuestionCreateFragment knows what .xml to use.
        if(type.equals("Multiple Choice")){
            isMultiple = true;
        }else{
            isMultiple = false;
        }

        // Create fragment.
        Fragment fragment = QuestionCreateFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_create_type_question, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }
}
