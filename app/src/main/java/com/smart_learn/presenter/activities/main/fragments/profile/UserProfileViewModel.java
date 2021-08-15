package com.smart_learn.presenter.activities.main.fragments.profile;

import android.app.Application;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.webkit.MimeTypeMap;

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
import com.smart_learn.core.helpers.ApplicationController;
import com.smart_learn.core.helpers.ConnexionChecker;
import com.smart_learn.core.helpers.CoreUtilities;
import com.smart_learn.core.services.UserService;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.presenter.activities.authentication.helpers.RegisterForm;
import com.smart_learn.presenter.helpers.view_models.BasicAndroidViewModel;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashSet;

import lombok.Getter;
import timber.log.Timber;

@Getter
public class UserProfileViewModel extends BasicAndroidViewModel {

    // size in bytes (in decimal)
    // https://www.gbmb.org/megabytes
    private static final long ONE_MB = 1000000;
    private static final long MAX_IMAGE_DIMENSION = 5 * ONE_MB;
    private static final String MAX_IMAGE_DIMENSION_DESCRIPTION = "5 MB";

    private int maxProfileName;
    private int providerId;
    private MutableLiveData<String> liveProfileName;
    private String previousProfileName;
    private String email;
    private String provider;
    private String registerTime;
    private MutableLiveData<Uri> liveProfilePhotoUri;

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
            liveProfilePhotoUri = new MutableLiveData<>(null);
            return;
        }

        // user was not null ==> set values
        liveProfilePhotoUri = new MutableLiveData<>(user.getPhotoUrl());
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

    protected void uploadImage(Uri profileImage, @NonNull @NotNull UserProfileFragment fragment){
        if(profileImage == null){
            liveToastMessage.setValue(fragment.getString(R.string.no_image_selected));
            return;
        }

        // Check if file is an image.
        // https://www.youtube.com/watch?v=gqIWrNitbbk&ab_channel=CodinginFlow
        // https://stackoverflow.com/questions/13760269/android-how-to-check-if-file-is-image/13760444
        String fileExtension = MimeTypeMap.getSingleton()
                .getExtensionFromMimeType(fragment.requireActivity().getContentResolver().getType(profileImage));

        HashSet<String> acceptedImagesFormat = new HashSet<>(Arrays.asList("jpg", "jpeg", "png", "bmp"));
        if(!acceptedImagesFormat.contains(fileExtension)){
            String error = fragment.getString(R.string.error_invalid_image_format_1) + " [" + fileExtension + "] " +
                    fragment.getString(R.string.error_invalid_image_format_2) + ". " +
                    fragment.getString(R.string.permitted_images_format) + " :jpg, jpeg, png, bmp";
            liveToastMessage.setValue(error);
            return;
        }

        // Check image dimension accept only images with max size of MAX_IMAGE_DIMENSION_DESCRIPTION.
        // https://stackoverflow.com/questions/29137003/how-to-check-image-size-less-then-100kb-android/51992147#51992147
        Cursor cursor = fragment.requireActivity().getContentResolver().query(profileImage, null, null, null, null);
        int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
        cursor.moveToFirst();
        long imageSize = cursor.getLong(sizeIndex);
        cursor.close();

        if(imageSize > MAX_IMAGE_DIMENSION){
            double sizeInMB =  (double)imageSize / (double)ONE_MB;
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            String error = fragment.getString(R.string.error_image_too_big_1) + " [" + decimalFormat.format(sizeInMB) + " MB]" +
                    fragment.getString(R.string.error_image_too_big_2) + " " + MAX_IMAGE_DIMENSION_DESCRIPTION;
            liveToastMessage.setValue(error);
            return;
        }

        // Here upload can start.

        fragment.showProgressDialog("",fragment.getString(R.string.upload_image));

        // for upload, internet is required
        new ConnexionChecker(new ConnexionChecker.Callback() {
            @Override
            public void isConnected() {
                fragment.requireActivity().runOnUiThread(() -> continueWithImageUpload(profileImage, fileExtension, fragment));
            }

            @Override
            public void networkDisabled() {
                liveToastMessage.postValue(fragment.getString(R.string.error_no_network));
            }

            @Override
            public void internetNotAvailable() {
                liveToastMessage.postValue(fragment.getString(R.string.error_no_internet_connection));
            }

            @Override
            public void notConnected() {
                fragment.requireActivity().runOnUiThread(fragment::closeProgressDialog);
            }
        }).check();
    }

    private void continueWithImageUpload(Uri profileImage, String fileExtension, @NonNull @NotNull UserProfileFragment fragment){

        // Every user will have a single profile photo, so if upload is made again old photo will
        // be overridden.
        String imageName = "user_" + UserService.getInstance().getUserUid() + "_profile_photo." + fileExtension;

        UserService.getInstance().uploadProfileImage(profileImage, imageName, new DataCallbacks.General() {
            @Override
            public void onSuccess() {
                fragment.requireActivity().runOnUiThread(() -> {
                    fragment.closeProgressDialog();
                    liveToastMessage.setValue(fragment.getString(R.string.success_upload_image));
                    fragment.loadProfileImage(UserService.getInstance().getUserPhotoUri());
                });
            }

            @Override
            public void onFailure() {
                fragment.requireActivity().runOnUiThread(() -> {
                    fragment.closeProgressDialog();
                    liveToastMessage.setValue(fragment.getString(R.string.error_upload_image));
                });
            }
        });

    }

}
