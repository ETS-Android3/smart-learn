package com.smart_learn.activities;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.smart_learn.R;
import com.smart_learn.activities.ui.test.ChatMessageModel;
import com.smart_learn.activities.ui.test.ParticipantModel;
import com.smart_learn.activities.ui.test.TestFragmentAdapter;
import com.smart_learn.config.CurrentConfig;
import com.smart_learn.remote.test.RemotePlay;
import com.smart_learn.services.TestService;
import com.smart_learn.utilities.GeneralUtilities;

import java.util.concurrent.atomic.AtomicBoolean;

public class TestOnlineActivity extends AppCompatActivity implements RemotePlay {

    private TabLayout tabs;

    private TextView tvStatus;
    private TextView tvTimer;
    private TextView tvResponse;
    private Button btnSubmit;

    /** used when test must be disabled to avoid to disable test multiple times */
    private AtomicBoolean disabledMade = new AtomicBoolean(false);

    /** used for access at fragments */
    private TestFragmentAdapter testFragmentAdapter;

    /** check if test started */
    private AtomicBoolean testStarted = new AtomicBoolean(false);


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initRemoteLayout();

        // set current context and activity
        CurrentConfig.getCurrentConfigInstance().makeNewConfig(this);
    }

    private void setToolbar(){
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle("Online test");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void initRemoteLayout(){
        setContentView(R.layout.activity_test_online);

        setToolbar();

        // set fragments
        testFragmentAdapter = new TestFragmentAdapter(this, getSupportFragmentManager());

        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(testFragmentAdapter);

        tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if(position == 0){
                    testFragmentAdapter.getTestFragment().setUnseenMoves(0);
                    testFragmentAdapter.getTestFragment().setUnselected(false);
                    tab.setText(testFragmentAdapter.getPageTitle(0));
                    return;
                }
                if(position == 1){
                    testFragmentAdapter.getChatFragment().setUnreadMessages(0);
                    testFragmentAdapter.getChatFragment().setUnselected(false);
                    tab.setText(testFragmentAdapter.getPageTitle(1));
                }
                if(position == 2){
                    testFragmentAdapter.getParticipantsFragment().setUnseenParticipants(0);
                    testFragmentAdapter.getParticipantsFragment().setUnselected(false);
                    tab.setText(testFragmentAdapter.getPageTitle(2));
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if(position == 0){
                    testFragmentAdapter.getTestFragment().setUnselected(true);
                }
                if(position == 1){
                    testFragmentAdapter.getChatFragment().setUnselected(true);
                }
                if(position == 2){
                    testFragmentAdapter.getParticipantsFragment().setUnselected(true);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if(position == 0){
                    testFragmentAdapter.getTestFragment().setUnseenMoves(0);
                    testFragmentAdapter.getTestFragment().setUnselected(false);
                    tab.setText(testFragmentAdapter.getPageTitle(0));
                    return;
                }
                if(position == 1){
                    testFragmentAdapter.getChatFragment().setUnreadMessages(0);
                    testFragmentAdapter.getChatFragment().setUnselected(false);
                    tab.setText(testFragmentAdapter.getPageTitle(1));
                }
                if(position == 2){
                    testFragmentAdapter.getParticipantsFragment().setUnseenParticipants(0);
                    testFragmentAdapter.getParticipantsFragment().setUnselected(false);
                    tab.setText(testFragmentAdapter.getPageTitle(2));
                }
            }
        });

    }


    @Override
    public void initLayoutElements(View view) {

        // for remote mode fragment will be used
        tvStatus = view.findViewById(R.id.tvStatus);
        tvTimer = view.findViewById(R.id.tvTimer);
        tvResponse = view.findViewById(R.id.tvResponse);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        Button btnStart = view.findViewById(R.id.btnStartTest);

        // by default some elements will be hidden
        tvTimer.setVisibility(View.GONE);
        tvResponse.setVisibility(View.GONE);
        btnSubmit.setVisibility(View.GONE);

        // make initial setup
        updateInfo();

        // set current test activity
        TestService.getTestServiceInstance().basicTestModeActivity = this;

        // set listeners
        btnStart.setOnClickListener(v -> {
            runOnUiThread(() -> {
                btnStart.setVisibility(View.GONE);
                tvTimer.setVisibility(View.VISIBLE);
                tvResponse.setVisibility(View.VISIBLE);
                btnSubmit.setVisibility(View.VISIBLE);
            });
            TestService.getTestServiceInstance().sendStartTestPayload();
        });

        btnSubmit.setOnClickListener(v -> {

            String response = tvResponse.getText().toString();
            if(response.isEmpty()){
                GeneralUtilities.showToast("No response inserted");
                return;
            }

            runOnUiThread(() -> {
                btnSubmit.setEnabled(false);
            });

            TestService.getTestServiceInstance().webSocketSendResponse(response);
        });

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void setNextQuestion(String question){
        runOnUiThread(() -> {
            btnSubmit.setEnabled(true);
            tvStatus.setText(question);
            tvTimer.setText("00:10");
        });
    }

    @Override
    public void disableTest(boolean goBack, String message){

        // stop test
        TestService.getTestServiceInstance().stopTest();

        // reset info from this activity

        // go back to previous activity if was requested
        if(goBack){
            onBackPressed();
            return;
        }

        // if test was already stopped don`t do anything
        if(disabledMade.get()){
            return;
        }

        // TODO: 'freeze' current test activity if current activity stays on display
        runOnUiThread(() -> {
            tvStatus.setText(message);
        });

        // mark that disabled have been made
        disabledMade.set(true);
    }

    @Override
    public void updateTable(String playerSymbol, int position, int color){

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void updateInfo(){
        if(testStarted.get()){
//            runOnUiThread(() -> {
//                tvStatus.setText("Test [" + TestService.getTestServiceInstance().testCode +
//                        " ] Not your turn. Your symbol is " + getSymbol(false));
//            });
        }
        else{
            runOnUiThread(() -> {
                tvStatus.setText("Test [" + TestService.getTestServiceInstance().testCode +
                        " ] not started. Wait for participants to join ...");
            });
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void updateTime(String time) {
        runOnUiThread(() -> tvTimer.setText("00:" + time));
    }

    @Override
    public void sendChatMessage(ChatMessageModel chatMessageModel) {
        TestService.getTestServiceInstance().sendChatMessage(chatMessageModel);
    }

    @Override
    public void addChatMessage(ChatMessageModel chatMessageModel) {
        testFragmentAdapter.getChatFragment().addMessage(chatMessageModel);
    }

    @Override
    public void addParticipant(ParticipantModel participantModel) {
        testFragmentAdapter.getParticipantsFragment().addParticipant(participantModel);
    }


    @Override
    public void notifyReceivedMove() {
        testFragmentAdapter.getTestFragment().registerMove();
    }

    @Override
    public TabLayout.Tab getTab(int index) {
        return tabs.getTabAt(index);
    }


    @Override
    public boolean onSupportNavigateUp() {
        if(TestService.getTestServiceInstance().currentTestMode.get() == TestService.LOCAL_MODE_TEST) {
            TestService.getTestServiceInstance().resetCurrentTestInfo();
            onBackPressed();
            return true;
        }

        // if test was stopped return
        if(TestService.getTestServiceInstance().getTestStopped().get()){
            onBackPressed();
            return true;
        }

        TestService.getTestServiceInstance().aborTestAlert(this);
        return true;
    }
}