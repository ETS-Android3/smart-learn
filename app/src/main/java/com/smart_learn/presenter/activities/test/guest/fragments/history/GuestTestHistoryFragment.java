package com.smart_learn.presenter.activities.test.guest.fragments.history;

import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.smart_learn.R;
import com.smart_learn.core.utilities.GeneralUtilities;
import com.smart_learn.data.room.entities.RoomTest;
import com.smart_learn.presenter.activities.test.TestActivity;
import com.smart_learn.presenter.activities.test.guest.GuestTestActivity;
import com.smart_learn.presenter.activities.test.guest.GuestTestSharedViewModel;
import com.smart_learn.presenter.helpers.fragments.tests.history.guest.standard.GuestStandardTestHistoryFragment;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;


public class GuestTestHistoryFragment extends GuestStandardTestHistoryFragment<GuestTestHistoryViewModel> {

    @Getter
    private GuestTestSharedViewModel sharedViewModel;

    @NonNull
    @Override
    protected @NotNull Class<GuestTestHistoryViewModel> getModelClassForViewModel() {
        return GuestTestHistoryViewModel.class;
    }

    @Override
    protected boolean isFragmentWithBottomNav() {
        return true;
    }

    @Override
    protected void onSeeTestResultPress(@NonNull @NotNull RoomTest item) {
        goToGuestTestResultsFragment(item);
    }

    @Override
    protected void onContinueTestPress(@NonNull @NotNull RoomTest item) {
        ((GuestTestActivity)requireActivity()).goToActivateTestFragment(item.getType(), item.getId());
    }

    @Override
    public void onResume() {
        super.onResume();
        ((TestActivity<?>)requireActivity()).showBottomNavigationMenu();
        sharedViewModel.setSelectedTestHistoryId(GuestTestSharedViewModel.NO_ITEM_SELECTED);
        sharedViewModel.setTestHistoryFragmentActive(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        sharedViewModel.setTestHistoryFragmentActive(false);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        super.onOptionsItemSelected(item);
        int id = item.getItemId();
        if(id == android.R.id.home){
            ((GuestTestActivity)requireActivity()).goToGuestActivity();
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

    private void goToGuestTestResultsFragment(RoomTest test){
        // when navigation is made a valid test id must be set on shared view model
        if(test == null || test.getTestId() == GuestTestSharedViewModel.NO_ITEM_SELECTED){
            GeneralUtilities.showShortToastMessage(this.requireContext(),getString(R.string.error_test_can_not_be_opened));
            return;
        }

        ((GuestTestActivity)requireActivity()).goToGuestTestResultsFragment(test.getTestId(), test.getType());
    }

}