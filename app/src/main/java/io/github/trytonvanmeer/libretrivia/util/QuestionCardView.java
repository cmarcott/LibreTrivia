package io.github.trytonvanmeer.libretrivia.util;

import androidx.recyclerview.widget.RecyclerView;
import io.github.trytonvanmeer.libretrivia.R;
import io.github.trytonvanmeer.libretrivia.activities.BaseActivity;
import io.github.trytonvanmeer.libretrivia.activities.QuestionViewActivity;
import io.github.trytonvanmeer.libretrivia.trivia.TriviaQuestion;
import io.github.trytonvanmeer.libretrivia.trivia.TriviaQuestionBoolean;
import io.github.trytonvanmeer.libretrivia.trivia.TriviaQuestionMultiple;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

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

        }
    }

    public void removeAt(int position) {
        questionList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, questionList.size());
    }
}
