package com.smart_learn.presenter.activities.authentication;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.smart_learn.presenter.activities.authentication.helpers.LoginForm;
import com.smart_learn.presenter.activities.authentication.helpers.RegisterForm;

import lombok.Getter;
import timber.log.Timber;

/** This SharedViewModel will be used to share login credentials between
 * LoginFragment and RegisterFragment for a better user experience. */
@Getter
public class AuthenticationSharedViewModel extends AndroidViewModel {

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

}
