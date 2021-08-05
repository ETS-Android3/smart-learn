package com.smart_learn.presenter.helpers.fragments.words.user.standard;

import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;
import com.smart_learn.R;
import com.smart_learn.data.room.entities.helpers.Translation;
import com.smart_learn.presenter.helpers.fragments.words.helpers.WordDialog;
import com.smart_learn.presenter.helpers.Callbacks;
import com.smart_learn.presenter.helpers.fragments.words.user.UserBasicWordsFragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public abstract class UserStandardWordsFragment <VM extends UserStandardWordsViewModel> extends UserBasicWordsFragment<VM> {

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
        return R.layout.layout_action_mode_fragment_user_words;
    }

    @Override
    protected int getParentBottomSheetLayoutId() {
        return R.id.parent_layout_include_layout_action_mode_fragment_user_words;
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
    protected void onAdapterLongClick(@NonNull @NotNull DocumentSnapshot item) {
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
        Button btnDeleteSelected = requireActivity().findViewById(R.id.btn_delete_include_layout_action_mode_fragment_user_words);
        btnDeleteSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.deleteSelectedWords();
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
                boolean isSharedLessonSelected = getArguments() != null && getArguments().getBoolean(IS_SHARED_LESSON_SELECTED);
                viewModel.addWord(isSharedLessonSelected, wordValue, phonetic, notes, translations);
            }
        });
        dialog.show(requireActivity().getSupportFragmentManager(), "UserStandardWordsFragment");
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