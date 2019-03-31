package com.example.sudouser.nadgodzinki.Settings.Categories.Cat2;

import android.os.Bundle;

import com.example.sudouser.nadgodzinki.R;

import androidx.preference.PreferenceFragmentCompat;

public class PreferenceCategory2 extends PreferenceFragmentCompat
{
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
    {
        setPreferencesFromResource(R.xml.preference_category_2, rootKey);
    }
}
