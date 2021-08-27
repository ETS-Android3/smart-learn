package com.smart_learn.presenter.activities.community.fragments.friends;

import androidx.annotation.NonNull;

import com.smart_learn.presenter.helpers.fragments.friends.standard.StandardFriendsFragment;

import org.jetbrains.annotations.NotNull;

public class FriendsFragment extends StandardFriendsFragment<FriendsViewModel> {

    @Override
    protected boolean isFragmentWithBottomNav() {
        // This is a fragment with bottom nav but return false in order to avoid having too much
        // bottom distance.
        // TODO: Fix activity_main layout and set bottom sheet out of the constraint layout and put
        //  inside of Coordinator layout (check test activity for an example). Also after you change
        //  this you should change layouts for the other 2 fragments (account_overview and user_profile)
        //  in order to add more space at bottom because bottom sheet will cover some bottom parts
        //  of the layout.
        return false;
    }

    @NonNull
    @Override
    protected @NotNull Class<FriendsViewModel> getModelClassForViewModel() {
        return FriendsViewModel.class;
    }
}