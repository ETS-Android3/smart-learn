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
import com.smart_learn.core.helpers.ConnexionChecker;
import com.smart_learn.presenter.activities.authentication.AuthenticationActivity;
import com.smart_learn.presenter.activities.authentication.helpers.RegisterForm;
import com.smart_learn.core.helpers.ApplicationController;
import com.smart_learn.presenter.helpers.view_models.BasicAndroidViewModel;

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

        // FIXME: Profile name can not be added when register is made because user document
        //  update can not be done because email is not verified so use a default value. This value
        //  will not be transmitted to Firestore when account is created, will be used only to pass
        //  validation for profile.
        liveRegisterForm = new MutableLiveData<>(new RegisterForm("empty value", null, null,
                null, null));
    }

    public void register(EmailRegisterFragment fragment){
        // Network checking is enough because if internet is NOT available will handled at register time.
        if(!ConnexionChecker.isNetworkAvailable()){
            liveToastMessage.setValue(fragment.getString(R.string.error_no_network));
            return;
        }

        // this never should be null but double check it
        if(liveRegisterForm.getValue() == null){
            liveToastMessage.setValue(fragment.getString(R.string.error_register_failed));
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
            fragment.getBinding().etProfileFragmentEmailRegister.setError(fragment.getString(R.string.error_required));
            return false;
        }

        // This check is already made in edit text field and never should enter here, but double check it.
        if(form.getProfile().length() > RegisterForm.MAX_PROFILE_LENGTH){
            fragment.getBinding().etProfileFragmentEmailRegister.setError(fragment.getString(R.string.error_profile_too_long));
            return false;
        }

        fragment.getBinding().etProfileFragmentEmailRegister.setError(null);
        return true;
    }

    private boolean goodEmail(EmailRegisterFragment fragment, RegisterForm form){
        if(TextUtils.isEmpty(form.getEmail())){
            fragment.getBinding().etEmailAddressFragmentEmailRegister.setError(fragment.getString(R.string.error_required));
            return false;
        }

        // This check is already made in edit text field and never should enter here, but double check it.
        if(form.getEmail().length() > RegisterForm.MAX_EMAIL_LENGTH){
            fragment.getBinding().etEmailAddressFragmentEmailRegister.setError(fragment.getString(R.string.error_email_too_long));
            return false;
        }

        if(!RegisterForm.EMAIL_REGEX_PATTERN.matcher(form.getEmail()).matches()) {
            fragment.getBinding().etEmailAddressFragmentEmailRegister.setError(fragment.getString(R.string.error_email_not_valid));
            return false;
        }

        fragment.getBinding().etEmailAddressFragmentEmailRegister.setError(null);
        return true;
    }

    private boolean goodRetypedEmail(EmailRegisterFragment fragment, RegisterForm form){
        if(TextUtils.isEmpty(form.getRetypedEmail())){
            fragment.getBinding().etRetypedEmailAddressFragmentEmailRegister.setError(fragment.getString(R.string.error_required));
            return false;
        }

        // This check is already made in edit text field and never should enter here, but double check it.
        if(form.getRetypedEmail().length() > RegisterForm.MAX_EMAIL_LENGTH){
            fragment.getBinding().etRetypedEmailAddressFragmentEmailRegister.setError(fragment.getString(R.string.error_email_too_long));
            return false;
        }

        fragment.getBinding().etRetypedEmailAddressFragmentEmailRegister.setError(null);
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
            fragment.getBinding().etEmailAddressFragmentEmailRegister.setError(fragment.getString(R.string.error_emails_not_matching));
            fragment.getBinding().etRetypedEmailAddressFragmentEmailRegister.setError(fragment.getString(R.string.error_emails_not_matching));
            return false;
        }

        fragment.getBinding().etEmailAddressFragmentEmailRegister.setError(null);
        fragment.getBinding().etRetypedEmailAddressFragmentEmailRegister.setError(null);
        return true;
    }

    private boolean goodPassword(EmailRegisterFragment fragment, RegisterForm form){
        if(TextUtils.isEmpty(form.getPassword())){
            fragment.getBinding().etPasswordFragmentEmailRegister.setError(fragment.getString(R.string.error_required));
            return false;
        }

        if(form.getPassword().length() < RegisterForm.MIN_PASSWORD_LENGTH){
            String error = fragment.getString(R.string.error_password_too_short) + " " + RegisterForm.MIN_PASSWORD_LENGTH + " " +
                    fragment.getString(R.string.characters);
            fragment.getBinding().etPasswordFragmentEmailRegister.setError(error);
            return false;
        }

        // This check is already made in edit text field and never should enter here, but double check it.
        if(form.getPassword().length() > RegisterForm.MAX_PASSWORD_LENGTH){
            String error = fragment.getString(R.string.error_password_too_long) + " " + RegisterForm.MAX_PASSWORD_LENGTH +
                    fragment.getString(R.string.characters) + " " + fragment.getString(R.string.are_allowed);
            fragment.getBinding().etPasswordFragmentEmailRegister.setError(error);
            return false;
        }

        if(!RegisterForm.PASSWORD_REGEX_PATTERN.matcher(form.getPassword()).matches()) {
            fragment.getBinding().etPasswordFragmentEmailRegister.setError(fragment.getString(R.string.error_weak_password));
            return false;
        }

        fragment.getBinding().etPasswordFragmentEmailRegister.setError(null);
        return true;
    }

    private boolean goodRetypedPassword(EmailRegisterFragment fragment, RegisterForm form){
        if(TextUtils.isEmpty(form.getRetypedPassword())){
            fragment.getBinding().etRetypedPasswordFragmentEmailRegister.setError(fragment.getString(R.string.error_required));
            return false;
        }

        // This check is already made in edit text field and never should enter here, but double check it.
        if(form.getRetypedPassword().length() > RegisterForm.MAX_PASSWORD_LENGTH){
            String error = fragment.getString(R.string.error_password_too_long) + " " + RegisterForm.MAX_PASSWORD_LENGTH +
                    fragment.getString(R.string.characters) + " " + fragment.getString(R.string.are_allowed);
            fragment.getBinding().etRetypedPasswordFragmentEmailRegister.setError(error);
            return false;
        }

        fragment.getBinding().etRetypedPasswordFragmentEmailRegister.setError(null);
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
            fragment.getBinding().etPasswordFragmentEmailRegister.setError(fragment.getString(R.string.error_passwords_not_matching));
            fragment.getBinding().etRetypedPasswordFragmentEmailRegister.setError(fragment.getString(R.string.error_passwords_not_matching));
            return false;
        }

        fragment.getBinding().etPasswordFragmentEmailRegister.setError(null);
        fragment.getBinding().etRetypedPasswordFragmentEmailRegister.setError(null);
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
                            // FIXME: Profile can not be updated when register is made because user
                            //  document update can not be done because email is not yet verified.
                            // updateUserProfile(form);
                            fragment.getSharedViewModel().sendVerificationEmail();

                            fragment.requireActivity().onBackPressed();
                            liveToastMessage.setValue(ApplicationController.getInstance().getString(R.string.success_register_successfully));

                            // User is registered and that means use is logged in, but email is not verified.
                            // Sign out user in order to force him to validate email and login properly.
                            FirebaseAuth.getInstance().signOut();
                        }
                        else {
                            // here register failed
                            ((AuthenticationActivity)fragment.requireActivity()).dismissLoadingDialog();
                            if(task.getException() == null){
                                liveToastMessage.setValue(fragment.getString(R.string.error_register_failed));
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
                                liveToastMessage.setValue(fragment.getString(R.string.error_no_internet_connection));
                            } catch(Exception e) {
                                liveToastMessage.setValue(fragment.getString(R.string.error_register_failed));
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
