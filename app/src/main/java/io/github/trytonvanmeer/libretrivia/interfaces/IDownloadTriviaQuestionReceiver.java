package io.github.trytonvanmeer.libretrivia.interfaces;

//implemented by TriviaGameActivity
public interface IDownloadTriviaQuestionReceiver {
    void onTriviaQuestionsDownloaded(String json); //what to do with the results of the query
}
