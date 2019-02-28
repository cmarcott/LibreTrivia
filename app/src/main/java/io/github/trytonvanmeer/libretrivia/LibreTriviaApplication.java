package io.github.trytonvanmeer.libretrivia;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

//essentially a wrapper for application. Extending application is discouraged by Google
public class LibreTriviaApplication extends Application {
    @SuppressLint("StaticFieldLeak")
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        LibreTriviaApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return LibreTriviaApplication.context;
    }
}
