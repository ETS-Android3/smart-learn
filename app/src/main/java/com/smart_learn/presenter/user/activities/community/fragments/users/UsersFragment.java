package com.smart_learn.presenter.user.activities.community.fragments.users;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.firestore.DocumentSnapshot;
import com.smart_learn.R;
import com.smart_learn.data.user.firebase.firestore.entities.UserDocument;
import com.smart_learn.databinding.FragmentUsersBinding;
import com.smart_learn.presenter.user.activities.community.fragments.users.helpers.UserDialog;
import com.smart_learn.presenter.common.helpers.PresenterUtilities;
import com.smart_learn.presenter.common.fragments.helpers.BasicFragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import lombok.Getter;

public class UsersFragment extends BasicFragment<UsersViewModel> {

    @Getter
    private FragmentUsersBinding binding;
    private TextView tvNoUserFound;
    private ProgressBar progressBar;

    @NonNull
    @Override
    protected @NotNull Class<UsersViewModel> getModelClassForViewModel() {
        return UsersViewModel.class;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = FragmentUsersBinding.inflate(inflater);
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setLayoutUtilities();
    }

    protected void setLayoutUtilities(){
        tvNoUserFound = binding.tvNoUserFoundFragmentUsers;
        progressBar = binding.progressBarFragmentUsers;

        tvNoUserFound.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);

        binding.btnSearchFragmentUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // avoid multiple clicks while search is in progress
                if(viewModel.getSearchIsInProgress().get()){
                    PresenterUtilities.General.showShortToastMessage(UsersFragment.this.requireContext(), getString(R.string.search_is_in_progress));
                    return;
                }
                viewModel.setSearchIsInProgress(true);

                resetSearchLayout();
                viewModel.searchUser(UsersFragment.this);
            }
        });

    }

    private void resetSearchLayout(){
        tvNoUserFound.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    protected void onUserNotFound(String email){
        // first hide progress bar
        progressBar.setVisibility(View.INVISIBLE);

        // then color 'email' using a SpannableString and show message with no user found
        String first = getString(R.string.user_with_email) + "\n";
        String second = "\n" + getString(R.string.not_found);
        showTvNoUserMessage(first, second, email);
    }

    protected void onCurrentUserFound(String email){
        // First hide progress bar.
        progressBar.setVisibility(View.INVISIBLE);

        // Then color 'email' using a SpannableString and show message that current mail belongs to
        // the current user.
        String first = getString(R.string.user_with_email) + "\n";
        String second = "\n" + getString(R.string.is_you_description);
        showTvNoUserMessage(first, second, email);
    }

    private void showTvNoUserMessage(String first, String second, String email){
        String value = first + email + second;
        ArrayList<Pair<Integer, Integer>> indexList = new ArrayList<>();
        indexList.add(new Pair<>(first.length(), first.length() + email.length()));
        viewModel.setLiveMessageNoUserFound(PresenterUtilities.Activities.generateSpannedString(indexList, value));
        tvNoUserFound.setVisibility(View.VISIBLE);
    }

    protected void onUserFound(@NonNull @NotNull DocumentSnapshot userSnapshot, boolean isPending,
                               boolean isFriend, boolean isRequestReceived){
        // first hide progress bar
        progressBar.setVisibility(View.INVISIBLE);

        // show user dialog
        DialogFragment dialogFragment = new UserDialog(new UserDialog.Callback() {
            @Override
            public void onSendFriendRequest(@NonNull @NotNull UserDialog.Listener listener) {
                viewModel.sendFriendRequest(UsersFragment.this, userSnapshot, listener);
            }

            @Override
            public void onAcceptFriendRequest(@NonNull @NotNull UserDialog.Listener listener) {
                viewModel.acceptFriendRequest(UsersFragment.this, userSnapshot, listener);
            }
        }, userSnapshot.toObject(UserDocument.class), isPending, isFriend, isRequestReceived);

        dialogFragment.show(requireActivity().getSupportFragmentManager(), "UsersFragment");
    }

    protected void showProgressBar(){
        progressBar.setVisibility(View.VISIBLE);
    }

    protected void hideProgressBar(){
        progressBar.setVisibility(View.INVISIBLE);
    }

}