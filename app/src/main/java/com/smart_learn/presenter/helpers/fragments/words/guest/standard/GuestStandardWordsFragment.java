package com.smart_learn.presenter.helpers.fragments.words.guest.standard;

import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.smart_learn.R;
import com.smart_learn.data.room.entities.Word;
import com.smart_learn.data.room.entities.helpers.Translation;
import com.smart_learn.presenter.helpers.PresenterUtilities;
import com.smart_learn.presenter.helpers.fragments.words.helpers.WordDialog;
import com.smart_learn.presenter.helpers.Callbacks;
import com.smart_learn.presenter.helpers.fragments.words.guest.GuestBasicWordsFragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public abstract class GuestStandardWordsFragment <VM extends GuestStandardWordsViewModel> extends GuestBasicWordsFragment<VM> {

    @Override
    protected boolean showFloatingActionButton() {
        return true;
    }

    @Override
    protected void onFloatingActionButtonPress() {
        showAddWordDialog();
    }

    @Override
    protected boolean isBottomSheetUsed() {
        return true;
    }

    @Override
    protected int getBottomSheetLayout() {
        return R.layout.layout_action_mode_fragment_guest_words;
    }

    @Override
    protected int getParentBottomSheetLayoutId() {
        return R.id.parent_layout_include_layout_action_mode_fragment_guest_words;
    }

    @Override
    protected boolean onAdapterShowOptionsToolbar() {
        return true;
    }

    @Override
    protected boolean onAdapterShowCheckedIcon() {
        return true;
    }

    @Override
    protected void onAdapterLongClick(@NonNull @NotNull Word item) {
        super.onAdapterLongClick(item);
        startFragmentActionMode();
    }

    @Override
    protected void onAdapterUpdateSelectedItemsCounter(int value) {
        super.onAdapterUpdateSelectedItemsCounter(value);
        showSelectedItems(value);
    }

    @Override
    protected void setLayoutUtilities(){
        super.setLayoutUtilities();

        // set bottom sheet listeners
        Button btnSelectAll = requireActivity().findViewById(R.id.btn_select_include_layout_action_mode_fragment_guest_words);
        btnSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.setAllItemsAreSelected(!viewModel.isAllItemsAreSelected());
                PresenterUtilities.Activities.changeSelectAllButtonStatus(viewModel.isAllItemsAreSelected(), btnSelectAll);
            }
        });

        Button btnDeleteSelected = requireActivity().findViewById(R.id.btn_delete_include_layout_action_mode_fragment_guest_words);
        btnDeleteSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.deleteSelectedWords();
                viewModel.setAllItemsAreSelected(false);
                PresenterUtilities.Activities.changeSelectAllButtonStatus(viewModel.isAllItemsAreSelected(), btnSelectAll);
            }
        });
    }

    private void showSelectedItems(int value){
        this.requireActivity().runOnUiThread(() -> {
            if(actionMode != null) {
                actionMode.setTitle(getString(R.string.selected_words_point) + " " + value);
            }
        });
    }

    private void startFragmentActionMode() {
        super.startActionMode(new Callbacks.ActionModeCustomCallback() {
            @Override
            public void onCreateActionMode() {
                onActionModeCreate();
            }
            @Override
            public void onDestroyActionMode() {
                onActionModeDestroy();
            }
        });
    }

    private void showAddWordDialog(){
        WordDialog dialog = new WordDialog(new WordDialog.Callback() {
            @Override
            public void onAddWord(@NonNull @NotNull String wordValue, @NonNull @NotNull String phonetic,
                                  @NonNull @NotNull String notes, @NonNull @NotNull ArrayList<Translation> translations) {
                viewModel.addWord(wordValue, phonetic, notes, translations);
            }
        });
        dialog.show(requireActivity().getSupportFragmentManager(), "GuestStandardWordsFragment");
    }

    protected void onActionModeCreate() {
        if(viewModel.getAdapter() != null){
            viewModel.getAdapter().setSelectionModeActive(true);
        }
    }

    protected void onActionModeDestroy() {
        if(viewModel.getAdapter() != null){
            viewModel.getAdapter().setSelectionModeActive(false);
        }
    }

}