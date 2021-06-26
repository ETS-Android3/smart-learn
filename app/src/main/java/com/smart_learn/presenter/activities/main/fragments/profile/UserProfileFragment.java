package com.smart_learn.presenter.activities.main.fragments.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.smart_learn.R;
import com.smart_learn.core.utilities.CoreUtilities;
import com.smart_learn.core.utilities.GeneralUtilities;
import com.smart_learn.databinding.FragmentUserProfileBinding;
import com.smart_learn.presenter.activities.main.MainActivity;
import com.smart_learn.presenter.helpers.Callbacks;
import com.smart_learn.presenter.helpers.Utilities;

import org.jetbrains.annotations.NotNull;

public class UserProfileFragment extends Fragment {

    private FragmentUserProfileBinding binding;
    private UserProfileViewModel userProfileViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setViewModel();
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUserProfileBinding.inflate(inflater);
        binding.setLifecycleOwner(this);
        binding.setViewModel(userProfileViewModel);

        // if is email-password provider display name can be changed, otherwise not
        if(userProfileViewModel.getProviderId() == CoreUtilities.Auth.PROVIDER_GOOGLE){
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
        Utilities.Activities.resetToolbarTitle((AppCompatActivity) requireActivity(),getResources().getString(R.string.account));
    }

    private void setLayoutUtilities(){

        binding.btnDeleteAccountFragmentUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = UserProfileFragment.this.getString(R.string.delete_account);
                String description = UserProfileFragment.this.getString(R.string.delete_account_description);

                Utilities.Activities.showStandardAlertDialog(UserProfileFragment.this.requireContext(),
                        title, description, new Callbacks.StandardAlertDialogCallback() {
                    @Override
                    public void onPositiveButtonPress() {
                        userProfileViewModel.deleteAccount(UserProfileFragment.this);
                    }
                });
            }
        });

        // layout for profile name (if is email-password provider display name can be changed, otherwise not)
        Utilities.Activities.setCustomEditableLayout(binding.toolbarFragmentUserProfile, binding.layoutProfileNameFragmentUserProfile,
                binding.tvProfileNameFragmentUserProfile, new Callbacks.CustomEditableLayoutCallback() {
                    @Override
                    public void savePreviousValue() {
                        userProfileViewModel.savePreviousProfileName();
                    }

                    @Override
                    public void revertToPreviousValue() {
                        userProfileViewModel.revertToPreviousProfileName();
                    }

                    @Override
                    public boolean isCurrentValueOk() {
                        return userProfileViewModel.goodProfileName(binding.layoutProfileNameFragmentUserProfile);
                    }

                    @Override
                    public void saveCurrentValue() {
                        userProfileViewModel.saveProfileName();
                    }
                });
    }

    private void setViewModel(){
        userProfileViewModel = new ViewModelProvider(this).get(UserProfileViewModel.class);

        // set observers
        userProfileViewModel.getLiveToastMessage().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                GeneralUtilities.showShortToastMessage(requireContext(), s);
            }
        });
    }

    protected void goToGuestActivity(){
        ((MainActivity)requireActivity()).goToGuestActivity();
    }
}