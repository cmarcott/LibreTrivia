package io.github.trytonvanmeer.libretrivia.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import io.github.trytonvanmeer.libretrivia.R;
import io.github.trytonvanmeer.libretrivia.activities.BaseActivity;
import io.github.trytonvanmeer.libretrivia.activities.QuestionCreateActivity;
import io.github.trytonvanmeer.libretrivia.util.TypeUtil;


public class QuestionCreateFragment extends Fragment {

    // Multiple form components.
    EditText correctEditText;
    EditText incorrectEditText1;
    EditText incorrectEditText2;
    EditText incorrectEditText3;
    Button multipleSubmit;

    // Boolean form components.
    Spinner spinnerBooleanAnswer;
    Button booleanSubmit;

    public QuestionCreateFragment(){

    }

    public static QuestionCreateFragment newInstance() {
        return new QuestionCreateFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view;

        // When fragment created, get question type.
        if (QuestionCreateActivity.isMultiple) {

            // Create multiple choice question.
            view = inflater.inflate(R.layout.fragment_question_create_multiple, container, false);
            ButterKnife.bind(this, view);

            // Multiple components.
            this.correctEditText = view.findViewById(R.id.correct_question_input);
            this.incorrectEditText1 = view.findViewById(R.id.incorrect_question_input_1);
            this.incorrectEditText2 = view.findViewById(R.id.incorrect_question_input_2);
            this.incorrectEditText3 = view.findViewById(R.id.incorrect_question_input_3);
            this.multipleSubmit = view.findViewById(R.id.multiple_submit);

            // Load animation.
            Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);

            multipleSubmit.setOnClickListener(v -> {
                Log.d("Q_CREATE","M BUTTON PRESSED");

                boolean valid = true;

                // Gather inputs from activity.
                QuestionCreateActivity parentActivity = (QuestionCreateActivity)getActivity();
                String question = parentActivity.questionEditText.getText().toString();
                String category = parentActivity.spinnerCategory.getSelectedItem().toString();
                String difficulty = parentActivity.spinnerDiff.getSelectedItem().toString();

                // Gather input from edit texts.
                String correctAnswer = correctEditText.getText().toString();
                String incorrectAnswer1 = incorrectEditText1.getText().toString();
                String incorrectAnswer2 = incorrectEditText2.getText().toString();
                String incorrectAnswer3 = incorrectEditText3.getText().toString();

                if(correctAnswer.equals("")){
                    valid = false;
                    correctEditText.setHintTextColor(Color.RED);
                    correctEditText.startAnimation(shake);
                }

                if(incorrectAnswer1.equals("")){
                    valid = false;
                    incorrectEditText1.setHintTextColor(Color.RED);
                    incorrectEditText1.startAnimation(shake);
                }

                if(incorrectAnswer2.equals("")){
                    valid = false;
                    incorrectEditText2.setHintTextColor(Color.RED);
                    incorrectEditText2.startAnimation(shake);
                }

                if(incorrectAnswer3.equals("")){
                    valid = false;
                    incorrectEditText3.setHintTextColor(Color.RED);
                    incorrectEditText3.startAnimation(shake);
                }

                if(question.equals("")){
                    parentActivity.questionEditText.setHintTextColor(Color.RED);
                    parentActivity.questionEditText.startAnimation(shake);
                    valid = false;
                }

                if(valid){

                    /*
                    Store MC Question in database
                     */
                    Integer diff = null;
                    switch (difficulty) {
                        case "Easy":
                            diff = TypeUtil.EASY;
                            break;
                        case "Medium":
                            diff = TypeUtil.MEDIUM;
                            break;
                        case "Hard":
                            diff = TypeUtil.HARD;
                            break;
                    }
                    // Uncomment when view returns to main page
                    BaseActivity.myDb.insertCustomQuestion(question, category, diff, TypeUtil.MC_TYPE, new ArrayList<String>(Arrays.asList(correctAnswer, incorrectAnswer1, incorrectAnswer2, incorrectAnswer3)));

                    Log.d("Q_CREATE", "\t" + question + "\n" +
                            "\t" + difficulty + "\n" +
                            "\t" + category + "\n" +
                            "\t" + correctAnswer + "\n" +
                            "\t" + incorrectAnswer1 + "\n" +
                            "\t" + incorrectAnswer2 + "\n" +
                            "\t" + incorrectAnswer3);

                    getActivity().onBackPressed();
                }
            });
        } else {

            // Create true false question.
            view = inflater.inflate(R.layout.fragment_question_create_boolean, container, false);
            ButterKnife.bind(this, view);

            // Bind components.
            this.spinnerBooleanAnswer = view.findViewById(R.id.spinner_correct_boolean);
            this.booleanSubmit = view.findViewById(R.id.boolean_submit);

            // Populate spinnerBooleanAnswer.
            List<String> typeArray =  new ArrayList<String>();
            typeArray.add("True");
            typeArray.add("False");
            ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, typeArray);
            typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerBooleanAnswer.setAdapter(typeAdapter);
            Animation shake = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
            booleanSubmit.setOnClickListener(v -> {
                Log.d("Q_CREATE","B BUTTON PRESSED");

                // Gather inputs from activity.
                QuestionCreateActivity parentActivity = (QuestionCreateActivity)getActivity();
                String question = parentActivity.questionEditText.getText().toString();
                String category = parentActivity.spinnerCategory.getSelectedItem().toString();
                String difficulty = parentActivity.spinnerDiff.getSelectedItem().toString();

                // Gather input from spinner.
                String answer = spinnerBooleanAnswer.getSelectedItem().toString();

                boolean valid = true;

                if(question.equals("")){
                    parentActivity.questionEditText.setHintTextColor(Color.RED);
                    parentActivity.questionEditText.startAnimation(shake);
                    valid = false;
                }

                if(valid){

                    /*
                    Store T/F question in database
                     */
                    Integer diff = null;
                    switch (difficulty) {
                        case "Easy":
                            diff = TypeUtil.EASY;
                            break;
                        case "Medium":
                            diff = TypeUtil.MEDIUM;
                            break;
                        case "Hard":
                            diff = TypeUtil.HARD;
                            break;
                    }

                    // Uncomment when view returns to main page
                    BaseActivity.myDb.insertCustomQuestion(question, category, diff, TypeUtil.TF_TYPE, new ArrayList<String>(Arrays.asList(answer)));

                    Log.d("Q_CREATE", "\t" + question + "\n" +
                            "\t" + difficulty + "\n" +
                            "\t" + category + "\n" +
                            "\t" + answer);

                    getActivity().onBackPressed();
                }

            });
        }



        return view;
    }
}
