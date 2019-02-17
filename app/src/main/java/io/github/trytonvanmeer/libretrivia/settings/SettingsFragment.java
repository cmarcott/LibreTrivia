package io.github.trytonvanmeer.libretrivia.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import io.github.trytonvanmeer.libretrivia.R;

public class SettingsFragment extends PreferenceFragment {
    @Override

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        Preference switchDark = findPreference("pref_dark_mode");

        // Action listener for the switch in preference menu.
        switchDark.setOnPreferenceClickListener(preference -> {

            // Get the T or F status of the switch.
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(switchDark.getContext());
            String key_dark = switchDark.getContext().getResources().getString(R.string.pref_dark_mode);
            boolean pref_dark = preferences.getBoolean(key_dark, true);

            //Set the theme accordingly.
            if (!pref_dark) {
                Log.d("SETTINGS","Was true. Making false.");
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }else{
                Log.d("SETTINGS","Was false. Making true.");
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }

            //Recreate the fragment's activity. Without this, the menu will not change color.
            getActivity().recreate();

            //As other activities are resumed, BaseActivity's onResume() function will recreate() them.
            return true;
        });

    }
}
