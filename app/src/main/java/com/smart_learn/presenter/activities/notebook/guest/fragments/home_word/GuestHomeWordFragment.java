package com.smart_learn.presenter.activities.notebook.guest.fragments.home_word;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.smart_learn.R;
import com.smart_learn.core.services.GuestWordService;
import com.smart_learn.data.room.entities.Word;
import com.smart_learn.presenter.activities.notebook.helpers.fragments.home_word.HomeWordFragment;
import com.smart_learn.presenter.activities.notebook.guest.fragments.GuestNotebookActivity;
import com.smart_learn.presenter.activities.notebook.guest.fragments.GuestNotebookSharedViewModel;
import com.smart_learn.presenter.helpers.Callbacks;
import com.smart_learn.presenter.helpers.Utilities;

import org.jetbrains.annotations.NotNull;


public class GuestHomeWordFragment extends HomeWordFragment<GuestHomeWordViewModel> {

    @NonNull
    @Override
    protected @NotNull Class<GuestHomeWordViewModel> getModelClassForViewModel() {
        return GuestHomeWordViewModel.class;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setLayoutUtilities();
    }

    @Override
    public void onResume() {
        super.onResume();
        Utilities.Activities.resetToolbarTitle((AppCompatActivity) requireActivity(),getResources().getString(R.string.home));
        ((GuestNotebookActivity)requireActivity()).hideBottomNavigationMenu();
    }

    private void setLayoutUtilities(){
        Utilities.Activities.setCustomEditableLayout(binding.toolbarFragmentHomeWord, binding.layoutLessonNameFragmentHomeWord,
                binding.tvLessonNameFragmentHomeWord, new Callbacks.CustomEditableLayoutCallback() {
                    @Override
                    public void savePreviousValue() {

                    }

                    @Override
                    public void revertToPreviousValue() {

                    }

                    @Override
                    public boolean isCurrentValueOk() {
                        return false;
                    }

                    @Override
                    public void saveCurrentValue() {

                    }

                });
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