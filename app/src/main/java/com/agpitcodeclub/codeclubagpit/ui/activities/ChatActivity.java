package com.agpitcodeclub.codeclubagpit.ui.activities;

import android.content.Intent;
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
    String receiverProfilePic;


    FirebaseFirestore db;
    FirebaseAuth auth;

    String senderUid;
    String receiverUid, receiverName;
    String chatId;
    ArrayList<MessageModel> messageList;
    ChatAdapter chatAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

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
        // ✅ Normal Open Case
        else {

            receiverUid = getIntent().getStringExtra("uid");
            receiverName = getIntent().getStringExtra("name");
            receiverProfilePic = getIntent().getStringExtra("profilePic");

            chatId = getChatId(senderUid, receiverUid);

            setupChatUI();
        }

        // Send Button
        sendBtn.setOnClickListener(v -> {
            String msg = messageBox.getText().toString().trim();
            if (!msg.isEmpty()) {
                sendMessage(msg);
                messageBox.setText("");
            }
        });
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

                        setupChatUI(); // Continue after loading

                    }
                });
    }


    private void setupChatUI() {

        userName.setText(receiverName);

        // Profile Image
        if (receiverProfilePic != null && !receiverProfilePic.isEmpty()) {

            Glide.with(this)
                    .load(receiverProfilePic)
                    .placeholder(R.drawable.ic_user_placeholder)
                    .circleCrop()
                    .into(profileImage);

        } else {
            profileImage.setImageResource(R.drawable.ic_user_placeholder);
        }

        // Create Chat Room
        createChatRoom();

        // Recycler Setup
        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, messageList, senderUid);

        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setStackFromEnd(true);

        chatRecycler.setLayoutManager(lm);
        chatRecycler.setAdapter(chatAdapter);

        // Load Messages
        loadMessages();
    }


    private void sendMessage(String text) {

        Map<String, Object> message = new HashMap<>();
        message.put("senderId", senderUid);
        message.put("receiverId", receiverUid);
        message.put("text", text);

        // ✅ Server timestamp (real)
        message.put("timestamp", FieldValue.serverTimestamp());

        // ✅ Local time (instant ordering)
        message.put("localTime", System.currentTimeMillis());

        db.collection("chats")
                .document(chatId)
                .collection("messages")
                .add(message);

        db.collection("chats")
                .document(chatId)
                .update(
                        "lastMessage", text,
                        "lastMessageTime", FieldValue.serverTimestamp()
                );
    }




    private void loadMessages() {
        db.collection("chats")
                .document(chatId)
                .collection("messages")
                .orderBy("localTime", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Log.e("ChatActivity", "Listen failed.", error);
                        return;
                    }

                    if (snapshots != null) {
                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    MessageModel msg = dc.getDocument().toObject(MessageModel.class);
                                    messageList.add(msg);
                                    chatAdapter.notifyItemInserted(messageList.size() - 1);
                                    chatRecycler.scrollToPosition(messageList.size() - 1);
                                    break;
                                case MODIFIED:
                                    // Handle edited messages if needed
                                    break;
                                case REMOVED:
                                    // Handle deleted messages if needed
                                    break;
                            }
                        }
                    }
                });
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

