package com.smart_learn.core.services;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.smart_learn.core.services.helpers.BasicFirestoreService;
import com.smart_learn.core.helpers.CoreUtilities;
import com.smart_learn.data.firebase.firestore.entities.FriendDocument;
import com.smart_learn.data.firebase.firestore.entities.UserDocument;
import com.smart_learn.data.firebase.firestore.entities.helpers.BasicProfileDocument;
import com.smart_learn.data.firebase.firestore.entities.helpers.DocumentMetadata;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.data.helpers.DataUtilities;
import com.smart_learn.data.repository.FriendRepository;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import timber.log.Timber;

public class FriendService extends BasicFirestoreService<FriendDocument, FriendRepository> {

    private static FriendService instance;

    private FriendService() {
        super(FriendRepository.getInstance());
    }

    public static FriendService getInstance() {
        if(instance == null){
            instance = new FriendService();
        }
        return instance;
    }

    public Query getQueryForAllAcceptedFriends(long limit) {
        return repositoryInstance.getQueryForAllAcceptedFriends(limit);
    }

    public Query getQueryForFilter(long limit, String value) {
        if(value == null){
            value = "";
        }
        return repositoryInstance.getQueryForFilter(limit, value);
    }

    public CollectionReference getFriendsCollectionReference(){
        return repositoryInstance.getFriendsCollectionReference();
    }

    public void sendFriendRequest(DocumentSnapshot userSnapshot, DataCallbacks.General callback){
        if(DataUtilities.Firestore.notGoodDocumentSnapshot(userSnapshot)){
            if(callback != null){
                callback.onFailure();
            }
            return;
        }

        if(callback == null){
            callback = DataUtilities.General.generateGeneralCallback("Request sent for snapshot ID = " + userSnapshot.getId(),
                    "Request NOT sent for snapshot ID = " + userSnapshot.getId());
        }

        UserDocument newFriend = userSnapshot.toObject(UserDocument.class);
        if(newFriend == null){
            callback.onFailure();
            return;
        }

        repositoryInstance.sendFriendRequest(newFriend.getDocumentMetadata().getOwner(), newFriend.getDisplayName(), callback);
    }

    public void acceptFriendRequest(DocumentSnapshot userSnapshot, DataCallbacks.General callback){
        if(DataUtilities.Firestore.notGoodDocumentSnapshot(userSnapshot)){
            if(callback != null){
                callback.onFailure();
            }
            return;
        }

        if(callback == null){
            callback = DataUtilities.General.generateGeneralCallback("Request accepted for snapshot ID = " + userSnapshot.getId(),
                    "Request NOT accepted for snapshot ID = " + userSnapshot.getId());
        }

        UserDocument friend = userSnapshot.toObject(UserDocument.class);
        if(friend == null){
            callback.onFailure();
            return;
        }

        repositoryInstance.acceptFriendRequest(friend.getDocumentMetadata().getOwner(), userSnapshot.getReference(), callback);
    }

    public void removeFriend(DocumentSnapshot friendSnapshot, DataCallbacks.General callback){
        if(DataUtilities.Firestore.notGoodDocumentSnapshot(friendSnapshot)){
            if(callback != null){
                callback.onFailure();
            }
            return;
        }

        if(callback == null){
            callback = DataUtilities.General.generateGeneralCallback("snapshot ID = " + friendSnapshot.getId() + " was removed",
                    "snapshot ID = " + friendSnapshot.getId() + " was NOT removed");
        }

        FriendDocument friend = friendSnapshot.toObject(FriendDocument.class);
        if(friend == null){
            callback.onFailure();
            return;
        }

        repositoryInstance.removeFriend(friend.getFriendUid(), friend.getDisplayName(), callback);
    }

    /**
     * Use to sync data inside a friendDocument with data from the UserDocumentReference which is
     * contained in the friendDocument. Check will be made in a worker thread.
     *
     * @param friendSnapshot DocumentSnapshot to be synced.
     * @param callback Callback to manage onSuccess(...) and onFailure(...) actions.
     * */
    public void syncFriendDocument(DocumentSnapshot friendSnapshot, @Nullable DataCallbacks.General callback){
        ThreadExecutorService.getInstance().execute(() -> tryToSyncFriendDocument(friendSnapshot, callback));
    }

    private void tryToSyncFriendDocument(DocumentSnapshot friendSnapshot, @Nullable DataCallbacks.General callback){
        if(DataUtilities.Firestore.notGoodDocumentSnapshot(friendSnapshot)){
            if(callback != null){
                callback.onFailure();
            }
            return;
        }

        FriendDocument friend = friendSnapshot.toObject(FriendDocument.class);
        if(friend == null){
            if(callback != null){
                callback.onFailure();
            }
            Timber.w("friendDocument is null");
            return;
        }

        if(friend.getUserDocumentReference() == null){
            if(callback != null){
                callback.onFailure();
            }
            Timber.w("friendDocument.getUserDocumentReference() is null");
            return;
        }

        // try extract document snapshot and try to sync data if necessary
        friend.getUserDocumentReference().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if(DataUtilities.Firestore.notGoodBasicResultConfiguration(task)){
                    if(callback != null){
                        callback.onFailure();
                    }
                    return;
                }

                UserDocument user = Objects.requireNonNull(task.getResult()).toObject(UserDocument.class);
                if(user == null){
                    if(callback != null){
                        callback.onFailure();
                    }
                    Timber.w("user is null");
                    return;
                }

                boolean changed = false;
                boolean newSearch = false;
                Map<String,Object> data = new HashMap<>();
                ArrayList<String> newSearchValues = new ArrayList<>();

                if(!friend.getDisplayName().equals(user.getDisplayName())){
                    data.put(BasicProfileDocument.Fields.DISPLAY_NAME_FIELD_NAME, user.getDisplayName());
                    newSearchValues.add(user.getDisplayName());
                    changed = true;
                    newSearch = true;
                }
                else {
                    newSearchValues.add(friend.getDisplayName());
                }

                if(!friend.getEmail().equals(user.getEmail())){
                    data.put(BasicProfileDocument.Fields.EMAIL_FIELD_NAME, user.getEmail());
                    newSearchValues.add(user.getEmail());
                    changed = true;
                    newSearch = true;
                }
                else {
                    newSearchValues.add(friend.getEmail());
                }

                if(!friend.getProfilePhotoUrl().equals(user.getProfilePhotoUrl())){
                    data.put(BasicProfileDocument.Fields.PROFILE_PHOTO_URL_FIELD_NAME, user.getProfilePhotoUrl());
                    changed = true;
                }

                if(!changed){
                    if(callback != null){
                        callback.onSuccess();
                    }
                    return;
                }

                // regenerate new search string if values were modified
                if(newSearch){
                    data.put(DocumentMetadata.Fields.SEARCH_LIST_FIELD_NAME, CoreUtilities.General.generateSearchListForFirestoreDocument(newSearchValues));
                }

                // Here were changes, so try to update the update document in database. Update is
                // made for the friend document snapshot.
                data.put(DocumentMetadata.Fields.COMPOSED_MODIFIED_AT_FIELD_NAME, System.currentTimeMillis());
                repositoryInstance.updateDocument(data, friendSnapshot, new DataCallbacks.General() {
                    @Override
                    public void onSuccess() {
                        if(callback != null){
                            callback.onSuccess();
                        }
                    }

                    @Override
                    public void onFailure() {
                        if(callback != null){
                            callback.onFailure();
                        }
                    }
                });
            }
        });
    }


}
