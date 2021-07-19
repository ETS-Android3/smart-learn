package com.smart_learn.presenter.activities.notebook.helpers.fragments.home_lesson;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.smart_learn.R;
import com.smart_learn.data.helpers.DataUtilities;
import com.smart_learn.databinding.FragmentHomeLessonBinding;
import com.smart_learn.presenter.activities.notebook.helpers.NotebookActivity;
import com.smart_learn.presenter.helpers.Utilities;
import com.smart_learn.presenter.helpers.dialogs.MultiLineEditableLayoutDialog;
import com.smart_learn.presenter.helpers.dialogs.SingleLineEditableLayoutDialog;
import com.smart_learn.presenter.helpers.fragments.helpers.BasicFragment;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;

public abstract class HomeLessonFragment <VM extends HomeLessonViewModel> extends BasicFragment<VM> {

    @Getter
    protected FragmentHomeLessonBinding binding;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = FragmentHomeLessonBinding.inflate(inflater);
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

        binding.btnUpdateLessonNameFragmentHomeLesson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SingleLineEditableLayoutDialog dialog = new SingleLineEditableLayoutDialog(
                        getString(R.string.update_lesson),
                        viewModel.getLiveLessonName().getValue(),
                        getString(R.string.lesson_name),
                        DataUtilities.Limits.MAX_LESSON_NAME,
                        new SingleLineEditableLayoutDialog.Callback() {
                            @Override
                            public void onUpdate(String oldValue, String newValue, @NonNull @NotNull TextInputLayout textInputLayout,
                                                 @NonNull @NotNull SingleLineEditableLayoutDialog.Listener listener) {
                                viewModel.updateLessonName(oldValue, newValue, textInputLayout, listener);
                            }
                });
                dialog.show(requireActivity().getSupportFragmentManager(), "HomeLessonFragment");
            }
        });

        binding.btnUpdateLessonNotesFragmentHomeLesson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MultiLineEditableLayoutDialog dialog = new MultiLineEditableLayoutDialog(
                        getString(R.string.update_notes),
                        viewModel.getLiveLessonNotes().getValue(),
                        DataUtilities.Limits.MAX_NOTES,
                        new MultiLineEditableLayoutDialog.Callback() {
                            @Override
                            public void onUpdate(String oldValue, String newValue, @NonNull @NotNull TextInputLayout textInputLayout,
                                                 @NonNull @NotNull MultiLineEditableLayoutDialog.Listener listener) {
                                viewModel.updateLessonNotes(oldValue, newValue, textInputLayout, listener);
                            }
                        });
                dialog.show(requireActivity().getSupportFragmentManager(), "HomeLessonFragment");
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Utilities.Activities.resetToolbarTitle((AppCompatActivity) requireActivity(), getString(R.string.lesson_overview));
        ((NotebookActivity<?>)requireActivity()).showBottomNavigationMenu();
    }
}

