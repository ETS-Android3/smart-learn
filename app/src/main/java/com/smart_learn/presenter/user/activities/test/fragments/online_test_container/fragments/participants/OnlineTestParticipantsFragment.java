package com.smart_learn.presenter.user.activities.test.fragments.online_test_container.fragments.participants;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.firestore.DocumentSnapshot;
import com.smart_learn.R;
import com.smart_learn.data.user.firebase.firestore.entities.TestDocument;
import com.smart_learn.presenter.user.activities.test.UserTestSharedViewModel;
import com.smart_learn.presenter.user.activities.test.fragments.online_test_container.OnlineTestContainerFragment;
import com.smart_learn.presenter.user.adapters.test.online.TestParticipantsAdapter;
import com.smart_learn.presenter.common.fragments.helpers.recycler_view.BasicFragmentForRecyclerView;
import com.smart_learn.presenter.common.fragments.test.tests_list.history.helpers.TestInfoDialog;

import org.jetbrains.annotations.NotNull;

import timber.log.Timber;


public class OnlineTestParticipantsFragment extends BasicFragmentForRecyclerView<OnlineTestParticipantsViewModel> {

    @NonNull
    @Override
    protected @NotNull Class<OnlineTestParticipantsViewModel> getModelClassForViewModel() {
        return OnlineTestParticipantsViewModel.class;
    }

    @Override
    protected boolean isFragmentWithBottomNav() {
        return false;
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

        viewModel.setAdapter(new TestParticipantsAdapter(sharedViewModel.getSelectedOnlineContainerTestId(), new TestParticipantsAdapter.Callback() {
            @Override
            public void onSimpleClick(@NonNull @NotNull DocumentSnapshot item) {
                showTestInfoDialog(item);
            }

            @Override
            public void onLongClick(@NonNull @NotNull DocumentSnapshot item) {
                // no action needed here
            }

            @Override
            public boolean showCheckedIcon() {
                return false;
            }

            @Override
            public boolean showToolbar() {
                return false;
            }

            @Override
            public void updateSelectedItemsCounter(int value) {
                // no action needed here
            }

            @NonNull
            @Override
            public @NotNull BasicFragmentForRecyclerView<?> getFragment() {
                return OnlineTestParticipantsFragment.this;
            }
        }));
    }

    private void showTestInfoDialog(DocumentSnapshot item){
        TestDocument test = item.toObject(TestDocument.class);
        if(test == null){
            Timber.w("testDocument is null");
            showMessage(R.string.error_can_not_open_test);
            return;
        }

        // in this fragment is an online test, but not an online test container document
        DialogFragment dialogFragment = new TestInfoDialog(test, true, false, new TestInfoDialog.Callback() {
            @Override
            public void onSeeResults() {
                // first is NavHostFragment, and then fragment OnlineTestContainerFragment where NavHost is attached
                Fragment parentFragment = requireParentFragment().requireParentFragment();
                if(parentFragment instanceof OnlineTestContainerFragment){
                    ((OnlineTestContainerFragment)parentFragment).goToUserTestResultsFragment(test.getDocumentMetadata().getOwner(), test.getType(), true);
                    return;
                }
                showMessage(R.string.error_can_not_continue);
            }

            @Override
            public void onContinueTest() {
                // no action needed here
            }
        });
        dialogFragment.show(requireActivity().getSupportFragmentManager(), "OnlineTestParticipantsFragment");
    }


}