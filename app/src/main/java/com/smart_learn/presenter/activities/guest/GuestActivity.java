package com.smart_learn.presenter.activities.guest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.smart_learn.R;
import com.smart_learn.databinding.ActivityGuestBinding;
import com.smart_learn.presenter.activities.authentication.AuthenticationActivity;

import timber.log.Timber;

public class GuestActivity extends AppCompatActivity {

    private ActivityGuestBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_guest);
        binding.setLifecycleOwner(this);

        setSupportActionBar(binding.toolbarActivityGuest);
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

    private void setNavigationDrawer(){
        // for this activity status bar should be transparent in order to show a full navigation drawer
        getWindow().setStatusBarColor(getResources().getColor(R.color.transparent,
                new ContextThemeWrapper(getBaseContext(), R.style.AppTheme).getTheme()));

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
                    case R.id.nav_test_menu_nav_drawer_activity_guest:
                    case R.id.nav_settings_menu_nav_drawer_activity_guest:
                    case R.id.nav_help_menu_nav_drawer_activity_guest:
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