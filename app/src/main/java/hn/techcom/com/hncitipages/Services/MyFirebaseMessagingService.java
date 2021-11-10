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
import hn.techcom.com.hncitipages.Activities.NotificationPostActivity;
import hn.techcom.com.hncitipages.Activities.ViewCommentedPost;
import hn.techcom.com.hncitipages.Activities.ViewLikedPost;
import hn.techcom.com.hncitipages.Activities.ViewSupportingProfile;
import hn.techcom.com.hncitipages.Fragments.ProfileSectionFragment;
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
        int postId = Integer.parseInt(Objects.requireNonNull(data.get("post_id"))); //is -1 for supporting notifications
        String title = data.get("title"); //user whose action has generated the notification for this logged in user
        String senderHnid = data.get("sender_hnid");
        String message = data.get("message");
        boolean isSupported = Boolean.parseBoolean(data.get("isSupported").toString());


        if (type != null) {
            switch (type){
                case "S":
                    showSupportNotification(type, postId, title, senderHnid, isSupported, message);
                    break;
                case "L":
                    showLikeNotification(type,postId,title,message,isSupported);
            }
        }
    }

    private void showSupportNotification(String type, int postId, String title, String hnid, boolean isSupported, String message){
        Log.d(TAG,"showSupportNotification() called = "+"YES");
        Intent intent = new Intent(this, MainActivity.class);

        intent.putExtra("type",type);
        intent.putExtra("postId",postId);
        intent.putExtra("sender_name",title);
        intent.putExtra("hnid", hnid);
        intent.putExtra("isSupported",isSupported);
        intent.putExtra("from","notificationPostActivity");
        intent.putExtra("show","supporter_profile");

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

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

    private void showLikeNotification(String type, int postId, String title, String message, boolean isSupported ){
        Intent intent = new Intent(this, NotificationPostActivity.class);

        intent.putExtra("type",type);
        intent.putExtra("postId",postId);
        intent.putExtra("sender_name",title);
        intent.putExtra("isSupported",isSupported);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

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
