package com.github.malyszaryczlowiek.nadgodzinki;

import android.icu.util.Calendar;
import android.os.Bundle;
import android.widget.Toast;

import com.github.malyszaryczlowiek.nadgodzinki.ViewModels.StatisticsViewModel;
import com.github.malyszaryczlowiek.nadgodzinki.db.Item;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

public class GraphFullScreenActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_full_screen);

        int position = getIntent().getIntExtra("position", 0);
        StatisticsViewModel mStatisticsViewModel = ViewModelProviders.of(this)
                .get(StatisticsViewModel.class);

        List<Item> lista;

        int year, month, day;

        switch (position)
        {
            case 0: // sumuj wszystkie
                lista = mStatisticsViewModel.getListOfItemsFrom(0,0,0);
                break;
            case 1: // ostatni miesiąc
                Calendar pastMonth = Calendar.getInstance();
                pastMonth.add(Calendar.MONTH, -1);
                year = pastMonth.get(Calendar.YEAR);
                month = pastMonth.get(Calendar.MONTH) + 1;
                day = pastMonth.get(Calendar.DAY_OF_MONTH);
                lista = mStatisticsViewModel.getListOfItemsFrom(year,month,day);
                break;
            case 2: // ostatni kwartał
                Calendar pastQuarter = Calendar.getInstance();
                pastQuarter.add(Calendar.MONTH, -3);
                year = pastQuarter.get(Calendar.YEAR);
                month = pastQuarter.get(Calendar.MONTH) + 1;
                day = pastQuarter.get(Calendar.DAY_OF_MONTH);
                lista = mStatisticsViewModel.getListOfItemsFrom(year,month,day);
                break;
            case 3: // ostatni rok
                Calendar pastYear = Calendar.getInstance();
                pastYear.add(Calendar.YEAR, -1);
                year = pastYear.get(Calendar.YEAR);
                month = pastYear.get(Calendar.MONTH) + 1;
                day = pastYear.get(Calendar.DAY_OF_MONTH);
                lista = mStatisticsViewModel.getListOfItemsFrom(year,month,day);
                break;
            default:
                lista = mStatisticsViewModel.getListOfItemsFrom(0,0,0);
                break;

        }

        DataPoint[] points = new DataPoint[lista.size()];

        for (int i = 0; i < lista.size(); ++i)
        {
            Item item = lista.get(i);
            int minutes = item.getNumberOfMinutes() + 60 * item.getNumberOfHours();
            points[i] = new DataPoint(i, minutes);
        }

        GraphView graphView = findViewById(R.id.full_screen_graph);
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
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
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
    }
}
