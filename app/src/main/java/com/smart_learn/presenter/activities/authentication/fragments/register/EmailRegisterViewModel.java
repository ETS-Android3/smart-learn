package com.smart_learn.presenter.activities.authentication.fragments.register;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.smart_learn.R;
import com.smart_learn.core.utilities.NetworkUtilities;
import com.smart_learn.presenter.activities.authentication.AuthenticationActivity;
import com.smart_learn.presenter.activities.authentication.helpers.RegisterForm;
import com.smart_learn.presenter.helpers.ApplicationController;
import com.smart_learn.presenter.helpers.BasicAndroidViewModel;

import lombok.Getter;
import timber.log.Timber;

@Getter
public class EmailRegisterViewModel extends BasicAndroidViewModel {

    // used in fragment for checking field length
    private final int maxProfileNameLength;

    private final MutableLiveData<RegisterForm> liveRegisterForm;

    public EmailRegisterViewModel(@NonNull Application application) {
        super(application);
        maxProfileNameLength = RegisterForm.MAX_PROFILE_LENGTH;
        liveRegisterForm = new MutableLiveData<>(new RegisterForm(null, null, null,
                null, null));
    }

    public void register(EmailRegisterFragment fragment){

        if(!NetworkUtilities.goodConnection()){
            liveToastMessage.setValue(fragment.getString(R.string.no_network));
            return;
        }

        // this never should be null but double check it
        if(liveRegisterForm.getValue() == null){
            liveToastMessage.setValue(fragment.getString(R.string.register_failed));
            Timber.e("liveRegisterForm.getValue() is null");
            return;
        }

        // update register with shared info`s and check if the current register form config is ok
        liveRegisterForm.getValue().updateFromLoginForm(fragment.getSharedViewModel().getLiveLoginForm().getValue());
        if(!goodRegisterCredentials(fragment, liveRegisterForm.getValue())){
            return;
        }

        // register form config is good
        // show a loading dialog while registering
        ((AuthenticationActivity)fragment.requireActivity()).showLoadingDialog("EmailRegisterFragment",
                fragment.getString(R.string.registering_user));

        // register user
        registerUser(fragment, liveRegisterForm.getValue());
    }

    private boolean goodRegisterCredentials(EmailRegisterFragment fragment, RegisterForm form){
        // leave these here because we need to call all 3 functions in order to set proper errors
        boolean goodProfile = goodProfile(fragment, form);
        boolean goodEmailConfiguration = goodEmailConfiguration(fragment, form);
        boolean goodPasswordConfiguration = goodPasswordConfiguration(fragment, form);

        return goodProfile && goodEmailConfiguration && goodPasswordConfiguration;
    }

    private boolean goodProfile(EmailRegisterFragment fragment, RegisterForm form){
        if(TextUtils.isEmpty(form.getProfile())){
            fragment.getBinding().etProfileEmailRegisterFragment.setError(fragment.getString(R.string.error_required));
            return false;
        }

        // This check is already made in edit text field and never should enter here, but double check it.
        if(form.getProfile().length() > RegisterForm.MAX_PROFILE_LENGTH){
            fragment.getBinding().etProfileEmailRegisterFragment.setError(fragment.getString(R.string.error_profile_too_long));
            return false;
        }

        fragment.getBinding().etProfileEmailRegisterFragment.setError(null);
        return true;
    }

    private boolean goodEmail(EmailRegisterFragment fragment, RegisterForm form){
        if(TextUtils.isEmpty(form.getEmail())){
            fragment.getBinding().etEmailAddressEmailRegisterFragment.setError(fragment.getString(R.string.error_required));
            return false;
        }

        // This check is already made in edit text field and never should enter here, but double check it.
        if(form.getEmail().length() > RegisterForm.MAX_EMAIL_LENGTH){
            fragment.getBinding().etEmailAddressEmailRegisterFragment.setError(fragment.getString(R.string.error_email_too_long));
            return false;
        }

        if(!RegisterForm.EMAIL_REGEX_PATTERN.matcher(form.getEmail()).matches()) {
            fragment.getBinding().etEmailAddressEmailRegisterFragment.setError(fragment.getString(R.string.email_not_valid));
            return false;
        }

        fragment.getBinding().etEmailAddressEmailRegisterFragment.setError(null);
        return true;
    }

