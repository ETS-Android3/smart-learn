package com.smart_learn.presenter.activities.test.user.fragments.online_test_container.fragments.messages_container;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.smart_learn.R;
import com.smart_learn.databinding.FragmentOnlineTestMessagesContainerBinding;
import com.smart_learn.presenter.activities.test.user.UserTestSharedViewModel;
import com.smart_learn.presenter.helpers.fragments.helpers.BasicFragment;

import org.jetbrains.annotations.NotNull;


public class OnlineTestMessagesContainerFragment extends BasicFragment<OnlineTestMessagesContainerViewModel> {

    protected FragmentOnlineTestMessagesContainerBinding binding;

    @NonNull
    @Override
    protected @NotNull Class<OnlineTestMessagesContainerViewModel> getModelClassForViewModel() {
        return OnlineTestMessagesContainerViewModel.class;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = FragmentOnlineTestMessagesContainerBinding.inflate(inflater);
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setLayoutUtilities();
    }

    protected void setLayoutUtilities(){

        binding.btnSendMessageFragmentOnlineTestMessagesContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.sendMessage();
            }
        });

    }

    @Override
    protected void setViewModel() {
        super.setViewModel();

        UserTestSharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(UserTestSharedViewModel.class);

        if (sharedViewModel.getSelectedOnlineContainerTestId() == null || sharedViewModel.getSelectedOnlineContainerTestId().isEmpty()) {
            showMessage(R.string.error_can_not_continue);
            requireActivity().onBackPressed();
            return;
        }

        viewModel.setTestId(sharedViewModel.getSelectedOnlineContainerTestId());
    }
}