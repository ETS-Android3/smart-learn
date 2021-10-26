package com.smart_learn.presenter.guest.activities.test.fragments.history;

import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.smart_learn.R;
import com.smart_learn.data.guest.room.entitites.RoomTest;
import com.smart_learn.presenter.common.activities.test.TestActivity;
import com.smart_learn.presenter.guest.activities.test.GuestTestActivity;
import com.smart_learn.presenter.guest.activities.test.GuestTestSharedViewModel;
import com.smart_learn.presenter.common.helpers.PresenterUtilities;
import com.smart_learn.presenter.guest.fragments.common.tests.history.standard.GuestStandardTestHistoryFragment;

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

    private void goToGuestTestResultsFragment(RoomTest test){
        // when navigation is made a valid test id must be set on shared view model
        if(test == null || test.getTestId() == GuestTestSharedViewModel.NO_ITEM_SELECTED){
            PresenterUtilities.General.showShortToastMessage(this.requireContext(),getString(R.string.error_test_can_not_be_opened));
            return;
        }

        ((GuestTestActivity)requireActivity()).goToGuestTestResultsFragment(test.getTestId(), test.getType());
    }

}