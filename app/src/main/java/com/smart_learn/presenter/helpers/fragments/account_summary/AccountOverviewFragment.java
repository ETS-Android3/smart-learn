package com.smart_learn.presenter.helpers.fragments.account_summary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.smart_learn.R;
import com.smart_learn.core.utilities.CoreUtilities;
import com.smart_learn.core.utilities.GeneralUtilities;
import com.smart_learn.databinding.FragmentAccountOverviewBinding;
import com.smart_learn.presenter.helpers.Utilities;

import org.jetbrains.annotations.NotNull;

public class AccountOverviewFragment extends Fragment {

    private FragmentAccountOverviewBinding binding;
    private AccountOverviewViewModel accountOverviewViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setViewModel();
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAccountOverviewBinding.inflate(inflater);
        binding.setLifecycleOwner(this);
        binding.setViewModel(accountOverviewViewModel);

        // if user is logged in, a different test summary will appear
        if(accountOverviewViewModel.isUserIsLoggedIn()){
            binding.linearLayoutTestsSummaryUserFragmentAccountOverview.setVisibility(View.VISIBLE);
            binding.linearLayoutTestsSummaryGuestFragmentAccountOverview.setVisibility(View.GONE);
        }
        else{
            binding.linearLayoutTestsSummaryUserFragmentAccountOverview.setVisibility(View.GONE);
            binding.linearLayoutTestsSummaryGuestFragmentAccountOverview.setVisibility(View.VISIBLE);
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Utilities.Activities.resetToolbarTitle((AppCompatActivity) requireActivity(),getResources().getString(R.string.account_overview));
    }

    private void setViewModel(){
        accountOverviewViewModel = new ViewModelProvider(this).get(AccountOverviewViewModel.class);

        // set observers
        accountOverviewViewModel.getLiveToastMessage().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                GeneralUtilities.showShortToastMessage(requireContext(), s);
            }
        });
    }
}