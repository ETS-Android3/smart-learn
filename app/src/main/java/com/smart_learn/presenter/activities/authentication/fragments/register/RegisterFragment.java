package com.smart_learn.presenter.activities.authentication.fragments.register;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.smart_learn.R;
import com.smart_learn.core.utilities.GeneralUtilities;
import com.smart_learn.databinding.FragmentRegisterBinding;
import com.smart_learn.presenter.activities.authentication.AuthenticationActivity;
import com.smart_learn.presenter.activities.authentication.AuthenticationSharedViewModel;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;

public class RegisterFragment extends Fragment {

    private RegisterViewModel registerViewModel;
    @Getter
    private AuthenticationSharedViewModel sharedViewModel;
    @Getter
    private FragmentRegisterBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setViewModel();
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater);
        binding.setLifecycleOwner(this);
        binding.setSharedViewModel(sharedViewModel);
        binding.setViewModel(registerViewModel);

        binding.btnRegisterRegisterFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerViewModel.register(RegisterFragment.this);
            }
        });

        binding.btnGoogleRegisterFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AuthenticationActivity)requireActivity()).signInWithGoogle();
            }
        });

        binding.btnLoginRegisterFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });

        return binding.getRoot();
    }

    private void setViewModel(){
        // set shared view model for passing data
        sharedViewModel = new ViewModelProvider(requireActivity()).get(AuthenticationSharedViewModel.class);

        // set fragment view model
        registerViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);
        registerViewModel.getLiveToastMessage().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                GeneralUtilities.showShortToastMessage(requireContext(), s);
            }
        });

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