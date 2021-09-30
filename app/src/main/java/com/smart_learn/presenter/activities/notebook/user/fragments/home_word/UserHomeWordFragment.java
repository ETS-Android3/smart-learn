package com.smart_learn.presenter.activities.notebook.user.fragments.home_word;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.smart_learn.data.firebase.firestore.entities.WordDocument;
import com.smart_learn.presenter.activities.notebook.helpers.fragments.home_word.HomeWordFragment;
import com.smart_learn.presenter.activities.notebook.user.UserNotebookSharedViewModel;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import timber.log.Timber;


public class UserHomeWordFragment extends HomeWordFragment<UserHomeWordViewModel> {

    public static final String IS_WORD_OWNER = "IS_WORD_OWNER";

    @Getter
    protected UserNotebookSharedViewModel sharedViewModel;

    @NonNull
    @Override
    protected @NotNull Class<UserHomeWordViewModel> getModelClassForViewModel() {
        return UserHomeWordViewModel.class;
    }

    @Override
    protected boolean isWordOwner() {
        return getArguments() != null && getArguments().getBoolean(IS_WORD_OWNER);
    }

    @Override
    protected void setViewModel(){
        super.setViewModel();

        // set shared view model
        sharedViewModel = new ViewModelProvider(requireActivity()).get(UserNotebookSharedViewModel.class);
    }

    @Override
    public void onStart() {
        super.onStart();

        // set listener
        // https://www.youtube.com/watch?v=LfkhFCDnkS0&ab_channel=CodinginFlow
        sharedViewModel
                .getSelectedWord()
                .getReference()
                .addSnapshotListener(this.requireActivity(), new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot value,
                                        @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Timber.e(error);
                            return;
                        }

                        if(value == null){
                            Timber.i("value is null");
                            return;
                        }

                        WordDocument wordDocument = value.toObject(WordDocument.class);
                        if(wordDocument == null){
                            Timber.i("wordDocument is null");
                            return;
                        }

                        viewModel.setLiveWord(value, wordDocument);
                    }
                });
    }
}