package com.agpitcodeclub.codeclubagpit.ui.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import com.agpitcodeclub.codeclubagpit.R;
import com.agpitcodeclub.codeclubagpit.model.MessageModel;
import com.agpitcodeclub.codeclubagpit.ui.adapters.ChatAdapter;
import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

//imports for notifications
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    TextView userName;
    EditText messageBox;
    ImageView sendBtn;
    RecyclerView chatRecycler;
    ImageView backBtn;
    ShapeableImageView profileImage;
    String receiverProfilePic, role, custom ;



    FirebaseFirestore db;
    FirebaseAuth auth;

    String senderUid;
    String receiverUid, receiverName;
    String chatId;
    ArrayList<MessageModel> messageList;
    ChatAdapter chatAdapter;
    private String currentUserName = "Someone"; // To store the sender's name


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Get the root view of your layout (ensure your activity_chat.xml has an ID like main)
        View mainView = findViewById(R.id.mainChatLayout);
        ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        // Bind Views
        userName = findViewById(R.id.userName);
        messageBox = findViewById(R.id.messageBox);
        sendBtn = findViewById(R.id.sendBtn);
        chatRecycler = findViewById(R.id.chatRecycler);
        backBtn = findViewById(R.id.backBtn);
        profileImage = findViewById(R.id.profileImage);

        // Firebase Init
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        senderUid = auth.getCurrentUser().getUid();

        // Back Button
        backBtn.setOnClickListener(v -> finish());

        // ✅ Notification Open Case
        String notificationChatId = getIntent().getStringExtra("chatId");

        if (notificationChatId != null) {

            chatId = notificationChatId;

            String[] parts = chatId.split("_");

            receiverUid = parts[0].equals(senderUid) ? parts[1] : parts[0];

            loadReceiverDetails(receiverUid);

        }
// ✅ Inside onCreate() Normal Open Case
        else {
            receiverUid = getIntent().getStringExtra("uid");
            receiverName = getIntent().getStringExtra("name");
            receiverProfilePic = getIntent().getStringExtra("profilePic");
            chatId = getChatId(senderUid, receiverUid);

            // Instead of setupChatUI() directly, load the details to get the role/tag
            loadReceiverDetails(receiverUid);
        }

        fetchCurrentUserName();

