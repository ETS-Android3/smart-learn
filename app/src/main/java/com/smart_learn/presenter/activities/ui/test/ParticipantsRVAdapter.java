package com.smart_learn.presenter.activities.ui.test;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
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

public class ParticipantsRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // chat codes
    public static final int VIEW_TYPE_CONNECTED = 0;
    public static final int VIEW_TYPE_DISCONNECTED = 1;

    private List<ParticipantModel> participantModelList;
    private final Context context;

    public ParticipantsRVAdapter(List<ParticipantModel> participantModelList, Context context) {
        this.participantModelList = participantModelList;
        this.context = context;
    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        ParticipantModel participant = participantModelList.get(position);
        Log.i(Logs.INFO,Logs.FUNCTION + "[getItemViewType] view type is [" + participant.getViewType() +"]");
        return participant.getViewType();
    }

    @Override
    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == VIEW_TYPE_CONNECTED) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.participant_connected_layout, parent, false);
            return new ParticipantConnectedHolder(view);
        }

        // here is an error
        Log.e(Logs.UNEXPECTED_ERROR,Logs.FUNCTION + "[onCreateViewHolder] view type [" + viewType + "] is not correct");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.participant_error_layout, parent, false);
        return new ErrorHolder(view);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ParticipantModel participant = participantModelList.get(position);
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_CONNECTED:
                ((ParticipantConnectedHolder) holder).bind(participant);
                break;
            default:
                Log.e(Logs.UNEXPECTED_ERROR,Logs.FUNCTION + "[onBindViewHolder] view ype [" +
                        holder.getItemViewType() + "] is not correct");
        }
    }

    @Override
    public int getItemCount() {
        return participantModelList.size();
    }


    private static final class ParticipantConnectedHolder extends RecyclerView.ViewHolder {

        private final TextView tvUserId;

        public ParticipantConnectedHolder(@NonNull View itemView) {
            super(itemView);
            tvUserId = itemView.findViewById(R.id.tvUserId);
        }

        void bind(ParticipantModel participantModel) {
            tvUserId.setText(participantModel.getParticipantId());

            if(participantModel.isTestAdmin()){
                tvUserId.setBackgroundColor(Color.GREEN);
            }
        }
    }

    private static final class ErrorHolder extends RecyclerView.ViewHolder{

        private final TextView message;

        public ErrorHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message);
        }

        @SuppressLint("SetTextI18n")
        void bind(ChatMessageModel messageModel){
            message.setText(Logs.UNEXPECTED_ERROR + "[ErrorHolder] No participant found");
        }
    }

}
