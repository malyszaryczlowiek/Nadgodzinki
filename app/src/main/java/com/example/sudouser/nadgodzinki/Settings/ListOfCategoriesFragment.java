package com.example.sudouser.nadgodzinki.Settings;

import android.os.Bundle;

import com.example.sudouser.nadgodzinki.R;

import androidx.preference.PreferenceFragmentCompat;

public class ListOfCategoriesFragment extends PreferenceFragmentCompat
{
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
    {
        setPreferencesFromResource(R.xml.preferences_test, rootKey);
    }
}