    private boolean goodRetypedEmail(EmailRegisterFragment fragment, RegisterForm form){
        if(TextUtils.isEmpty(form.getRetypedEmail())){
            fragment.getBinding().etRetypedEmailAddressEmailRegisterFragment.setError(fragment.getString(R.string.error_required));
            return false;
        }

        // This check is already made in edit text field and never should enter here, but double check it.
        if(form.getRetypedEmail().length() > RegisterForm.MAX_EMAIL_LENGTH){
            fragment.getBinding().etRetypedEmailAddressEmailRegisterFragment.setError(fragment.getString(R.string.error_email_too_long));
            return false;
        }

        fragment.getBinding().etRetypedEmailAddressEmailRegisterFragment.setError(null);
        return true;
    }

    private boolean goodEmailConfiguration(EmailRegisterFragment fragment, RegisterForm form){
        // leave these here because we need to call both functions in order to set proper errors
        boolean goodEmail = goodEmail(fragment, form);
        boolean goodRetypedEmail = goodRetypedEmail(fragment, form);
        if(!goodEmail && !goodRetypedEmail){
            return false;
        }

        if(!form.getEmail().equals(form.getRetypedEmail())){
            fragment.getBinding().etEmailAddressEmailRegisterFragment.setError(fragment.getString(R.string.emails_not_matching));
            fragment.getBinding().etRetypedEmailAddressEmailRegisterFragment.setError(fragment.getString(R.string.emails_not_matching));
            return false;
        }

        fragment.getBinding().etEmailAddressEmailRegisterFragment.setError(null);
        fragment.getBinding().etRetypedEmailAddressEmailRegisterFragment.setError(null);
        return true;
    }

    private boolean goodPassword(EmailRegisterFragment fragment, RegisterForm form){
        if(TextUtils.isEmpty(form.getPassword())){
            fragment.getBinding().etPasswordEmailRegisterFragment.setError(fragment.getString(R.string.error_required));
            return false;
        }

        if(form.getPassword().length() < RegisterForm.MIN_PASSWORD_LENGTH){
            String error = fragment.getString(R.string.password_too_short) + " " + RegisterForm.MIN_PASSWORD_LENGTH + " " +
                    fragment.getString(R.string.characters);
            fragment.getBinding().etPasswordEmailRegisterFragment.setError(error);
            return false;
        }

        // This check is already made in edit text field and never should enter here, but double check it.
        if(form.getPassword().length() > RegisterForm.MAX_PASSWORD_LENGTH){
            String error = fragment.getString(R.string.password_too_long) + " " + RegisterForm.MAX_PASSWORD_LENGTH +
                    fragment.getString(R.string.characters) + " " + fragment.getString(R.string.are_allowed);
            fragment.getBinding().etPasswordEmailRegisterFragment.setError(error);
            return false;
        }

        if(!RegisterForm.PASSWORD_REGEX_PATTERN.matcher(form.getPassword()).matches()) {
            fragment.getBinding().etPasswordEmailRegisterFragment.setError(fragment.getString(R.string.error_weak_password));
            return false;
        }

        fragment.getBinding().etPasswordEmailRegisterFragment.setError(null);
        return true;
    }

