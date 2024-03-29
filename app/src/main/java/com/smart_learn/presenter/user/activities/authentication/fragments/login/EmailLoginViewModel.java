package com.smart_learn.presenter.user.activities.authentication.fragments.login;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.smart_learn.R;
import com.smart_learn.core.common.helpers.ConnexionChecker;
import com.smart_learn.presenter.user.activities.authentication.AuthenticationActivity;
import com.smart_learn.presenter.user.activities.authentication.helpers.LoginForm;
import com.smart_learn.presenter.user.activities.authentication.helpers.RegisterForm;
import com.smart_learn.core.common.helpers.ApplicationController;
import com.smart_learn.presenter.common.view_models.BasicAndroidViewModel;

import timber.log.Timber;

public class EmailLoginViewModel extends BasicAndroidViewModel {

    public EmailLoginViewModel(@NonNull Application application) {
        super(application);
    }

    public void login(EmailLoginFragment fragment){
        // Network checking is enough because if internet is NOT available will handled at login time.
        if(!ConnexionChecker.isNetworkAvailable()){
            liveToastMessage.setValue(fragment.getString(R.string.error_no_network));
            return;
        }

        LoginForm loginForm = fragment.getSharedViewModel().getLiveLoginForm().getValue();
        if(loginForm == null){
            liveToastMessage.setValue(fragment.getString(R.string.error_login_failed));
            Timber.e("loginForm is null");
            return;
        }

        // check if the current config is ok
        if(!goodLoginCredentials(fragment, loginForm)){
            return;
        }

        // current config is ok ==> try to login
        // show a loading dialog while login
        ((AuthenticationActivity)fragment.requireActivity()).showLoadingDialog("EmailLoginFragment", fragment.getString(R.string.sign_in_user));

        // login user
        loginUser(fragment, loginForm.getEmail(), loginForm.getPassword());
    }

