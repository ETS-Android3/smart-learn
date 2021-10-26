package com.smart_learn.presenter.user.activities.authentication.fragments.register;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.smart_learn.R;
import com.smart_learn.databinding.FragmentEmailRegisterBinding;
import com.smart_learn.presenter.user.activities.authentication.AuthenticationActivity;
import com.smart_learn.presenter.user.activities.authentication.AuthenticationSharedViewModel;
import com.smart_learn.presenter.common.fragments.helpers.BasicFragment;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;

public class EmailRegisterFragment extends BasicFragment<EmailRegisterViewModel> {

    @Getter
    private AuthenticationSharedViewModel sharedViewModel;
    @Getter
    private FragmentEmailRegisterBinding binding;

    @NonNull
    @Override
    protected @NotNull Class<EmailRegisterViewModel> getModelClassForViewModel() {
        return EmailRegisterViewModel.class;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEmailRegisterBinding.inflate(inflater);
        binding.setLifecycleOwner(this);
        binding.setSharedViewModel(sharedViewModel);
        binding.setViewModel(viewModel);

        binding.btnRegisterFragmentEmailRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.register(EmailRegisterFragment.this);
            }
        });

        return binding.getRoot();
    }

    @Override
    protected void setViewModel(){
        super.setViewModel();
        // set shared view model for passing data
        sharedViewModel = new ViewModelProvider(requireActivity()).get(AuthenticationSharedViewModel.class);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AuthenticationActivity)requireActivity()).resetToolbar(getResources().getString(R.string.register));
    }

    @Override
    public void onPause() {
        super.onPause();
        ((AuthenticationActivity)requireActivity()).dismissLoadingDialog();
    }

}