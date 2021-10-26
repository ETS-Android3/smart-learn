package com.smart_learn.presenter.user.fragments.common.friends;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.presenter.user.adapters.FriendsAdapter;
import com.smart_learn.presenter.common.fragments.helpers.recycler_view.BasicViewModelForRecyclerView;

import org.jetbrains.annotations.NotNull;

public abstract class BasicFriendsViewModel extends BasicViewModelForRecyclerView<FriendsAdapter> {

    public BasicFriendsViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

}