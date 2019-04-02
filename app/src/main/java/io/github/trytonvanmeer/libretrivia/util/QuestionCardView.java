package io.github.trytonvanmeer.libretrivia.util;

import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;
import io.github.trytonvanmeer.libretrivia.LibreTriviaApplication;
import io.github.trytonvanmeer.libretrivia.R;
import io.github.trytonvanmeer.libretrivia.activities.BaseActivity;
import io.github.trytonvanmeer.libretrivia.activities.QuestionViewActivity;
import io.github.trytonvanmeer.libretrivia.interfaces.OnBindCallBack;
import io.github.trytonvanmeer.libretrivia.trivia.TriviaQuestion;
import io.github.trytonvanmeer.libretrivia.trivia.TriviaQuestionBoolean;
import io.github.trytonvanmeer.libretrivia.trivia.TriviaQuestionMultiple;
import io.github.trytonvanmeer.libretrivia.bluetooth.BluetoothShareService;

import android.Manifest;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class QuestionCardView extends RecyclerView.Adapter<QuestionCardView.QuestionCardViewHolder>{

    // Use this context to inflate the layout.
    private Context mCtx;

    // Store all the questions in a list.
    private List<TriviaQuestion> questionList;

    // Get the context and question list with constructor.
    public QuestionCardView(Context mCtx, List<TriviaQuestion> questionList) {
        this.mCtx = mCtx;

        // Use fake list of questions. Use DB in future.
        this.questionList = questionList;
    }

    public OnBindCallBack onBind;

    // Bluetooth adapter for sharing questions
    private BluetoothAdapter bluetoothAdapter = null;

    private static final String filename = "questionsJSON.txt";

    @Override
    public QuestionCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.multiple_card, null);
        return new QuestionCardViewHolder(view);
    }

    // Populate a card with Trivia Question Data.
    @Override
    public void onBindViewHolder(QuestionCardViewHolder holder, int position) {

        /**
        if (onBind != null) {
            onBind.onViewBound(holder, position);
        }
         */

        // Get the question of the specified position.
        TriviaQuestion question = questionList.get(position);

        // Bind the data with the view holder views.
        holder.multiple_card_question.setText(question.getQuestion());
        holder.multiple_card_cat.setText(question.getCategory().toString());
        holder.multiple_card_diff.setText(question.getDifficulty().toString());



        holder.card_delete_button.setOnClickListener(v -> {

            // When delete button is pressed, get question text.
            String questionText = holder.multiple_card_question.getText().toString();

            // Reverse lookup question text in DB to get ID.
            String id = BaseActivity.myDb.getQuestionID(questionText);
            BaseActivity.myDb.deleteCustomQuestions(id);


            Log.d("Q_VIEW","DETECTED BUTTON PUSH.");

            // Remove the item from the list.
            int pos = holder.getAdapterPosition();
            removeAt(pos);

        });

        // Build card based on type of question.
        if(question instanceof TriviaQuestionMultiple){

            holder.multiple_card_type.setText("Multiple Choice");
            holder.multiple_card_option_1.setText(((TriviaQuestionMultiple) question).getAnswerList()[0]);
            holder.multiple_card_option_2.setText(((TriviaQuestionMultiple) question).getAnswerList()[1]);
            holder.multiple_card_option_3.setText(((TriviaQuestionMultiple) question).getAnswerList()[2]);
            holder.multiple_card_option_4.setText(((TriviaQuestionMultiple) question).getAnswerList()[3]);

        }else{

            holder.multiple_card_type.setText("True / False");
            holder.multiple_card_option_1.setText(((TriviaQuestionBoolean)question).getBooleanAnswer().toString());
            holder.multiple_card_option_2.setText("");
            holder.multiple_card_option_3.setText("");
            holder.multiple_card_option_4.setText("");

        }

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        QuestionViewActivity activity = new QuestionViewActivity();

        //Functionality to share question via Bluetooth
        holder.card_share_button.setOnClickListener(v -> {
            if (!bluetoothAdapter.isEnabled()) {
                Toast.makeText(LibreTriviaApplication.getAppContext(), "Bluetooth is not enabled", Toast.LENGTH_SHORT).show();
                return;
            }

            String fileBody;

            // Get info from card
            String questionText = holder.multiple_card_question.getText().toString();
            String category = holder.multiple_card_cat.getText().toString();
            String diff = holder.multiple_card_diff.getText().toString();
            String a1, a2, a3, a4;
            String type;
            JSONObject jsonObj = new JSONObject();

            if(question instanceof TriviaQuestionMultiple){
                a1 = (((TriviaQuestionMultiple) question).getAnswerList()[0]);
                a2 = (((TriviaQuestionMultiple) question).getAnswerList()[1]);
                a3 = (((TriviaQuestionMultiple) question).getAnswerList()[2]);
                a4 = (((TriviaQuestionMultiple) question).getAnswerList()[3]);
                type = "multiple";

            }else{
                a1 = (((TriviaQuestionBoolean) question).getBooleanAnswer().toString());
                a2 = "";
                a3 = "";
                a4 = "";
                type = "boolean";

            }

            try{
                jsonObj.put("question", questionText);
                jsonObj.put("category", category);
                jsonObj.put("difficulty", diff);
                jsonObj.put("type", type);
                jsonObj.put("a1", a1);
                jsonObj.put("a2", a2);
                jsonObj.put("a3", a3);
                jsonObj.put("a4", a4);
            } catch(JSONException e) {
                e.printStackTrace();
            }

            fileBody = jsonObj.toString();
            Log.d("JSON", fileBody);

            //Get application path and write JSONObject to file
            File file = new File(LibreTriviaApplication.getAppContext().getExternalFilesDir(null), "shared_bluetooth_questions");
            if(!file.exists()) {
                file.mkdir();
            } else {
                fileBody = "\n" + fileBody;
            }

            File questionFile = null;
            try{
                questionFile = new File(file, filename);
                FileWriter writer = new FileWriter(questionFile, true);
                writer.append(fileBody);
                writer.flush();
                writer.close();
                Log.d("SHARE", "File created in "+LibreTriviaApplication.getAppContext().getExternalFilesDir(null)+" ");

            }catch (Exception e){
                e.printStackTrace();
                Log.d("SHARE", "Error! File not created");
            }

            //Bluetooth sharing
            Intent discoveryIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoveryIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            //startActivityForResult(discoveryIntent, 1);

            try {
                Intent bluetoothIntent = new Intent();
                Log.d("BLUETOOTH", "try mf");
                Uri fileUri = FileProvider.getUriForFile(mCtx,
                        LibreTriviaApplication.getAppContext().getPackageName()
                        + ".provider", questionFile);
                Log.d("BLUETOOTH", fileUri.toString());
                bluetoothIntent.setAction(Intent.ACTION_SEND);
                bluetoothIntent.setType("*/*");
                bluetoothIntent.putExtra(Intent.EXTRA_STREAM, fileUri);

                PackageManager pm = LibreTriviaApplication.getAppContext().getPackageManager();
                List<ResolveInfo> appsList = pm.queryIntentActivities(bluetoothIntent, 0);

                if (appsList.size() > 0) {
                    String packageName = null;
                    String className = null;
                    boolean found = false;

                    for (ResolveInfo info : appsList) {
                        packageName = info.activityInfo.packageName;
                        if (packageName.equals("com.android.bluetooth")) {
                            className = info.activityInfo.name;
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        Toast.makeText(LibreTriviaApplication.getAppContext(), "Bluetooth has not been found",
                                Toast.LENGTH_LONG).show();
                    } else {
                        bluetoothIntent.setClassName(packageName, className);
                        mCtx.startActivity(bluetoothIntent);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }


    @Override
    public int getItemCount() {
        return questionList.size();
    }

    class QuestionCardViewHolder extends RecyclerView.ViewHolder {

        TextView multiple_card_question,
                multiple_card_cat,
                multiple_card_diff,
                multiple_card_type,
                multiple_card_option_1,
                multiple_card_option_2,
                multiple_card_option_3,
                multiple_card_option_4;

        Button card_delete_button;
        Button card_share_button;
        public QuestionCardViewHolder(View itemView) {
            super(itemView);


            multiple_card_question = itemView.findViewById(R.id.multiple_card_question);
            multiple_card_cat = itemView.findViewById(R.id.multiple_card_cat);
            multiple_card_diff = itemView.findViewById(R.id.multiple_card_diff);
            multiple_card_type = itemView.findViewById(R.id.multiple_card_type);
            multiple_card_option_1 = itemView.findViewById(R.id.multiple_card_option_1);
            multiple_card_option_2 = itemView.findViewById(R.id.multiple_card_option_2);
            multiple_card_option_3 = itemView.findViewById(R.id.multiple_card_option_3);
            multiple_card_option_4 = itemView.findViewById(R.id.multiple_card_option_4);
            card_delete_button = itemView.findViewById(R.id.card_delete_button);
            card_share_button = itemView.findViewById(R.id.card_share_button);

        }


    }

    public void removeAt(int position) {
        questionList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, questionList.size());
    }


}
