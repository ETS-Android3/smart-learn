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
import androidx.navigation.NavController;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.smart_learn.R;
import com.smart_learn.core.utilities.CoreUtilities;
import com.smart_learn.core.utilities.GeneralUtilities;
import com.smart_learn.databinding.ActivityMainBinding;
import com.smart_learn.presenter.activities.guest.GuestActivity;
import com.smart_learn.presenter.helpers.Utilities;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!CoreUtilities.Auth.isUserLoggedIn()){
            // user is not logged in ==> redirect it to the guest activity
            goToGuestActivity();
            return;
        }

        // user is logged in ==> do the normal stuff for this activity
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        binding.setLifecycleOwner(this);

        setSupportActionBar(binding.toolbarActivityMain);

        setLayoutUtilities();

        // if navigation graph cannot be set, then stop activity
        if(navController == null){
            GeneralUtilities.showShortToastMessage(this, getString(R.string.error_loading_screen));
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
        // main activity is home activity so check 'Home' from the navigation menu
        binding.navigationViewActivityMain.setCheckedItem(R.id.nav_home_menu_nav_drawer_activity_main);
    }

    @Override
    public void onBackPressed() {
        // disable back from main activity because is not necessary
        // super.onBackPressed();
        if(binding.drawerLayoutActivityMain.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayoutActivityMain.closeDrawer(GravityCompat.START);
        }
    }

    private void setLayoutUtilities(){
        // try to set navigation graph
        navController = Utilities.Activities.setNavigationGraphWithBottomMenu(this, R.id.nav_host_fragment_activity_main,
                binding.bottomNavigationActivityMain, null, null);
    }

    private void setNavigationDrawer(){
        // for this activity status bar should be transparent in order to show a full navigation drawer
        getWindow().setStatusBarColor(getResources().getColor(R.color.transparent,
                new ContextThemeWrapper(getBaseContext(), R.style.AppTheme).getTheme()));

        // set navigation drawer
        // https://www.youtube.com/watch?v=HwYENW0RyY4
        binding.navigationViewActivityMain.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.drawerLayoutActivityMain, binding.toolbarActivityMain,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayoutActivityMain.addDrawerListener(toggle);
        toggle.syncState();

        // set on menu item listener
        binding.navigationViewActivityMain.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_home_menu_nav_drawer_activity_main:
                        break;
                    case R.id.nav_logout_menu_nav_drawer_activity_main:
                        signOut();
                        break;
                    case R.id.nav_account_menu_nav_drawer_activity_main:
                    case R.id.nav_community_menu_nav_drawer_activity_main:
                    case R.id.nav_lessons_menu_nav_drawer_activity_main:
                    case R.id.nav_test_menu_nav_drawer_activity_main:
                    case R.id.nav_settings_menu_nav_drawer_activity_main:
                    case R.id.nav_help_menu_nav_drawer_activity_main:
                        break;
                    default:
                        Timber.e("Item id [" + item.getItemId() + "] is not good");
                }
                binding.drawerLayoutActivityMain.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    public void goToGuestActivity(){
        startActivity(new Intent(this, GuestActivity.class));
        this.finish();
    }

    private void signOut(){
        // Firebase sign out
        FirebaseAuth.getInstance().signOut();

        // If login provider was google, then sign out from Google client also. This is necessary for
        // showing the dialog with google accounts every time when login is made.
        // https://developers.google.com/identity/sign-in/android/sign-in
        // https://stackoverflow.com/questions/38707133/google-firebase-sign-out-and-forget-user-in-android-app
        if (GoogleSignIn.getLastSignedInAccount(this) != null){
            GoogleSignIn.getClient(this, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()).signOut()
                    .addOnCompleteListener(MainActivity.this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Timber.i("GoogleSignInClient signed out successfully");
                                return;
                            }

                            Timber.w(task.getException());
                        }
                    });
        }

        goToGuestActivity();
    }
}