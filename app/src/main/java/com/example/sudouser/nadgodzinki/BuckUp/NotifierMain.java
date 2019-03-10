package com.example.sudouser.nadgodzinki.BuckUp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.sudouser.nadgodzinki.R;

import androidx.core.app.NotificationCompat;

public class NotifierMain
{
    private String CHANNEL_ID = "CHANNEL_ID_2";
    private int notificationId = 1;

    public NotifierMain(Context context, Intent[] intent)
    {
        createNotification(context, intent);
    }


    private void createNotification(Context context, Intent[] intent)
    {
        PendingIntent pendingIntent = PendingIntent.getActivities(context, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("content title")
                .setContentText("content text")
                .setStyle(new NotificationCompat.BigTextStyle().bigText("tu jest text który pojawi się " +
                        "gdy notyfikacja jest expandible. tzn pociągając w dół pojawiają się dodadtkowe" +
                        " linie textu."))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true); // to automatycznie usówa notyfikacje gdy urzytkownik na nią klilknie

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            CharSequence name = context.getString(R.string.notification_channel_name);
            String description = context.getString(R.string.notification_channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager =  context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            notificationManager.notify(notificationId, builder.build());
        }
    }
}
