package com.smart_learn.presenter.activities.authentication.fragments.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.smart_learn.R;
import com.smart_learn.databinding.FragmentEmailLoginBinding;
import com.smart_learn.presenter.activities.authentication.AuthenticationActivity;
import com.smart_learn.presenter.activities.authentication.AuthenticationSharedViewModel;
import com.smart_learn.presenter.helpers.fragments.helpers.BasicFragment;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;

public class EmailLoginFragment extends BasicFragment<EmailLoginViewModel> {

    @Getter
    private AuthenticationSharedViewModel sharedViewModel;
    @Getter
    private FragmentEmailLoginBinding binding;

    @NonNull
    @Override
    protected @NotNull Class<EmailLoginViewModel> getModelClassForViewModel() {
        return EmailLoginViewModel.class;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEmailLoginBinding.inflate(inflater);
        binding.setLifecycleOwner(this);
        binding.setSharedViewModel(sharedViewModel);

        // set buttons listeners
        binding.btnLoginFragmentEmailLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.login(EmailLoginFragment.this);
            }
        });

        binding.btnForgotPasswordFragmentEmailLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.sendPasswordResetEmail(EmailLoginFragment.this);
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AuthenticationActivity)requireActivity()).resetToolbar(getResources().getString(R.string.login));
    }

    @Override
    public void onPause() {
        super.onPause();
        ((AuthenticationActivity)requireActivity()).dismissLoadingDialog();
    }

    @Override
    protected void setViewModel(){
        super.setViewModel();
        // set shared view model for passing data
        sharedViewModel = new ViewModelProvider(requireActivity()).get(AuthenticationSharedViewModel.class);
    }
}