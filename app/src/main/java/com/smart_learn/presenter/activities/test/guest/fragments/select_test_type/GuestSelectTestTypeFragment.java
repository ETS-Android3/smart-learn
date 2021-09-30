package com.smart_learn.presenter.activities.test.guest.fragments.select_test_type;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.smart_learn.presenter.activities.test.guest.GuestTestActivity;
import com.smart_learn.presenter.activities.test.guest.GuestTestSharedViewModel;
import com.smart_learn.presenter.activities.test.helpers.fragments.select_test_type.SelectTestTypeFragment;

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