package com.smart_learn.presenter.helpers.fragments.account_summary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.smart_learn.R;
import com.smart_learn.databinding.FragmentAccountOverviewBinding;
import com.smart_learn.presenter.helpers.Utilities;
import com.smart_learn.presenter.helpers.fragments.helpers.BasicFragment;

import org.jetbrains.annotations.NotNull;

/**
 * From this fragment, UserAccountOverviewFragment and GuestAccountOverviewFragment will be extended
 * in order to obtain a better control flow of the navigation.
 *
 * @param <VM> A ViewModel class that extends AccountOverviewViewModel that will be used by the fragment
 *             as main view model.
 * */
public abstract class AccountOverviewFragment <VM extends AccountOverviewViewModel> extends BasicFragment<VM> {

    protected FragmentAccountOverviewBinding binding;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAccountOverviewBinding.inflate(inflater);
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        Utilities.Activities.resetToolbarTitle((AppCompatActivity) requireActivity(),getResources().getString(R.string.account_overview));
    }

}