package com.smart_learn.presenter.activities.notebook.guest.fragments.home_word;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.smart_learn.core.services.GuestWordService;
import com.smart_learn.data.room.entities.Word;
import com.smart_learn.presenter.activities.notebook.guest.GuestNotebookSharedViewModel;
import com.smart_learn.presenter.activities.notebook.helpers.fragments.home_word.HomeWordFragment;

import org.jetbrains.annotations.NotNull;


public class GuestHomeWordFragment extends HomeWordFragment<GuestHomeWordViewModel> {

    @NonNull
    @Override
    protected @NotNull Class<GuestHomeWordViewModel> getModelClassForViewModel() {
        return GuestHomeWordViewModel.class;
    }

    @Override
    protected void setViewModel(){
        super.setViewModel();

        // set shared view model
        sharedViewModel = new ViewModelProvider(requireActivity()).get(GuestNotebookSharedViewModel.class);

        GuestWordService.getInstance().getSampleLiveWord(sharedViewModel.getSelectedWordId()).observe(this, new Observer<Word>() {
            @Override
            public void onChanged(Word word) {
                viewModel.setLiveWord(word);
            }
        });
    }
}