    private void loginUser(EmailLoginFragment fragment, String email, String password){
        FirebaseAuth firebaseAuthInstance = FirebaseAuth.getInstance();
        firebaseAuthInstance.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            // This never should happen. If login is successfully user must not be null.
                            if(firebaseUser == null){
                                liveToastMessage.setValue(fragment.getString(R.string.error_login_failed));
                                Timber.e("firebaseUser is null");
                                return;
                            }

                            // refresh user data
                            firebaseUser.reload();

                            // Check if user email address was verified. User can log in only if email was verified.
                            if(!firebaseUser.isEmailVerified()){
                                liveToastMessage.setValue(fragment.getString(R.string.error_email_not_verified));
                                ((AuthenticationActivity)fragment.requireActivity()).dismissLoadingDialog();

                                fragment.getSharedViewModel().sendVerificationEmail();

                                // User is logged in but email is not verified.
                                // Sign out user in order to force him to validate email and login properly.
                                FirebaseAuth.getInstance().signOut();
                                return;
                            }

                            // everything is ok
                            SharedPreferences preferences = fragment.requireActivity().getSharedPreferences(ApplicationController.LOGIN_STATUS_KEY, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putBoolean(ApplicationController.LOGGED_IN, true);
                            editor.apply();

                            ((AuthenticationActivity)fragment.requireActivity()).goToMainActivity();
                        }
                        else {
                            // here login failed
                            ((AuthenticationActivity)fragment.requireActivity()).dismissLoadingDialog();
                            if(task.getException() == null){
                                liveToastMessage.setValue(fragment.getString(R.string.error_login_failed));
                                Timber.e("task.getException() is null");
                                return;
                            }

                            // task exception was not null ==> try to exit gracefully
                            try {
                                throw task.getException();
                            } catch(FirebaseAuthInvalidCredentialsException e) {
                                liveToastMessage.setValue(fragment.getString(R.string.error_invalid_login_credentials));
                            } catch(FirebaseAuthInvalidUserException e) {
                                liveToastMessage.setValue(fragment.getString(R.string.error_user_not_exist));
                            } catch(FirebaseNetworkException e) {
                                liveToastMessage.setValue(fragment.getString(R.string.error_no_internet_connection));
                            } catch(FirebaseTooManyRequestsException e) {
                                liveToastMessage.setValue(fragment.getString(R.string.error_to_many_login_requests));
                            } catch(Exception e) {
                                liveToastMessage.setValue(fragment.getString(R.string.error_login_failed));
                                Timber.e(e);
                            }
                        }

                    }
                });
    }

    public void sendPasswordResetEmail(EmailLoginFragment fragment){
        LoginForm loginForm = fragment.getSharedViewModel().getLiveLoginForm().getValue();
        if(loginForm == null){
            liveToastMessage.setValue(fragment.getString(R.string.error_send_password_reset_email));
            Timber.e("loginForm is null");
            return;
        }

        if(!goodEmail(fragment, loginForm)){
            liveToastMessage.setValue(fragment.getString(R.string.error_invalid_email));
            return;
        }

        FirebaseAuth.getInstance().sendPasswordResetEmail(loginForm.getEmail())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            liveToastMessage.setValue(fragment.getString(R.string.success_send_password_reset_email));
                            return;
                        }

                        // reset email failed
                        ((AuthenticationActivity)fragment.requireActivity()).dismissLoadingDialog();
                        if(task.getException() == null){
                            liveToastMessage.setValue(fragment.getString(R.string.error_send_password_reset_email));
                            Timber.e("task.getException() is null");
                            return;
                        }

                        // task exception was not null ==> try to exit gracefully
                        try {
                            throw task.getException();
                        } catch(FirebaseAuthInvalidCredentialsException e) {
                            liveToastMessage.setValue(fragment.getString(R.string.error_invalid_email));
                        } catch(FirebaseNetworkException e) {
                            liveToastMessage.setValue(fragment.getString(R.string.error_no_internet_connection));
                        } catch(Exception e) {
                            liveToastMessage.setValue(fragment.getString(R.string.error_send_password_reset_email));
                            Timber.e(e);
                        }
                    }
                });
    }

    private boolean goodLoginCredentials(EmailLoginFragment fragment, LoginForm form){
        // leave these here because we need to call both functions in order to set proper errors
        boolean goodPassword = goodPassword(fragment, form);
        boolean goodEmail = goodEmail(fragment, form);

        return goodPassword && goodEmail;
    }

    private boolean goodEmail(EmailLoginFragment fragment, LoginForm form){
        // These checks are different from 'EmailRegisterFragment' because we don`t need to check everything.
        // Check only for null value and if length is too big. Firebase will handle the rest.

        if(TextUtils.isEmpty(form.getEmail())){
            fragment.getBinding().etEmailAddressFragmentEmailLogin.setError(fragment.getString(R.string.error_required));
            return false;
        }

        // This check is already made in edit text field and never should enter here, but double check it.
        if(form.getEmail().length() > RegisterForm.MAX_EMAIL_LENGTH){
            fragment.getBinding().etEmailAddressFragmentEmailLogin.setError(fragment.getString(R.string.error_email_too_long));
            return false;
        }

        fragment.getBinding().etEmailAddressFragmentEmailLogin.setError(null);
        return true;
    }

    private boolean goodPassword(EmailLoginFragment fragment, LoginForm form){
        // These checks are different from 'EmailRegisterFragment' because we don`t need to check everything.
        // Check only for null value and if length is too big. Firebase will handle the rest.

        if(TextUtils.isEmpty(form.getPassword())){
            fragment.getBinding().etPasswordFragmentEmailLogin.setError(fragment.getString(R.string.error_required));
            return false;
        }

        // This check is already made in edit text field and never should enter here, but double check it.
        if(form.getPassword().length() > RegisterForm.MAX_PASSWORD_LENGTH){
            String error = fragment.getString(R.string.error_password_too_long) + " " + RegisterForm.MAX_PASSWORD_LENGTH +
                    fragment.getString(R.string.characters) + " " + fragment.getString(R.string.are_allowed);
            fragment.getBinding().etPasswordFragmentEmailLogin.setError(error);
            return false;
        }

        fragment.getBinding().etPasswordFragmentEmailLogin.setError(null);
        return true;
    }
}

