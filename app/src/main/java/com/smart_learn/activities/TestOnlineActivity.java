package com.smart_learn.activities;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    private EditText etResponse;
    private Button btnStart;
    private Button btnSubmit;
    private Button btnDetails;

    /** used when test must be disabled to avoid to disable test multiple times */
    private AtomicBoolean disabledMade = new AtomicBoolean(false);

    /** used for access at fragments */
    private TestFragmentAdapter testFragmentAdapter;

    // used for keep data when fragment is changed
    private String currentQuestion = "";
    private String currentParticipantResponse = "";
    private String currentTvStatusMessage = "";
    private boolean submitButtonEnabledStatus = false;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initRemoteLayout();

        // set current context and activity
        CurrentConfig.getCurrentConfigInstance().makeNewConfig(this);

        // set current test activity
        TestService.getTestServiceInstance().basicTestModeActivity = this;

        TestService.getTestServiceInstance().startOnlineTest();
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
        // FIXME: Find why onCreateView on fragment is called multiple times
        testFragmentAdapter = new TestFragmentAdapter(this, getSupportFragmentManager());

        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(testFragmentAdapter);

        tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            // FIXME: fin other way to make checks for null fragments
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if(position == 0 && testFragmentAdapter.getTestFragment() != null){
                    testFragmentAdapter.getTestFragment().setUnseenMoves(0);
                    testFragmentAdapter.getTestFragment().setUnselected(false);
                    tab.setText(testFragmentAdapter.getPageTitle(0));
                    return;
                }
                if(position == 1 && testFragmentAdapter.getChatFragment() != null){
                    testFragmentAdapter.getChatFragment().setUnreadMessages(0);
                    testFragmentAdapter.getChatFragment().setUnselected(false);
                    tab.setText(testFragmentAdapter.getPageTitle(1));
                }
                if(position == 2 && testFragmentAdapter.getParticipantsFragment() != null){
                    testFragmentAdapter.getParticipantsFragment().setUnseenParticipants(0);
                    testFragmentAdapter.getParticipantsFragment().setUnselected(false);
                    tab.setText(testFragmentAdapter.getPageTitle(2));
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if(position == 0 && testFragmentAdapter.getTestFragment() != null){
                    testFragmentAdapter.getTestFragment().setUnselected(true);
                }
                if(position == 1 && testFragmentAdapter.getChatFragment() != null){
                    testFragmentAdapter.getChatFragment().setUnselected(true);
                }
                if(position == 2){
                    testFragmentAdapter.getParticipantsFragment().setUnselected(true);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if(position == 0 && testFragmentAdapter.getTestFragment() != null){
                    testFragmentAdapter.getTestFragment().setUnseenMoves(0);
                    testFragmentAdapter.getTestFragment().setUnselected(false);
                    tab.setText(testFragmentAdapter.getPageTitle(0));
                    return;
                }
                if(position == 1 && testFragmentAdapter.getChatFragment() != null){
                    testFragmentAdapter.getChatFragment().setUnreadMessages(0);
                    testFragmentAdapter.getChatFragment().setUnselected(false);
                    tab.setText(testFragmentAdapter.getPageTitle(1));
                }
                if(position == 2 && testFragmentAdapter.getParticipantsFragment() != null){
                    testFragmentAdapter.getParticipantsFragment().setUnseenParticipants(0);
                    testFragmentAdapter.getParticipantsFragment().setUnselected(false);
                    tab.setText(testFragmentAdapter.getPageTitle(2));
                }
            }
        });

    }


    @Override
    public void initLayoutElements(View view) {

        // set general data
        // this data are used for TEST FRAGMENT

        // TODO: create different initLayout functions for every fragment
        tvStatus = view.findViewById(R.id.tvStatus);
        tvTimer = view.findViewById(R.id.tvTimer);
        etResponse = view.findViewById(R.id.etResponse);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        btnDetails = view.findViewById(R.id.btnDetails);
        btnStart = view.findViewById(R.id.btnStartTest);

        // set listeners
        btnStart.setOnClickListener(v -> {
            runOnUiThread(() -> {
                btnStart.setVisibility(View.GONE);
                tvStatus.setText("");
                tvTimer.setVisibility(View.VISIBLE);
                etResponse.setVisibility(View.VISIBLE);
                btnSubmit.setVisibility(View.VISIBLE);
                btnSubmit.setEnabled(true);
            });
            submitButtonEnabledStatus = true;
            TestService.getTestServiceInstance().sendStartTestPayload();
        });

        btnSubmit.setOnClickListener(v -> {

            String response = etResponse.getText().toString();
            if(response.isEmpty()){
                GeneralUtilities.showToast("No response inserted");
                return;
            }

            runOnUiThread(() -> {
                btnSubmit.setEnabled(false);
            });

            submitButtonEnabledStatus = false;
            currentParticipantResponse = response;

            TestService.getTestServiceInstance()
                    .webSocketSendResponse(TestService.getTestServiceInstance().currentQuestionId.get(),response);
        });


        // set view depending on test status

        // test finished
        if(TestService.getTestServiceInstance().testSuccessfullyFinished.get()) {
            runOnUiThread(() -> {
                tvStatus.setText(currentTvStatusMessage);
                tvTimer.setVisibility(View.GONE);
                btnStart.setVisibility(View.GONE);
                btnSubmit.setVisibility(View.GONE);
                etResponse.setVisibility(View.GONE);
                btnDetails.setVisibility(View.VISIBLE);
            });
            return;
        }

        // test does not start
        if(!TestService.getTestServiceInstance().testStarted.get()){
            runOnUiThread(() -> {
                tvTimer.setVisibility(View.GONE);
                etResponse.setVisibility(View.GONE);
                btnSubmit.setVisibility(View.GONE);
                btnDetails.setVisibility(View.GONE);
            });

            // make initial setup
            updateInfo();
           return;
        }

        // test is in progress
        runOnUiThread(() -> {
            btnStart.setVisibility(View.GONE);
            tvTimer.setVisibility(View.VISIBLE);
            etResponse.setVisibility(View.VISIBLE);
            btnSubmit.setVisibility(View.VISIBLE);
            btnSubmit.setEnabled(submitButtonEnabledStatus);
            btnDetails.setVisibility(View.GONE);
            tvStatus.setText(currentQuestion);
            etResponse.setText(currentParticipantResponse);
        });

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void setNextQuestion(String question){

        // FIXME: at first question loading for participants who connected to test
        //  view is not good and this must be done
        if(!TestService.getTestServiceInstance().isTestAdmin.get()){
            runOnUiThread(() -> {
                btnStart.setVisibility(View.GONE);
                tvTimer.setVisibility(View.VISIBLE);
                btnSubmit.setVisibility(View.VISIBLE);
                etResponse.setVisibility(View.VISIBLE);
            });
        }

        runOnUiThread(() -> {
            btnSubmit.setEnabled(true);
            tvStatus.setText(question);

            etResponse.setText("");
            tvTimer.setText("00:10");
        });
        submitButtonEnabledStatus = true;
        currentQuestion = question;
        currentParticipantResponse = "";
    }

    @Override
    public void disableTest(boolean goBack, String message){

        // stop test
        TestService.getTestServiceInstance().stopTest();

        runOnUiThread(() -> {
            btnSubmit.setVisibility(View.GONE);
            etResponse.setVisibility(View.GONE);
            btnDetails.setVisibility(View.VISIBLE);
        });


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
        currentTvStatusMessage = message;

        // mark that disabled have been made
        disabledMade.set(true);
    }

    @Override
    public void updateTable(String playerSymbol, int position, int color){

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void updateInfo(){
        if(TestService.getTestServiceInstance().testStarted.get()){
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
        if(testFragmentAdapter.getParticipantsFragment() == null){
            TestService.getTestServiceInstance().participantModelList.add(participantModel);
            return;
        }
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

        TestService.getTestServiceInstance().abortTestAlert(this);
        return true;
    }
}