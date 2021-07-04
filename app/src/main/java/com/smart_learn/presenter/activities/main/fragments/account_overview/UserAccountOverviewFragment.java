package com.smart_learn.presenter.activities.main.fragments.account_overview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.smart_learn.presenter.helpers.fragments.account_summary.AccountOverviewFragment;

import org.jetbrains.annotations.NotNull;

public class UserAccountOverviewFragment extends AccountOverviewFragment<UserAccountOverviewViewModel> {

    @NonNull
    @Override
    protected @NotNull Class<UserAccountOverviewViewModel> getModelClassForViewModel() {
        return UserAccountOverviewViewModel.class;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // this is a user fragment so a user test summary will appear
        binding.linearLayoutTestsSummaryUserFragmentAccountOverview.setVisibility(View.VISIBLE);
        binding.linearLayoutTestsSummaryGuestFragmentAccountOverview.setVisibility(View.GONE);
        return binding.getRoot();
    }
}