package com.smart_learn.presenter.guest.activities.guest.fragments_account_overview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.smart_learn.presenter.common.fragments.account_overview.AccountOverviewFragment;

import org.jetbrains.annotations.NotNull;

public class GuestAccountOverviewFragment extends AccountOverviewFragment<GuestAccountOverviewViewModel> {

    @NonNull
    @Override
    protected @NotNull Class<GuestAccountOverviewViewModel> getModelClassForViewModel() {
        return GuestAccountOverviewViewModel.class;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // this is a guest fragment so a guest test summary will appear
        binding.linearLayoutTestsSummaryUserFragmentAccountOverview.setVisibility(View.GONE);
        binding.linearLayoutTestsSummaryGuestFragmentAccountOverview.setVisibility(View.VISIBLE);
        return binding.getRoot();
    }
}