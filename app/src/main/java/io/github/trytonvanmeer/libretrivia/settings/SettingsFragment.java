package io.github.trytonvanmeer.libretrivia.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import androidx.annotation.Nullable;
import io.github.trytonvanmeer.libretrivia.R;

//Display the setings page
public class SettingsFragment extends PreferenceFragment {
     //load with saved user settings
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
