package com.example.sudouser.nadgodzinki;

import com.example.sudouser.nadgodzinki.ViewModels.ItemViewModel;
import com.example.sudouser.nadgodzinki.ViewModels.StatisticsViewModel;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class Statistics extends AppCompatActivity
{
    private StatisticsViewModel statisticsViewModel;

    /**
     * The {@link androidx.viewpager.widget.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * androidx.fragment.app.FragmentStatePagerAdapter.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        statisticsViewModel = ViewModelProviders.of(this).get(StatisticsViewModel.class);

        // definiuję toolbar i  ustawiam mu powrotny przycisk do głównego okna
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setTitle(R.string.statistics);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

        // jak w opisie powyżej jest to podklasa FragmentPagerAdapter, czyli
        // tej, która ma określoną ilość tabów w przeciwieństwie do
        // FragmentStatePaferAdapter, która loaduje na bierząco bo nie wiadomo
        // ilo może mieć tabów.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.view_pager_container);
        // aby umieścić child views czyli pojedyńcze fragmenty dla danej podstworny trzeba
        // zaczepić ten layout (czyli view który ma różne fragmenty) w odpowiednim adapterze
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // wczytuję taby.
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        // dodaję listenera, że zmienia się tab
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        // definiuję floating action bar, który pojawia się na dole i od razu przypisuję mmu
        // listenera.
        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
         */
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_statistics, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
     */


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment
    {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static StatisticsViewModel mStatisticsViewModel;

        // o ile dobrze pamiętam to trzeba chyba zaimplementować defaultowy
        // konstrktor, bo jak nie to krzyczał, że nie ma takowego.
        public PlaceholderFragment()
        {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber, StatisticsViewModel statisticsViewModel)
        {
            PlaceholderFragment fragment = new PlaceholderFragment(); // tworzę nowy pusty eglemplarz klasy
            fragment.setViewModel(statisticsViewModel);
            Bundle args = new Bundle();// generuję bundle w którym zapisze dane do przechowywania, na wypadek zmiany konfiguracji
            args.putInt(ARG_SECTION_NUMBER, sectionNumber); // przypisuję dane na wypadek konfiguracji tutaj jest to numer tabu w którym był wyświetlany dany fragment przed zminą konfiguracji
            fragment.setArguments(args); // przypisuję bundle do obiektu fragmentu
            return fragment; // zwracam fragment
        }

        /**
         * metoda, która jest wywoływana w momencie
         * @param inflater
         * @param container
         * @param savedInstanceState
         * @return zwrócone view będzie tym, które wyświetli sie w
         */
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState)
        {
            // tworzę całą hierarchię gdzie pokolei argumentami są: plik xml, z konfiguracją widgetu
            // kontener do jakiego ten cały widget ma zostać zapisany (tutaj jest to kontener w aktywności)
            View rootView = inflater.inflate(R.layout.fragment_statistics, container, false);


            TextView time_to_take = rootView.findViewById(R.id.text_view_time_to_take);
            time_to_take.setVisibility(View.GONE);

            TextView statistics_time_to_take = rootView.findViewById(R.id.statisticsTimeToTake);
            statistics_time_to_take.setVisibility(View.GONE);
            int minutes_to_take, hours_to_take;
            int min = mStatisticsViewModel.allNumberOfMinutesTaken();
            hours_to_take = min / 60;
            minutes_to_take = min % 60;
            String m = (minutes_to_take < 10) ? "0" + minutes_to_take : String.valueOf(minutes_to_take);
            String s = hours_to_take + ":"+ m;
            statistics_time_to_take.setText(s);

            Calendar pastYear = Calendar.getInstance();
            pastYear.add(Calendar.YEAR, -1);

            Calendar pastQuarter = Calendar.getInstance();
            pastQuarter.add(Calendar.MONTH, -3);

            Calendar pastMonth = Calendar.getInstance();
            pastMonth.add(Calendar.MONTH, -1);


            int minutesDone, minutesTaken;
            int year, month, day;
            int itemNumber = getArguments().getInt(ARG_SECTION_NUMBER);

            switch (itemNumber)
            {
                case 0: // sumuj wszystkie
                    time_to_take.setVisibility(View.VISIBLE);
                    statistics_time_to_take.setVisibility(View.VISIBLE);
                    minutesDone = mStatisticsViewModel.numberOfMinutesDone(0,0,0);
                    minutesTaken = mStatisticsViewModel.numberOfMinutesTaken(0, 0,0);
                    break;
                case 1: // ostatni miesiąc
                    year = pastMonth.get(Calendar.YEAR);
                    month = pastMonth.get(Calendar.MONTH) + 1;
                    day = pastMonth.get(Calendar.DAY_OF_MONTH);
                    minutesDone = mStatisticsViewModel.numberOfMinutesDone(year, month, day);
                    minutesTaken = mStatisticsViewModel.numberOfMinutesTaken(year, month, day);
                    break;
                case 2: // ostatni kwartał
                    year = pastQuarter.get(Calendar.YEAR);
                    month = pastQuarter.get(Calendar.MONTH) + 1;
                    day = pastQuarter.get(Calendar.DAY_OF_MONTH);
                    minutesDone = mStatisticsViewModel.numberOfMinutesDone(year, month, day);
                    minutesTaken = mStatisticsViewModel.numberOfMinutesTaken(year, month, day);
                    break;
                case 3: // ostatni rok
                    year = pastYear.get(Calendar.YEAR);
                    month = pastYear.get(Calendar.MONTH) + 1;
                    day = pastYear.get(Calendar.DAY_OF_MONTH);
                    minutesDone = mStatisticsViewModel.numberOfMinutesDone(year, month, day);
                    minutesTaken = mStatisticsViewModel.numberOfMinutesTaken(year, month, day);
                    break;
                default:
                    minutesDone = 0;
                    minutesTaken = 0;
                    break;

            }

            // przeliczam sobie tutaj ilość minut na konkretną ilośc godzin i minut
            int minutesT, minutesD, hoursT, hoursD;
            minutesD = minutesDone % 60;
            hoursD = minutesDone / 60;

            minutesT = minutesTaken % 60;
            hoursT = minutesTaken/ 60;

            String md = (minutesD < 10) ? "0" + minutesD : String.valueOf(minutesD);
            String done = hoursD + ":" + md;


            String mt= (minutesT < 10) ? "0" + minutesT : String.valueOf(minutesT);
            String taken = hoursT + ":" + mt;


            TextView doneViewText = rootView.findViewById(R.id.statisticsDoneOvertime);
            TextView takenViewText = rootView.findViewById(R.id.statisticsTakenOvertime);

            doneViewText.setText(done);
            takenViewText.setText(taken);



            // TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            // textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            // zwracam widget (fragment, który ma zostać wyświetlony), do głównej aktywności gdzie
            // wszystkie widgety są już odpowiednio zainicjalizowane.
            return rootView;
        }

        private void setViewModel(StatisticsViewModel statisticsViewModel)
        {
            mStatisticsViewModel = statisticsViewModel;
        }
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     * Jak wyżej, klasa ma za zadanie zwrócić Fragment w zależności od tego
     * jaka karta jest aktualnie otworzona.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter
    {

        private SectionsPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            // Zwracamy fragment, w zależności od tego jaką zakładke mamy  w
            // danym momencie otwartą.
            return PlaceholderFragment.newInstance(position, statisticsViewModel);
        }

        /**
         * metoda zwraca ilość kart
         * @return ilość kart
         */
        @Override
        public int getCount()
        {
            // Show 4 total pages.
            // ilość tabów jak m abyć wyświetlona
            return 4;
        }
    }
}