// Replace the two listeners with this single one
        sendBtn.setOnClickListener(v -> {
            String msg = messageBox.getText().toString().trim();
            if (!msg.isEmpty()) {
                sendMessage(msg);
                prepareNotification(msg); // Calls the notification logic
                messageBox.setText("");
            }
        });
    }

    private void fetchCurrentUserName() {
        db.collection("users").document(senderUid).get().addOnSuccessListener(doc -> {
             role = doc.getString("role");
             custom = doc.getString("customTitle");
            if (doc.exists()) {
                currentUserName = doc.getString("name");
            }

        });
    }

    private void prepareNotification(String messageText) {
        // 1. Get the receiver's FCM token from Firestore
        db.collection("users").document(receiverUid).get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                String receiverToken = doc.getString("fcmToken"); // Ensure your users have this field!
                if (receiverToken != null && !receiverToken.isEmpty()) {
                    sendChatNotification(receiverToken, messageText);
                }
            }
        });
    }

    private void sendChatNotification(String token, String messageText) {
        String url = "https://scaling-trust-ai.onrender.com/send-chat-notification";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("token", token);
            jsonBody.put("chatId", chatId);
            jsonBody.put("senderName", currentUserName);
            jsonBody.put("message", messageText);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonBody,
                response -> Log.d("CHAT_PUSH", "Notification Sent: " + response.toString()),
                error -> Log.e("CHAT_PUSH", "Notification Failed", error)
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        // Standard Volley timeout for Render free tier wake-up
        request.setRetryPolicy(new com.android.volley.DefaultRetryPolicy(
                60000, 0, com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        Volley.newRequestQueue(this).add(request);
    }

    // Generate chatId
    private String getChatId(String uid1, String uid2) {
        return uid1.compareTo(uid2) < 0
                ? uid1 + "_" + uid2
                : uid2 + "_" + uid1;
    }

    private void loadReceiverDetails(String receiverUid) {
        db.collection("users")
                .document(receiverUid)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        receiverName = doc.getString("name");
                        receiverProfilePic = doc.getString("profilePic");

                        // ✅ Get the Receiver's Role and Custom Title
                        String receiverRole = doc.getString("role");
                        String receiverCustom = doc.getString("customTitle");

                        setupChatUI(receiverRole, receiverCustom);
                    }
                });
    }


    // Update the method signature to accept these strings
    private void setupChatUI(String rRole, String rCustom) {

        userName.setText(receiverName);
        TextView txtRoleTag = findViewById(R.id.txtRoleTag);

        // ✅ Set the Tag based on Receiver's data
        if (rCustom != null && !rCustom.isEmpty()) {
            txtRoleTag.setText(rCustom);
            txtRoleTag.setTextColor(Color.parseColor("#FFD700")); // Aesthetic Gold
        } else {
            txtRoleTag.setText(rRole != null ? rRole.toUpperCase() : "MEMBER");
            txtRoleTag.setTextColor(Color.parseColor("#BDBDBD")); // Light Grey for standard
        }

        // Profile Image Logic
        if (receiverProfilePic != null && !receiverProfilePic.isEmpty()) {
            Glide.with(this)
                    .load(receiverProfilePic)
                    .placeholder(R.drawable.ic_user_placeholder)
                    .circleCrop()
                    .into(profileImage);
            // ✅ 1. Add Click Listener for Full Screen
            profileImage.setOnClickListener(v -> {
                Intent intent = new Intent(ChatActivity.this, FullScreenImageActivity.class);
                intent.putExtra("IMAGE_URL", receiverProfilePic);
                startActivity(intent);
            });
        } else {
            profileImage.setOnClickListener(v -> {
                    Toast.makeText(ChatActivity.this, "No profile picture available", Toast.LENGTH_SHORT).show();
            });
            profileImage.setImageResource(R.drawable.ic_user_placeholder);
        }

        // Initialize Recycler and Load Messages
        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, messageList, senderUid);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setStackFromEnd(true);
        chatRecycler.setLayoutManager(lm);
        chatRecycler.setAdapter(chatAdapter);

        createChatRoom();
        loadMessages();
    }


    private void sendMessage(String text) {
        if (chatId == null || chatId.isEmpty()) {
            Toast.makeText(this, "Chat not initialized. Please wait.", Toast.LENGTH_SHORT).show();
            return;
        }

        long currentTime = System.currentTimeMillis();
        Map<String, Object> message = new HashMap<>();
        message.put("senderId", senderUid);
        message.put("receiverId", receiverUid);
        message.put("text", text);
        message.put("localTime", currentTime);
        message.put("timestamp", FieldValue.serverTimestamp());


        // Use a listener to check if the write actually happened
        db.collection("chats").document(chatId).collection("messages")
                .add(message)
                .addOnFailureListener(e -> Log.e("ChatActivity", "Write failed: " + e.getMessage()));

        // Update last message
        db.collection("chats").document(chatId).update(
                "lastMessage", text,
                "lastMessageTime", FieldValue.serverTimestamp()
        );
    }


    private void loadMessages() {
        if (chatId == null) return;

        db.collection("chats")
                .document(chatId)
                .collection("messages")
                .orderBy("localTime", Query.Direction.ASCENDING)
                .addSnapshotListener(com.google.firebase.firestore.MetadataChanges.INCLUDE, (snapshots, error) -> {
                    if (error != null) {
                        Log.e("ChatActivity", "Listen failed.", error);
                        return;
                    }

                    if (snapshots != null) {
                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            // Convert document to object
                            MessageModel msg = dc.getDocument().toObject(MessageModel.class);

                            // ✅ Crucial: Manually attach the Firestore Doc ID
                            msg.setMessageId(dc.getDocument().getId());

                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                if (!isAlreadyInList(msg.getMessageId())) {
                                    messageList.add(msg);
                                    chatAdapter.notifyItemInserted(messageList.size() - 1);
                                    chatRecycler.scrollToPosition(messageList.size() - 1);
                                }
                            }
                        }
                    }
                });
    }

    // ✅ Fix: Check by unique Message ID
    private boolean isAlreadyInList(String id) {
        for (MessageModel m : messageList) {
            if (m.getMessageId() != null && m.getMessageId().equals(id)) {
                return true;
            }
        }
        return false;
    }



    private void createChatRoom() {

        Map<String, Object> chatRoom = new HashMap<>();
        chatRoom.put("participants", Arrays.asList(senderUid, receiverUid));
        chatRoom.put("lastMessage", "");
        chatRoom.put("lastMessageTime", FieldValue.serverTimestamp());

        db.collection("chats")
                .document(chatId)
                .set(chatRoom, SetOptions.merge());
    }




}

