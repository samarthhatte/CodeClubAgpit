package com.agpitcodeclub.codeclubagpit.model;

import com.google.firebase.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MessageModel {

    private String senderId;
    private String receiverId;
    private String text;
    private Timestamp timestamp;
    private long localTime;
    private String messageId; // ✅ Add this

    public MessageModel() {}

    public MessageModel(String senderId, String receiverId, String text, Timestamp timestamp, long localTime) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.text = text;
        this.timestamp = timestamp;
        this.localTime = localTime;
    }

    public String getFormattedTime() {
        Date date;
        if (timestamp != null) {
            date = timestamp.toDate();
        } else if (localTime > 0) {
            date = new Date(localTime);
        } else {
            return "Sending...";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return sdf.format(date);
    }

    // Getters and Setters
    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }
    public String getSenderId() { return senderId; }
    public String getReceiverId() { return receiverId; }
    public String getText() { return text; }
    public Timestamp getTimestamp() { return timestamp; }
    public long getLocalTime() { return localTime; }
}