package com.smart_learn.presenter.activities.authentication;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.smart_learn.R;
import com.smart_learn.core.utilities.GeneralUtilities;
import com.smart_learn.databinding.ActivityAuthenticationBinding;
import com.smart_learn.presenter.activities.main.MainActivity;
import com.smart_learn.presenter.helpers.LoadingDialog;

import timber.log.Timber;

public class AuthenticationActivity extends AppCompatActivity {

    private static final int GOOGLE_SIGN_IN = 9001;
    private AuthenticationSharedViewModel sharedViewModel;
    private DialogFragment loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityAuthenticationBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_authentication);
        binding.setLifecycleOwner(this);

        setSupportActionBar(binding.toolbarAuthenticationActivity);

        setViewModel();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onStop() {
        dismissLoadingDialog();
        super.onStop();
    }

    /** Used for resetting toolbar title, from fragments. */
    public void resetToolbar(String title){
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setTitle(title);
        }
    }

    public void showLoadingDialog(String tag, String message){
        loadingDialog = new LoadingDialog(message);
        loadingDialog.show(getSupportFragmentManager(), tag);
    }

    public void dismissLoadingDialog(){
        if(loadingDialog != null){
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }

    public void goToMainActivity(){
        startActivity(new Intent(this, MainActivity.class));
        this.finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...)
        if (requestCode == GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if(account == null){
                    GeneralUtilities.showShortToastMessage(this, getString(R.string.google_login_failed));
                    return;
                }

                // Google Sign In was successful ==> authenticate with Firebase
                sharedViewModel.signInWithGoogle(account.getIdToken(), this);

            } catch (ApiException e) {
                GeneralUtilities.showShortToastMessage(this, getString(R.string.google_login_failed));
                Timber.e(e);
            }
        }
    }

    public void signInWithGoogle() {
        // configure Google Sign In
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Check for existing Google Sign In account. If the user is already signed in,
        // the GoogleSignInAccount will be non-null. This check is necessary for showing the dialog
        // with google accounts every time when login is made.
        // https://developers.google.com/identity/sign-in/android/sign-in
        // https://stackoverflow.com/questions/38707133/google-firebase-sign-out-and-forget-user-in-android-app
        //
        // Obs: App never should enter here, because when sign out is made, sign out from google client is made also.
        // But double check it.
        if (GoogleSignIn.getLastSignedInAccount(this) == null){

            Timber.e("GoogleSignIn.getLastSignedInAccount is NOT null");

            GoogleSignIn.getClient(this, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()).signOut()
                    .addOnCompleteListener(AuthenticationActivity.this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Timber.i("GoogleSignInClient signed out successfully");
                                // start logging process
                                startGoogleIntent(googleSignInOptions);
                                return;
                            }

                            // previous account can not be signed out ==> abort connexion
                            GeneralUtilities.showShortToastMessage(AuthenticationActivity.this, getString(R.string.google_login_failed));
                            Timber.w(task.getException());
                        }
                    });

            return;
        }

        // everything was ok
        startGoogleIntent(googleSignInOptions);
    }

    private void startGoogleIntent(GoogleSignInOptions googleSignInOptions){
        Intent intent = GoogleSignIn.getClient(this, googleSignInOptions).getSignInIntent();
        startActivityForResult(intent, GOOGLE_SIGN_IN);
    }

    private void setViewModel(){
        sharedViewModel = new ViewModelProvider(this).get(AuthenticationSharedViewModel.class);
        sharedViewModel.getLiveToastMessage().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                GeneralUtilities.showShortToastMessage(AuthenticationActivity.this, s);
            }
        });
    }
}