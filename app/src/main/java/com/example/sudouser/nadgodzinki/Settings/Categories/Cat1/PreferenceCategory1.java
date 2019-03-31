package com.example.sudouser.nadgodzinki.Settings.Categories.Cat1;

import android.os.Bundle;

import com.example.sudouser.nadgodzinki.R;

import androidx.preference.PreferenceFragmentCompat;

public class PreferenceCategory1 extends PreferenceFragmentCompat
{
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
    {
        setPreferencesFromResource(R.xml.preference_category_1, rootKey);
    }
}
