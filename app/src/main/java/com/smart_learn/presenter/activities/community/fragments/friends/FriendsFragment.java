package com.smart_learn.presenter.activities.community.fragments.friends;

import androidx.annotation.NonNull;

import com.smart_learn.presenter.helpers.fragments.friends.standard.StandardFriendsFragment;

import org.jetbrains.annotations.NotNull;

public class FriendsFragment extends StandardFriendsFragment<FriendsViewModel> {

    @NonNull
    @Override
    protected @NotNull Class<FriendsViewModel> getModelClassForViewModel() {
        return FriendsViewModel.class;
    }
}