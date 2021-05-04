package com.smart_learn.presenter.activities.authentication.fragments.register;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.smart_learn.R;
import com.smart_learn.core.helpers.ResponseInfo;
import com.smart_learn.core.utilities.GeneralUtilities;
import com.smart_learn.databinding.FragmentRegisterBinding;
import com.smart_learn.presenter.activities.authentication.AuthenticationActivity;
import com.smart_learn.presenter.activities.authentication.AuthenticationSharedViewModel;
import com.smart_learn.presenter.activities.authentication.helpers.LoginForm;

import org.jetbrains.annotations.NotNull;

public class RegisterFragment extends Fragment {

    private RegisterViewModel registerViewModel;
    private AuthenticationSharedViewModel sharedViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setViewModel();
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentRegisterBinding binding = FragmentRegisterBinding.inflate(inflater);
        binding.setLifecycleOwner(this);
        binding.setSharedViewModel(sharedViewModel);
        binding.setViewModel(registerViewModel);

        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResponseInfo responseInfo = registerViewModel.getLiveRegisterForm().getValue().goodRegisterCredentials();
                if(responseInfo.isOk()){
                    registerViewModel.register(registerViewModel.getLiveRegisterForm().getValue());
                    return;
                }

                // register credentials are not good
                GeneralUtilities.showShortToastMessage(requireContext(), responseInfo.getInfo());
            }
        });
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void setViewModel(){
        // set shared view model for passing data
        sharedViewModel = new ViewModelProvider(requireActivity()).get(AuthenticationSharedViewModel.class);

        // when login data from shared view model are changed, modify data in fragment view model also
        sharedViewModel.getLiveLoginForm().observe(this, new Observer<LoginForm>() {
            @Override
            public void onChanged(LoginForm loginForm) {
                registerViewModel.getLiveRegisterForm().getValue().updateFromLoginForm(loginForm);
            }
        });

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
        // update register form if data were passing
        registerViewModel.getLiveRegisterForm().getValue().updateFromLoginForm(sharedViewModel.getLiveLoginForm().getValue());
    }

}