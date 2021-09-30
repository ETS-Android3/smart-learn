package com.smart_learn.presenter.activities.test.user.fragments.history;

import android.text.TextUtils;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.firestore.DocumentSnapshot;
import com.smart_learn.R;
import com.smart_learn.data.firebase.firestore.entities.TestDocument;
import com.smart_learn.presenter.activities.test.TestActivity;
import com.smart_learn.presenter.activities.test.user.UserTestActivity;
import com.smart_learn.presenter.activities.test.user.UserTestSharedViewModel;
import com.smart_learn.presenter.helpers.PresenterUtilities;
import com.smart_learn.presenter.helpers.fragments.tests.history.user.standard.UserStandardTestHistoryFragment;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;


public class UserTestHistoryFragment extends UserStandardTestHistoryFragment<UserTestHistoryViewModel> {

    @Getter
    private UserTestSharedViewModel sharedViewModel;

    @NonNull
    @Override
    protected @NotNull Class<UserTestHistoryViewModel> getModelClassForViewModel() {
        return UserTestHistoryViewModel.class;
    }

    @Override
    protected boolean isFragmentWithBottomNav() {
        return true;
    }

    @Override
    protected void onSeeTestResultPress(@NonNull @NotNull DocumentSnapshot item) {
        goToUserTestResultsFragment(item);
    }

    @Override
    protected void onContinueTestPress(@NonNull @NotNull DocumentSnapshot item) {
        TestDocument test = item.toObject(TestDocument.class);
        if(test == null){
            showMessage(R.string.error_can_not_continue);
            return;
        }
        ((UserTestActivity)requireActivity()).goToActivateTestFragment(test.getType(), item.getId(), test.isOnline());
    }

    @Override
    protected void onContinueWithOnlineTest(@NonNull @NotNull DocumentSnapshot item) {
        viewModel.onContinueWithOnlineTest(UserTestHistoryFragment.this, item);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((TestActivity<?>)requireActivity()).showBottomNavigationMenu();
        sharedViewModel.setSelectedTestHistoryId("");
        sharedViewModel.setSelectedOnlineContainerTestId("");
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
            ((UserTestActivity)requireActivity()).onSupportNavigateUp();
            return true;
        }
        return true;
    }

    @Override
    protected void setViewModel(){
        super.setViewModel();

        // set shared view model
        sharedViewModel = new ViewModelProvider(requireActivity()).get(UserTestSharedViewModel.class);
    }

    private void goToUserTestResultsFragment(DocumentSnapshot testSnapshot){
        TestDocument test = testSnapshot.toObject(TestDocument.class);
        if(test == null || TextUtils.isEmpty(testSnapshot.getId())){
            PresenterUtilities.General.showShortToastMessage(this.requireContext(),getString(R.string.error_test_can_not_be_opened));
            return;
        }

        ((UserTestActivity)requireActivity()).goToUserTestResultsFragment(testSnapshot.getId(), test.getType());
    }

    protected void goToUserOnlineTestContainerFragment(int testType, String testId, boolean isFinished){
        ((UserTestActivity)requireActivity()).goToUserOnlineTestContainerFragment(testType, testId, isFinished);
    }

}