package com.example.sudouser.nadgodzinki.Settings;

import android.os.Bundle;

import com.example.sudouser.nadgodzinki.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class ListOfCategoriesActivity extends AppCompatActivity implements
        PreferenceFragmentCompat.OnPreferenceStartFragmentCallback
{
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new ListOfCategoriesFragment() ).commit();
    }


    /**
     * metoda, która jest wywoływana gdy użytkowanik kliknie na wybór odpowiedniej kategorii
     * @param caller jest to fragment - klasa która który jest kliknięty przez uż
     * @param pref
     * @return
     */
    @Override
    public boolean onPreferenceStartFragment(PreferenceFragmentCompat caller, Preference pref)
    {
        final Bundle args = pref.getExtras();
        // budujemy fragment z klasy
        final Fragment fragment = getSupportFragmentManager()
                .getFragmentFactory()
                .instantiate( getClassLoader(), pref.getFragment(), args);
        fragment.setArguments(args);
        fragment.setTargetFragment(caller, 0);
        // Replace the existing Fragment with the new Fragment
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, fragment).addToBackStack(null).commit();

        return true;
    }
}
