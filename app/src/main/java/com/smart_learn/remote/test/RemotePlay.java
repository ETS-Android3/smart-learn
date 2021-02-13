package com.smart_learn.remote.test;

import android.view.View;

import com.google.android.material.tabs.TabLayout;
import com.smart_learn.presenter.activities.ui.test.ChatMessageModel;
import com.smart_learn.presenter.activities.ui.test.ParticipantModel;

public interface RemotePlay extends BasicPlayMode {
    void initLayoutElements(View view);
    void disableTest(boolean goBack, String Message);
    void updateTable(String playerSymbol, int position, int color);
    void updateTime(String time);
    void sendChatMessage(ChatMessageModel chatMessageModel);
    void addChatMessage(ChatMessageModel chatMessageModel);
    void addParticipant(ParticipantModel participantModel);
    void notifyReceivedMove();
    TabLayout.Tab getTab(int index);
    void setNextQuestion(String question);
}

