package com.licenta.smart_learn.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import com.licenta.smart_learn.R;
import com.licenta.smart_learn.config.CurrentConfig;
import com.licenta.smart_learn.remote.game.sockets.LoadingConnectionDialog;
import com.licenta.smart_learn.services.GameService;
import com.licenta.smart_learn.utilities.GeneralUtilities;
import com.licenta.smart_learn.utilities.Logs;

public class GameGenerationActivity extends AppCompatActivity {

    private EditText etGeneratedCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_generation);

        // set the toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle("Game generation");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // set current context and activity
        CurrentConfig.getCurrentConfigInstance().makeNewConfig(this);

        // get View components
        etGeneratedCode = findViewById(R.id.etGeneratedCode);

        // set listeners
        findViewById(R.id.btnGenerateGame).setOnClickListener(v -> goToGameSettingsActivity());

        findViewById(R.id.btnConnectTogame).setOnClickListener(v -> {

            String code = etGeneratedCode.getText().toString();
            if (code.isEmpty()){
                GeneralUtilities.showToast("No code entered");
                return;
            }


            // check for connection and make initial setup
            /*
            if(NetworkUtilities.notGoodConnection()){
                return;
            }
             */

            // reset remote game info
            GameService.getGameServiceInstance().resetCurrentGameInfo();

            // mark that the player don`t generated the game
            GameService.getGameServiceInstance().playerGenerateGame.set(false);

            // delete previous code
            GameService.getGameServiceInstance().gameCode
                    .delete(0,  GameService.getGameServiceInstance().gameCode.length());
            // and add new code
            GameService.getGameServiceInstance().gameCode.append(code);

            // show a loading dialog while connecting to game
            LoadingConnectionDialog loadingDialog = new LoadingConnectionDialog("Requesting a game code ...", "");
            // Code does no exists . Will be obtained after request made in dialog.
            loadingDialog.startGameConnection(code, GameService.getGameServiceInstance().playerGenerateGame.get());

        });
    }

    private void goToGameSettingsActivity(){

        Intent intent;
        switch(GameService.getGameServiceInstance().currentGame){
            case GameService.NO_GAME:
                GeneralUtilities.showToast(Logs.ERROR + "No game is selected");
                return;
            case GameService.GAME_A:
                intent = new Intent(this, GameSettingsActivity.class);
                break;
            default:
                GeneralUtilities.showToast(Logs.UNEXPECTED_ERROR + "No existing game type");
                return;
        }
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // set current context and activity
        CurrentConfig.getCurrentConfigInstance().makeNewConfig(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}