package com.smart_learn.presenter.guest.activities.notebook.fragments.words;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.smart_learn.R;
import com.smart_learn.core.common.helpers.CoreUtilities;
import com.smart_learn.data.guest.room.entitites.Word;
import com.smart_learn.presenter.guest.activities.notebook.GuestNotebookActivity;
import com.smart_learn.presenter.guest.activities.notebook.GuestNotebookSharedViewModel;
import com.smart_learn.presenter.common.activities.notebook.NotebookActivity;
import com.smart_learn.presenter.common.helpers.PresenterUtilities;
import com.smart_learn.presenter.guest.fragments.common.words.standard.GuestStandardWordsFragment;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;

public class GuestWordsFragment extends GuestStandardWordsFragment<GuestWordsViewModel> {

    @Getter
    private GuestNotebookSharedViewModel sharedViewModel;

    @NonNull
    @Override
    protected @NotNull Class<GuestWordsViewModel> getModelClassForViewModel() {
        return GuestWordsViewModel.class;
    }

    @Override
    protected boolean isFragmentWithBottomNav() {
        return true;
    }

    @Override
    protected void onAdapterSimpleClick(@NonNull @NotNull Word item) {
        super.onAdapterSimpleClick(item);
        goToGuestWordContainerFragment(item);
    }

    @Override
    protected void onActionModeCreate() {
        super.onActionModeCreate();
        ((GuestNotebookActivity)requireActivity()).hideBottomNavigationMenu();
    }

    @Override
    protected void onActionModeDestroy() {
        super.onActionModeDestroy();
        ((GuestNotebookActivity)requireActivity()).showBottomNavigationMenu();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((NotebookActivity<?>)requireActivity()).showBottomNavigationMenu();
        sharedViewModel.setSelectedWordId(GuestNotebookSharedViewModel.NO_ITEM_SELECTED);
    }

    @Override
    protected void setViewModel(){
        super.setViewModel();
        sharedViewModel = new ViewModelProvider(requireActivity()).get(GuestNotebookSharedViewModel.class);
    }

    private void goToGuestWordContainerFragment(Word word){
        if(word == null || word.getWordId() == GuestNotebookSharedViewModel.NO_ITEM_SELECTED){
            PresenterUtilities.General.showShortToastMessage(this.requireContext(),getString(R.string.error_word_can_not_be_opened));
            return;
        }

        // first set current word id (word which is clicked) and specific url`s on the shared view model
        sharedViewModel.setSelectedWordId(word.getWordId());
        String value = CoreUtilities.General.getStringForUrlSearch(word.getWord());
        sharedViewModel.setMeaningUrl("https://www.google.ro/search?q=the+meaning+of+" + value);
        sharedViewModel.setExamplesUrl("https://www.google.ro/search?q=images+example+of+" + value);

        // and then navigate
        ((GuestNotebookActivity)requireActivity()).goToGuestWordContainerFragment();
    }

}