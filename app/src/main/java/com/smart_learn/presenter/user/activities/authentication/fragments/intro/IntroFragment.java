package com.smart_learn.presenter.user.activities.authentication.fragments.intro;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.smart_learn.R;
import com.smart_learn.databinding.FragmentIntroBinding;
import com.smart_learn.databinding.LayoutBottomSheetAuthOptionsBinding;
import com.smart_learn.presenter.user.activities.authentication.AuthenticationActivity;
import com.smart_learn.presenter.common.helpers.PresenterUtilities;
import com.smart_learn.presenter.common.fragments.helpers.BasicFragment;

import org.jetbrains.annotations.NotNull;

public class IntroFragment extends BasicFragment<IntroViewModel> {

    private NavController navController;

    @NonNull
    @Override
    protected @NotNull Class<IntroViewModel> getModelClassForViewModel() {
        return IntroViewModel.class;
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
        viewModel.setOptions(IntroFragment.this, login);

        // create dialog and load layout
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.AuthenticationActivityBottomSheetDialogTheme);

        LayoutBottomSheetAuthOptionsBinding bottomSheetBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.layout_bottom_sheet_auth_options,null, false);
        bottomSheetBinding.setLifecycleOwner(IntroFragment.this);

        bottomSheetBinding.setViewModel(viewModel);
        bottomSheetDialog.setContentView(bottomSheetBinding.getRoot());
        bottomSheetDialog.show();

        // set button listeners
        bottomSheetBinding.btnEmailOptionLayoutBottomSheetAuthOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                if(login){
                    navController.navigate(R.id.action_intro_fragment_to_email_login_fragment_nav_graph_activity_authentication, null,
                            PresenterUtilities.getNavOptions());
                }
                else{
                    navController.navigate(R.id.action_intro_fragment_to_email_register_fragment_nav_graph_activity_authentication, null,
                            PresenterUtilities.getNavOptions());
                }
            }
        });

        bottomSheetBinding.btnGoogleOptionLayoutBottomSheetAuthOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                ((AuthenticationActivity)requireActivity()).signInWithGoogle();
            }
        });

        bottomSheetBinding.btnChangeOptionLayoutBottomSheetAuthOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                showOptionDialog(!login);
            }
        });
    }

}