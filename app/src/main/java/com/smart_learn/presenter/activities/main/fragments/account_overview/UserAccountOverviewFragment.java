package com.smart_learn.presenter.activities.main.fragments.account_overview;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.smart_learn.core.services.UserService;
import com.smart_learn.data.firebase.firestore.entities.UserDocument;
import com.smart_learn.presenter.helpers.fragments.account_overview.AccountOverviewFragment;

import org.jetbrains.annotations.NotNull;

import timber.log.Timber;

public class UserAccountOverviewFragment extends AccountOverviewFragment<UserAccountOverviewViewModel> {

    private ListenerRegistration listenerRegistration;

    @NonNull
    @Override
    protected @NotNull Class<UserAccountOverviewViewModel> getModelClassForViewModel() {
        return UserAccountOverviewViewModel.class;
    }

    @Override
    public void onStart() {
        super.onStart();
        listenerRegistration = UserService.getInstance()
                .getUserDocumentReference()
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Timber.e(error);
                            return;
                        }

                        if(value == null){
                            Timber.w("value is null");
                            return;
                        }

                        UserDocument userDocument = value.toObject(UserDocument.class);
                        if(userDocument == null){
                            Timber.i("userDocument is null");
                            return;
                        }

                        viewModel.setValues(userDocument);
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(listenerRegistration != null){
            listenerRegistration.remove();
            listenerRegistration = null;
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // this is a user fragment so a user test summary will appear
        binding.linearLayoutTestsSummaryUserFragmentAccountOverview.setVisibility(View.VISIBLE);
        binding.linearLayoutTestsSummaryGuestFragmentAccountOverview.setVisibility(View.GONE);
        return binding.getRoot();
    }
}