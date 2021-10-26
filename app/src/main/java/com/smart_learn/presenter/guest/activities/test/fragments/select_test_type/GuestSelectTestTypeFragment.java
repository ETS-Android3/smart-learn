package com.smart_learn.presenter.guest.activities.test.fragments.select_test_type;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.smart_learn.presenter.guest.activities.test.GuestTestActivity;
import com.smart_learn.presenter.guest.activities.test.GuestTestSharedViewModel;
import com.smart_learn.presenter.common.activities.test.fragments.select_test_type.SelectTestTypeFragment;

import org.jetbrains.annotations.NotNull;


public class GuestSelectTestTypeFragment extends SelectTestTypeFragment<GuestSelectTestTypeViewModel> {

    @NonNull
    @Override
    protected @NotNull Class<GuestSelectTestTypeViewModel> getModelClassForViewModel() {
        return GuestSelectTestTypeViewModel.class;
    }


    @Override
    protected void navigateToTestSetupFragment() {
        ((GuestTestActivity)requireActivity()).goToGuestTestSetupFragment();
    }

    @Override
    protected void setViewModel(){
        super.setViewModel();

        // set shared view model
        sharedViewModel = new ViewModelProvider(requireActivity()).get(GuestTestSharedViewModel.class);
    }
}