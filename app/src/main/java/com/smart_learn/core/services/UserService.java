package com.smart_learn.core.services;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentReference;
import com.smart_learn.data.firebase.firestore.entities.UserDocument;
import com.smart_learn.data.firebase.firestore.entities.helpers.DocumentMetadata;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.data.helpers.DataUtilities;
import com.smart_learn.data.repository.UserRepository;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import timber.log.Timber;

public class UserService extends BasicFirestoreService<UserDocument, UserRepository>{

    private static UserService instance;

    private UserService() {
        super(UserRepository.getInstance());
    }

    public static UserService getInstance() {
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
        return repositoryInstance.getUserPhotoUrl();
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

}
