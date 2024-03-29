package com.smart_learn.presenter.common.fragments.account_overview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.smart_learn.R;
import com.smart_learn.databinding.FragmentAccountOverviewBinding;
import com.smart_learn.presenter.common.helpers.PresenterUtilities;
import com.smart_learn.presenter.common.fragments.helpers.BasicFragment;

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
    public void onStart() {
        super.onStart();
        viewModel.getLiveSuccessRate().observe(this, new Observer<Float>() {
            @Override
            public void onChanged(Float aFloat) {
                if(aFloat == null){
                    return;
                }
                viewModel.setLiveSuccessRateDescription(aFloat);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        PresenterUtilities.Activities.resetToolbarTitle((AppCompatActivity) requireActivity(),getResources().getString(R.string.account_overview));
    }

}