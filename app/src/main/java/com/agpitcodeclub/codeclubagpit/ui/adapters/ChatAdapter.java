package com.agpitcodeclub.codeclubagpit.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.agpitcodeclub.codeclubagpit.R;
import com.agpitcodeclub.codeclubagpit.model.MessageModel;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    ArrayList<MessageModel> messageList;
    String senderUid;

    int ITEM_SENT = 1;
    int ITEM_RECEIVED = 2;

    public ChatAdapter(Context context, ArrayList<MessageModel> messageList, String senderUid) {
        this.context = context;
        this.messageList = messageList;
        this.senderUid = senderUid;
    }

    @Override
    public int getItemViewType(int position) {

        MessageModel msg = messageList.get(position);

        if (msg.getSenderId() != null && msg.getSenderId().equals(senderUid)) {
            return ITEM_SENT;
        } else {
            return ITEM_RECEIVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == ITEM_SENT) {
            View view = LayoutInflater.from(context)
                    .inflate(R.layout.item_message_sent, parent, false);
            return new SentViewHolder(view);
        } else {
            View view = LayoutInflater.from(context)
                    .inflate(R.layout.item_message_received, parent, false);
            return new ReceivedViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MessageModel msg = messageList.get(position);

        // Call the helper method from your updated MessageModel
        String formattedTime = msg.getFormattedTime();

        if (holder instanceof SentViewHolder) {
            SentViewHolder sentHolder = (SentViewHolder) holder;
            sentHolder.txtMessage.setText(msg.getText());
            sentHolder.txtTime.setText(formattedTime); // Set time
        } else {
            ReceivedViewHolder receivedHolder = (ReceivedViewHolder) holder;
            receivedHolder.txtMessage.setText(msg.getText());
            receivedHolder.txtTime.setText(formattedTime); // Set time
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    class SentViewHolder extends RecyclerView.ViewHolder {
        TextView txtMessage, txtTime;

        public SentViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMessage = itemView.findViewById(R.id.txtMessage);
            txtTime = itemView.findViewById(R.id.txtTime); // Match ID in item_message_sent.xml
        }
    }

    class ReceivedViewHolder extends RecyclerView.ViewHolder {
        TextView txtMessage, txtTime;

        public ReceivedViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMessage = itemView.findViewById(R.id.txtMessage);
            txtTime = itemView.findViewById(R.id.txtTime); // Match ID in item_message_received.xml
        }
    }
}
