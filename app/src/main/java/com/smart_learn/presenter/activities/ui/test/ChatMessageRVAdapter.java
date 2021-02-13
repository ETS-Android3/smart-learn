package com.smart_learn.presenter.activities.ui.test;


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smart_learn.R;
import com.smart_learn.utilities.Logs;

import java.util.List;


/**
 * https://www.youtube.com/watch?v=vv3x7opE98U
 * https://github.com/SujeetKr9/Chat-App-Design
 */
public class ChatMessageRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // chat codes
    public static final int VIEW_TYPE_MESSAGE_SENT = 0;
    public static final int VIEW_TYPE_MESSAGE_RECEIVED = 1;

    private List<ChatMessageModel> chatMessageModelList;
    private final Context context;

    public ChatMessageRVAdapter(List<ChatMessageModel> chatMessageModelList, Context context) {
        this.chatMessageModelList = chatMessageModelList;
        this.context = context;
    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        ChatMessageModel message = chatMessageModelList.get(position);
        Log.i(Logs.INFO,Logs.FUNCTION + "[getItemViewType] view type is [" + message.getViewType() +"]");
        return message.getViewType();
    }

    @Override
    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.msj_send_layout, parent, false);
            return new SentMessageHolder(view);
        }

        if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.msj_rec_layout, parent, false);
            return new ReceivedMessageHolder(view);
        }

        // here is an error
        Log.e(Logs.UNEXPECTED_ERROR,Logs.FUNCTION + "[onCreateViewHolder] view type [" + viewType + "] is not correct");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.msj_error_layout, parent, false);
        return new ErrorHolder(view);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ChatMessageModel message = chatMessageModelList.get(position);
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
                break;
            default:
                Log.e(Logs.UNEXPECTED_ERROR,Logs.FUNCTION + "[onBindViewHolder] view ype [" +
                        holder.getItemViewType() + "] is not correct");
        }
    }

    @Override
    public int getItemCount() {
        return chatMessageModelList.size();
    }


    private static final class SentMessageHolder extends RecyclerView.ViewHolder {

        private final TextView message;
        private final TextView time;

        public SentMessageHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message);
            time = itemView.findViewById(R.id.time);
        }

        void bind(ChatMessageModel messageModel) {
            message.setText(messageModel.getText());
            time.setText(messageModel.getTime());
        }
    }

    private static final class ReceivedMessageHolder extends RecyclerView.ViewHolder{

        private final TextView message;
        private final TextView time;

        public ReceivedMessageHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message);
            time = itemView.findViewById(R.id.time);
        }

        void bind(ChatMessageModel messageModel){
            message.setText(messageModel.getText());
            time.setText(messageModel.getTime());
        }
    }

    private static final class ErrorHolder extends RecyclerView.ViewHolder{

        private final TextView message;
        private final TextView time;

        public ErrorHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message);
            time = itemView.findViewById(R.id.time);
        }

        @SuppressLint("SetTextI18n")
        void bind(ChatMessageModel messageModel){
            message.setText(Logs.UNEXPECTED_ERROR + "[ErrorHolder] No message found");
            time.setText(Logs.UNEXPECTED_ERROR + "[ErrorHolder] No time found");
        }
    }


}
