package com.smart_learn.presenter.activities.notebook.guest.fragments.words;

import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.smart_learn.R;
import com.smart_learn.core.services.GuestWordService;
import com.smart_learn.core.utilities.GeneralUtilities;
import com.smart_learn.data.room.entities.Word;
import com.smart_learn.presenter.activities.notebook.helpers.fragments.words.WordsFragment;
import com.smart_learn.presenter.activities.notebook.guest.fragments.words.helpers.WordsAdapter;
import com.smart_learn.presenter.activities.notebook.guest.fragments.GuestNotebookActivity;
import com.smart_learn.presenter.activities.notebook.guest.fragments.GuestNotebookSharedViewModel;
import com.smart_learn.presenter.helpers.Callbacks;
import com.smart_learn.presenter.helpers.Utilities;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import lombok.Getter;

public class GuestWordsFragment extends WordsFragment<GuestWordsViewModel> {

    @Getter
    private GuestNotebookSharedViewModel sharedViewModel;

    @NonNull
    @Override
    protected @NotNull Class<GuestWordsViewModel> getModelClassForViewModel() {
        return GuestWordsViewModel.class;
    }

    @Override
    protected int getBottomSheetLayout() {
        return R.layout.include_layout_action_mode_guest;
    }

    @Override
    protected int getParentBottomSheetLayoutId() {
        return R.id.parent_layout_include_layout_action_mode_guest;
    }

    @Override
    protected void onFilter(String newText) {
        if(viewModel.getAdapter() != null){
            viewModel.getAdapter().getFilter().filter(newText);
        }
    }

    @Override
    protected void onActionModeCreate() {
        ((GuestNotebookActivity)requireActivity()).hideBottomNavigationMenu();

        // Use this to prevent any previous selection. If an error occurred and
        // action mode could not be closed then items could not be disabled and will
        // hang as selected.  FIXME: try yo find a better way to do that
        GuestWordService.getInstance().updateSelectAll(false, sharedViewModel.getSelectedLessonId());
    }

    @Override
    protected void onActionModeDestroy() {
        ((GuestNotebookActivity)requireActivity()).showBottomNavigationMenu();

        // use this to disable all selection
        GuestWordService.getInstance().updateSelectAll(false, sharedViewModel.getSelectedLessonId());
    }

    @Override
    public void onResume() {
        super.onResume();
        Utilities.Activities.resetToolbarTitle((AppCompatActivity) requireActivity(),getResources().getString(R.string.words));
        ((GuestNotebookActivity)requireActivity()).showBottomNavigationMenu();
        sharedViewModel.setSelectedWordId(GuestNotebookSharedViewModel.NO_ITEM_SELECTED);
    }

    @Override
    protected void setLayoutUtilities(){
        super.setLayoutUtilities();

        // this fragment does not need refreshing
        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setRefreshing(false);

        // set bottom sheet listeners
        Button btnSelectAll = requireActivity().findViewById(R.id.btn_select_include_layout_action_mode_guest);
        btnSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.setAllItemsAreSelected(!viewModel.isAllItemsAreSelected());
                GuestWordService.getInstance().updateSelectAll(viewModel.isAllItemsAreSelected(), sharedViewModel.getSelectedLessonId());
                Utilities.Activities.changeSelectAllButtonStatus(viewModel.isAllItemsAreSelected(), btnSelectAll);
            }
        });

        Button btnDeleteSelected = requireActivity().findViewById(R.id.btn_delete_include_layout_action_mode_guest);
        btnDeleteSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GuestWordService.getInstance().deleteSelectedItems(sharedViewModel.getSelectedLessonId());
            }
        });


        // recycler view include layout listeners
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


    @Override
    protected void setViewModel(){
        super.setViewModel();

        // set shared view model
        sharedViewModel = new ViewModelProvider(requireActivity()).get(GuestNotebookSharedViewModel.class);

        // set fragment view model adapter
        viewModel.setAdapter(new WordsAdapter(new Callbacks.FragmentGeneralCallback<GuestWordsFragment>() {
            @Override
            public GuestWordsFragment getFragment() {
                return GuestWordsFragment.this;
            }
        }));

        // set observers
        GuestWordService.getInstance().getLiveSelectedItemsCount(sharedViewModel.getSelectedLessonId()).observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(actionMode != null){
                    actionMode.setTitle(getString(R.string.selected) + " " + integer);
                }
            }
        });

        GuestWordService.getInstance().getCurrentLessonLiveWords(sharedViewModel.getSelectedLessonId()).observe(this, new Observer<List<Word>>() {
            @Override
            public void onChanged(List<Word> words) {
                Utilities.Activities.changeTextViewStatus(words.isEmpty(), emptyLabel);
                if(viewModel.getAdapter() != null){
                    viewModel.getAdapter().setItems(words);
                }
            }
        });

    }

    public void goToHomeWordFragment(Word word){
        if(word == null || word.getWordId() == GuestNotebookSharedViewModel.NO_ITEM_SELECTED){
            GeneralUtilities.showShortToastMessage(this.requireContext(),getString(R.string.error_word_can_not_be_opened));
            return;
        }

        // First set current lesson id (lesson which is clicked) on the shared view model and
        // then you can navigate.
        sharedViewModel.setSelectedWordId(word.getWordId());
        ((GuestNotebookActivity)requireActivity()).goToHomeWordFragment();
    }

}