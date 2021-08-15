package com.smart_learn.presenter.activities.notebook.user.fragments.friends;

import android.app.Application;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;
import com.smart_learn.R;
import com.smart_learn.core.services.lesson.UserLessonService;
import com.smart_learn.core.services.UserService;
import com.smart_learn.core.helpers.ConnexionChecker;
import com.smart_learn.core.helpers.CoreUtilities;
import com.smart_learn.data.firebase.firestore.entities.LessonDocument;
import com.smart_learn.data.firebase.firestore.entities.helpers.DocumentMetadata;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.core.helpers.ApplicationController;
import com.smart_learn.presenter.helpers.PresenterUtilities;
import com.smart_learn.presenter.helpers.fragments.friends.select.SelectFriendsViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import timber.log.Timber;

public class FriendsViewModel extends SelectFriendsViewModel {

    public FriendsViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

    protected void shareLesson(FriendsFragment fragment, DocumentSnapshot lessonSnapshot) {
        if(adapter == null){
            liveToastMessage.setValue(fragment.getString(R.string.error_share_lesson));
            Timber.w("adapter is null");
            return;
        }

        ArrayList<DocumentSnapshot> friends = adapter.getSelectedValues();
        if(friends.isEmpty()){
            liveToastMessage.setValue(fragment.getString(R.string.no_friend_selected));
            return;
        }

        fragment.showProgressDialog("", fragment.getString(R.string.share_lesson));

        // check if connection is available in order to continue with sharing lesson
        new ConnexionChecker(new ConnexionChecker.Callback() {
            @Override
            public void isConnected() {
                fragment.requireActivity().runOnUiThread(() -> continueWithLessonShare(fragment, lessonSnapshot, friends));
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

    private void continueWithLessonShare(FriendsFragment fragment, DocumentSnapshot lesson, ArrayList<DocumentSnapshot> friends){
        UserLessonService.getInstance().shareLesson(lesson, friends, new DataCallbacks.General() {
            @Override
            public void onSuccess() {
                fragment.requireActivity().runOnUiThread(() -> {
                    fragment.closeProgressDialog();
                    (fragment.requireActivity()).onBackPressed();
                    PresenterUtilities.General.showShortToastMessage(ApplicationController.getInstance(),
                            ApplicationController.getInstance().getString(R.string.success_shared_lesson));
                });
            }

            @Override
            public void onFailure() {
                fragment.requireActivity().runOnUiThread(() -> {
                    fragment.closeProgressDialog();
                    (fragment.requireActivity()).onBackPressed();
                    PresenterUtilities.General.showShortToastMessage(ApplicationController.getInstance(),
                            ApplicationController.getInstance().getString(R.string.error_share_lesson));
                });
            }
        });
    }

    protected void addNewEmptySharedLesson(FriendsFragment fragment, @NonNull @NotNull String lessonName) {
        if(adapter == null){
            liveToastMessage.setValue(fragment.getString(R.string.error_add_new_shared_lesson));
            Timber.w("adapter is null");
            return;
        }

        ArrayList<DocumentSnapshot> friends = adapter.getSelectedValues();
        if(friends.isEmpty()){
            liveToastMessage.setValue(fragment.getString(R.string.no_friend_selected));
            return;
        }

        fragment.showProgressDialog("", fragment.getString(R.string.add_new_shared_lesson));

        // check if connection is available in order to continue with adding new shared lesson
        new ConnexionChecker(new ConnexionChecker.Callback() {
            @Override
            public void isConnected() {
                continueWithAddNewEmptySharedLesson(fragment, lessonName, friends);
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

    private void continueWithAddNewEmptySharedLesson(FriendsFragment fragment, String lessonName, ArrayList<DocumentSnapshot> friends){

        ArrayList<String> searchValues = new ArrayList<>();
        searchValues.add(lessonName);
        LessonDocument lesson = new LessonDocument(
                new DocumentMetadata(
                        UserService.getInstance().getUserUid(),
                        System.currentTimeMillis(),
                        CoreUtilities.General.generateSearchListForFirestoreDocument(searchValues)),
                "",
                LessonDocument.Types.SHARED,
                lessonName
        );

        UserLessonService.getInstance().addEmptySharedLesson(lesson, friends, new DataCallbacks.General() {
            @Override
            public void onSuccess() {
                fragment.requireActivity().runOnUiThread(() -> {
                    fragment.closeProgressDialog();
                    (fragment.requireActivity()).onBackPressed();
                    PresenterUtilities.General.showShortToastMessage(ApplicationController.getInstance(),
                            ApplicationController.getInstance().getString(R.string.success_add_new_shared_lesson));
                });
            }

            @Override
            public void onFailure() {
                fragment.requireActivity().runOnUiThread(() -> {
                    fragment.closeProgressDialog();
                    (fragment.requireActivity()).onBackPressed();
                    PresenterUtilities.General.showShortToastMessage(ApplicationController.getInstance(),
                            ApplicationController.getInstance().getString(R.string.error_add_new_shared_lesson));
                });
            }
        });
    }
}