package com.smart_learn.core.user.services;

import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentReference;
import com.smart_learn.core.user.services.helpers.BasicFirestoreService;
import com.smart_learn.data.user.firebase.firestore.entities.UserDocument;
import com.smart_learn.data.user.firebase.firestore.entities.helpers.DocumentMetadata;
import com.smart_learn.data.common.helpers.DataCallbacks;
import com.smart_learn.data.common.helpers.DataUtilities;
import com.smart_learn.data.user.repository.UserRepository;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import timber.log.Timber;

public class UserService extends BasicFirestoreService<UserDocument, UserRepository> {

    private static UserService instance;

    private UserService() {
        super(UserRepository.getInstance());
    }

    public static synchronized UserService getInstance() {
        if(instance == null){
            instance = new UserService();
        }
        return instance;
    }

    public String getUserUid(){
        return repositoryInstance.getUserUid();
    }

    public String getUserEmail(){
        return repositoryInstance.getUserEmail();
    }

    public String getUserDisplayName(){
        return repositoryInstance.getUserDisplayName();
    }

    public String getUserPhotoUrl(){
        return repositoryInstance.getUserProfilePhotoUrl();
    }

    public Uri getUserPhotoUri(){
        return repositoryInstance.getUserProfilePhotoUri();
    }

    public DocumentReference getUserDocumentReference(){
        return repositoryInstance.getUserDocumentReference();
    }

    public DocumentReference getSpecificUserDocumentReference(String userUid){
        if(TextUtils.isEmpty(userUid)){
            throw new UnsupportedOperationException("userUid must not be null or empty");
        }
        return repositoryInstance.getSpecificUserDocumentReference(userUid);
    }

    public void createInitialAccountDocument(DataCallbacks.General callback){

        if(callback == null){
            callback = DataUtilities.General.generateGeneralCallback("Account for user UID [" + getUserUid() + "] created",
                    "Account for user UID [" + getUserUid() + "] NOT created");
        }

        UserDocument user = new UserDocument(
                new DocumentMetadata(getUserUid(), System.currentTimeMillis(), new ArrayList<>()),
                getUserEmail(),
                getUserDisplayName(),
                getUserPhotoUrl(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );

        repositoryInstance.createInitialAccountDocument(user, callback);
    }

    public void createInitialAccountDocument(String email, String displayName, String photoUrl, DataCallbacks.General callback){
        if(TextUtils.isEmpty(email)){
            throw new UnsupportedOperationException("email must not be null or empty");
        }

        if(callback == null){
            callback = DataUtilities.General.generateGeneralCallback("Account for user UID [" + getUserUid() + "] created",
                    "Account for user UID [" + getUserUid() + "] NOT created");
        }

        UserDocument user = new UserDocument(
                new DocumentMetadata(getUserUid(), System.currentTimeMillis(), new ArrayList<>()),
                email,
                displayName == null ? "" : displayName,
                photoUrl == null ? "" : photoUrl,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );

        repositoryInstance.createInitialAccountDocument(user, callback);
    }

    public void searchUserByEmail(String email, @NonNull @NotNull DataCallbacks.SearchUserCallback callback) {
        if(email == null){
            callback.onFailure();
            Timber.w("email is null");
            return;
        }
        repositoryInstance.searchUserByEmail(email, callback);
    }

    public void updateUserDocumentProfileName(String profileName, DataCallbacks.General callback){
        if(profileName == null || profileName.isEmpty()){
            if(callback != null){
                callback.onFailure();
            }
            Timber.w("Profile name can not be null or empty");
            return;
        }

        if(callback == null){
            callback = DataUtilities.General.generateGeneralCallback("Profile name [" + profileName + "] updated",
                    "Profile name [" + profileName + "] was NOT updated");
        }

        repositoryInstance.updateUserDocumentProfileName(profileName, callback);
    }

    public void updateUserDocumentPhotoUrl(String photoUrl, DataCallbacks.General callback){
        if(photoUrl == null){
            // empty value can be used to unset photo url
            photoUrl = "";
        }

        if(callback == null){
            callback = DataUtilities.General.generateGeneralCallback("PhotoUrl [" + photoUrl + "] updated",
                    "PhotoUrl [" + photoUrl + "] was NOT updated");
        }

        repositoryInstance.updateUserDocumentPhotoUrl(photoUrl, callback);
    }

    public void uploadProfileImage(Uri profileImage, String imageName, DataCallbacks.General callback){
        if(profileImage == null){
            if(callback != null){
                callback.onFailure();
            }
            Timber.w("profileImage is null");
            return;
        }

        if(callback == null){
            callback = DataUtilities.General.generateGeneralCallback("PhotoUrl [" + profileImage.toString() + "] uploaded",
                    "PhotoUrl [" + profileImage.toString() + "] was NOT uploaded");
        }

        if(imageName == null || imageName.isEmpty()){
            callback.onFailure();
            Timber.w("imageName is null or empty");
            return;
        }

        repositoryInstance.uploadProfileImage(profileImage, imageName, callback);
    }

}
