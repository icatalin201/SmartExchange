package smartexchange.expert.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import smartexchange.expert.R;
import smartexchange.expert.activity.SplashActivity;
import smartexchange.expert.util.Utils;

public class NotificationManager extends JobService {

    private static final String CHANNEL_ID = "SmartExchange";

    @Override
    public boolean onStartJob(JobParameters params) {
        sendNotification(this);
        Utils.launchJob(this, NotificationManager.class);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    private String createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "smart_exchange";
            String description = "Smart Exchange Notification Channel";
            int importance = android.app.NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            android.app.NotificationManager notificationManager = context.getSystemService(android.app.NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
            return channel.getId();
        } else {
            return CHANNEL_ID;
        }
    }

    private void sendNotification(Context context) {
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent intent = new Intent(context, SplashActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        Notification notification = new NotificationCompat
                .Builder(context, createNotificationChannel(context))
                .setSound(soundUri)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_baseline_attach_money_24px)
                .setContentTitle(context.getResources().getString(R.string.notification_title))
                .setContentText(context.getResources().getString(R.string.notification_text))
                .setAutoCancel(true).build();

        android.app.NotificationManager notifManager = (android.app.NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        if (notifManager != null) {
            notifManager.notify(1, notification);
        }
    }
}
