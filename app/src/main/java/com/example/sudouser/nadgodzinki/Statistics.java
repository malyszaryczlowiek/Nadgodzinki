package com.example.sudouser.nadgodzinki;

import com.example.sudouser.nadgodzinki.ViewModels.StatisticsViewModel;
import com.example.sudouser.nadgodzinki.db.Item;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


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
    //private FullScreenButtonListener mListener;
    private TabLayout tabLayout;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener()
    {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item)
        {
            int position;
            switch (item.getItemId())
            {
                case R.id.next_tab:
                    position = tabLayout.getSelectedTabPosition() + 1;
                    if (position >= tabLayout.getTabCount())
                        tabLayout.getTabAt(0).select();
                    else
                        tabLayout.getTabAt(position).select();
                    return true;
                case R.id.statistics_full_screen_button:
                    position = tabLayout.getSelectedTabPosition();
                    showGraphInFullScreen(position);
                    return true;
                case R.id.previous_tab:
                    position = tabLayout.getSelectedTabPosition();
                    if (position == 0)
                        tabLayout.getTabAt(3).select();
                    else
                        tabLayout.getTabAt(position -1).select();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        statisticsViewModel = ViewModelProviders.of(this).get(StatisticsViewModel.class);

        //mListener = (FullScreenButtonListener) getContextFromFragment();

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
        mViewPager = findViewById(R.id.view_pager_container);
        // aby umieścić child views czyli pojedyńcze fragmenty dla danej podstworny trzeba
        // zaczepić ten layout (czyli view który ma różne fragmenty) w odpowiednim adapterze
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // wczytuję taby.
        tabLayout = findViewById(R.id.tabs);

        // dodaję listenera, że zmienia się tab
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        BottomNavigationView navigation = findViewById(R.id.bottom_navigation_bar);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


    }

    void showGraphInFullScreen(int position)
    {
        Intent intent = new Intent(this, GraphFullScreenActivity.class).putExtra("position", position);
        startActivity(intent);
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment // implements FullScreenButtonListener
    {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static StatisticsViewModel mStatisticsViewModel;
        private List<Item> lista;
        private int year, month, day, position;


        //private Context context;

        // o ile dobrze pamiętam to trzeba chyba zaimplementować defaultowy
        // konstrktor, bo jak nie to krzyczał, że nie ma takowego.
        public PlaceholderFragment()
        {
        }

        public void setContext(Context context)
        {
         //   this.context = context;
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber, StatisticsViewModel statisticsViewModel)
        {
            PlaceholderFragment fragment = new PlaceholderFragment(); // tworzę nowy pusty eglemplarz klasy
            fragment.setViewModel(statisticsViewModel);
            //fragment.setContext(context);
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

            //mListener = (FullScreenButtonListener) context;

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

            int minutesDone, minutesTaken;

            position = getArguments().getInt(ARG_SECTION_NUMBER);

            year = 0;
            month = 0;
            day = 0;
            switch (position)
            {
                case 0: // sumuj wszystkie
                    time_to_take.setVisibility(View.VISIBLE);
                    statistics_time_to_take.setVisibility(View.VISIBLE);
                    minutesDone = mStatisticsViewModel.numberOfMinutesDone(0,0,0);
                    minutesTaken = mStatisticsViewModel.numberOfMinutesTaken(0, 0,0);
                    lista = mStatisticsViewModel.getListOfItemsFrom(0,0,0);
                    break;
                case 1: // ostatni miesiąc
                    Calendar pastMonth = Calendar.getInstance();
                    pastMonth.add(Calendar.MONTH, -1);
                    year = pastMonth.get(Calendar.YEAR);
                    month = pastMonth.get(Calendar.MONTH) + 1;
                    day = pastMonth.get(Calendar.DAY_OF_MONTH);
                    minutesDone = mStatisticsViewModel.numberOfMinutesDone(year, month, day);
                    minutesTaken = mStatisticsViewModel.numberOfMinutesTaken(year, month, day);
                    lista = mStatisticsViewModel.getListOfItemsFrom(year,month,day);
                    break;
                case 2: // ostatni kwartał
                    Calendar pastQuarter = Calendar.getInstance();
                    pastQuarter.add(Calendar.MONTH, -3);
                    year = pastQuarter.get(Calendar.YEAR);
                    month = pastQuarter.get(Calendar.MONTH) + 1;
                    day = pastQuarter.get(Calendar.DAY_OF_MONTH);
                    minutesDone = mStatisticsViewModel.numberOfMinutesDone(year, month, day);
                    minutesTaken = mStatisticsViewModel.numberOfMinutesTaken(year, month, day);
                    lista = mStatisticsViewModel.getListOfItemsFrom(year,month,day);
                    break;
                case 3: // ostatni rok
                    Calendar pastYear = Calendar.getInstance();
                    pastYear.add(Calendar.YEAR, -1);
                    year = pastYear.get(Calendar.YEAR);
                    month = pastYear.get(Calendar.MONTH) + 1;
                    day = pastYear.get(Calendar.DAY_OF_MONTH);
                    minutesDone = mStatisticsViewModel.numberOfMinutesDone(year, month, day);
                    minutesTaken = mStatisticsViewModel.numberOfMinutesTaken(year, month, day);
                    lista = mStatisticsViewModel.getListOfItemsFrom(year,month,day);
                    break;
                default:
                    minutesDone = 0;
                    minutesTaken = 0;
                    lista = mStatisticsViewModel.getListOfItemsFrom(0,0,0);
                    break;

            }

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

            // TODO customizowanie graphu, to można zrobić w innym wątku tzn. razem
            // z wczytaniem danych za ostatni okres

             //= mStatisticsViewModel.getListOfItemsFrom().getValue();
            DataPoint[] points = new DataPoint[lista.size()];

            for (int i = 0; i < lista.size(); ++i)
            {
                Item item = lista.get(i);
                int minutes = item.getNumberOfMinutes() + 60 * item.getNumberOfHours();
                points[i] = new DataPoint(i, minutes);
            }

            GraphView graphView = rootView.findViewById(R.id.graph);
            registerForContextMenu(graphView); //żeby można było użyc context menu

            BarGraphSeries<DataPoint> series = new BarGraphSeries<>(points);
            //series.setSpacing(50);
            series.setDrawValuesOnTop(true);
            series.setAnimated(true);
            series.setValuesOnTopSize(10);
            series.setOnDataPointTapListener(new OnDataPointTapListener()
            {
                @Override
                public void onTap(Series series, DataPointInterface dataPoint)
                {
                    int minutes = ((int) Math.round(dataPoint.getY())) % 60;
                    int hours = ((int) Math.round(dataPoint.getY())) / 60;
                    int index = (int) Math.round(dataPoint.getX());
                    int day = lista.get(index).getDayOfOvertime();
                    int month = lista.get(index).getMonthOfOvertime();
                    int year = lista.get(index).getYearOfOvertime();
                    String s = day + "." + month + "." + year + "   " + hours + "h:" + minutes + "min";
                    Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
                }
            });

            graphView.setTitle(getResources().getText(R.string.statistics).toString());
            graphView.addSeries(series);
            graphView.getViewport().setScalable(true);
            graphView.getViewport().setYAxisBoundsManual(true);
            //graphView.getViewport().setMinY(-7);
            //graphView.getViewport().setMaxY(7);
            graphView.getViewport().setXAxisBoundsManual(true);
            graphView.getViewport().setMinX(-1);
            graphView.getViewport().setMaxX(lista.size()+1);

            //graphView.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
            //graphView.getGridLabelRenderer().setNumHorizontalLabels(7);

            graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter()
            {
                @Override
                public String formatLabel(double value, boolean isValueX)
                {
                    if (isValueX)
                    {
                        int i = (int) Math.round(value);
                        if (i >= 0 && i < lista.size())
                            return  lista.get(i).getDayOfOvertime() + "/" + lista.get(i).getMonthOfOvertime()
                                    + "\n" + lista.get(i).getYearOfOvertime();
                        else
                            return "";
                    }
                    else
                    {
                        return super.formatLabel(value, isValueX);
                    }
                }
            });

            graphView.getGridLabelRenderer().setHorizontalAxisTitle(getResources().getString(R.string.date));
            graphView.getGridLabelRenderer().setVerticalAxisTitle(getResources().getString(R.string.minutes));
            graphView.getGridLabelRenderer().setLabelHorizontalHeight(100);

            return rootView;
        }

        private void setViewModel(StatisticsViewModel statisticsViewModel)
        {
            mStatisticsViewModel = statisticsViewModel;
        }

        @Override
        public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo)
        {
            super.onCreateContextMenu(menu, v, menuInfo);

            MenuInflater menuInflater = getActivity().getMenuInflater();
            menuInflater.inflate(R.menu.menu_context_graph_view, menu);
        }

        @Override
        public boolean onContextItemSelected(@NonNull MenuItem item)
        {
            switch (item.getItemId())
            {
                case R.id.open_graph_in_new_activity:
                    openGraphInNewActivity();
                    return true;
                default:
                    return super.onContextItemSelected(item);
            }
        }

        void openGraphInNewActivity()
        {
            //if (getContext() != null)
            {
                //int position = this.position;
                Intent intent = new Intent(getActivity(), GraphFullScreenActivity.class).putExtra("position", position);
                startActivity(intent);
            }
            //else
            //    Toast.makeText(getActivity(), "nie można uruchomić intentu", Toast.LENGTH_SHORT).show();
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


        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.statistics_forward_button);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                int position = tabLayout.getSelectedTabPosition() + 1;
                if (position >= tabLayout.getTabCount())
                    tabLayout.getTabAt(0).select();
                else
                    tabLayout.getTabAt(position).select();
            }
        });

        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.statistics_backward_button);
        fab2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                int position = tabLayout.getSelectedTabPosition();
                if (position == 0)
                    tabLayout.getTabAt(3).select();
                else
                    tabLayout.getTabAt(position -1).select();
            }
        });
         */


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



























