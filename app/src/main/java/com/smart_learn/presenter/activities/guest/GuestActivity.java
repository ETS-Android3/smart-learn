package com.smart_learn.presenter.activities.guest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;

import com.google.android.material.navigation.NavigationView;
import com.smart_learn.R;
import com.smart_learn.databinding.ActivityGuestBinding;
import com.smart_learn.presenter.activities.authentication.AuthenticationActivity;
import com.smart_learn.presenter.activities.notebook.guest.GuestNotebookActivity;
import com.smart_learn.presenter.activities.settings.GuestSettingsActivity;
import com.smart_learn.presenter.activities.test.guest.GuestTestActivity;
import com.smart_learn.presenter.helpers.activities.BasicActivity;
import com.smart_learn.presenter.helpers.PresenterUtilities;

import timber.log.Timber;

public class GuestActivity extends BasicActivity {

    private ActivityGuestBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_guest);
        binding.setLifecycleOwner(this);

        setSupportActionBar(binding.toolbarActivityGuest);

        setLayoutUtilities();

        // if navigation graph cannot be set, then stop activity
        if(navController == null){
            PresenterUtilities.General.showShortToastMessage(this, getString(R.string.error_loading_screen));
            // If drawer cannot be opened finish activity and close application, because navigation
            // cannot be done. I close application here because on this activity onBackPressed() is
            // disabled. So this is the last point.
            // https://stackoverflow.com/questions/17719634/how-to-exit-an-android-app-programmatically/40231289#40231289
            this.finishAndRemoveTask();
            return;
        }

        setNavigationDrawer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // guest activity is home activity so check 'Home' from the navigation menu
        binding.navigationViewActivityGuest.setCheckedItem(R.id.nav_home_menu_nav_drawer_activity_guest);
    }

    @Override
    public void onBackPressed() {
        // disable back from guest activity because is not necessary
        // super.onBackPressed();
        if(binding.drawerLayoutActivityGuest.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayoutActivityGuest.closeDrawer(GravityCompat.START);
        }
    }

    private void setLayoutUtilities(){
        // try to set navigation graph
        navController = PresenterUtilities.Activities.setNavigationGraph(this, R.id.nav_host_fragment_activity_guest);
    }

    private void setNavigationDrawer(){
        // set navigation drawer
        // https://www.youtube.com/watch?v=HwYENW0RyY4
        binding.navigationViewActivityGuest.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.drawerLayoutActivityGuest, binding.toolbarActivityGuest,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayoutActivityGuest.addDrawerListener(toggle);
        toggle.syncState();

        // set on menu item listener
        binding.navigationViewActivityGuest.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_home_menu_nav_drawer_activity_guest:
                        break;
                    case R.id.nav_login_menu_nav_drawer_activity_guest:
                        startActivity(new Intent(GuestActivity.this, AuthenticationActivity.class));
                        break;
                    case R.id.nav_lessons_menu_nav_drawer_activity_guest:
                        startActivity(new Intent(GuestActivity.this, GuestNotebookActivity.class));
                        break;
                    case R.id.nav_test_menu_nav_drawer_activity_guest:
                        startActivity(new Intent(GuestActivity.this, GuestTestActivity.class));
                        break;
                    case R.id.nav_settings_menu_nav_drawer_activity_guest:
                        startActivity(new Intent(GuestActivity.this, GuestSettingsActivity.class));
                        break;
                    default:
                        Timber.e("Item id [" + item.getItemId() + "] is not good");
                }
                binding.drawerLayoutActivityGuest.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

}