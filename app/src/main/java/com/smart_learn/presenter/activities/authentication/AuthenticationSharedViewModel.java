package com.smart_learn.presenter.activities.authentication;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.smart_learn.R;
import com.smart_learn.presenter.activities.authentication.helpers.LoginForm;
import com.smart_learn.presenter.activities.authentication.helpers.RegisterForm;
import com.smart_learn.presenter.helpers.ApplicationController;
import com.smart_learn.presenter.helpers.view_models.BasicAndroidViewModel;

import lombok.Getter;
import timber.log.Timber;

/** This SharedViewModel will be used to share login credentials between
 * EmailLoginFragment and EmailRegisterFragment for a better user experience. */
@Getter
public class AuthenticationSharedViewModel extends BasicAndroidViewModel {

    // used in fragments for checking fields length
    private final int maxPasswordLength;
    private final int maxEmailLength;

    private final MutableLiveData<LoginForm> liveLoginForm;

    public AuthenticationSharedViewModel(@NonNull Application application) {
        super(application);
        maxPasswordLength = RegisterForm.MAX_PASSWORD_LENGTH;
        maxEmailLength = RegisterForm.MAX_EMAIL_LENGTH;
        liveLoginForm = new MutableLiveData<>(new LoginForm(null, null));
    }

    public void sendVerificationEmail(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser == null){
            Timber.e("firebaseUser is null. Verification email NOT sent.");
            return;
        }

        firebaseUser.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Timber.i("Verification email sent to [" + firebaseUser.getEmail() + "]");
                            return;
                        }
                        Timber.e(task.getException(), "Verification email NOT sent to [" + firebaseUser.getEmail() + "]");
                    }
                });
    }

    public void signInWithGoogle(String idToken, AuthenticationActivity activity) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // everything is ok so mark login
                            SharedPreferences preferences = activity.getSharedPreferences(ApplicationController.LOGIN_STATUS_KEY, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putBoolean(ApplicationController.LOGGED_IN, true);
                            editor.apply();

                            activity.goToMainActivity();
                            return;
                        }

                        // some error occurred
                        liveToastMessage.setValue(activity.getString(R.string.error_google_login_failed));
                        Timber.e(task.getException());
                    }
                });
    }

}
