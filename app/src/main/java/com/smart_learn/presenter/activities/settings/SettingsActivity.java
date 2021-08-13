package com.smart_learn.presenter.activities.settings;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;

import com.smart_learn.R;
import com.smart_learn.core.utilities.GeneralUtilities;
import com.smart_learn.databinding.ActivitySettingsBinding;
import com.smart_learn.presenter.helpers.BasicActivity;
import com.smart_learn.presenter.helpers.PresenterUtilities;

public abstract class SettingsActivity extends BasicActivity {

    private NavController navController;

    public abstract void goToStartActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySettingsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        binding.setLifecycleOwner(this);
        setSupportActionBar(binding.toolbarActivitySettings);

        setLayoutUtilities();

        // if navigation graph cannot be set, then go back from activity
        if(navController == null){
            GeneralUtilities.showShortToastMessage(this, getString(R.string.error_loading_screen));
            onBackPressed();
            this.finish();
            // return;
        }

    }

    private void setLayoutUtilities(){
        // try to set navigation graph
        navController = PresenterUtilities.Activities.setNavigationGraph(this, R.id.nav_host_fragment_activity_settings);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}