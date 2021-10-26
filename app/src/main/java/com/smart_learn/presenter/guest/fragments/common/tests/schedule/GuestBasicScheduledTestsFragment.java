package com.smart_learn.presenter.guest.fragments.common.tests.schedule;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;

import com.smart_learn.core.common.services.TestService;
import com.smart_learn.data.guest.room.entitites.RoomTest;
import com.smart_learn.presenter.common.helpers.PresenterUtilities;
import com.smart_learn.presenter.guest.adapters.test.GuestScheduledTestsAdapter;
import com.smart_learn.presenter.common.fragments.helpers.recycler_view.BasicFragmentForRecyclerView;
import com.smart_learn.presenter.common.fragments.test.tests_list.BasicTestFragment;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public abstract class GuestBasicScheduledTestsFragment <VM extends GuestBasicScheduledTestsViewModel> extends BasicTestFragment<RoomTest, VM> {

    protected abstract void onAdapterCompleteCreateLocalTestFromScheduledTest(int type, int testId);

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

        viewModel.setAdapter(new GuestScheduledTestsAdapter(new GuestScheduledTestsAdapter.Callback() {
            @Override
            public void onCompleteCreateLocalTestFromScheduledTest(int type, int testId) {
                onAdapterCompleteCreateLocalTestFromScheduledTest(type, testId);
            }

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
                return GuestBasicScheduledTestsFragment.this;
            }
        }));

        // set observers
        TestService.getInstance().getAllLiveScheduledTests().observe(this, new Observer<List<RoomTest>>() {
            @Override
            public void onChanged(List<RoomTest> tests) {
                PresenterUtilities.Activities.changeTextViewStatus(tests.isEmpty(), emptyLabel);
                if(viewModel.getAdapter() != null){
                    viewModel.getAdapter().setItems(tests);
                }
            }
        });
    }
}