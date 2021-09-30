package com.smart_learn.presenter.activities.settings;

import android.content.Intent;

import com.smart_learn.presenter.activities.guest.GuestActivity;

public class GuestSettingsActivity extends SettingsActivity {

    @Override
    public void goToStartActivity() {
        // When going back to GuestActivity clear backstack and finish current activity,
        // because GuestActivity is central point when user is not logged in.
        Intent intent = new Intent(this, GuestActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        this.finish();
    }

}