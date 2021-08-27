package com.smart_learn.presenter.helpers.fragments.tests.history.guest;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;

import com.smart_learn.R;
import com.smart_learn.core.services.SettingsService;
import com.smart_learn.core.services.test.TestService;
import com.smart_learn.data.room.entities.RoomTest;
import com.smart_learn.presenter.helpers.PresenterUtilities;
import com.smart_learn.presenter.helpers.adapters.test.history.GuestTestHistoryAdapter;
import com.smart_learn.presenter.helpers.fragments.recycler_view_with_bottom_menu.BasicFragmentForRecyclerView;
import com.smart_learn.presenter.helpers.fragments.tests.BasicTestFragment;

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