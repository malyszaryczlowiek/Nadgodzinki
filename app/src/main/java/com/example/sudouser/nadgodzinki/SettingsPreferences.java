package com.example.sudouser.nadgodzinki;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity; // to tez działa
import androidx.preference.PreferenceFragmentCompat;


public class SettingsPreferences extends AppCompatActivity // moze też być  PreferenceActivity
{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsPreferencesFragment() ).commit();
    }


    public static class SettingsPreferencesFragment extends PreferenceFragmentCompat
    {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
        {
            setPreferencesFromResource(R.xml.preferences, rootKey);
            //EditTextPreference preference = (EditTextPreference) findPreference("buckup_email");
        }
    }
}










