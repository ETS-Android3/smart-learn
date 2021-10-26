package com.smart_learn.presenter.user.activities.main.profile;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.smart_learn.R;
import com.smart_learn.core.common.helpers.CoreUtilities;
import com.smart_learn.databinding.FragmentUserProfileBinding;
import com.smart_learn.presenter.user.activities.main.MainActivity;
import com.smart_learn.presenter.common.helpers.PresenterCallbacks;
import com.smart_learn.presenter.common.helpers.PresenterUtilities;
import com.smart_learn.presenter.common.fragments.helpers.BasicFragment;

import org.jetbrains.annotations.NotNull;

public class UserProfileFragment extends BasicFragment<UserProfileViewModel> {

    public static final int LOAD_IMAGE_REQUEST = 1;

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

        // if is email-password provider display name and profile photo can be changed, otherwise not
        if(viewModel.getProviderId() == CoreUtilities.Auth.PROVIDER_GOOGLE){
            binding.linearLayoutProfileGoogleProviderFragmentUserProfile.setVisibility(View.VISIBLE);
            binding.linearLayoutProfileEmailProviderFragmentUserProfile.setVisibility(View.GONE);
            binding.btnChangeProfileImageFragmentUserProfile.setVisibility(View.INVISIBLE);
        }
        else {
            binding.linearLayoutProfileGoogleProviderFragmentUserProfile.setVisibility(View.GONE);
            binding.linearLayoutProfileEmailProviderFragmentUserProfile.setVisibility(View.VISIBLE);
            binding.btnChangeProfileImageFragmentUserProfile.setVisibility(View.VISIBLE);
        }

        // use this to set toolbar menu inside fragment
        // https://stackoverflow.com/questions/15653737/oncreateoptionsmenu-inside-fragments/31360073#31360073
        setHasOptionsMenu(true);

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

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_toolbar_fragment_user_profile, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        int id = item.getItemId();
        if(id == R.id.action_delete_menu_toolbar_fragment_user_profile){
            onDeleteAccountPress();
            return true;
        }
        return false;
    }

    private void setLayoutUtilities(){

        binding.btnChangeProfileImageFragmentUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser();
            }
        });

        // layout for profile name (if is email-password provider display name can be changed, otherwise not)
        PresenterUtilities.Activities.setCustomEditableLayout(binding.toolbarFragmentUserProfile, binding.layoutProfileNameFragmentUserProfile,
                binding.tvProfileNameFragmentUserProfile, new PresenterCallbacks.CustomEditableLayoutCallback() {
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

        // set observers
        viewModel.getLiveProfilePhotoUri().observe(getViewLifecycleOwner(), new Observer<Uri>() {
            @Override
            public void onChanged(Uri uri) {
                loadProfileImage(uri);
            }
        });
    }

    protected void goToGuestActivity(){
        ((MainActivity)requireActivity()).goToGuestActivity();
    }

    private void onDeleteAccountPress(){
        String title = UserProfileFragment.this.getString(R.string.delete_account);
        String description = UserProfileFragment.this.getString(R.string.delete_account_description);

        PresenterUtilities.Activities.showStandardAlertDialog(UserProfileFragment.this.requireContext(),
                title, description, new PresenterCallbacks.StandardAlertDialogCallback() {
                    @Override
                    public void onPositiveButtonPress() {
                        viewModel.deleteAccount(UserProfileFragment.this);
                    }
                });
    }

    private void openImageChooser(){
        // https://www.youtube.com/watch?v=gqIWrNitbbk&ab_channel=CodinginFlow
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, LOAD_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK && requestCode == LOAD_IMAGE_REQUEST && data != null && data.getData() != null){
            viewModel.uploadImage(data.getData(), this);
        }
    }

    protected void loadProfileImage(Uri image) {
        PresenterUtilities.Activities.loadProfileImage(image, binding.ivProfileFragmentUserProfile);
    }
}