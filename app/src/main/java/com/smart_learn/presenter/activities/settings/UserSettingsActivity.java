package com.smart_learn.presenter.activities.settings;

import android.content.Intent;

import com.smart_learn.presenter.activities.main.MainActivity;

public class UserSettingsActivity extends SettingsActivity {

    @Override
    public void goToStartActivity(){
        // When going back to MainActivity clear backstack and finish current activity,
        // because MainActivity is central point when user is logged in.
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        this.finish();
    }

}