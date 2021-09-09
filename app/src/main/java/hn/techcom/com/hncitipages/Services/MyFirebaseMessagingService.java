package hn.techcom.com.hncitipages.Services;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Objects;

import hn.techcom.com.hncitipages.Activities.MainActivity;
import hn.techcom.com.hncitipages.Activities.ViewCommentedPost;
import hn.techcom.com.hncitipages.Activities.ViewLikedPost;
import hn.techcom.com.hncitipages.Activities.ViewSupportingProfile;
import hn.techcom.com.hncitipages.R;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FMessagingService";
    private static final String CHANNEL_ID = "101";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG,"the token refreshed: "+token);

        //Send FCM Token to server
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        Map<String, String> data = remoteMessage.getData();
        Log.d(TAG,"data message = "+data.toString());

        String type = data.get("notification_type");

        if (type != null) {
            switch (type){
                case "S":
                    String hnid = data.get("sender_hnid");
                    showSupportNotification(Objects.requireNonNull(remoteMessage.getNotification()).getTitle(), remoteMessage.getNotification().getBody(), hnid);
                    break;
                case "L":
                    String postid = data.get("post_id");
                    showLikeNotification(Objects.requireNonNull(remoteMessage.getNotification()).getTitle(), remoteMessage.getNotification().getBody(), postid);
                    break;
                case "C":
                    String comment_postid = data.get("post_id");
                    showCommentNotification(Objects.requireNonNull(remoteMessage.getNotification()).getTitle(), remoteMessage.getNotification().getBody(), comment_postid);
                    break;
            }
        }
    }

    private void showSupportNotification(String title, String message, String hnid){
        Intent intent = new Intent(this, ViewSupportingProfile.class);


        Bundle bundle = new Bundle();
        bundle.putString("hnid", hnid);
        bundle.putString("name",title);
        intent.putExtras(bundle);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.newlogo)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        if (am.getRingerMode() != AudioManager.RINGER_MODE_SILENT)
            builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, builder.build());
    }

    private void showLikeNotification(String title, String message, String postid){
        Intent intent = new Intent(this, ViewLikedPost.class);


        Bundle bundle = new Bundle();
        bundle.putString("postid", postid);
        bundle.putString("name",title);
        intent.putExtras(bundle);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.newlogo)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        if (am.getRingerMode() != AudioManager.RINGER_MODE_SILENT)
            builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, builder.build());
    }

    private void showCommentNotification(String title, String message, String postid){
        Intent intent = new Intent(this, ViewCommentedPost.class);

        Bundle bundle = new Bundle();
        bundle.putString("postid", postid);
        bundle.putString("name",title);
        intent.putExtras(bundle);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.newlogo)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        if (am.getRingerMode() != AudioManager.RINGER_MODE_SILENT)
            builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, builder.build());
    }
}
