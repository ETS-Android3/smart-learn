package com.smart_learn.presenter.guest.fragments.common.tests.history;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;

import com.smart_learn.R;
import com.smart_learn.core.common.services.SettingsService;
import com.smart_learn.core.common.services.TestService;
import com.smart_learn.data.guest.room.entitites.RoomTest;
import com.smart_learn.presenter.common.helpers.PresenterUtilities;
import com.smart_learn.presenter.guest.adapters.test.GuestTestHistoryAdapter;
import com.smart_learn.presenter.common.fragments.helpers.recycler_view.BasicFragmentForRecyclerView;
import com.smart_learn.presenter.common.fragments.test.tests_list.BasicTestFragment;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import timber.log.Timber;


public abstract class GuestBasicTestHistoryFragment <VM extends GuestBasicTestHistoryViewModel> extends BasicTestFragment<RoomTest, VM> {

    private Observer<List<RoomTest>> observer;

    @Override
    protected int getToolbarTitle() {
        int option = SettingsService.getInstance().getGuestTestFilterOption();
        switch (option){
            case TestService.SHOW_ONLY_LOCAL_NON_SCHEDULED_FINISHED_TESTS:
                return R.string.finished_tests;
            case TestService.SHOW_ONLY_LOCAL_NON_SCHEDULED_IN_PROGRESS_TESTS:
                return R.string.in_progress_tests;
            case TestService.SHOW_ONLY_LOCAL_NON_SCHEDULED_TESTS:
            default:
                return R.string.tests;
        }
    }

    @Override
    protected void setLayoutUtilities() {
        super.setLayoutUtilities();

        // guest fragment does not need refreshing
        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    protected void setViewModel(){
        super.setViewModel();

        viewModel.setAdapter(new GuestTestHistoryAdapter(new GuestTestHistoryAdapter.Callback() {
            @Override
            public void onSimpleClick(@NonNull @NotNull RoomTest item) {
                onAdapterSimpleClick(item);
            }

            @Override
            public void onLongClick(@NonNull @NotNull RoomTest item) {
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
                return GuestBasicTestHistoryFragment.this;
            }
        }));

        // set a central observe
        observer = new Observer<List<RoomTest>>() {
            @Override
            public void onChanged(List<RoomTest> tests) {
                PresenterUtilities.Activities.changeTextViewStatus(tests.isEmpty(), emptyLabel);
                if(viewModel.getAdapter() != null){
                    viewModel.getAdapter().setItems(tests);
                }
            }
        };

        // set initial query for adapter
        changeTestObserver();
    }

    protected void changeTestObserver(){
        if(observer == null){
            Timber.w("observer is null");
            return;
        }

        // first remove exiting observer
        TestService.getInstance().getAllLiveNonScheduledTests().removeObserver(observer);
        TestService.getInstance().getAllLiveFinishedTests().removeObserver(observer);
        TestService.getInstance().getAllLiveInProgressTests().removeObserver(observer);

        // then attach observer to specific query
        int option = SettingsService.getInstance().getGuestTestFilterOption();
        switch (option){
            case TestService.SHOW_ONLY_LOCAL_NON_SCHEDULED_FINISHED_TESTS:
                TestService.getInstance().getAllLiveFinishedTests().observe(this, observer);
                break;
            case TestService.SHOW_ONLY_LOCAL_NON_SCHEDULED_IN_PROGRESS_TESTS:
                TestService.getInstance().getAllLiveInProgressTests().observe(this, observer);
                break;
            case TestService.SHOW_ONLY_LOCAL_NON_SCHEDULED_TESTS:
            default:
                TestService.getInstance().getAllLiveNonScheduledTests().observe(this, observer);
                break;
        }
    }

}