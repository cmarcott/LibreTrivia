package io.github.trytonvanmeer.libretrivia.highscores;

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

public class HSCardView extends RecyclerView.Adapter<HSCardView.HSCardViewHolder>{

    // Use this context to inflate the layout.
    private Context mCtx;

    // Store all the questions in a list.
    private List<HighScore> hsList;

    // Get the context and question list with constructor.
    public HSCardView(Context mCtx, List<HighScore> hsList) {
        this.mCtx = mCtx;
        this.hsList = hsList;
    }

    @Override
    public HSCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.multiple_card, null);
        return new HSCardViewHolder(view);
    }

    // Populate a card with Trivia Question Data.
    @Override
    public void onBindViewHolder(HSCardViewHolder holder, int position) {

        // Get the question of the specified position.
        HighScore hs = hsList.get(position);

        // Build the card
        holder.hs_card_score.setText(String.format("%.2f%%", hs.getScore()));
        holder.hs_card_cat.setText(hs.getCategory());
        holder.hs_card_diff.setText(hs.getDifficulty());
        holder.hs_card_quiz_length.setText(hs.getQuizLength());
    }

    @Override
    public int getItemCount() {
        return hsList.size();
    }

    class HSCardViewHolder extends RecyclerView.ViewHolder {

        TextView hs_card_score,
                hs_card_cat,
                hs_card_diff,
                hs_card_quiz_length;

        public HSCardViewHolder(View itemView) {
            super(itemView);

            hs_card_score = itemView.findViewById(R.id.multiple_card_question);
            hs_card_cat = itemView.findViewById(R.id.multiple_card_cat);
            hs_card_diff = itemView.findViewById(R.id.multiple_card_diff);
            hs_card_quiz_length = itemView.findViewById(R.id.multiple_card_option_4);
        }
    }
}
