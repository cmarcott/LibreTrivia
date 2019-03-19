package io.github.trytonvanmeer.libretrivia.trivia;

import java.io.Serializable;

//Holds data necessary to mak a query to openTDB
public class TriviaQuery implements Serializable {
    private static final String BASE = "https://opentdb.com/api.php?";
    private static final int DEFAULT_AMOUNT = 10;

    private final int amount;
    private final TriviaCategory category;
    private final TriviaDifficulty difficulty;
    private final TriviaType type;

    private TriviaQuery(Builder builder) {
        this.amount = builder.amount;
        this.category = builder.category;
        this.difficulty = builder.difficulty;
        this.type = builder.type;
    }

    //handles value-checking for query creation
    public static class Builder {
        private final int amount;
        private TriviaCategory category;
        private TriviaDifficulty difficulty;
        private TriviaType type;

        public Builder() {
            this.amount = DEFAULT_AMOUNT;
        }

        //cap quiz at 50 questions
        public Builder(int amount) {
            if (amount > 50) {
                this.amount = 50;
            } else {
                this.amount = amount;
            }
        }

        public Builder category(TriviaCategory category) {
            this.category = category;
            return this;
        }

        public Builder difficulty(TriviaDifficulty difficulty) {
            this.difficulty = difficulty;
            return this;
        }

        public Builder type(TriviaType type) {
            this.type = type;
            return this;
        }

        public TriviaQuery build() {
            return new TriviaQuery(this);
        }
    }

    public TriviaCategory getCategory(){
        return this.category;
    }

    public TriviaDifficulty getDifficulty(){
        return this.difficulty;
    }

    //turns the query object into a url
    @Override
    public String toString() {
        StringBuilder url = new StringBuilder();

        url.append(BASE);//openTDB URL
        url.append("amount=").append(this.amount);

        if (this.category != null & this.category != TriviaCategory.ANY) {
            url.append("&category=").append(this.category.getID());
        }
        if (this.difficulty != null & this.difficulty != TriviaDifficulty.ANY) {
            url.append("&difficulty=").append(this.difficulty.getName());
        }
        if (this.type != null & this.type != TriviaType.ANY) {
            url.append("&type=").append(this.type.getName());
        }

        return url.toString();
    }
}
