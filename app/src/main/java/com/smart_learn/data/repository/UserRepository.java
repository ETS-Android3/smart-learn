package com.smart_learn.data.repository;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.smart_learn.core.services.UserService;
import com.smart_learn.core.services.helpers.ThreadExecutorService;
import com.smart_learn.data.firebase.firestore.entities.UserDocument;
import com.smart_learn.data.firebase.firestore.entities.helpers.BasicProfileDocument;
import com.smart_learn.data.firebase.firestore.entities.helpers.DocumentMetadata;
import com.smart_learn.data.firebase.firestore.repository.BasicFirestoreRepository;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.data.helpers.DataUtilities;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import lombok.Getter;
import timber.log.Timber;


public class UserRepository extends BasicFirestoreRepository<UserDocument> {

    private static UserRepository instance;
    @Getter
    private final CollectionReference usersCollectionReference;

    private UserRepository() {
        usersCollectionReference = FirebaseFirestore.getInstance().collection(COLLECTION_USERS);
    }

    public static UserRepository getInstance() {
        if(instance == null){
            instance = new UserRepository();
        }
        return instance;
    }

    public String getUserUid(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser == null){
            throw new UnsupportedOperationException("User is not logged in and no UID exists");
        }
        return firebaseUser.getUid();
    }

    public String getUserEmail(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser == null){
            throw new UnsupportedOperationException("User is not logged in and no email exists");
        }
        if(firebaseUser.getEmail() == null){
            throw new UnsupportedOperationException("User email does not exists");
        }
        return firebaseUser.getEmail();
    }

    public String getUserDisplayName(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser == null){
            throw new UnsupportedOperationException("User is not logged in and no display name exists");
        }
        return firebaseUser.getDisplayName() == null ? "" : firebaseUser.getDisplayName();
    }

    public String getUserProfilePhotoUrl(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser == null){
            throw new UnsupportedOperationException("User is not logged in and no photo url exists");
        }
        return firebaseUser.getPhotoUrl() == null ? "" : firebaseUser.getPhotoUrl().toString();
    }

    @Nullable
    public Uri getUserProfilePhotoUri(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser == null){
            throw new UnsupportedOperationException("User is not logged in and no photo exists");
        }
        return firebaseUser.getPhotoUrl();
    }

    public DocumentReference getUserDocumentReference(){
        return usersCollectionReference.document(getUserUid());
    }

    public DocumentReference getSpecificUserDocumentReference(@NonNull @NotNull String userUid){
        return usersCollectionReference.document(userUid);
    }

    private StorageReference getProfilePhotosStorageReference(){
        return FirebaseStorage.getInstance().getReference(FOLDER_PROFILE_PHOTOS);
    }

    public void createInitialAccountDocument(@NonNull @NotNull UserDocument document,
                                             @NonNull @NotNull DataCallbacks.General callback){
        usersCollectionReference
                .document(document.getDocumentMetadata().getOwner())
                .set(document)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if(task.isSuccessful()){
                            callback.onSuccess();
                            return;
                        }

                        // here add operation failed
                        Timber.w(task.getException());
                        callback.onFailure();
                    }
                });

    }

    public void searchUserByEmail(@NonNull @NotNull String email, @NonNull @NotNull DataCallbacks.SearchUserCallback callback) {
        usersCollectionReference
                .whereEqualTo(BasicProfileDocument.Fields.EMAIL_FIELD_NAME, email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if(DataUtilities.Firestore.notGoodBasicResultConfiguration(task)){
                            callback.onFailure();
                            return;
                        }

                        List<DocumentSnapshot> documentSnapshotList = Objects.requireNonNull(task.getResult()).getDocuments();
                        if(documentSnapshotList.isEmpty()){
                            callback.onFailure();
                            Timber.w("documentSnapshotList is empty");
                            return;
                        }

                        // for same email always maxim one user should be found
                        if(documentSnapshotList.size() > 1){
                            callback.onFailure();
                            Timber.e("Find more users with same email [" + email + "]");
                            return;
                        }

                        DocumentSnapshot secondUserSnapshot = documentSnapshotList.get(0);
                        UserDocument queryUser = secondUserSnapshot.toObject(UserDocument.class);
                        if(queryUser == null){
                            callback.onFailure();
                            Timber.w("queryUser is null");
                            return;
                        }

                        // Here queryUser was found.
                        // Check if queryUser is in currentUser UID lists.
                        checkCurrentUserListsRelatedToUid(queryUser, secondUserSnapshot, callback);
                    }
                });
    }

    /**
     * Check if queryUser is in currentUser pending list or in currentUser friends list or in current
     * user request received list.
     * */
    private void checkCurrentUserListsRelatedToUid(@NonNull @NotNull UserDocument secondUser,
                                                   @NonNull @NotNull DocumentSnapshot secondUserSnapshot,
                                                   @NonNull @NotNull DataCallbacks.SearchUserCallback callback){
        getUserDocumentReference()
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                        if(DataUtilities.Firestore.notGoodBasicResultConfiguration(task)){
                            callback.onFailure();
                            return;
                        }

                        UserDocument currentUser = Objects.requireNonNull(task.getResult()).toObject(UserDocument.class);
                        if(currentUser == null){
                            callback.onFailure();
                            Timber.w("currentUser is null");
                            return;
                        }

                        boolean isReceivedRequest = false;
                        for(String receivedUid : currentUser.getReceivedRequest()){
                            if(receivedUid.equals(secondUser.getDocumentMetadata().getOwner())){
                                isReceivedRequest = true;
                                break;
                            }
                        }

                        boolean isPending = false;
                        for(String pendingUid : currentUser.getPendingFriends()){
                            if(pendingUid.equals(secondUser.getDocumentMetadata().getOwner())){
                                isPending = true;
                                break;
                            }
                        }

                        boolean isFriend = false;
                        for(String friendUid : currentUser.getFriends()){
                            if(friendUid.equals(secondUser.getDocumentMetadata().getOwner())){
                                isFriend = true;
                                break;
                            }
                        }

                        callback.onSuccess(secondUserSnapshot, isPending, isFriend, isReceivedRequest);
                    }
        });
    }

    public void updateUserPhotoUrl(@NonNull @NotNull String photoUrl, @NonNull @NotNull DataCallbacks.General callback){
        HashMap<String, Object> data = new HashMap<>();
        data.put(BasicProfileDocument.Fields.PROFILE_PHOTO_URL_FIELD_NAME, photoUrl);
        data.put(DocumentMetadata.Fields.COMPOSED_MODIFIED_AT_FIELD_NAME, System.currentTimeMillis());
        getUserDocumentReference().update(data);
        // TODO: same problem as in batchCommit method
        callback.onSuccess();
    }

    public void uploadProfileImage(Uri profileImage, String imageName, DataCallbacks.General callback){
        ThreadExecutorService.getInstance().execute(() -> tryToUploadProfileImage(profileImage, imageName, callback));
    }

    public void tryToUploadProfileImage(Uri profileImage, String imageName, DataCallbacks.General callback){
        // https://www.youtube.com/watch?v=gqIWrNitbbk&ab_channel=CodinginFlow
        final StorageReference imageReference = getProfilePhotosStorageReference().child(imageName);
        imageReference
                .putFile(profileImage)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Here image was loaded on Firebase Storage, so update user document profile
                        // image url. First get photo location url.
                        // https://stackoverflow.com/questions/37374868/how-to-get-url-from-firebase-storage-getdownloadurl/54696675#54696675
                        imageReference.getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        // And then update user document and user account.
                                        continueWithSavingPhotoUrlOnUserAccount(uri, callback);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        callback.onFailure();
                                        Timber.w(e);
                                    }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onFailure();
                        Timber.w(e);
                    }
                });
    }

    private void continueWithSavingPhotoUrlOnUserAccount(Uri profileImage, DataCallbacks.General callback){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser == null){
            Timber.e("firebaseUser is null");
            // Even if photo is uploaded on storage, if url is not updated
            // on user document, photo can not be retrieved. So call onFailure()
            // in order to make user to try again the upload.
            callback.onFailure();
            return;
        }

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(profileImage)
                .build();

        firebaseUser
                .updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(!task.isSuccessful()){
                            // Even if photo is uploaded on storage, if url is not updated
                            // on user document, photo can not be retrieved. So call onFailure()
                            // in order to make user to try again the upload.
                            callback.onFailure();
                            return;
                        }

                        // Here account update was successfully so update profilePhotoUrl on user
                        // document. This will be used to show photo to the other users.
                        UserService.getInstance().updateUserPhotoUrl(profileImage.toString(), new DataCallbacks.General() {
                            @Override
                            public void onSuccess() {
                                // Here photo is loaded and url was updated on user account and
                                // user document.
                                callback.onSuccess();
                            }

                            @Override
                            public void onFailure() {
                                // Even if photo is uploaded on storage, if url is not updated
                                // on user document, photo can not be retrieved. So call onFailure()
                                // in order to make user to try again the upload.
                                callback.onFailure();
                            }
                        });
                    }
                });

    }

}
