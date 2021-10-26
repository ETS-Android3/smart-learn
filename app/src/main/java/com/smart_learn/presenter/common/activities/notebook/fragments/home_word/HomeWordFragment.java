package com.smart_learn.presenter.common.activities.notebook.fragments.home_word;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.smart_learn.R;
import com.smart_learn.data.common.helpers.DataCallbacks;
import com.smart_learn.data.common.helpers.DataUtilities;
import com.smart_learn.data.common.entities.Translation;
import com.smart_learn.databinding.FragmentHomeWordBinding;
import com.smart_learn.presenter.common.activities.notebook.fragments.home_word.helpers.TranslationDialog;
import com.smart_learn.presenter.common.activities.notebook.fragments.home_word.helpers.TranslationsAdapter;
import com.smart_learn.presenter.common.adapters.helpers.ItemDecoration;
import com.smart_learn.presenter.common.helpers.PresenterUtilities;
import com.smart_learn.presenter.common.dialogs.MultiLineEditableLayoutDialog;
import com.smart_learn.presenter.common.dialogs.SingleLineEditableLayoutDialog;
import com.smart_learn.presenter.common.fragments.helpers.BasicFragment;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;

public abstract class HomeWordFragment <VM extends HomeWordViewModel> extends BasicFragment<VM> {

    @Getter
    protected FragmentHomeWordBinding binding;

    protected abstract boolean isWordOwner();

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = FragmentHomeWordBinding.inflate(inflater);
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
        setRecyclerView();
        setListeners();
    }

    private void setListeners(){
        binding.btnUpdateWordValueFragmentHomeWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SingleLineEditableLayoutDialog dialog = new SingleLineEditableLayoutDialog(
                        getString(R.string.update_word),
                        viewModel.getLiveWordValue().getValue(),
                        getString(R.string.word),
                        DataUtilities.Limits.MAX_WORD,
                        new SingleLineEditableLayoutDialog.Callback() {
                            @Override
                            public void onUpdate(String oldValue, String newValue, @NonNull @NotNull TextInputLayout textInputLayout,
                                                 @NonNull @NotNull SingleLineEditableLayoutDialog.Listener listener) {
                                viewModel.updateWordValue(oldValue, newValue, textInputLayout, listener);
                            }
                        });
                dialog.show(requireActivity().getSupportFragmentManager(), "HomeWordFragment");
            }
        });

        binding.btnUpdateWordPhoneticFragmentHomeWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SingleLineEditableLayoutDialog dialog = new SingleLineEditableLayoutDialog(
                        getString(R.string.update_phonetic),
                        viewModel.getLiveWordPhonetic().getValue(),
                        getString(R.string.phonetic),
                        DataUtilities.Limits.MAX_WORD_PHONETIC,
                        new SingleLineEditableLayoutDialog.Callback() {
                            @Override
                            public void onUpdate(String oldValue, String newValue, @NonNull @NotNull TextInputLayout textInputLayout,
                                                 @NonNull @NotNull SingleLineEditableLayoutDialog.Listener listener) {
                                viewModel.updateWordPhonetic(oldValue, newValue, textInputLayout, listener);
                            }
                        });
                dialog.show(requireActivity().getSupportFragmentManager(), "HomeWordFragment");
            }
        });

        binding.btnAddTranslationFragmentHomeWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TranslationDialog dialog = new TranslationDialog(false,false, false, isWordOwner(), null, viewModel.getAllTranslations(),
                        new TranslationDialog.Callback() {
                            @Override
                            public void onAddOrUpdatePositiveButtonPress(@NonNull @NotNull Translation newTranslation) {
                                viewModel.addTranslation(newTranslation);
                            }
                        });
                dialog.show(requireActivity().getSupportFragmentManager(), "HomeWordFragment");
            }
        });

        binding.btnUpdateWordNotesFragmentHomeWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MultiLineEditableLayoutDialog dialog = new MultiLineEditableLayoutDialog(
                        getString(R.string.update_notes),
                        viewModel.getLiveWordNotes().getValue(),
                        getString(R.string.notes),
                        DataUtilities.Limits.MAX_NOTES,
                        new MultiLineEditableLayoutDialog.Callback() {
                            @Override
                            public void onUpdate(String oldValue, String newValue, @NonNull @NotNull TextInputLayout textInputLayout,
                                                 @NonNull @NotNull MultiLineEditableLayoutDialog.Listener listener) {
                                viewModel.updateWordNotes(oldValue, newValue, textInputLayout, listener);
                            }
                        });
                dialog.show(requireActivity().getSupportFragmentManager(), "HomeWordFragment");
            }
        });
    }

    private void setRecyclerView(){
        PresenterUtilities.Activities.initializeRecyclerView(requireContext(), binding.rvTranslationsFragmentHomeWord,
                new ItemDecoration(20), viewModel.getAdapter(), null);
    }

    @Override
    protected void setViewModel() {
        super.setViewModel();

        // set adapter
        viewModel.setAdapter(new TranslationsAdapter(new TranslationsAdapter.Callback() {
            @Override
            public boolean isForExpression() {
                return false;
            }

            @Override
            public AppCompatActivity getActivity() {
                return (AppCompatActivity)HomeWordFragment.this.requireActivity();
            }

            @Override
            public void onSimpleClick(@NonNull @NotNull Translation translation) {
                showTranslationDialogForSimpleView(translation);
            }

            @Override
            public void onDelete(Translation translation, @NonNull @NotNull DataCallbacks.General callback) {
                viewModel.deleteTranslation(translation, callback);
            }

            @Override
            public boolean isOwner() {
                return isWordOwner();
            }
        }));
    }

    private void showTranslationDialogForSimpleView(@NonNull @NotNull Translation translation){
        TranslationDialog dialog = new TranslationDialog(false,true, false, isWordOwner(), translation, viewModel.getAllTranslations(),
                new TranslationDialog.Callback() {
                    @Override
                    public void onViewPositiveButtonPress() {
                        showTranslationDialogForUpdate(translation);
                    }
                });
        dialog.show(requireActivity().getSupportFragmentManager(), "HomeWordFragment");
    }

    private void showTranslationDialogForUpdate(@NonNull @NotNull Translation translation){
        TranslationDialog dialog = new TranslationDialog(false,false, true, isWordOwner(), translation, viewModel.getAllTranslations(),
                new TranslationDialog.Callback() {
                    @Override
                    public void onAddOrUpdatePositiveButtonPress(@NonNull @NotNull Translation newTranslation) {
                        viewModel.updateTranslation(translation, newTranslation, new DataCallbacks.General() {
                            @Override
                            public void onSuccess() {
                                HomeWordFragment.this.requireActivity().runOnUiThread(() -> {
                                    PresenterUtilities.General.showShortToastMessage(HomeWordFragment.this.requireContext(),
                                            getString(R.string.success_update_translations));
                                });
                            }

                            @Override
                            public void onFailure() {
                                HomeWordFragment.this.requireActivity().runOnUiThread(() -> {
                                    PresenterUtilities.General.showShortToastMessage(HomeWordFragment.this.requireContext(),
                                            getString(R.string.error_update_translations));
                                });
                            }
                        });
                    }
                });
        dialog.show(requireActivity().getSupportFragmentManager(), "HomeWordFragment");
    }
}
