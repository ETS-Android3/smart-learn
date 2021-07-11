package com.smart_learn.data.repository;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.smart_learn.data.firebase.firestore.entities.UserDocument;
import com.smart_learn.data.firebase.firestore.entities.helpers.BasicProfileDocument;
import com.smart_learn.data.firebase.firestore.repository.BasicFirestoreRepository;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.data.helpers.DataUtilities;

import org.jetbrains.annotations.NotNull;

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

    public String getUserPhotoUrl(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser == null){
            throw new UnsupportedOperationException("User is not logged in and no display name exists");
        }
        return firebaseUser.getPhotoUrl() == null ? "" : firebaseUser.getPhotoUrl().getPath();
    }

    public DocumentReference getUserDocumentReference(){
        return usersCollectionReference.document(getUserUid());
    }

    public DocumentReference getSpecificUserDocumentReference(@NonNull @NotNull String userUid){
        return usersCollectionReference.document(userUid);
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

}
