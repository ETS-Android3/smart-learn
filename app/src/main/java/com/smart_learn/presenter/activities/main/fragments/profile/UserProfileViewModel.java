package com.smart_learn.presenter.activities.main.fragments.profile;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.smart_learn.R;
import com.smart_learn.core.helpers.CoreUtilities;
import com.smart_learn.presenter.activities.authentication.helpers.RegisterForm;
import com.smart_learn.presenter.helpers.ApplicationController;
import com.smart_learn.presenter.helpers.view_models.BasicAndroidViewModel;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import timber.log.Timber;

@Getter
public class UserProfileViewModel extends BasicAndroidViewModel {

    private int maxProfileName;
    private int providerId;
    private MutableLiveData<String> liveProfileName;
    private String previousProfileName;
    private String email;
    private String provider;
    private String registerTime;

    public UserProfileViewModel(@NonNull @NotNull Application application) {
        super(application);
        setInitialValues(application);
    }

    private void setInitialValues(Application application){
        maxProfileName = RegisterForm.MAX_PROFILE_LENGTH;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null) {
            liveProfileName = new MutableLiveData<>("");
            email = "";
            provider = "";
            registerTime = "";
            return;
        }

        // user was not null ==> set values
        liveProfileName = new MutableLiveData<>(user.getDisplayName());
        email = user.getEmail();

        // set provider
        providerId = CoreUtilities.Auth.getProvider();
        switch(providerId){
            case CoreUtilities.Auth.PROVIDER_EMAIL:
                provider = application.getResources().getString(R.string.email_and_password);
                break;
            case CoreUtilities.Auth.PROVIDER_GOOGLE:
                provider = "Google";
                break;
            default:
                provider = "";
                break;
        }

        // set account creation time
        if(user.getMetadata() != null){
            registerTime = CoreUtilities.General.longToDate(user.getMetadata().getCreationTimestamp());
        }
        else{
            registerTime = "";
        }
    }

    protected boolean goodProfileName(TextInputLayout textInputLayout) {
        String profileName = liveProfileName.getValue();
        if(profileName == null || profileName.isEmpty()){
            textInputLayout.setError(ApplicationController.getInstance().getString(R.string.error_required));
            return false;
        }

        if(profileName.equals(previousProfileName)){
            textInputLayout.setError(ApplicationController.getInstance().getString(R.string.error_profile_name_is_same));
            return false;
        }

        // This check is already made in edit text field and never should enter here, but double check it.
        if(profileName.length() > maxProfileName){
            textInputLayout.setError(ApplicationController.getInstance().getString(R.string.error_profile_too_long));
            return false;
        }

        textInputLayout.setError(null);
        return true;
    }

    protected void savePreviousProfileName(){
        String profileName = liveProfileName.getValue();
        if(profileName == null){
            previousProfileName = "";
            return;
        }
        previousProfileName = profileName;
    }

    protected void revertToPreviousProfileName(){
        liveProfileName.setValue(previousProfileName);
    }

    protected void saveProfileName(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser == null){
            liveProfileName.setValue(previousProfileName); // restore previous value
            liveToastMessage.setValue(ApplicationController.getInstance().getResources()
                    .getString(R.string.error_updating_user_display_name));
            Timber.w("firebaseUser is null");
            return;
        }

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(liveProfileName.getValue())
                .build();

        firebaseUser.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            liveToastMessage.setValue(ApplicationController.getInstance().getResources()
                                    .getString(R.string.success_display_name_updated));
                            return;
                        }

                        // profile update failed
                        liveProfileName.setValue(previousProfileName); // restore previous value

                        if(task.getException() == null){
                            liveToastMessage.setValue(ApplicationController.getInstance().getResources()
                                    .getString(R.string.error_updating_user_display_name));
                            Timber.e("task.getException() is null");
                            return;
                        }

                        // task exception was not null ==> try to exit gracefully
                        try {
                            throw task.getException();
                        } catch(FirebaseNetworkException e) {
                            liveToastMessage.setValue(ApplicationController.getInstance().getResources()
                                    .getString(R.string.error_no_internet_connection));
                        } catch(Exception e) {
                            liveToastMessage.setValue(ApplicationController.getInstance().getResources()
                                    .getString(R.string.error_updating_user_display_name));
                            Timber.w(e);
                        }
                    }
                });
    }

    protected void deleteAccount(UserProfileFragment fragment){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser == null){
            liveToastMessage.setValue(ApplicationController.getInstance().getResources()
                    .getString(R.string.error_deleting_user));
            Timber.w("firebaseUser is null");
            return;
        }

        firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if(task.isSuccessful()){
                    liveToastMessage.setValue(ApplicationController.getInstance().getResources()
                            .getString(R.string.success_account_deleted));
                    fragment.goToGuestActivity();
                    return;
                }

                // account deletion failed
                if(task.getException() == null){
                    liveToastMessage.setValue(ApplicationController.getInstance().getResources()
                            .getString(R.string.error_deleting_user));
                    Timber.e("task.getException() is null");
                    return;
                }

                // task exception was not null ==> try to exit gracefully
                try {
                    throw task.getException();
                } catch(FirebaseNetworkException e) {
                    liveToastMessage.setValue(ApplicationController.getInstance().getResources()
                            .getString(R.string.error_no_internet_connection));
                } catch (FirebaseAuthRecentLoginRequiredException e){
                    liveToastMessage.setValue(ApplicationController.getInstance().getResources()
                            .getString(R.string.error_sensitive_deletion));
                }
                catch(Exception e) {
                    liveToastMessage.setValue(ApplicationController.getInstance().getResources()
                            .getString(R.string.error_deleting_user));
                    Timber.w(e);
                }

            }
        });
    }


}
