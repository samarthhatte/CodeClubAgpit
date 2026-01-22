package com.agpitcodeclub.codeclubagpit.firebase;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.agpitcodeclub.codeclubagpit.R;
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


            String title = null;
        String message = null;
        String imageUrl = null;

        // Notification payload
        if (remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle();
            message = remoteMessage.getNotification().getBody();
            imageUrl = remoteMessage.getNotification().getImageUrl() != null
                    ? remoteMessage.getNotification().getImageUrl().toString()
                    : null;
        }

        // Data payload (preferred for control)
        if (!remoteMessage.getData().isEmpty()) {
            if (remoteMessage.getData().containsKey("title"))
                title = remoteMessage.getData().get("title");

            if (remoteMessage.getData().containsKey("message"))
                message = remoteMessage.getData().get("message");

            if (remoteMessage.getData().containsKey("image"))
                imageUrl = remoteMessage.getData().get("image");
        }

        if (imageUrl != null && !imageUrl.isEmpty()) {
            showImageNotification(title, message, imageUrl);
        } else {
            showTextNotification(title, message);
        }
    }

    // TEXT ONLY
    private void showTextNotification(String title, String message) {
        showNotification(title, message, null);
    }

    // IMAGE + TEXT
    private void showImageNotification(String title, String message, String imageUrl) {
        new Thread(() -> {
            Bitmap bitmap = getBitmapFromUrl(imageUrl);
            showNotification(title, message, bitmap);
        }).start();
    }

    private void showNotification(String title, String message, Bitmap bitmap) {

        String channelId = "club_channel";

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true);

        if (bitmap != null) {
            builder.setStyle(
                    new NotificationCompat.BigPictureStyle()
                            .bigPicture(bitmap)
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
