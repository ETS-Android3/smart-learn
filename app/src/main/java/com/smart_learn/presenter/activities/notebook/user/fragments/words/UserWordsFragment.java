package com.smart_learn.presenter.activities.notebook.user.fragments.words;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.firestore.DocumentSnapshot;
import com.smart_learn.R;
import com.smart_learn.core.utilities.CoreUtilities;
import com.smart_learn.core.utilities.GeneralUtilities;
import com.smart_learn.data.firebase.firestore.entities.WordDocument;
import com.smart_learn.presenter.activities.notebook.helpers.fragments.words.WordsFragment;
import com.smart_learn.presenter.activities.notebook.user.UserNotebookActivity;
import com.smart_learn.presenter.activities.notebook.user.UserNotebookSharedViewModel;
import com.smart_learn.presenter.activities.notebook.user.fragments.words.helpers.WordsAdapter;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;


public class UserWordsFragment extends WordsFragment<UserWordsViewModel> {

    @Getter
    private UserNotebookSharedViewModel sharedViewModel;

    @NonNull
    @Override
    protected @NotNull Class<UserWordsViewModel> getModelClassForViewModel() {
        return UserWordsViewModel.class;
    }

    @Override
    protected int getBottomSheetLayout() {
        return R.layout.layout_action_mode_fragment_user_words;
    }

    @Override
    protected int getParentBottomSheetLayoutId() {
        return R.id.parent_layout_include_layout_action_mode_fragment_user_words;
    }

    @Override
    protected void onFilter(String newText) {
        if(viewModel.getAdapter() == null){
            return;
        }

        if(TextUtils.isEmpty(newText)){
            viewModel.getAdapter().setInitialOption(UserWordsFragment.this);
        }
        else {
            viewModel.getAdapter().setFilterOption(UserWordsFragment.this, newText);
        }
    }

    @Override
    protected void onActionModeCreate() {
        if(viewModel.getAdapter() != null){
            ((UserNotebookActivity)requireActivity()).hideBottomNavigationMenu();
            viewModel.getAdapter().resetSelectedItems();
            viewModel.getAdapter().setLiveActionMode(true);
        }
    }

    @Override
    protected void onActionModeDestroy() {
        if(viewModel.getAdapter() != null){
            ((UserNotebookActivity)requireActivity()).showBottomNavigationMenu();
            viewModel.getAdapter().resetSelectedItems();
            viewModel.getAdapter().setLiveActionMode(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        sharedViewModel.setSelectedWord(UserNotebookSharedViewModel.NO_DOCUMENT_SELECTED);
    }

    @Override
    protected void setLayoutUtilities(){
        super.setLayoutUtilities();

        // set bottom sheet listeners
        Button btnDeleteSelected = requireActivity().findViewById(R.id.btn_delete_include_layout_action_mode_fragment_user_words);
        btnDeleteSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.deleteSelectedWords();
            }
        });
    }


    @Override
    protected void setViewModel(){
        super.setViewModel();

        // set shared view model
        sharedViewModel = new ViewModelProvider(requireActivity()).get(UserNotebookSharedViewModel.class);

        // set current lesson on view model for further operations inside view model
        viewModel.setCurrentLessonSnapshot(sharedViewModel.getSelectedLesson());

        // set fragment view model adapter
        viewModel.setAdapter(new WordsAdapter(sharedViewModel.getSelectedLesson(), new WordsAdapter.Callback<UserWordsFragment>() {
            @Override
            public UserWordsFragment getFragment() {
                return UserWordsFragment.this;
            }
        }));

    }

    public void goToUserWordContainerFragment(DocumentSnapshot wordSnapshot){
        // when navigation is made a valid word must be set on shared view model
        if(wordSnapshot.equals(UserNotebookSharedViewModel.NO_DOCUMENT_SELECTED)){
            GeneralUtilities.showShortToastMessage(this.requireContext(),getString(R.string.error_word_can_not_be_opened));
            return;
        }

        WordDocument word = wordSnapshot.toObject(WordDocument.class);
        if(word == null){
            GeneralUtilities.showShortToastMessage(this.requireContext(),getString(R.string.error_word_can_not_be_opened));
            return;
        }

        // First set current word snapshot on the shared view model and specific url`s on the shared view model
        // then you can navigate.
        sharedViewModel.setSelectedWord(wordSnapshot);
        String value = CoreUtilities.General.getStringForUrlSearch(word.getWord());
        sharedViewModel.setMeaningUrl("https://www.google.ro/search?q=the+meaning+of+" + value);
        sharedViewModel.setExamplesUrl("https://www.google.ro/search?q=images+example+of+" + value);

        // and then navigate
        ((UserNotebookActivity)requireActivity()).goToUserWordContainerFragment();
    }

    public void showSelectedItems(int value){
        this.requireActivity().runOnUiThread(() -> {
            if(actionMode != null) {
                actionMode.setTitle(getString(R.string.selected_words_point) + " " + value);
            }
        });
    }
}