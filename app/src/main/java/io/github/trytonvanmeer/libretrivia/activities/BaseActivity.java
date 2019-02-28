package io.github.trytonvanmeer.libretrivia.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import io.github.trytonvanmeer.libretrivia.R;
import io.github.trytonvanmeer.libretrivia.settings.SettingsActivity;


//The activity from which most other activities inherit.  Contains the top actionbar
@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {


    boolean isDarkMode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        isDarkMode = preferences.getBoolean("pref_dark_mode", true);

        if(!isDarkMode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

    }

    //creates options menu from xml (the ... button's menu)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_menu, menu);
        return true;
    }

    //Redirects menu item clicks to appropriate handlers
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                onSettings();
                return true;
            case R.id.about:
                onAbout();
                return true;
            case android.R.id.home: //bottom-right back button
                onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //opens the settings activity
    private void onSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    //opens the about activity
    private void onAbout() {
        String appName = getResources().getString(R.string.app_name);
        String appDescription = getResources().getString(R.string.app_description);

        //this is third-party stuff to generate the list of libraries used
        new LibsBuilder()
                .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                .withAboutIconShown(true)
                .withAboutAppName(appName)
                .withAboutVersionShownName(true)
                .withAboutDescription(appDescription)
                .start(this);
    }

    /**
     * Function runs when the activity is paused then resumed.
     * Currently onResume() serves only to handle the recreation of activities after switching to dark mode.
     *
     * @author Christopher Oehler
     * @since 2019-02-15
     */
    protected void onResume() {

        super.onResume();

        // Get the status of dark mode (T or F).
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean currentIsNotDark = preferences.getBoolean("pref_dark_mode", true);

        // Handler creates a 1 millisecond delay.
        // Recreate() cannot be run from onResume without a non-fatal exception. This delay solves this for some reason.
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            if(currentIsNotDark != isDarkMode){
                recreate();
            }
        }, 1);
    }
}
