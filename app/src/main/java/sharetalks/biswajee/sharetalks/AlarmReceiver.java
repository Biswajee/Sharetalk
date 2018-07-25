package sharetalks.biswajee.sharetalks;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

/**
 * Created by Biswajit Roy on 25-07-2018.
 */

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = new NotificationCompat.Builder(context)
                .setContentTitle("Sharetalk Mantra")
                .setContentText("Plan your portfolio. Check out stock prices.")
                .setSmallIcon(R.drawable.notification_icon)
                .setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0))
                .build();

        notificationManager.notify(0, notification);
    }
}