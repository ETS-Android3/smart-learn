package com.smart_learn.presenter.activities.main.fragments.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.smart_learn.R;
import com.smart_learn.core.helpers.CoreUtilities;
import com.smart_learn.databinding.FragmentUserProfileBinding;
import com.smart_learn.presenter.activities.main.MainActivity;
import com.smart_learn.presenter.helpers.Callbacks;
import com.smart_learn.presenter.helpers.PresenterUtilities;
import com.smart_learn.presenter.helpers.fragments.helpers.BasicFragment;

import org.jetbrains.annotations.NotNull;

public class UserProfileFragment extends BasicFragment<UserProfileViewModel> {

    private FragmentUserProfileBinding binding;

    @NonNull
    @Override
    protected @NotNull Class<UserProfileViewModel> getModelClassForViewModel() {
        return UserProfileViewModel.class;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUserProfileBinding.inflate(inflater);
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);

        // if is email-password provider display name can be changed, otherwise not
        if(viewModel.getProviderId() == CoreUtilities.Auth.PROVIDER_GOOGLE){
            binding.linearLayoutProfileGoogleProviderFragmentUserProfile.setVisibility(View.VISIBLE);
            binding.linearLayoutProfileEmailProviderFragmentUserProfile.setVisibility(View.GONE);
        }
        else{
            binding.linearLayoutProfileGoogleProviderFragmentUserProfile.setVisibility(View.GONE);
            binding.linearLayoutProfileEmailProviderFragmentUserProfile.setVisibility(View.VISIBLE);
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setLayoutUtilities();
    }

    @Override
    public void onResume() {
        super.onResume();
        PresenterUtilities.Activities.resetToolbarTitle((AppCompatActivity) requireActivity(),getResources().getString(R.string.account));
    }

    private void setLayoutUtilities(){

        binding.btnDeleteAccountFragmentUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = UserProfileFragment.this.getString(R.string.delete_account);
                String description = UserProfileFragment.this.getString(R.string.delete_account_description);

                PresenterUtilities.Activities.showStandardAlertDialog(UserProfileFragment.this.requireContext(),
                        title, description, new Callbacks.StandardAlertDialogCallback() {
                    @Override
                    public void onPositiveButtonPress() {
                        viewModel.deleteAccount(UserProfileFragment.this);
                    }
                });
            }
        });

        // layout for profile name (if is email-password provider display name can be changed, otherwise not)
        PresenterUtilities.Activities.setCustomEditableLayout(binding.toolbarFragmentUserProfile, binding.layoutProfileNameFragmentUserProfile,
                binding.tvProfileNameFragmentUserProfile, new Callbacks.CustomEditableLayoutCallback() {
                    @Override
                    public void savePreviousValue() {
                        viewModel.savePreviousProfileName();
                    }

                    @Override
                    public void revertToPreviousValue() {
                        viewModel.revertToPreviousProfileName();
                    }

                    @Override
                    public boolean isCurrentValueOk() {
                        return viewModel.goodProfileName(binding.layoutProfileNameFragmentUserProfile);
                    }

                    @Override
                    public void saveCurrentValue() {
                        viewModel.saveProfileName();
                    }
                });
    }

    protected void goToGuestActivity(){
        ((MainActivity)requireActivity()).goToGuestActivity();
    }
}