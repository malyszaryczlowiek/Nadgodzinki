package com.example.sudouser.nadgodzinki;

import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.example.sudouser.nadgodzinki.Settings.ListOfCategoriesActivity;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.SliceProvider;
import androidx.slice.builders.ListBuilder;
import androidx.slice.builders.SliceAction;

public class OvertimeSliceProvider extends SliceProvider
{

    @Override
    public boolean onCreateSliceProvider()
    {
        return true;
    }

    @NonNull
    @Override
    public Uri onMapIntentToUri(Intent intent)
    {
        Uri.Builder uriBuilder = new Uri.Builder().scheme(ContentResolver.SCHEME_CONTENT);
        if (intent == null)
            return uriBuilder.build();
        Uri data = intent.getData();
        if (data != null && data.getPath() != null)
        {
            String path = data.getPath().replace("/", "");
            uriBuilder = uriBuilder.path(path);
        }
        if (getContext() != null)
            uriBuilder = uriBuilder.authority(getContext().getPackageName());
        return uriBuilder.build();
    }

    @Override
    public Slice onBindSlice(Uri sliceUri)
    {
        if (getContext() == null)
            return null;
        // sprawdź uri path
        SliceAction sliceAction = createActivityAction();
        SliceAction moveToList = createMoveToAction();
        if ("/".equals(sliceUri.getPath()))
            return  new ListBuilder(getContext(), sliceUri, ListBuilder.INFINITY)
                    .setAccentColor(0xfff4b400)
                    .setHeader(new ListBuilder.HeaderBuilder()
                            .setTitle("Title of Header")
                            .setSubtitle("Subtitle")
                            .setSummary("Summary")
                            .setContentDescription("Content description")
                            .setLayoutDirection(View.LAYOUT_DIRECTION_INHERIT))
                    .addRow(new ListBuilder.RowBuilder()
                            .setTitle("URI FOUND")
                            .setPrimaryAction(sliceAction))
                    .addAction(moveToList)
                    .build();
        else
            return  new ListBuilder(getContext(), sliceUri, ListBuilder.INFINITY)
                    .addRow(new ListBuilder.RowBuilder().setTitle("URI not FOUND").setPrimaryAction(sliceAction))
                    .build();
    }

    @Override
    public void onSlicePinned(Uri sliceUri)
    {
        super.onSlicePinned(sliceUri);
    }

    @Override
    public void onSliceUnpinned(Uri sliceUri)
    {
        super.onSliceUnpinned(sliceUri);
    }

    public SliceAction createActivityAction()
    {
        if (getContext() == null)
            return null;
        Intent intent = new Intent(getContext(), MainActivity.class);
        return SliceAction.create(PendingIntent.getActivity(getContext(), 0, intent, 0),
                IconCompat.createWithResource(getContext(), R.drawable.ic_notifications_black_24dp),
                ListBuilder.ICON_IMAGE,
                "Enter app");
    }

    public SliceAction createMoveToAction()
    {
        if (getContext() == null)
            return null;
        Intent intent = new Intent(getContext(), ListOfCategoriesActivity.class);
        return  SliceAction.create(PendingIntent.getActivity(getContext(), 0, intent, 0),
                IconCompat.createWithResource(getContext(),R.drawable.ic_notifications_black_24dp),
                ListBuilder.ICON_IMAGE,
                "Open preferences");
    }
}




/*
@Override
    public boolean onCreateSliceProvider()
    {
        return true;
    }

    /**
     * metoda, która służy do zbudowania slice'u.
     * @param sliceUri
     * @return

@Override
public Slice onBindSlice(Uri sliceUri)
{
    if (getContext() == null)
        return null;
    SliceAction activityAction = createActivityAction();
    ListBuilder listBuilder = new ListBuilder(getContext(), sliceUri, ListBuilder.INFINITY);
    // tworzymy list builder
    //if ("/slices".equals(sliceUri.getPath()))
    {
        listBuilder.addRow(new ListBuilder.RowBuilder()
                .setTitle("Hello World")
                .setPrimaryAction(activityAction)
        );
    }
        /*

        else
        {
            listBuilder.addRow(new ListBuilder.RowBuilder()
                    .setTitle("URI not Recognized")
                    .setPrimaryAction(activityAction)
            );
        }

    return listBuilder.build();
}

    public static Uri getUri(Context context, String path) {
        return new Uri.Builder()
                .scheme(ContentResolver.SCHEME_CONTENT)
                .authority(context.getPackageName())
                .appendPath(path)
                .build();
    }

    public Slice createSlice(Uri sliceUri)
    {
        if (getContext() == null)
            return null;
        SliceAction sliceAction = createActivityAction();
        return  new ListBuilder(getContext(), sliceUri, ListBuilder.INFINITY)
                .addRow(new ListBuilder.RowBuilder()
                        .setTitle("Perform action in app")
                        .setPrimaryAction(sliceAction))
                .build();

    }

    public SliceAction createActivityAction() {
        if (getContext() == null) {
            return null;
        }
        return SliceAction.create(
                PendingIntent.getActivity( getContext(), 0, new Intent(getContext(), Statistics.class), 0),
                IconCompat.createWithResource(getContext(), R.drawable.notification_icon),
                ListBuilder.ICON_IMAGE,
                "Enter app"
        );
    }
 */