package com.agpitcodeclub.codeclubagpit.firebase;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.agpitcodeclub.codeclubagpit.R;
import com.agpitcodeclub.codeclubagpit.ui.activities.ChatActivity;
import com.agpitcodeclub.codeclubagpit.ui.activities.EventDetailActivity;
import com.agpitcodeclub.codeclubagpit.ui.activities.EventsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
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
        String chatId = null;

        if (!remoteMessage.getData().isEmpty()) {
            type = remoteMessage.getData().get("type");
            eventId = remoteMessage.getData().get("eventId");
            chatId = remoteMessage.getData().get("chatId");
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
            showImageNotification(title, message, imageUrl, type, eventId, chatId);
        } else {
            showTextNotification(title, message, type, eventId, chatId);
        }
    }

    // TEXT ONLY
    private void showTextNotification(String title, String message,
                                      String type, String eventId, String chatId) {
        showNotification(title, message, null, type, eventId, chatId);
    }

    // IMAGE + TEXT
    private void showImageNotification(String title, String message,
                                       String imageUrl, String type, String eventId, String chatId) {
        new Thread(() -> {
            Bitmap bitmap = getBitmapFromUrl(imageUrl);
            showNotification(title, message, bitmap, type, eventId, chatId);
        }).start();
    }

    private void showNotification(String title, String message, Bitmap bitmap,
                                  String type, String eventId, String chatId) {

        String channelId = "club_channel";
        Intent intent;

        // ✅ CHAT NOTIFICATION
        if ("chat".equals(type) && chatId != null) {
            intent = new Intent(this, ChatActivity.class);
            intent.putExtra("chatId", chatId);
        }

        // ✅ EVENT NOTIFICATION
        else if ("event".equals(type) && eventId != null && !eventId.isEmpty()) {
            intent = new Intent(this, EventDetailActivity.class);
            intent.putExtra("eventId", eventId);
        }

        // ✅ DEFAULT SCREEN
        else {
            intent = new Intent(this, EventsActivity.class);
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        int requestCode = (int) System.currentTimeMillis();

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                requestCode,
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

        // ✅ IMAGE SUPPORT
        if (bitmap != null) {
            builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap));
        } else {
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
        }

        NotificationManagerCompat manager = NotificationManagerCompat.from(this);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {

                Log.d("FCM_DEBUG", "Notification permission not granted");
                return;
            }
        }

        manager.notify((int) System.currentTimeMillis(), builder.build());
    }


    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        Log.d("FCM_DEBUG", "New Token: " + token);

        String uid = FirebaseAuth.getInstance().getUid();
        if(uid != null){
            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(uid)
                    .update("fcmToken", token);
        }
    }


    // Download image
    @Nullable
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
