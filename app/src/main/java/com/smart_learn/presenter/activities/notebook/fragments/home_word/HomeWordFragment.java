package com.smart_learn.presenter.activities.notebook.fragments.home_word;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.smart_learn.R;
import com.smart_learn.core.utilities.GeneralUtilities;
import com.smart_learn.data.models.room.entities.Word;
import com.smart_learn.databinding.FragmentHomeWordBinding;
import com.smart_learn.presenter.activities.notebook.NotebookActivity;
import com.smart_learn.presenter.activities.notebook.NotebookSharedViewModel;
import com.smart_learn.presenter.helpers.Callbacks;
import com.smart_learn.presenter.helpers.Utilities;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;

public class HomeWordFragment extends Fragment {

    @Getter
    private FragmentHomeWordBinding binding;
    @Getter
    private HomeWordViewModel homeWordViewModel;
    @Getter
    private NotebookSharedViewModel sharedViewModel;


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeWordBinding.inflate(inflater);
        binding.setLifecycleOwner(this);
        setViewModel();
        binding.setViewModel(homeWordViewModel);
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
        Utilities.Activities.resetToolbarTitle((AppCompatActivity) requireActivity(),getResources().getString(R.string.home));
        ((NotebookActivity)requireActivity()).hideBottomNavigationMenu();
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

    private void setViewModel(){
        // set shared view model
        sharedViewModel = new ViewModelProvider(requireActivity()).get(NotebookSharedViewModel.class);

        // set fragment view model
        homeWordViewModel = new ViewModelProvider(this).get(HomeWordViewModel.class);

        // set observers
        homeWordViewModel.getLiveToastMessage().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                GeneralUtilities.showShortToastMessage(requireContext(), s);
            }
        });

        homeWordViewModel.getWordsService().getSampleLiveWord(sharedViewModel.getSelectedWordId()).observe(getViewLifecycleOwner(), new Observer<Word>() {
            @Override
            public void onChanged(Word word) {
                homeWordViewModel.setLiveWord(word);
            }
        });
    }
}
