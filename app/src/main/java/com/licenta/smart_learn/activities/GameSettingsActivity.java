package com.licenta.smart_learn.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import com.licenta.smart_learn.R;
import com.licenta.smart_learn.config.CurrentConfig;
import com.licenta.smart_learn.remote.game.config.StrictCodes;
import com.licenta.smart_learn.services.GameService;
import com.licenta.smart_learn.utilities.GeneralUtilities;
import com.licenta.smart_learn.utilities.Logs;

public class GameSettingsActivity extends AppCompatActivity {

    private RadioButton rbSymbolA;
    private RadioButton rbSymbolB;
    private RadioButton rbFirstMoveYou;
    private RadioButton rbFirstMoveOpponent;
    private EditText etDifficulty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_settings);

        // set toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle("Game Settings");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // set current context and activity
        CurrentConfig.getCurrentConfigInstance().makeNewConfig(this);

        // get View components
        rbSymbolA = findViewById(R.id.rbSymbolA);
        rbSymbolB = findViewById(R.id.rbSymbolB);
        rbFirstMoveYou = findViewById(R.id.rbFirstMoveYou);
        rbFirstMoveOpponent = findViewById(R.id.rbFirstMoveOpponent);
        etDifficulty = findViewById(R.id.etDifficulty);

        //initial setup
        runOnUiThread(() -> {
            rbSymbolA.setChecked(true);
            rbSymbolB.setChecked(false);
            rbFirstMoveYou.setChecked(true);
            rbFirstMoveOpponent.setChecked(false);
            etDifficulty.setVisibility(View.GONE);
        });

        if(GameService.getGameServiceInstance().currentPlayMode.get() == GameService.LOCAL_MODE_PLAY){
            runOnUiThread(() -> {
                etDifficulty.setVisibility(View.VISIBLE);
                etDifficulty.setText("0");
            });
        }

        // set listeners
        rbSymbolA.setOnClickListener(v -> {
            runOnUiThread(() -> {
                rbSymbolB.setChecked(false);
                rbSymbolA.setChecked(true);
            });
        });

        rbSymbolB.setOnClickListener(v -> {
            runOnUiThread(() -> {
                rbSymbolB.setChecked(true);
                rbSymbolA.setChecked(false);
            });
        });

        rbFirstMoveYou.setOnClickListener(v -> {
            runOnUiThread(() -> {
                rbFirstMoveYou.setChecked(true);
                rbFirstMoveOpponent.setChecked(false);
            });
        });

        rbFirstMoveOpponent.setOnClickListener(v -> {
            runOnUiThread(() -> {
                rbFirstMoveYou.setChecked(false);
                rbFirstMoveOpponent.setChecked(true);
            });
        });

        findViewById(R.id.btnStartGameA).setOnClickListener(v -> {

            // if no game type is selected until this point an unexpected error appeared
            if( GameService.getGameServiceInstance().currentGame ==  GameService.NO_GAME){
                GeneralUtilities.showToast(Logs.UNEXPECTED_ERROR + "No game is selected");
                return;
            }

            // launch game depending on game mode
            switch (GameService.getGameServiceInstance().currentPlayMode.get()){
                case GameService.NO_PLAY_MODE:
                    GeneralUtilities.showToast(Logs.ERROR + "No play mode is selected");
                    break;
                case GameService.LOCAL_MODE_PLAY:
                    int difficulty = Integer.parseInt(String.valueOf(etDifficulty.getText()));

                    if(difficulty <= 0){
                        GeneralUtilities.showToast("Difficulty must be > 0");
                        return;
                    }

                    GameService.getGameServiceInstance().createLocalCPUGame(getPlayerSymbol(),getFirstPlayerToMove(),difficulty);
                    break;
                case GameService.REMOTE_MODE_PLAY:
                    // if this is a remote game
                    GameService.getGameServiceInstance().createRemoteHostConnection(getPlayerSymbol(),getFirstPlayerToMove());
                    break;
                default:
                    GeneralUtilities.showToast(Logs.UNEXPECTED_ERROR + "No valid mode selected");
            }
        });
    }

    private String getPlayerSymbol(){
        if(rbSymbolA.isChecked()){
            return StrictCodes.PLAYER_A_SYMBOL;
        }
        return StrictCodes.PLAYER_B_SYMBOL;
    }

    private boolean getFirstPlayerToMove(){
        return rbFirstMoveYou.isChecked();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // set current context and activity
        CurrentConfig.getCurrentConfigInstance().makeNewConfig(this);
    }
}