package com.smart_learn.presenter.user.fragments.common.tests.history;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;
import com.smart_learn.R;
import com.smart_learn.core.common.services.SettingsService;
import com.smart_learn.core.common.services.TestService;
import com.smart_learn.presenter.user.adapters.test.UserTestHistoryAdapter;
import com.smart_learn.presenter.common.fragments.helpers.recycler_view.BasicFragmentForRecyclerView;
import com.smart_learn.presenter.common.fragments.test.tests_list.BasicTestFragment;

import org.jetbrains.annotations.NotNull;


public abstract class UserBasicTestHistoryFragment <VM extends UserBasicTestHistoryViewModel> extends BasicTestFragment<DocumentSnapshot, VM> {

    @Override
    protected int getToolbarTitle() {
        int option = SettingsService.getInstance().getUserTestFilterOption();
        switch (option){
            case TestService.SHOW_ONLY_ONLINE_TESTS:
                return R.string.common_tests;
            case TestService.SHOW_ONLY_LOCAL_NON_SCHEDULED_FINISHED_TESTS:
                return R.string.finished_local_tests;
            case TestService.SHOW_ONLY_LOCAL_NON_SCHEDULED_IN_PROGRESS_TESTS:
                return R.string.in_progress_local_tests;
            case TestService.SHOW_ONLY_LOCAL_NON_SCHEDULED_TESTS:
                return R.string.local_tests;
            default:
                return R.string.tests;
        }
    }

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