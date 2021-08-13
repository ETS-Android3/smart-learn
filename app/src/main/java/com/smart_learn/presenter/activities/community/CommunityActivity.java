package com.smart_learn.presenter.activities.community;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.smart_learn.R;
import com.smart_learn.core.utilities.GeneralUtilities;
import com.smart_learn.databinding.ActivityCommunityBinding;
import com.smart_learn.presenter.activities.main.MainActivity;
import com.smart_learn.presenter.helpers.BasicActivity;
import com.smart_learn.presenter.helpers.PresenterUtilities;

import org.jetbrains.annotations.NotNull;

public class CommunityActivity extends BasicActivity {

    private ActivityCommunityBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_community);
        binding.setLifecycleOwner(this);
        setSupportActionBar(binding.toolbarActivityCommunity);

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
        navController = PresenterUtilities.Activities.setNavigationGraphWithBottomMenu(this, R.id.nav_host_fragment_activity_community,
                binding.bottomNavigationActivityCommunity, new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @SuppressLint("NonConstantResourceId")
                    @Override
                    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.friends_fragment_nav_graph_activity_community:
                                navController.navigate(R.id.friends_fragment_nav_graph_activity_community,null,
                                        PresenterUtilities.Activities.getVisibleBottomMenuNavOptions());
                                return true;
                            case R.id.users_fragment_nav_graph_activity_community:
                                navController.navigate(R.id.users_fragment_nav_graph_activity_community,null,
                                        PresenterUtilities.Activities.getVisibleBottomMenuNavOptions());
                                return true;
                        }
                        return false;
                    }
                }, null);
    }

    @Override
    public boolean onSupportNavigateUp() {
        goToMainActivity();
        return true;
    }

    private void goToMainActivity(){
        // When going back to MainActivity clear backstack and finish current activity,
        // because MainActivity is central point when user is logged in.
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        this.finish();
    }

}