package com.sameer.Medicines_Information;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

public static int NOTIFICATION_ID = 1;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        generateNotifications(remoteMessage.getNotification().getBody(),remoteMessage.getNotification().getTitle());
    }

    private void generateNotifications(String body, String title) {

        Intent intent = new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this).
                setSmallIcon(R.mipmap.ic_launcher).
                setContentTitle(title).
                setContentText(body).
                setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        if(NOTIFICATION_ID > 1073741824){
            NOTIFICATION_ID = 0;
        }
        notificationManager.notify(NOTIFICATION_ID,notificationBuilder.build());
    }

   /* @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("New Friend Request")
                .setContentText("You've received a new friend request");

        int mNotificationId = (int)System.currentTimeMillis();
        NotificationManager mNotifyMGR = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        mNotifyMGR.notify(mNotificationId,mBuilder.build());
    }

    */
}





/*public static int NOTIFICATION_ID = 1;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        generateNotifications(remoteMessage.getNotification().getBody(),remoteMessage.getNotification().getTitle());
    }

    private void generateNotifications(String body, String title) {

        Intent intent = new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this).
                setSmallIcon(R.mipmap.ic_launcher).
                setContentTitle(title).
                setContentText(body).
                setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        if(NOTIFICATION_ID > 1073741824){
            NOTIFICATION_ID = 0;
        }
        notificationManager.notify(NOTIFICATION_ID,notificationBuilder.build());
    }*/
