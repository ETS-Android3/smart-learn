package com.smart_learn.presenter.user.activities.community.fragments.users;

import android.app.Application;
import android.text.SpannableString;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentSnapshot;
import com.smart_learn.R;
import com.smart_learn.core.user.services.FriendService;
import com.smart_learn.core.user.services.UserService;
import com.smart_learn.core.common.helpers.ConnexionChecker;
import com.smart_learn.data.common.helpers.DataCallbacks;
import com.smart_learn.presenter.user.activities.authentication.helpers.RegisterForm;
import com.smart_learn.presenter.user.activities.community.fragments.users.UsersFragment;
import com.smart_learn.presenter.user.activities.community.fragments.users.helpers.UserDialog;
import com.smart_learn.presenter.common.view_models.BasicAndroidViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;

import lombok.Getter;

@Getter
public class UsersViewModel extends BasicAndroidViewModel {

    private final int maxEmailLength;
    private final MutableLiveData<String> liveEmail;
    private final MutableLiveData<SpannableString> liveMessageNoUserFound;
    private final AtomicBoolean searchIsInProgress;

    public UsersViewModel(@NonNull @NotNull Application application) {
        super(application);
        maxEmailLength = RegisterForm.MAX_EMAIL_LENGTH;
        liveEmail = new MutableLiveData<>("");
        liveMessageNoUserFound = new MutableLiveData<>(new SpannableString(""));
        searchIsInProgress = new AtomicBoolean(false);
    }

    protected void setSearchIsInProgress(boolean value){
        searchIsInProgress.set(value);
    }

    public LiveData<SpannableString> getLiveMessageNoUserFound() {
        return liveMessageNoUserFound;
    }

    public void setLiveMessageNoUserFound(SpannableString value){
        liveMessageNoUserFound.setValue(value);
    }

    private boolean validEmail(UsersFragment fragment, String email){
        if(TextUtils.isEmpty(email)){
            fragment.getBinding().etEmailFragmentUsers.setError(fragment.getString(R.string.error_required));
            return false;
        }

        // This check is already made in edit text field and never should enter here, but double check it.
        if(email.length() > maxEmailLength){
            fragment.getBinding().etEmailFragmentUsers.setError(fragment.getString(R.string.error_email_too_long));
            return false;
        }

        if(!RegisterForm.EMAIL_REGEX_PATTERN.matcher(email).matches()) {
            fragment.getBinding().etEmailFragmentUsers.setError(fragment.getString(R.string.error_email_not_valid));
            return false;
        }

        fragment.getBinding().etEmailFragmentUsers.setError(null);

        return true;
    }

    protected void searchUser(@NonNull @NotNull UsersFragment fragment){
        if(liveEmail == null || liveEmail.getValue() == null){
            liveToastMessage.setValue(fragment.getString(R.string.error_search_user));
            searchIsInProgress.set(false);
            return;
        }

        String email = liveEmail.getValue();
        if(!validEmail(fragment, email)){
            searchIsInProgress.set(false);
            return;
        }

        // check if email is not the current user email
        if(email.equals(UserService.getInstance().getUserEmail())){
            fragment.onCurrentUserFound(email);
            searchIsInProgress.set(false);
            return;
        }

        fragment.showProgressBar();

        // check if connection is available in order to continue with search
        new ConnexionChecker(new ConnexionChecker.Callback() {
            @Override
            public void isConnected() {
                fragment.requireActivity().runOnUiThread(() -> continueSearch(fragment, email));
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
                searchIsInProgress.set(false);
                fragment.requireActivity().runOnUiThread(fragment::hideProgressBar);
            }
        }).check();
    }

    private void continueSearch(UsersFragment fragment, String email){
        UserService.getInstance().searchUserByEmail(email, new DataCallbacks.SearchUserCallback() {
            @Override
            public void onSuccess(@NonNull @NotNull DocumentSnapshot userSnapshot,
                                  boolean isPending, boolean isFriend, boolean isRequestReceived) {
                searchIsInProgress.set(false);
                fragment.requireActivity().runOnUiThread(() -> fragment.onUserFound(userSnapshot, isPending, isFriend, isRequestReceived));
            }

            @Override
            public void onFailure() {
                searchIsInProgress.set(false);
                fragment.requireActivity().runOnUiThread(() -> fragment.onUserNotFound(email));
            }
        });
    }

    protected void sendFriendRequest(@NonNull @NotNull UsersFragment fragment, DocumentSnapshot userSnapshot,
                                     @NonNull @NotNull UserDialog.Listener listener){

        fragment.showProgressDialog("", fragment.getString(R.string.sending_request));

        // check if connection is available in order to continue with sending friend request
        new ConnexionChecker(new ConnexionChecker.Callback() {
            @Override
            public void isConnected() {
                fragment.requireActivity().runOnUiThread(() -> continueWithSendFriendRequest(fragment, userSnapshot, listener));
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
                fragment.requireActivity().runOnUiThread(() -> {
                    fragment.closeProgressDialog();
                    listener.onFailure();
                });
            }
        }).check();
    }

    private void continueWithSendFriendRequest(UsersFragment fragment, DocumentSnapshot userSnapshot,
                                               UserDialog.Listener listener){
        FriendService.getInstance().sendFriendRequest(userSnapshot, new DataCallbacks.General() {
            @Override
            public void onSuccess() {
                fragment.requireActivity().runOnUiThread(() -> {
                    fragment.closeProgressDialog();
                    liveToastMessage.setValue(fragment.getString(R.string.success_send_friend_request));
                    listener.onSuccess();
                });
            }

            @Override
            public void onFailure() {
                fragment.requireActivity().runOnUiThread(() -> {
                    fragment.closeProgressDialog();
                    liveToastMessage.setValue(fragment.getString(R.string.error_send_friend_request));
                    listener.onFailure();
                });
            }
        });
    }

    protected void acceptFriendRequest(@NonNull @NotNull UsersFragment fragment, DocumentSnapshot userSnapshot,
                                       @NonNull @NotNull UserDialog.Listener listener){

        fragment.showProgressDialog("", fragment.getString(R.string.accepting_request));

        // check if connection is available in order to continue with sending friend request
        new ConnexionChecker(new ConnexionChecker.Callback() {
            @Override
            public void isConnected() {
                fragment.requireActivity().runOnUiThread(() -> continueWithAcceptingFriendRequest(fragment, userSnapshot, listener));
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
                fragment.requireActivity().runOnUiThread(() -> {
                    fragment.closeProgressDialog();
                    listener.onFailure();
                });
            }
        }).check();
    }

    private void continueWithAcceptingFriendRequest(UsersFragment fragment, DocumentSnapshot userSnapshot,
                                                    UserDialog.Listener listener){
        FriendService.getInstance().acceptFriendRequest(userSnapshot,
                new DataCallbacks.General() {
                    @Override
                    public void onSuccess() {
                        fragment.requireActivity().runOnUiThread(() -> {
                            fragment.closeProgressDialog();
                            listener.onSuccess();
                        });
                    }
                    @Override
                    public void onFailure() {
                        fragment.requireActivity().runOnUiThread(() -> {
                            fragment.closeProgressDialog();
                            liveToastMessage.setValue(fragment.getString(R.string.error_accept_request));
                            listener.onFailure();
                        });
                    }
                });
    }
}