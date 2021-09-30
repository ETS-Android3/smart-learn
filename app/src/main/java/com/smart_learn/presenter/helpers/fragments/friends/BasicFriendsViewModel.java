package com.smart_learn.presenter.helpers.fragments.friends;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.presenter.helpers.adapters.friends.FriendsAdapter;
import com.smart_learn.presenter.helpers.fragments.recycler_view_with_bottom_menu.BasicViewModelForRecyclerView;

import org.jetbrains.annotations.NotNull;

public abstract class BasicFriendsViewModel extends BasicViewModelForRecyclerView<FriendsAdapter> {

    public BasicFriendsViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

}