package com.smart_learn.presenter.user.fragments.common.tests.schedule;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;
import com.smart_learn.presenter.user.adapters.test.UserScheduledTestsAdapter;
import com.smart_learn.presenter.common.fragments.helpers.recycler_view.BasicFragmentForRecyclerView;
import com.smart_learn.presenter.common.fragments.test.tests_list.BasicTestFragment;

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
