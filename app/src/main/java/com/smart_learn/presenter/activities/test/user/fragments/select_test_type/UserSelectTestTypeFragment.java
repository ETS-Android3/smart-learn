package com.smart_learn.presenter.activities.test.user.fragments.select_test_type;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.smart_learn.presenter.activities.test.helpers.fragments.select_test_type.SelectTestTypeFragment;
import com.smart_learn.presenter.activities.test.user.UserTestActivity;
import com.smart_learn.presenter.activities.test.user.UserTestSharedViewModel;

import org.jetbrains.annotations.NotNull;


public class UserSelectTestTypeFragment extends SelectTestTypeFragment<UserSelectTestTypeViewModel> {

    @NonNull
    @Override
    protected @NotNull Class<UserSelectTestTypeViewModel> getModelClassForViewModel() {
        return UserSelectTestTypeViewModel.class;
    }

    @Override
    protected void navigateToTestSetupFragment() {
        ((UserTestActivity)requireActivity()).goToUserTestSetupFragment();
    }

    @Override
    protected void setViewModel(){
        super.setViewModel();

        // set shared view model
        sharedViewModel = new ViewModelProvider(requireActivity()).get(UserTestSharedViewModel.class);
    }
}