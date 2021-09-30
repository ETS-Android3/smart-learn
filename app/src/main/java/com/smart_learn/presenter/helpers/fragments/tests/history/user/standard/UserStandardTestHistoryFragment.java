package com.smart_learn.presenter.helpers.fragments.tests.history.user.standard;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentSnapshot;
import com.smart_learn.R;
import com.smart_learn.core.services.SettingsService;
import com.smart_learn.core.services.test.TestService;
import com.smart_learn.data.firebase.firestore.entities.TestDocument;
import com.smart_learn.databinding.LayoutBottomSheetShowUserTestFilterOptionsBinding;
import com.smart_learn.presenter.helpers.PresenterUtilities;
import com.smart_learn.presenter.helpers.fragments.tests.history.helpers.TestInfoDialog;
import com.smart_learn.presenter.helpers.fragments.tests.history.user.UserBasicTestHistoryFragment;

import org.jetbrains.annotations.NotNull;

import timber.log.Timber;


public abstract class UserStandardTestHistoryFragment <VM extends UserStandardTestHistoryViewModel> extends UserBasicTestHistoryFragment<VM> {

    protected abstract void onSeeTestResultPress(@NonNull @NotNull DocumentSnapshot item);
    protected abstract void onContinueTestPress(@NonNull @NotNull DocumentSnapshot item);
    protected abstract void onContinueWithOnlineTest(@NonNull @NotNull DocumentSnapshot item);

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
    protected void onAdapterSimpleClick(@NonNull @NotNull DocumentSnapshot item) {
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

    private void showTestInfoDialog(DocumentSnapshot item){
        TestDocument test = item.toObject(TestDocument.class);
        if(test == null){
            Timber.w("testDocument is null");
            showMessage(R.string.error_can_not_open_test);
            return;
        }

        // In this fragment if test is online, then is a test online container document, but not an
        // online test document. Always isOnline test will be false in this fragment.
        boolean isOnlineTestContainer = test.isOnline();
        DialogFragment dialogFragment = new TestInfoDialog(test, false, isOnlineTestContainer, new TestInfoDialog.Callback() {
            @Override
            public void onSeeResults() {
                if(isOnlineTestContainer){
                    onContinueWithOnlineTest(item);
                }
                else{
                    onSeeTestResultPress(item);
                }
            }

            @Override
            public void onContinueTest() {
                // on continue test button is disabled if isOnlineTestContainer but double check it
                if(isOnlineTestContainer){
                    // no action needed here
                    return;
                }
                onContinueTestPress(item);
            }
        });
        dialogFragment.show(requireActivity().getSupportFragmentManager(), "UserStandardTestHistoryFragment");
    }

    private void showFilterOptionsDialog(){

        // create dialog and load layout
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this.requireContext(), R.style.AppTheme_BottomSheetDialogTheme);

        LayoutBottomSheetShowUserTestFilterOptionsBinding bottomSheetBinding = DataBindingUtil.inflate(LayoutInflater.from(this.requireContext()),
                R.layout.layout_bottom_sheet_show_user_test_filter_options,null, false);
        bottomSheetBinding.setLifecycleOwner(this);

        // set selected button (by default all are unselected)
        bottomSheetBinding.setIsOnlineSelected(false);
        bottomSheetBinding.setIsLocalSelected(false);
        bottomSheetBinding.setIsFinishedSelected(false);
        bottomSheetBinding.setIsInProgressSelected(false);

        int option = SettingsService.getInstance().getUserTestFilterOption();
        switch (option){
            case TestService.SHOW_ONLY_ONLINE_TESTS:
                bottomSheetBinding.setIsOnlineSelected(true);
                break;
            case TestService.SHOW_ONLY_LOCAL_NON_SCHEDULED_FINISHED_TESTS:
                bottomSheetBinding.setIsFinishedSelected(true);
                break;
            case TestService.SHOW_ONLY_LOCAL_NON_SCHEDULED_IN_PROGRESS_TESTS:
                bottomSheetBinding.setIsInProgressSelected(true);
                break;
            case TestService.SHOW_ONLY_LOCAL_NON_SCHEDULED_TESTS:
            default:
                bottomSheetBinding.setIsLocalSelected(true);
                break;
        }

        bottomSheetDialog.setContentView(bottomSheetBinding.getRoot());
        bottomSheetDialog.show();

        // set button listeners
        bottomSheetBinding.btnShowLocalLayoutBottomSheetShowUserTestFilterOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bottomSheetBinding.getIsLocalSelected()){
                    bottomSheetDialog.dismiss();
                    return;
                }
                bottomSheetDialog.dismiss();
                SettingsService.getInstance().saveUserTestFilterOption(TestService.SHOW_ONLY_LOCAL_NON_SCHEDULED_TESTS);
                PresenterUtilities.Activities.resetToolbarTitle((AppCompatActivity) requireActivity(), getString(R.string.local_tests));
                if(viewModel.getAdapter() != null){
                    viewModel.getAdapter().refreshData(UserStandardTestHistoryFragment.this);
                }
            }
        });

        bottomSheetBinding.btnShowFinishedLayoutBottomSheetShowUserTestFilterOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bottomSheetBinding.getIsFinishedSelected()){
                    bottomSheetDialog.dismiss();
                    return;
                }
                bottomSheetDialog.dismiss();
                SettingsService.getInstance().saveUserTestFilterOption(TestService.SHOW_ONLY_LOCAL_NON_SCHEDULED_FINISHED_TESTS);
                PresenterUtilities.Activities.resetToolbarTitle((AppCompatActivity) requireActivity(), getString(R.string.finished_local_tests));
                if(viewModel.getAdapter() != null){
                    viewModel.getAdapter().refreshData(UserStandardTestHistoryFragment.this);
                }
            }
        });

        bottomSheetBinding.btnShowInProgressLayoutBottomSheetShowUserTestFilterOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bottomSheetBinding.getIsInProgressSelected()){
                    bottomSheetDialog.dismiss();
                    return;
                }
                bottomSheetDialog.dismiss();
                SettingsService.getInstance().saveUserTestFilterOption(TestService.SHOW_ONLY_LOCAL_NON_SCHEDULED_IN_PROGRESS_TESTS);
                PresenterUtilities.Activities.resetToolbarTitle((AppCompatActivity) requireActivity(), getString(R.string.in_progress_local_tests));
                if(viewModel.getAdapter() != null){
                    viewModel.getAdapter().refreshData(UserStandardTestHistoryFragment.this);
                }
            }
        });

        bottomSheetBinding.btnShowOnlineLayoutBottomSheetShowUserTestFilterOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bottomSheetBinding.getIsOnlineSelected()){
                    bottomSheetDialog.dismiss();
                    return;
                }
                bottomSheetDialog.dismiss();
                SettingsService.getInstance().saveUserTestFilterOption(TestService.SHOW_ONLY_ONLINE_TESTS);
                PresenterUtilities.Activities.resetToolbarTitle((AppCompatActivity) requireActivity(), getString(R.string.common_tests));
                if(viewModel.getAdapter() != null){
                    viewModel.getAdapter().refreshData(UserStandardTestHistoryFragment.this);
                }
            }
        });
    }

}