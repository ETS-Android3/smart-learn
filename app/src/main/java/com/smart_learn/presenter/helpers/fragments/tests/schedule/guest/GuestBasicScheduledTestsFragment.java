package com.smart_learn.presenter.helpers.fragments.tests.schedule.guest;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;

import com.smart_learn.core.services.test.TestService;
import com.smart_learn.data.room.entities.RoomTest;
import com.smart_learn.presenter.helpers.Utilities;
import com.smart_learn.presenter.helpers.adapters.test.schedule.GuestScheduledTestsAdapter;
import com.smart_learn.presenter.helpers.fragments.recycler_view_with_bottom_menu.BasicFragmentForRecyclerView;
import com.smart_learn.presenter.helpers.fragments.tests.BasicTestFragment;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public abstract class GuestBasicScheduledTestsFragment <VM extends GuestBasicScheduledTestsViewModel> extends BasicTestFragment<RoomTest, VM> {

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
                Utilities.Activities.changeTextViewStatus(tests.isEmpty(), emptyLabel);
                if(viewModel.getAdapter() != null){
                    viewModel.getAdapter().setItems(tests);
                }
            }
        });
    }
}