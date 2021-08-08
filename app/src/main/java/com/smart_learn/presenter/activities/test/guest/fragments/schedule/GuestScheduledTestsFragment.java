package com.smart_learn.presenter.activities.test.guest.fragments.schedule;

import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.smart_learn.R;
import com.smart_learn.core.utilities.GeneralUtilities;
import com.smart_learn.data.room.entities.RoomTest;
import com.smart_learn.presenter.activities.test.TestActivity;
import com.smart_learn.presenter.activities.test.guest.GuestTestActivity;
import com.smart_learn.presenter.activities.test.guest.GuestTestSharedViewModel;
import com.smart_learn.presenter.helpers.fragments.tests.schedule.guest.standard.GuestStandardScheduledTestsFragment;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;


public class GuestScheduledTestsFragment extends GuestStandardScheduledTestsFragment<GuestScheduledTestsViewModel> {

    @Getter
    private GuestTestSharedViewModel sharedViewModel;

    @NonNull
    @Override
    protected @NotNull Class<GuestScheduledTestsViewModel> getModelClassForViewModel() {
        return GuestScheduledTestsViewModel.class;
    }

    @Override
    protected boolean isFragmentWithBottomNav() {
        return true;
    }

    @Override
    protected void onAdapterSimpleClick(@NonNull @NotNull RoomTest item) {
        super.onAdapterSimpleClick(item);
        goToGuestScheduledTestInfoFragmentForUpdate(item);
    }

    @Override
    protected void onFloatingActionButtonPress() {
        super.onFloatingActionButtonPress();
        goToGuestScheduledTestInfoFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((TestActivity<?>)requireActivity()).showBottomNavigationMenu();
        sharedViewModel.setGeneratedTest(null);
        sharedViewModel.setScheduledTestFragmentActive(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        sharedViewModel.setScheduledTestFragmentActive(false);
    }

    @Override
    protected boolean useToolbarMenu() {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        super.onOptionsItemSelected(item);
        int id = item.getItemId();
        if(id == android.R.id.home){
            ((GuestTestActivity)requireActivity()).onSupportNavigateUp();
            return true;
        }
        return true;
    }

    @Override
    protected void setViewModel(){
        super.setViewModel();

        // set shared view model
        sharedViewModel = new ViewModelProvider(requireActivity()).get(GuestTestSharedViewModel.class);
    }

    private void goToGuestScheduledTestInfoFragmentForUpdate(RoomTest test){
        // when navigation is made a valid test id must be set on shared view model
        if(test == null || test.getTestId() == GuestTestSharedViewModel.NO_ITEM_SELECTED){
            GeneralUtilities.showShortToastMessage(this.requireContext(),getString(R.string.error_test_can_not_be_opened));
            return;
        }

        ((GuestTestActivity)requireActivity()).goToGuestScheduledTestInfoFragmentForUpdate(test.getTestId());
    }

    private void goToGuestScheduledTestInfoFragment(){
        sharedViewModel.setGeneratedTest(RoomTest.generateEmptyObject());
        ((GuestTestActivity)requireActivity()).goToGuestScheduledTestInfoFragment();
    }

}
