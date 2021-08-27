package com.smart_learn.presenter.helpers.fragments.tests.history.guest.standard;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.smart_learn.R;
import com.smart_learn.core.services.SettingsService;
import com.smart_learn.core.services.test.TestService;
import com.smart_learn.data.room.entities.RoomTest;
import com.smart_learn.databinding.LayoutBottomSheetShowGuestTestFilterOptionsBinding;
import com.smart_learn.presenter.helpers.PresenterUtilities;
import com.smart_learn.presenter.helpers.fragments.tests.history.guest.GuestBasicTestHistoryFragment;
import com.smart_learn.presenter.helpers.fragments.tests.history.helpers.TestInfoDialog;

import org.jetbrains.annotations.NotNull;


public abstract class GuestStandardTestHistoryFragment <VM extends GuestStandardTestHistoryViewModel> extends GuestBasicTestHistoryFragment<VM> {

    protected abstract void onSeeTestResultPress(@NonNull @NotNull RoomTest item);
    protected abstract void onContinueTestPress(@NonNull @NotNull RoomTest item);

    @Override
    protected boolean useToolbarMenu() {
        return true;
    }

    @Override
    protected boolean useSecondaryGroupOnMenu() {
        return true;
    }

    @Override
    protected boolean onAdapterShowOptionsToolbar() {
        return true;
    }

    @Override
    protected void onAdapterSimpleClick(@NonNull @NotNull RoomTest item) {
        super.onAdapterSimpleClick(item);
        showTestInfoDialog(item);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        super.onOptionsItemSelected(item);
        int id = item.getItemId();
        if(id == R.id.action_filter_menu_layout_with_recycler_view){
            showFilterOptionsDialog();
            return true;
        }
        return true;
    }

    private void showTestInfoDialog(RoomTest item){
        DialogFragment dialogFragment = new TestInfoDialog(item, new TestInfoDialog.Callback() {
            @Override
            public void onSeeResults() {
                onSeeTestResultPress(item);
            }

            @Override
            public void onContinueTest() {
                onContinueTestPress(item);
            }
        });
        dialogFragment.show(requireActivity().getSupportFragmentManager(), "GuestStandardTestHistoryFragment");
    }

    private void showFilterOptionsDialog(){

        // create dialog and load layout
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this.requireContext(), R.style.AppTheme_BottomSheetDialogTheme);

        LayoutBottomSheetShowGuestTestFilterOptionsBinding bottomSheetBinding = DataBindingUtil.inflate(LayoutInflater.from(this.requireContext()),
                R.layout.layout_bottom_sheet_show_guest_test_filter_options,null, false);
        bottomSheetBinding.setLifecycleOwner(this);

        // set selected button (by default all are unselected)
        bottomSheetBinding.setIsAllSelected(false);
        bottomSheetBinding.setIsFinishedSelected(false);
        bottomSheetBinding.setIsInProgressSelected(false);

        int option = SettingsService.getInstance().getGuestTestFilterOption();
        switch (option){
            case TestService.SHOW_ONLY_LOCAL_NON_SCHEDULED_FINISHED_TESTS:
                bottomSheetBinding.setIsFinishedSelected(true);
                break;
            case TestService.SHOW_ONLY_LOCAL_NON_SCHEDULED_IN_PROGRESS_TESTS:
                bottomSheetBinding.setIsInProgressSelected(true);
                break;
            case TestService.SHOW_ONLY_LOCAL_NON_SCHEDULED_TESTS:
            default:
                bottomSheetBinding.setIsAllSelected(true);
                break;
        }

        bottomSheetDialog.setContentView(bottomSheetBinding.getRoot());
        bottomSheetDialog.show();

        // set button listeners
        bottomSheetBinding.btnShowAllLayoutBottomSheetShowGuestTestFilterOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bottomSheetBinding.getIsAllSelected()){
                    bottomSheetDialog.dismiss();
                    return;
                }
                bottomSheetDialog.dismiss();
                SettingsService.getInstance().saveGuestTestFilterOption(TestService.SHOW_ONLY_LOCAL_NON_SCHEDULED_TESTS);
                PresenterUtilities.Activities.resetToolbarTitle((AppCompatActivity) requireActivity(), getString(R.string.tests));
                GuestStandardTestHistoryFragment.super.changeTestObserver();
            }
        });

        bottomSheetBinding.btnShowFinishedLayoutBottomSheetShowGuestTestFilterOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bottomSheetBinding.getIsFinishedSelected()){
                    bottomSheetDialog.dismiss();
                    return;
                }
                bottomSheetDialog.dismiss();
                SettingsService.getInstance().saveGuestTestFilterOption(TestService.SHOW_ONLY_LOCAL_NON_SCHEDULED_FINISHED_TESTS);
                PresenterUtilities.Activities.resetToolbarTitle((AppCompatActivity) requireActivity(), getString(R.string.finished_tests));
                GuestStandardTestHistoryFragment.super.changeTestObserver();
            }
        });

        bottomSheetBinding.btnShowInProgressLayoutBottomSheetShowGuestTestFilterOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bottomSheetBinding.getIsInProgressSelected()){
                    bottomSheetDialog.dismiss();
                    return;
                }
                bottomSheetDialog.dismiss();
                SettingsService.getInstance().saveGuestTestFilterOption(TestService.SHOW_ONLY_LOCAL_NON_SCHEDULED_IN_PROGRESS_TESTS);
                PresenterUtilities.Activities.resetToolbarTitle((AppCompatActivity) requireActivity(), getString(R.string.in_progress_tests));
                GuestStandardTestHistoryFragment.super.changeTestObserver();
            }
        });
    }

}