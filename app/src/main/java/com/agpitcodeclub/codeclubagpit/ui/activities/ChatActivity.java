package com.agpitcodeclub.codeclubagpit.ui.activities;

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
        EdgeToEdge.enable(this);

        // ✅ Bind UI Views FIRST
        userName = findViewById(R.id.userName);
        messageBox = findViewById(R.id.messageBox);
        sendBtn = findViewById(R.id.sendBtn);
        chatRecycler = findViewById(R.id.chatRecycler);

        // ✅ Firebase init
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // ✅ Sender UID
        senderUid = auth.getCurrentUser().getUid();

        // ✅ Receiver Data
        receiverUid = getIntent().getStringExtra("uid");
        receiverName = getIntent().getStringExtra("name");

        userName.setText(receiverName);

        // ✅ Chat ID
        chatId = getChatId(senderUid, receiverUid);

        createChatRoom();

        // ✅ RecyclerView Setup
        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, messageList, senderUid);

        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setStackFromEnd(true);

        chatRecycler.setLayoutManager(lm);
        chatRecycler.setAdapter(chatAdapter);

        // ✅ Send Button
        sendBtn.setOnClickListener(v -> {
            String msg = messageBox.getText().toString().trim();

            if (!msg.isEmpty()) {
                sendMessage(msg);
                messageBox.setText("");
            }
        });

        // ✅ Load Messages
        loadMessages();
    }

    // Generate chatId
    private String getChatId(String uid1, String uid2) {
        return uid1.compareTo(uid2) < 0
                ? uid1 + "_" + uid2
                : uid2 + "_" + uid1;
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

                    if (error != null || snapshots == null) return;

                    messageList.clear();

                    for (DocumentSnapshot doc : snapshots.getDocuments()) {

                        MessageModel msg = doc.toObject(MessageModel.class);

                        if (msg != null) {
                            messageList.add(msg);
                        }
                    }

                    chatAdapter.notifyDataSetChanged();

                    if (!messageList.isEmpty()) {
                        chatRecycler.scrollToPosition(messageList.size() - 1);
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

