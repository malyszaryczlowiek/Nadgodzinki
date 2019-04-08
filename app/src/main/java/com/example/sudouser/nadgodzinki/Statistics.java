package com.example.sudouser.nadgodzinki;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.slice.SliceViewManager;
import androidx.slice.widget.SliceView;

public class Statistics extends AppCompatActivity
{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        SliceViewManager manager = SliceViewManager.getInstance(this);

        SliceView sliceView = findViewById(R.id.slice);
        SliceView sliceView2 = findViewById(R.id.slice2);

        sliceView.setSlice(manager.bindSlice(Uri.parse("content://com.example.sudouser.nadgodzinki/")));
        sliceView2.setSlice(manager.bindSlice(Uri.parse("content://com.example.sudouser.nadgodzinki/")));

        sliceView.setMode(SliceView.MODE_LARGE);
        sliceView2.setMode(SliceView.MODE_LARGE);
    }
}
