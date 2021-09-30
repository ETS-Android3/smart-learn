package com.smart_learn.presenter.activities.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.smart_learn.R;
import com.smart_learn.core.services.UserService;
import com.smart_learn.core.helpers.CoreUtilities;
import com.smart_learn.data.firebase.firestore.entities.UserDocument;
import com.smart_learn.databinding.ActivityMainBinding;
import com.smart_learn.databinding.LayoutNavHeaderActivityMainBinding;
import com.smart_learn.presenter.activities.community.CommunityActivity;
import com.smart_learn.presenter.activities.guest.GuestActivity;
import com.smart_learn.presenter.activities.notebook.user.UserNotebookActivity;
import com.smart_learn.presenter.activities.settings.UserSettingsActivity;
import com.smart_learn.presenter.activities.test.user.UserTestActivity;
import com.smart_learn.presenter.helpers.activities.BasicActivity;
import com.smart_learn.presenter.helpers.PresenterCallbacks;
import com.smart_learn.presenter.helpers.PresenterUtilities;

import timber.log.Timber;

public class MainActivity extends BasicActivity {

    public static final String CALLED_BY_PUSH_NOTIFICATION_KEY = "CALLED_BY_PUSH_NOTIFICATION_KEY";

    private ActivityMainBinding binding;
    private NavController navController;
    private BadgeDrawable unreadNotificationsBadge;

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
            PresenterUtilities.General.showShortToastMessage(this, getString(R.string.error_loading_screen));
            // If drawer cannot be opened finish activity and close application, because navigation
            // cannot be done. I close application here because on this activity onBackPressed() is
            // disabled. So this is the last point.
            // https://stackoverflow.com/questions/17719634/how-to-exit-an-android-app-programmatically/40231289#40231289
            this.finishAndRemoveTask();
            return;
        }

        setNavigationDrawer();

        // check if is called by push notification
        Bundle args = getIntent().getExtras();
        if(args != null && args.getBoolean(CALLED_BY_PUSH_NOTIFICATION_KEY)){
            processPushNotification();
            //return;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        UserService.getInstance()
                .getUserDocumentReference()
                .addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Timber.e(error);
                            return;
                        }

                        if (value == null) {
                            Timber.w("value is null");
                            return;
                        }

                        UserDocument userDocument = value.toObject(UserDocument.class);
                        if (userDocument == null) {
                            Timber.i("userDocument is null");
                            return;
                        }

                        setNotificationsBadgeNumber((int)userDocument.getNrOfUnreadNotifications());
                    }
                });
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
        navController = PresenterUtilities.Activities.setNavigationGraphWithBottomMenu(this, R.id.nav_host_fragment_activity_main,
                binding.bottomNavigationActivityMain, null, null);

        // set badge for notifications (by default will be hidden in order to avoid showing an empty
        // badge if no unread notifications exists)
        // https://material.io/develop/android/components/badging
        unreadNotificationsBadge = binding.bottomNavigationActivityMain.getOrCreateBadge(R.id.notifications_fragment_nav_graph_activity_main);
        setNotificationsBadgeNumber(0);
    }

    private void setNotificationsBadgeNumber(int unreadNotifications){
        if(unreadNotificationsBadge != null){
            if(unreadNotifications > 0){
                unreadNotificationsBadge.setVisible(true);
                unreadNotificationsBadge.setNumber(unreadNotifications);
            }
            else {
                unreadNotificationsBadge.setVisible(false);
                unreadNotificationsBadge.clearNumber();
            }
        }
    }

    private void setNavigationDrawer(){
        // set navigation drawer
        // https://www.youtube.com/watch?v=HwYENW0RyY4
        binding.navigationViewActivityMain.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.drawerLayoutActivityMain, binding.toolbarActivityMain,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayoutActivityMain.addDrawerListener(toggle);
        toggle.syncState();

        // set binding for navigation drawer header
        // https://stackoverflow.com/questions/33962548/how-to-data-bind-to-a-header/39741990#39741990
        LayoutNavHeaderActivityMainBinding navBinding = DataBindingUtil.inflate(getLayoutInflater(),
                R.layout.layout_nav_header_activity_main, binding.navigationViewActivityMain, false);
        binding.navigationViewActivityMain.addHeaderView(navBinding.getRoot());
        PresenterUtilities.Activities.loadProfileImage(UserService.getInstance().getUserPhotoUri(), navBinding.ivProfileLayoutNavHeaderActivityMain);
        navBinding.setUserHelloMessage(this.getString(R.string.hi) + " " + UserService.getInstance().getUserDisplayName());

        // set on menu item listener
        binding.navigationViewActivityMain.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_home_menu_nav_drawer_activity_main:
                        break;
                    case R.id.nav_community_menu_nav_drawer_activity_main:
                        goToCommunityActivity();
                        break;
                    case R.id.nav_lessons_menu_nav_drawer_activity_main:
                        startActivity(new Intent(MainActivity.this, UserNotebookActivity.class));
                        break;
                    case R.id.nav_test_menu_nav_drawer_activity_main:
                        startActivity(new Intent(MainActivity.this, UserTestActivity.class));
                        break;
                    case R.id.nav_settings_menu_nav_drawer_activity_main:
                        startActivity(new Intent(MainActivity.this, UserSettingsActivity.class));
                        break;
                    case R.id.nav_logout_menu_nav_drawer_activity_main:
                        PresenterUtilities.Activities.showStandardAlertDialog(
                                MainActivity.this,
                                MainActivity.this.getString(R.string.logout),
                                MainActivity.this.getString(R.string.logout_message),
                                MainActivity.this.getString(R.string.logout),
                                new PresenterCallbacks.StandardAlertDialogCallback() {
                                    @Override
                                    public void onPositiveButtonPress() {
                                        signOut();
                                    }

                                    @Override
                                    public void onNegativeButtonPress() {
                                        // When logout is pressed menu item corresponding to the logout will be checked,
                                        // so check back so check 'Home'  from the navigation menu.
                                        binding.navigationViewActivityMain.setCheckedItem(R.id.nav_home_menu_nav_drawer_activity_main);
                                    }
                                }
                        );
                        // Return from function because is no need to close drawer.
                        return true;
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

    private void goToCommunityActivity(){
        startActivity(new Intent(this, CommunityActivity.class));
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

    private void processPushNotification(){
        // processing push notification means only to navigate to the notifications center
        navController.navigate(R.id.notifications_fragment_nav_graph_activity_main, null, null);
    }
}