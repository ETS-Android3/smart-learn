package com.licenta.smart_learn.remote.game;

import android.view.View;

import com.google.android.material.tabs.TabLayout;
import com.licenta.smart_learn.activities.ui.game_a.ChatMessageModel;

public interface RemotePlay extends BasicPlayMode {
    void initLayoutElements(View view);
    void disableGame(boolean goBack, String Message);
    void updateTable(String playerSymbol, int position, int color);
    void updateTime(String time);
    void sendChatMessage(ChatMessageModel chatMessageModel);
    void addChatMessage(ChatMessageModel chatMessageModel);
    void notifyReceivedMove();
    TabLayout.Tab getTab(int index);
}

