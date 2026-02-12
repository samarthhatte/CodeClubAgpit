package com.agpitcodeclub.codeclubagpit.model;

import com.google.firebase.Timestamp;

public class MessageModel {

    private String senderId;
    private String receiverId;
    private String text;
    private Timestamp timestamp;

    public MessageModel() {
        // Required empty constructor for Firestore
    }

    public MessageModel(String senderId, String receiverId, String text, Timestamp timestamp) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.text = text;
        this.timestamp = timestamp;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public String getText() {
        return text;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }
}
