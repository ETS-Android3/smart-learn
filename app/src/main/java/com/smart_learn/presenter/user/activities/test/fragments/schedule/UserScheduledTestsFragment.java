package com.smart_learn.presenter.user.activities.test.fragments.schedule;

import android.text.TextUtils;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.firestore.DocumentSnapshot;
import com.smart_learn.R;
import com.smart_learn.core.user.services.UserService;
import com.smart_learn.data.user.firebase.firestore.entities.TestDocument;
import com.smart_learn.data.user.firebase.firestore.entities.helpers.DocumentMetadata;
import com.smart_learn.presenter.common.activities.test.TestActivity;
import com.smart_learn.presenter.user.activities.test.UserTestActivity;
import com.smart_learn.presenter.user.activities.test.UserTestSharedViewModel;
import com.smart_learn.presenter.common.helpers.PresenterUtilities;
import com.smart_learn.presenter.user.fragments.common.tests.schedule.standard.UserStandardScheduledTestsFragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import lombok.Getter;


public class UserScheduledTestsFragment extends UserStandardScheduledTestsFragment<UserScheduledTestsViewModel> {

    @Getter
    private UserTestSharedViewModel sharedViewModel;

    @NonNull
    @Override
    protected @NotNull Class<UserScheduledTestsViewModel> getModelClassForViewModel() {
        return UserScheduledTestsViewModel.class;
    }

    @Override
    protected boolean isFragmentWithBottomNav() {
        return true;
    }

    @Override
    protected void onAdapterSimpleClick(@NonNull @NotNull DocumentSnapshot item) {
        super.onAdapterSimpleClick(item);
        goToUserScheduledTestInfoFragmentForUpdate(item);
    }

    @Override
    protected void onFloatingActionButtonPress() {
        super.onFloatingActionButtonPress();
        goToUserScheduledTestInfoFragment();
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
            ((UserTestActivity)requireActivity()).onSupportNavigateUp();
            return true;
        }
        return true;
    }

    @Override
    protected void onAdapterCompleteCreateLocalTestFromScheduledTest(int type, @NonNull @NotNull String testId) {
        ((UserTestActivity)requireActivity()).goToActivateTestFragment(type, testId, false);
    }

    @Override
    protected void setViewModel(){
        super.setViewModel();

        // set shared view model
        sharedViewModel = new ViewModelProvider(requireActivity()).get(UserTestSharedViewModel.class);
    }

    private void goToUserScheduledTestInfoFragmentForUpdate(DocumentSnapshot test){
        if(test == null || TextUtils.isEmpty(test.getId())){
            PresenterUtilities.General.showShortToastMessage(this.requireContext(),getString(R.string.error_test_can_not_be_opened));
            return;
        }

        ((UserTestActivity)requireActivity()).goToUserScheduledTestInfoFragmentForUpdate(test.getId());
    }

    private void goToUserScheduledTestInfoFragment(){
        sharedViewModel.setGeneratedTest(new TestDocument(new DocumentMetadata(UserService.getInstance().getUserUid(),
                System.currentTimeMillis(), new ArrayList<>())));
        ((UserTestActivity)requireActivity()).goToUserScheduledTestInfoFragment();
    }

}
