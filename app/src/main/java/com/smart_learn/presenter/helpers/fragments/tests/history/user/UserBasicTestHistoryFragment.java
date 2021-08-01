package com.smart_learn.presenter.helpers.fragments.tests.history.user;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;
import com.smart_learn.presenter.helpers.adapters.test.history.UserTestHistoryAdapter;
import com.smart_learn.presenter.helpers.fragments.recycler_view_with_bottom_menu.BasicFragmentForRecyclerView;
import com.smart_learn.presenter.helpers.fragments.tests.BasicTestFragment;

import org.jetbrains.annotations.NotNull;


public abstract class UserBasicTestHistoryFragment <VM extends UserBasicTestHistoryViewModel> extends BasicTestFragment<DocumentSnapshot, VM> {

    @Override
    protected void setViewModel(){
        super.setViewModel();

        viewModel.setAdapter(new UserTestHistoryAdapter(new UserTestHistoryAdapter.Callback() {
            @Override
            public void onSimpleClick(@NonNull @NotNull DocumentSnapshot item) {
                onAdapterSimpleClick(item);
            }

            @Override
            public void onLongClick(@NonNull @NotNull DocumentSnapshot item) {
                onAdapterLongClick(item);
            }

            @Override
            public boolean showCheckedIcon() {
                return onAdapterShowCheckedIcon();
            }

            @Override
            public boolean showToolbar() {
                return onAdapterShowOptionsToolbar();
            }

            @Override
            public void updateSelectedItemsCounter(int value) {
                onAdapterUpdateSelectedItemsCounter(value);
            }

            @NonNull
            @Override
            public @NotNull BasicFragmentForRecyclerView<?> getFragment() {
                return UserBasicTestHistoryFragment.this;
            }
        }));

    }

}