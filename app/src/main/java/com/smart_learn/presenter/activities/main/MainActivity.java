package com.smart_learn.presenter.activities.main;

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
import com.smart_learn.R;
import com.smart_learn.databinding.ActivityMainBinding;
import com.smart_learn.presenter.activities.authentication.AuthenticationActivity;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        binding.setLifecycleOwner(this);

        setSupportActionBar(binding.toolbarMainActivity);
        setNavigationDrawer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // main activity is home activity so check 'Home' from the navigation menu
        binding.navigationViewMainActivity.setCheckedItem(R.id.nav_home);
    }

    @Override
    public void onBackPressed() {
        // on back pressed is disabled for main activity
        // super.onBackPressed();
        if(binding.drawerLayoutMainActivity.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayoutMainActivity.closeDrawer(GravityCompat.START);
        }
    }

    private void setNavigationDrawer(){
        // for this activity status bar should be transparent in order to show a full navigation drawer
        getWindow().setStatusBarColor(getResources().getColor(R.color.transparent,
                new ContextThemeWrapper(getBaseContext(), R.style.AppTheme).getTheme()));

        // set navigation drawer
        // https://www.youtube.com/watch?v=HwYENW0RyY4
        binding.navigationViewMainActivity.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.drawerLayoutMainActivity, binding.toolbarMainActivity,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayoutMainActivity.addDrawerListener(toggle);
        toggle.syncState();

        // set on menu item listener
        binding.navigationViewMainActivity.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_home:
                        break;
                    case R.id.nav_login:
                        startActivity(new Intent(MainActivity.this, AuthenticationActivity.class));
                        break;
                    case R.id.nav_account:
                    case R.id.nav_community:
                    case R.id.nav_lessons:
                    case R.id.nav_test:
                    case R.id.nav_settings:
                    case R.id.nav_help:
                        break;
                    default:
                        Timber.e("Item id [" + item.getItemId() + "] is not good");
                }
                binding.drawerLayoutMainActivity.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

}