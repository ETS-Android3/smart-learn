package com.smart_learn.presenter.helpers.fragments.tests.schedule.user;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;
import com.smart_learn.presenter.helpers.adapters.test.schedule.UserScheduledTestsAdapter;
import com.smart_learn.presenter.helpers.fragments.recycler_view_with_bottom_menu.BasicFragmentForRecyclerView;
import com.smart_learn.presenter.helpers.fragments.tests.BasicTestFragment;

import org.jetbrains.annotations.NotNull;


public abstract class UserBasicScheduledTestsFragment <VM extends UserBasicScheduledTestsViewModel> extends BasicTestFragment<DocumentSnapshot, VM> {

    protected abstract void onAdapterCompleteCreateLocalTestFromScheduledTest(int type, @NonNull @NotNull String testId);

    @Override
    protected void setViewModel(){
        super.setViewModel();

        viewModel.setAdapter(new UserScheduledTestsAdapter(new UserScheduledTestsAdapter.Callback() {
            @Override
            public void onCompleteCreateLocalTestFromScheduledTest(int type, @NonNull @NotNull String testId) {
                onAdapterCompleteCreateLocalTestFromScheduledTest(type, testId);
            }

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
                return UserBasicScheduledTestsFragment.this;
            }
        }));

    }
}
