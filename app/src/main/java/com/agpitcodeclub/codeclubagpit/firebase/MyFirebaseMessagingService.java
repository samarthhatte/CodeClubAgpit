package com.agpitcodeclub.codeclubagpit.firebase;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.agpitcodeclub.codeclubagpit.R;
import com.agpitcodeclub.codeclubagpit.ui.activities.EventDetailActivity;
import com.agpitcodeclub.codeclubagpit.ui.activities.EventsActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d("FCM_DEBUG", "onMessageReceived called");
        Log.d("FCM_DEBUG", "Data payload: " + remoteMessage.getData());

        // ✅ LOCAL variables (safe)
        String type = null;
        String eventId = null;

        if (!remoteMessage.getData().isEmpty()) {
            type = remoteMessage.getData().get("type");
            eventId = remoteMessage.getData().get("eventId");
        }

        String title = "Code Club";
        String message = "New update available!";
        String imageUrl = null;

        // Notification payload
        if (remoteMessage.getNotification() != null) {
            if (remoteMessage.getNotification().getTitle() != null)
                title = remoteMessage.getNotification().getTitle();

            if (remoteMessage.getNotification().getBody() != null)
                message = remoteMessage.getNotification().getBody();

            if (remoteMessage.getNotification().getImageUrl() != null)
                imageUrl = remoteMessage.getNotification().getImageUrl().toString();
        }

        // Data payload override (preferred)
        if (!remoteMessage.getData().isEmpty()) {
            if (remoteMessage.getData().containsKey("title"))
                title = remoteMessage.getData().get("title");

            if (remoteMessage.getData().containsKey("message"))
                message = remoteMessage.getData().get("message");

            if (remoteMessage.getData().containsKey("image"))
                imageUrl = remoteMessage.getData().get("image");
        }

        if (imageUrl != null && !imageUrl.isEmpty()) {
            showImageNotification(title, message, imageUrl, type, eventId);
        } else {
            showTextNotification(title, message, type, eventId);
        }
    }

    // TEXT ONLY
    private void showTextNotification(String title, String message,
                                      String type, String eventId) {
        showNotification(title, message, null, type, eventId);
    }

    // IMAGE + TEXT
    private void showImageNotification(String title, String message,
                                       String imageUrl, String type, String eventId) {
        new Thread(() -> {
            Bitmap bitmap = getBitmapFromUrl(imageUrl);
            showNotification(title, message, bitmap, type, eventId);
        }).start();
    }

    private void showNotification(String title, String message, Bitmap bitmap,
                                  String type, String eventId) {

        String channelId = "club_channel";

        Intent intent;
        if ("event".equals(type) && eventId != null && !eventId.isEmpty()) {
            intent = new Intent(this, EventDetailActivity.class);
            intent.putExtra("eventId", eventId);
        } else {
            intent = new Intent(this, EventsActivity.class);
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);

        if (bitmap != null) {
            builder.setStyle(
                    new NotificationCompat.BigPictureStyle().bigPicture(bitmap)
            );
        }

        NotificationManagerCompat manager = NotificationManagerCompat.from(this);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        manager.notify((int) System.currentTimeMillis(), builder.build());
    }

    // Download image
    private Bitmap getBitmapFromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
