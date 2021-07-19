package com.smart_learn.presenter.activities.notebook.helpers.fragments.home_expression;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.smart_learn.R;
import com.smart_learn.core.utilities.GeneralUtilities;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.data.helpers.DataUtilities;
import com.smart_learn.data.room.entities.helpers.Translation;
import com.smart_learn.databinding.FragmentHomeExpressionBinding;
import com.smart_learn.presenter.activities.notebook.guest.GuestNotebookSharedViewModel;
import com.smart_learn.presenter.activities.notebook.helpers.NotebookActivity;
import com.smart_learn.presenter.activities.notebook.helpers.fragments.home_word.helpers.TranslationDialog;
import com.smart_learn.presenter.activities.notebook.helpers.fragments.home_word.helpers.TranslationsAdapter;
import com.smart_learn.presenter.helpers.ItemDecoration;
import com.smart_learn.presenter.helpers.Utilities;
import com.smart_learn.presenter.helpers.dialogs.MultiLineEditableLayoutDialog;
import com.smart_learn.presenter.helpers.fragments.helpers.BasicFragment;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;


public abstract class HomeExpressionFragment <VM extends HomeExpressionViewModel> extends BasicFragment<VM> {

    @Getter
    protected FragmentHomeExpressionBinding binding;
    @Getter
    protected GuestNotebookSharedViewModel sharedViewModel;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = FragmentHomeExpressionBinding.inflate(inflater);
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
        binding.btnUpdateExpressionValueFragmentHomeExpression.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MultiLineEditableLayoutDialog dialog = new MultiLineEditableLayoutDialog(
                        getString(R.string.update_expression),
                        viewModel.getLiveExpressionValue().getValue(),
                        getString(R.string.expression),
                        DataUtilities.Limits.MAX_EXPRESSION,
                        new MultiLineEditableLayoutDialog.Callback() {
                            @Override
                            public void onUpdate(String oldValue, String newValue, @NonNull @NotNull TextInputLayout textInputLayout,
                                                 @NonNull @NotNull MultiLineEditableLayoutDialog.Listener listener) {
                                viewModel.updateExpressionValue(oldValue, newValue, textInputLayout, listener);
                            }

                        });
                dialog.show(requireActivity().getSupportFragmentManager(), "HomeExpressionFragment");
            }
        });

        binding.btnAddTranslationFragmentHomeExpression.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TranslationDialog dialog = new TranslationDialog(true,false, false, null, viewModel.getAllTranslations(),
                        new TranslationDialog.Callback() {
                            @Override
                            public void onAddOrUpdatePositiveButtonPress(@NonNull @NotNull Translation newTranslation) {
                                viewModel.addTranslation(newTranslation);
                            }
                        });
                dialog.show(requireActivity().getSupportFragmentManager(), "HomeExpressionFragment");
            }
        });

        binding.btnUpdateExpressionNotesFragmentHomeExpression.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MultiLineEditableLayoutDialog dialog = new MultiLineEditableLayoutDialog(
                        getString(R.string.update_notes),
                        viewModel.getLiveExpressionNotes().getValue(),
                        getString(R.string.notes),
                        DataUtilities.Limits.MAX_NOTES,
                        new MultiLineEditableLayoutDialog.Callback() {
                            @Override
                            public void onUpdate(String oldValue, String newValue, @NonNull @NotNull TextInputLayout textInputLayout,
                                                 @NonNull @NotNull MultiLineEditableLayoutDialog.Listener listener) {
                                viewModel.updateExpressionNotes(oldValue, newValue, textInputLayout, listener);
                            }
                        });
                dialog.show(requireActivity().getSupportFragmentManager(), "HomeExpressionFragment");
            }
        });
    }

    private void setRecyclerView(){
        Utilities.Activities.initializeRecyclerView(requireContext(), binding.rvTranslationsFragmentHomeExpression,
                new ItemDecoration(20), viewModel.getAdapter(), null);
    }

    @Override
    public void onResume() {
        super.onResume();
        Utilities.Activities.resetToolbarTitle((AppCompatActivity) requireActivity(), getString(R.string.expression_overview));
        ((NotebookActivity<?>)requireActivity()).hideBottomNavigationMenu();
    }

    @Override
    protected void setViewModel() {
        super.setViewModel();

        // set adapter
        viewModel.setAdapter(new TranslationsAdapter(new TranslationsAdapter.Callback() {
            @Override
            public boolean isForExpression() {
                return true;
            }

            @Override
            public AppCompatActivity getActivity() {
                return (AppCompatActivity) HomeExpressionFragment.this.requireActivity();
            }

            @Override
            public void onSimpleClick(@NonNull @NotNull Translation translation) {
                showTranslationDialogForSimpleView(translation);
            }

            @Override
            public void onDelete(Translation translation, @NonNull @NotNull DataCallbacks.General callback) {
                viewModel.deleteTranslation(translation, callback);
            }
        }));
    }

    private void showTranslationDialogForSimpleView(@NonNull @NotNull Translation translation){
        TranslationDialog dialog = new TranslationDialog(true,true, false, translation, viewModel.getAllTranslations(),
                new TranslationDialog.Callback() {
                    @Override
                    public void onViewPositiveButtonPress() {
                        showTranslationDialogForUpdate(translation);
                    }
                });
        dialog.show(requireActivity().getSupportFragmentManager(), "HomeExpressionFragment");
    }

    private void showTranslationDialogForUpdate(@NonNull @NotNull Translation translation){
        TranslationDialog dialog = new TranslationDialog(true,false, true, translation, viewModel.getAllTranslations(),
                new TranslationDialog.Callback() {
                    @Override
                    public void onAddOrUpdatePositiveButtonPress(@NonNull @NotNull Translation newTranslation) {
                        viewModel.updateTranslation(translation, newTranslation, new DataCallbacks.General() {
                            @Override
                            public void onSuccess() {
                                HomeExpressionFragment.this.requireActivity().runOnUiThread(() -> {
                                    GeneralUtilities.showShortToastMessage(HomeExpressionFragment.this.requireContext(),
                                            getString(R.string.success_update_translations));
                                });
                            }

                            @Override
                            public void onFailure() {
                                HomeExpressionFragment.this.requireActivity().runOnUiThread(() -> {
                                    GeneralUtilities.showShortToastMessage(HomeExpressionFragment.this.requireContext(),
                                            getString(R.string.error_update_translations));
                                });
                            }
                        });
                    }
                });
        dialog.show(requireActivity().getSupportFragmentManager(), "HomeExpressionFragment");
    }
}
