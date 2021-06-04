package com.smart_learn.presenter.activities.authentication.fragments.login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.smart_learn.R;
import com.smart_learn.core.helpers.ResponseInfo;
import com.smart_learn.core.utilities.GeneralUtilities;
import com.smart_learn.core.utilities.NetworkUtilities;
import com.smart_learn.databinding.FragmentLoginBinding;
import com.smart_learn.presenter.activities.authentication.AuthenticationActivity;
import com.smart_learn.presenter.activities.authentication.AuthenticationSharedViewModel;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;

public class LoginFragment extends Fragment {

    private LoginViewModel loginViewModel;
    @Getter
    private AuthenticationSharedViewModel sharedViewModel;
    @Getter
    private FragmentLoginBinding binding;

    private NavController navController;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setViewModel();
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater);
        binding.setLifecycleOwner(this);
        binding.setSharedViewModel(sharedViewModel);

        // set buttons listeners
        binding.btnLoginLoginFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginViewModel.login(LoginFragment.this);
            }
        });

        binding.btnGoogleLoginFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AuthenticationActivity)requireActivity()).signInWithGoogle();
            }
        });

        binding.btnRegisterLoginFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_login_fragment_authentication_activity_to_register_fragment_authentication_activity);
            }
        });

        binding.btnForgotPasswordLoginFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginViewModel.sendPasswordResetEmail(LoginFragment.this);
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
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

    private void setViewModel(){
        // set shared view model for passing data
        sharedViewModel = new ViewModelProvider(requireActivity()).get(AuthenticationSharedViewModel.class);

        // set fragment view model
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        loginViewModel.getLiveToastMessage().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                GeneralUtilities.showShortToastMessage(requireContext(), s);
            }
        });
    }
}