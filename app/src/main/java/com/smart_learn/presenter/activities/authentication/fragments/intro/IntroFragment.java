package com.smart_learn.presenter.activities.authentication.fragments.intro;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.smart_learn.R;
import com.smart_learn.core.utilities.GeneralUtilities;
import com.smart_learn.databinding.BottomSheetActivityAuthenticationBinding;
import com.smart_learn.databinding.FragmentIntroBinding;
import com.smart_learn.presenter.activities.authentication.AuthenticationActivity;
import com.smart_learn.presenter.helpers.Utilities;

import org.jetbrains.annotations.NotNull;

public class IntroFragment extends Fragment {

    private NavController navController;
    private IntroViewModel introViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setViewModel();
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentIntroBinding binding = FragmentIntroBinding.inflate(inflater);
        binding.setLifecycleOwner(this);

        // set buttons listeners
        binding.btnLoginFragmentIntro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionDialog(true);
            }
        });

        binding.btnRegisterFragmentIntro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionDialog(false);
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
        ((AuthenticationActivity)requireActivity()).resetToolbar(getResources().getString(R.string.intro));
    }

    @Override
    public void onPause() {
        super.onPause();
        ((AuthenticationActivity)requireActivity()).dismissLoadingDialog();
    }

    private void showOptionDialog(boolean login){
        // set text attributes for fields
        introViewModel.setOptions(IntroFragment.this, login);

        // create dialog and load layout
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.AuthenticationActivityBottomSheetDialogTheme);

        BottomSheetActivityAuthenticationBinding bottomSheetBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.bottom_sheet_activity_authentication,null, false);
        bottomSheetBinding.setLifecycleOwner(IntroFragment.this);

        bottomSheetBinding.setViewModel(introViewModel);
        bottomSheetDialog.setContentView(bottomSheetBinding.getRoot());
        bottomSheetDialog.show();

        // set button listeners
        bottomSheetBinding.btnEmailOptionBottomSheetActivityAuthentication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                if(login){
                    navController.navigate(R.id.action_intro_fragment_nav_graph_activity_authentication_to_email_login_fragment_nav_graph_activity_authentication, null,
                            Utilities.getNavOptions());
                }
                else{
                    navController.navigate(R.id.action_intro_fragment_nav_graph_activity_authentication_to_email_register_fragment_nav_graph_activity_authentication, null,
                            Utilities.getNavOptions());
                }
            }
        });

        bottomSheetBinding.btnGoogleOptionBottomSheetActivityAuthentication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                ((AuthenticationActivity)requireActivity()).signInWithGoogle();
            }
        });

        bottomSheetBinding.btnChangeOptionBottomSheetActivityAuthentication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                showOptionDialog(!login);
            }
        });
    }

    private void setViewModel(){
        // set fragment view model
        introViewModel = new ViewModelProvider(this).get(IntroViewModel.class);
        introViewModel.getLiveToastMessage().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                GeneralUtilities.showShortToastMessage(requireContext(), s);
            }
        });
    }

}