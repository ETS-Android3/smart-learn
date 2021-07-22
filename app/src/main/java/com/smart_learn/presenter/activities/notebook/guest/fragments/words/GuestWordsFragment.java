package com.smart_learn.presenter.activities.notebook.guest.fragments.words;

import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.smart_learn.R;
import com.smart_learn.core.services.GuestWordService;
import com.smart_learn.core.utilities.CoreUtilities;
import com.smart_learn.core.utilities.GeneralUtilities;
import com.smart_learn.data.room.entities.Word;
import com.smart_learn.presenter.activities.notebook.guest.GuestNotebookActivity;
import com.smart_learn.presenter.activities.notebook.guest.GuestNotebookSharedViewModel;
import com.smart_learn.presenter.helpers.adapters.words.GuestWordsAdapter;
import com.smart_learn.presenter.activities.notebook.helpers.fragments.words.WordsFragment;
import com.smart_learn.presenter.helpers.Utilities;
import com.smart_learn.presenter.helpers.fragments.recycler_view_with_bottom_menu.BasicFragmentForRecyclerView;

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
        return R.layout.layout_action_mode_fragment_guest_words;
    }

    @Override
    protected int getParentBottomSheetLayoutId() {
        return R.id.parent_layout_include_layout_action_mode_fragment_guest_words;
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

        // mark that action mode started
        if(viewModel.getAdapter() != null){
            viewModel.getAdapter().setSelectionModeActive(true);
        }
    }

    @Override
    protected void onActionModeDestroy() {
        ((GuestNotebookActivity)requireActivity()).showBottomNavigationMenu();

        // mark that action mode finished
        if(viewModel.getAdapter() != null){
            viewModel.getAdapter().setSelectionModeActive(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        sharedViewModel.setSelectedWordId(GuestNotebookSharedViewModel.NO_ITEM_SELECTED);
    }

    @Override
    protected void setLayoutUtilities(){
        super.setLayoutUtilities();

        // this fragment does not need refreshing
        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setRefreshing(false);

        // set bottom sheet listeners
        Button btnSelectAll = requireActivity().findViewById(R.id.btn_select_include_layout_action_mode_fragment_guest_words);
        btnSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.setAllItemsAreSelected(!viewModel.isAllItemsAreSelected());
                Utilities.Activities.changeSelectAllButtonStatus(viewModel.isAllItemsAreSelected(), btnSelectAll);
            }
        });

        Button btnDeleteSelected = requireActivity().findViewById(R.id.btn_delete_include_layout_action_mode_fragment_guest_words);
        btnDeleteSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.deleteSelectedWords();
                viewModel.setAllItemsAreSelected(false);
                Utilities.Activities.changeSelectAllButtonStatus(viewModel.isAllItemsAreSelected(), btnSelectAll);
            }
        });
    }


    @Override
    protected void setViewModel(){
        super.setViewModel();

        // set shared view model
        sharedViewModel = new ViewModelProvider(requireActivity()).get(GuestNotebookSharedViewModel.class);

        // set current lesson on view model for further operations inside view model
        viewModel.setCurrentLessonId(sharedViewModel.getSelectedLessonId());

        // set fragment view model adapter
        viewModel.setAdapter(new GuestWordsAdapter(sharedViewModel.getSelectedLessonId(), new GuestWordsAdapter.Callback(){

            @Override
            public void onSimpleClick(@NonNull @NotNull Word item) {
                goToGuestWordContainerFragment(item);
            }

            @Override
            public void onLongClick(@NonNull @NotNull Word item) {
                startFragmentActionMode();
            }

            @Override
            public boolean showCheckedIcon() {
                return true;
            }

            @Override
            public boolean showToolbar() {
                return true;
            }

            @Override
            public void updateSelectedItemsCounter(int value) {
                showSelectedItems(value);
            }

            @NonNull
            @Override
            public @NotNull BasicFragmentForRecyclerView<?> getFragment() {
                return GuestWordsFragment.this;
            }
        }));

        // set observers
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

    public void goToGuestWordContainerFragment(Word word){
        if(word == null || word.getWordId() == GuestNotebookSharedViewModel.NO_ITEM_SELECTED){
            GeneralUtilities.showShortToastMessage(this.requireContext(),getString(R.string.error_word_can_not_be_opened));
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

    public void showSelectedItems(int value){
        this.requireActivity().runOnUiThread(() -> {
            if(actionMode != null) {
                actionMode.setTitle(getString(R.string.selected_words_point) + " " + value);
            }
        });
    }


}