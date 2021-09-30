package com.smart_learn.presenter.activities.notebook.user.fragments.words;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.firestore.DocumentSnapshot;
import com.smart_learn.R;
import com.smart_learn.core.services.UserService;
import com.smart_learn.core.helpers.CoreUtilities;
import com.smart_learn.data.firebase.firestore.entities.WordDocument;
import com.smart_learn.presenter.activities.notebook.helpers.NotebookActivity;
import com.smart_learn.presenter.activities.notebook.user.UserNotebookActivity;
import com.smart_learn.presenter.activities.notebook.user.UserNotebookSharedViewModel;
import com.smart_learn.presenter.helpers.PresenterUtilities;
import com.smart_learn.presenter.helpers.fragments.words.user.standard.UserStandardWordsFragment;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;


public class UserWordsFragment extends UserStandardWordsFragment<UserWordsViewModel> {

    @Getter
    private UserNotebookSharedViewModel sharedViewModel;

    @NonNull
    @Override
    protected @NotNull Class<UserWordsViewModel> getModelClassForViewModel() {
        return UserWordsViewModel.class;
    }

    @Override
    protected boolean isFragmentWithBottomNav() {
        return true;
    }

    @Override
    protected void onAdapterSimpleClick(@NonNull @NotNull DocumentSnapshot item) {
        super.onAdapterSimpleClick(item);
        goToUserWordContainerFragment(item);
    }

    @Override
    protected void onActionModeCreate() {
        super.onActionModeCreate();
        ((UserNotebookActivity)requireActivity()).hideBottomNavigationMenu();
    }

    @Override
    protected void onActionModeDestroy() {
        super.onActionModeDestroy();
        ((UserNotebookActivity)requireActivity()).showBottomNavigationMenu();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((NotebookActivity<?>)requireActivity()).showBottomNavigationMenu();
        sharedViewModel.setSelectedWord(null);
    }

    @Override
    protected void setViewModel(){
        super.setViewModel();

        // set shared view model
        sharedViewModel = new ViewModelProvider(requireActivity()).get(UserNotebookSharedViewModel.class);
    }

    private void goToUserWordContainerFragment(DocumentSnapshot wordSnapshot){
        // when navigation is made a valid word must be set on shared view model
        if(wordSnapshot == null){
            PresenterUtilities.General.showShortToastMessage(this.requireContext(),getString(R.string.error_word_can_not_be_opened));
            return;
        }

        WordDocument word = wordSnapshot.toObject(WordDocument.class);
        if(word == null){
            PresenterUtilities.General.showShortToastMessage(this.requireContext(),getString(R.string.error_word_can_not_be_opened));
            return;
        }

        // First set current word snapshot on the shared view model and specific url`s on the shared view model
        // then you can navigate.
        sharedViewModel.setSelectedWord(wordSnapshot);
        String value = CoreUtilities.General.getStringForUrlSearch(word.getWord());
        sharedViewModel.setMeaningUrl("https://www.google.ro/search?q=the+meaning+of+" + value);
        sharedViewModel.setExamplesUrl("https://www.google.ro/search?q=images+example+of+" + value);

        // and then navigate
        final boolean isWordOwner = word.getDocumentMetadata().getOwner().equals(UserService.getInstance().getUserUid());
        ((UserNotebookActivity)requireActivity()).goToUserWordContainerFragment(isWordOwner);
    }

}