    private boolean goodRetypedPassword(EmailRegisterFragment fragment, RegisterForm form){
        if(TextUtils.isEmpty(form.getRetypedPassword())){
            fragment.getBinding().etRetypedPasswordEmailRegisterFragment.setError(fragment.getString(R.string.error_required));
            return false;
        }

        // This check is already made in edit text field and never should enter here, but double check it.
        if(form.getRetypedPassword().length() > RegisterForm.MAX_PASSWORD_LENGTH){
            String error = fragment.getString(R.string.password_too_long) + " " + RegisterForm.MAX_PASSWORD_LENGTH +
                    fragment.getString(R.string.characters) + " " + fragment.getString(R.string.are_allowed);
            fragment.getBinding().etRetypedPasswordEmailRegisterFragment.setError(error);
            return false;
        }

        fragment.getBinding().etRetypedPasswordEmailRegisterFragment.setError(null);
        return true;
    }

    private boolean goodPasswordConfiguration(EmailRegisterFragment fragment, RegisterForm form){
        // leave these here because we need to call both functions in order to set proper errors
        boolean goodPassword = goodPassword(fragment, form);
        boolean goodRetypedPassword = goodRetypedPassword(fragment, form);
        if(!goodPassword && !goodRetypedPassword){
            return false;
        }

        if(!form.getPassword().equals(form.getRetypedPassword())){
            fragment.getBinding().etPasswordEmailRegisterFragment.setError(fragment.getString(R.string.passwords_not_matching));
            fragment.getBinding().etRetypedPasswordEmailRegisterFragment.setError(fragment.getString(R.string.passwords_not_matching));
            return false;
        }

        fragment.getBinding().etPasswordEmailRegisterFragment.setError(null);
        fragment.getBinding().etRetypedPasswordEmailRegisterFragment.setError(null);
        return true;
    }

    private void registerUser(EmailRegisterFragment fragment, RegisterForm form){
        FirebaseAuth firebaseAuthInstance = FirebaseAuth.getInstance();
        firebaseAuthInstance.createUserWithEmailAndPassword(form.getEmail(), form.getPassword())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // register was made ==> update user profile and send verification email
                            updateUserProfile(form);
                            fragment.getSharedViewModel().sendVerificationEmail();

                            fragment.requireActivity().onBackPressed();
                            liveToastMessage.setValue(ApplicationController.getInstance().getString(R.string.register_successfully));

                            // User is registered and that means use is logged in, but email is not verified.
                            // Sign out user in order to force him to validate email and login properly.
                            FirebaseAuth.getInstance().signOut();
                        }
                        else {
                            // here register failed
                            ((AuthenticationActivity)fragment.requireActivity()).dismissLoadingDialog();
                            if(task.getException() == null){
                                liveToastMessage.setValue(fragment.getString(R.string.register_failed));
                                Timber.e("task.getException() is null");
                                return;
                            }

                            // task exception was not null ==> try to exit gracefully
                            try {
                                throw task.getException();
                            } catch(FirebaseAuthWeakPasswordException e) {
                                liveToastMessage.setValue(fragment.getString(R.string.error_weak_password));
                            } catch(FirebaseAuthInvalidCredentialsException e) {
                                liveToastMessage.setValue(fragment.getString(R.string.error_invalid_email));
                            } catch(FirebaseAuthUserCollisionException e) {
                                liveToastMessage.setValue(fragment.getString(R.string.error_user_exists));
                            } catch(FirebaseNetworkException e) {
                                liveToastMessage.setValue(fragment.getString(R.string.no_internet_connection));
                            } catch(Exception e) {
                                liveToastMessage.setValue(fragment.getString(R.string.register_failed));
                                Timber.e(e);
                            }
                        }
                    }
                });
    }

    private void updateUserProfile(RegisterForm form){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser == null){
            Timber.e("firebaseUser is null");
            return;
        }

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(form.getProfile())
                .build();

        firebaseUser.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Timber.i("Profile updated [" + firebaseUser.getDisplayName() + "]");
                            return;
                        }
                        Timber.e(task.getException(), "Profile was NOT updated for account [" + firebaseUser.getEmail() + "]");
                    }
                });
    }

}
