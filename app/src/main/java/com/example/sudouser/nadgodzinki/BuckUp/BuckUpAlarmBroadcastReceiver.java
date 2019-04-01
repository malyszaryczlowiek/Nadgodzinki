package com.example.sudouser.nadgodzinki.BuckUp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;

import com.example.sudouser.nadgodzinki.R;
import com.example.sudouser.nadgodzinki.ListOfOvertimesActivity;

import androidx.core.app.NotificationCompat;


/**
 * Broadcast receiver, który jest uruchamiany w momencie gdy AlarmManager z poziomu systemu
 * wysyła alarm w postaci intentu o setAction("com.example.sudouser.nadgodzinki.BuckUpNotification")
 *
 */
public class BuckUpAlarmBroadcastReceiver extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intentFromAlarm)
    {
        // lepiej jest zamiast wywoływać nowy wątek przy użyciu asyncTask'a, zastosować WorkManager'a
        // https://developer.android.com/topic/libraries/architecture/workmanager
        // ewentualnie używać service:
        // https://developer.android.com/guide/components/broadcasts#effects-process-state
        // new ShowNotification().execute(context);
        String CHANNEL_ID = "CHANNEL_ID_2";
        int notificationId = 1;
        Intent intent = new Intent(context, ListOfOvertimesActivity.class).putExtra("fromNotifier", true);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(context.getText(R.string.buckup_notification_title)) // R.string.buckup_notififcation_title
                .setContentText(context.getText(R.string.buckup_notification_text))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setStyle(new NotificationCompat
                        .BigTextStyle()
                        .bigText(context.getText(R.string.buckup_notification_text_big)))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true); // to automatycznie usówa notyfikacje gdy urzytkownik na nią klilknie


        // towrzenie już istniejącego kanału jest operacją typu no-op czyli nie
        // wnosi nic nowego, nie ma żadnego wpływu
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
            //NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            //notificationManagerCompat.createNotificationChannel(channel);
            //notificationManagerCompat.notify(notificationId, builder.build());
        }
    }

    /**
     * Class to run notification in background thread.
     */
    private static class ShowNotification extends AsyncTask<Context, Void, Void>
    {
        @Override
        protected Void doInBackground(Context... contexts)
        {
            Context context = contexts[0];
            String CHANNEL_ID = "CHANNEL_ID_2";
            int notificationId = 1;
            Intent intent = new Intent(context, ListOfOvertimesActivity.class).putExtra("fromNotifier", true);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.notification_icon)
                    .setContentTitle(context.getText(R.string.buckup_notification_title)) // R.string.buckup_notififcation_title
                    .setContentText(context.getText(R.string.buckup_notification_text))
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setStyle(new NotificationCompat
                            .BigTextStyle()
                            .bigText(context.getText(R.string.buckup_notification_text_big)))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    // Set the intent that will fire when the user taps the notification
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true); // to automatycznie usówa notyfikacje gdy urzytkownik na nią klilknie


            // towrzenie już istniejącego kanału jest operacją typu no-op czyli nie
            // wnosi nic nowego, nie ma żadnego wpływu
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
                //NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                //notificationManagerCompat.createNotificationChannel(channel);
                //notificationManagerCompat.notify(notificationId, builder.build());
            }

            return null;
        }
    }
}




















