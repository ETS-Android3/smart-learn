package com.licenta.smart_learn.activities;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.licenta.smart_learn.R;
import com.licenta.smart_learn.activities.ui.game_a.ChatMessageModel;
import com.licenta.smart_learn.activities.ui.game_a.GameFragmentAdapter;
import com.licenta.smart_learn.config.CurrentConfig;
import com.licenta.smart_learn.remote.game.RemotePlay;
import com.licenta.smart_learn.remote.game.config.StrictCodes;
import com.licenta.smart_learn.services.GameService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class GameAActivity extends AppCompatActivity implements RemotePlay {

    private TabLayout tabs;

    private TextView tvStatus;
    private TextView tvTimer;
    private List<Button> btnList = new ArrayList<>();

    /** used when game must be disabled to avoid to disable game multiple times */
    private AtomicBoolean disabledMade = new AtomicBoolean(false);

    /** used for access at fragments */
    private GameFragmentAdapter gameFragmentAdapter;



    /** for local cpu play */
    private AtomicInteger column = new AtomicInteger();
    private AtomicInteger line = new AtomicInteger();


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(GameService.getGameServiceInstance().currentPlayMode.get() == GameService.LOCAL_MODE_PLAY) {
            initCpuLayout();
        }
        else{
            initRemoteLayout();
        }

        // set current context and activity
        CurrentConfig.getCurrentConfigInstance().makeNewConfig(this);
    }

    private void setToolbar(){
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle("Game A");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void initRemoteLayout(){
        setContentView(R.layout.activity_game_a_remote);

        setToolbar();

        // set fragments
        gameFragmentAdapter = new GameFragmentAdapter(this, getSupportFragmentManager());

        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(gameFragmentAdapter);

        tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if(position == 0){
                    gameFragmentAdapter.getGameFragment().setUnseenMoves(0);
                    gameFragmentAdapter.getGameFragment().setUnselected(false);
                    tab.setText(gameFragmentAdapter.getPageTitle(0));
                    return;
                }
                if(position == 1){
                    gameFragmentAdapter.getChatFragment().setUnreadMessages(0);
                    gameFragmentAdapter.getChatFragment().setUnselected(false);
                    tab.setText(gameFragmentAdapter.getPageTitle(1));
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if(position == 0){
                    gameFragmentAdapter.getGameFragment().setUnselected(true);
                }
                if(position == 1){
                    gameFragmentAdapter.getChatFragment().setUnselected(true);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if(position == 0){
                    gameFragmentAdapter.getGameFragment().setUnseenMoves(0);
                    gameFragmentAdapter.getGameFragment().setUnselected(false);
                    tab.setText(gameFragmentAdapter.getPageTitle(0));
                    return;
                }
                if(position == 1){
                    gameFragmentAdapter.getChatFragment().setUnreadMessages(0);
                    gameFragmentAdapter.getChatFragment().setUnselected(false);
                    tab.setText(gameFragmentAdapter.getPageTitle(1));
                }
            }
        });
    }

    private void initCpuLayout(){
        setContentView(R.layout.activity_game_a_local);
        setToolbar();
        initLayoutElements(null);
    }

    @Override
    public void initLayoutElements(View view) {

        if(view == null){
            // for local and cpu mode
            tvStatus = findViewById(R.id.tvStatus);
            tvTimer = findViewById(R.id.tvTimer);
            btnList.add(findViewById(R.id.btn1));
            btnList.add(findViewById(R.id.btn2));
            btnList.add(findViewById(R.id.btn3));
            btnList.add(findViewById(R.id.btn4));
            btnList.add(findViewById(R.id.btn5));
            btnList.add(findViewById(R.id.btn6));
            btnList.add(findViewById(R.id.btn7));
            btnList.add(findViewById(R.id.btn8));
            btnList.add(findViewById(R.id.btn9));

            // make initial setup
            updateInfo();

            // set listeners
            for(int i = 0; i < btnList.size(); i++){

                int finalI = i;
                btnList.get(i).setOnClickListener(v -> {
/*
                    if(GameService.getGameServiceInstance().canMakeMove.get()){
                        switch (finalI){
                            case 0:
                                press(0,0);
                                break;
                            case 1:
                                press(0,1);
                                break;
                            case 2:
                                press(0,2);
                                break;
                            case 3:
                                press(1,0);
                                break;
                            case 4:
                                press(1,1);
                                break;
                            case 5:
                                press(1,2);
                                break;
                            case 6:
                                press(2,0);
                                break;
                            case 7:
                                press(2,1);
                                break;
                            case 8:
                                press(2,2);
                                break;
                        }


                    }

 */
                });
            }

            // start game
            GameService.getGameServiceInstance().startCPUGame(this);

        }
        else{
            // for remote mode fragment will be used
            tvStatus = view.findViewById(R.id.tvStatus);
            tvTimer = view.findViewById(R.id.tvTimer);
            btnList.add(view.findViewById(R.id.btn1));
            btnList.add(view.findViewById(R.id.btn2));
            btnList.add(view.findViewById(R.id.btn3));
            btnList.add(view.findViewById(R.id.btn4));
            btnList.add(view.findViewById(R.id.btn5));
            btnList.add(view.findViewById(R.id.btn6));
            btnList.add(view.findViewById(R.id.btn7));
            btnList.add(view.findViewById(R.id.btn8));
            btnList.add(view.findViewById(R.id.btn9));

            // make initial setup
            updateInfo();

            // set listeners
            for(int i = 0; i < btnList.size(); i++){

                final int finalI = i;
                btnList.get(i).setOnClickListener(v -> {
                    //btnList.get(finalI).setBackgroundColor(Color.RED);

                    if(GameService.getGameServiceInstance().notYourMove(true)){
                        return;
                    }

                    GameService.getGameServiceInstance().selectConnectionFunction(finalI,true,true);
                });
            }

            // start game
            GameService.getGameServiceInstance().startRemoteGame(this);

        }

    }

    /** is called only by MAIN_GAME_TIMER*/
    @Override
    public void disableGame(boolean goBack, String message){
        // mark this true to send stop message
        GameService.getGameServiceInstance().getAbortGameConnection().set(true);

        // stop game
        GameService.getGameServiceInstance().stopGame();

        // reset info from this activity

        // go back to previous activity if was requested
        if(goBack){
            onBackPressed();
            return;
        }

        // if game was already stopped don`t do anything
        if(disabledMade.get()){
            return;
        }

        // 'freeze' current game activity if current activity stays on display
        runOnUiThread(() -> {
            for(Button btn: btnList){
                btn.setEnabled(false);
            }
            tvStatus.setText(message);
        });

        // mark that disabled have been made
        disabledMade.set(true);
    }

    @Override
    public void updateTable(String playerSymbol, int position, int color){
        runOnUiThread(() -> {
            Button btn = getBtnList().get(position);
            btn.setText(playerSymbol);
            btn.setBackgroundColor(color);
            btn.setEnabled(false);
        });
        updateInfo();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void updateInfo(){
        if(GameService.getGameServiceInstance().canMakeMove.get()){
            runOnUiThread(() -> {
                tvStatus.setText("Game: [" + GameService.getGameServiceInstance().gameCode +
                        "] Your turn to move. Your symbol is " + getSymbol(false));
            });
        }
        else{
            runOnUiThread(() -> {
                tvStatus.setText("Game [" + GameService.getGameServiceInstance().gameCode +
                        " ] Not your turn. Your symbol is " + getSymbol(false));
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
        GameService.getGameServiceInstance().sendChatMessage(chatMessageModel);
    }

    @Override
    public void addChatMessage(ChatMessageModel chatMessageModel) {
        gameFragmentAdapter.getChatFragment().addMessage(chatMessageModel);
    }

    @Override
    public void notifyReceivedMove() {
        gameFragmentAdapter.getGameFragment().registerMove();
    }

    @Override
    public TabLayout.Tab getTab(int index) {
        return tabs.getTabAt(index);
    }


/*
    @Override
    public boolean playerMoved() {
        // if player can not make a move means that he made is his move
        return !GameService.getGameServiceInstance().canMakeMove.get();
    }

    private void press(int i, int j){
        GameService.getGameServiceInstance().canMakeMove.set(false);
        line.set(i);
        column.set(j);
    }

    @Override
    public void setPlayerCanMove(boolean value) {
        GameService.getGameServiceInstance().canMakeMove.set(value);
    }

    @Override
    public int getLine() {
        return line.get();
    }

    @Override
    public int getColumn() {
        return column.get();
    }

    @Override
    public void setButton(Character player, int colorCode, byte line, byte column) {
        int buttonNumber =  line * 3 + column;

        Button button =  btnList.get(buttonNumber);

        if(!button.getText().equals("")){
            System.out.println("\nERROR in setBtn in XAndZero: [button already pressed]");
        }
        else {
            runOnUiThread(() -> {
                button.setEnabled(false);
                button.setText(String.valueOf(player));

                if(player.equals(getSymbol(false).charAt(0))){
                    button.setBackgroundColor(Color.RED);
                }
                else{
                    button.setBackgroundColor(Color.BLUE);
                }

            });
        }
    }

    private void disableButtons(){
        runOnUiThread(() -> {
            for(int i = 0; i < btnList.size(); i++) {
                btnList.get(i).setEnabled(false);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void setResult(Character winner, List<Pair<Byte, Byte>> slicesIndexes) {
        disableButtons();
        Character minPlayer = getSymbol(false).charAt(0);
        Character maxPlayer = getSymbol(true).charAt(0);

        //check for draw game
        if(!winner.equals(minPlayer) && !winner.equals(maxPlayer)){
            //this.resultLabel.setText("draw game");
            // set listeners
            runOnUiThread(() -> {
                for(int i = 0; i < btnList.size(); i++) {
                    btnList.get(i).setBackgroundColor(Color.YELLOW);
                }

                tvStatus.setText("Draw Game");
            });

            return;
        }

        //this.resultLabel.setText(winner + " has won !");
        for(Pair<Byte,Byte> indexes: slicesIndexes){
            byte indexButton = (byte) (indexes.first * 3 + indexes.second); // line * 3 + column
            runOnUiThread(() -> {
                btnList.get(indexButton).setBackgroundColor(Color.GREEN);
                tvStatus.setText(winner + " has won !");
            });
        }
    }

 */


    @Override
    public String getSymbol(boolean opponent){
        /*
        FIXME: Error when this function is called from cpu local mode
        if(!GameService.getGameServiceInstance().goodPlayerSymbol()){
            disableGame(false,Logs.UNEXPECTED_ERROR + "Symbol [" +
                    GameService.getGameServiceInstance().goodPlayerSymbol() + "] unrecognized. Game aborted.");
            return "";
        }

         */

        // check what symbol have local player
        if (GameService.getGameServiceInstance().playerSymbol.equals(StrictCodes.PLAYER_A_SYMBOL)){
            // if you want opponent symbol opponent have symbol B
            if(opponent) {
                return "O";
            }
            return "X";
        }

        // local player have symbol B
        if(opponent){
            return "X";
        }
        return "O";
    }

    public List<Button> getBtnList() {
        return btnList;
    }

    @Override
    public boolean onSupportNavigateUp() {
        if(GameService.getGameServiceInstance().currentPlayMode.get() == GameService.LOCAL_MODE_PLAY) {
            GameService.getGameServiceInstance().resetCurrentGameInfo();
            onBackPressed();
            return true;
        }

        // if game was stopped return
        if(GameService.getGameServiceInstance().getGameStopped().get()){
            onBackPressed();
            return true;
        }
        GameService.getGameServiceInstance().abortGameAlert(this);
        return true;
    }
}