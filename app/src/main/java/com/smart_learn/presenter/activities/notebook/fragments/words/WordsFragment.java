package com.smart_learn.presenter.activities.notebook.fragments.words;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.smart_learn.R;
import com.smart_learn.core.utilities.GeneralUtilities;
import com.smart_learn.data.models.room.entities.Word;
import com.smart_learn.databinding.FragmentWordsBinding;
import com.smart_learn.presenter.activities.notebook.NotebookActivity;
import com.smart_learn.presenter.activities.notebook.NotebookSharedViewModel;
import com.smart_learn.presenter.activities.notebook.fragments.words.helpers.WordsAdapter;
import com.smart_learn.presenter.helpers.Callbacks;
import com.smart_learn.presenter.helpers.Utilities;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import lombok.Getter;

public class WordsFragment extends Fragment {

    @Getter
    private FragmentWordsBinding binding;
    @Getter
    private WordsViewModel wordsViewModel;
    @Getter
    private NotebookSharedViewModel sharedViewModel;

    private CoordinatorLayout includeRVLayout;
    private FloatingActionButton floatingBtnAddWord;
    private RecyclerView recyclerView;
    private TextView tvNoItem;

    // used for showing action mode options when no user is logged in
    private LinearLayoutCompat guestBottomSheetLayout;
    private BottomSheetBehavior<LinearLayoutCompat> guestBottomSheetBehavior;

    // used for showing action mode options when user is logged in
    private LinearLayoutCompat userBottomSheetLayout;
    private BottomSheetBehavior<LinearLayoutCompat> userBottomSheetBehavior;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setViewModel();
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWordsBinding.inflate(inflater);
        binding.setLifecycleOwner(this);
        // use this to set toolbar menu inside fragment
        // https://stackoverflow.com/questions/15653737/oncreateoptionsmenu-inside-fragments/31360073#31360073
        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setLayoutUtilities();
        setPersistentBottomSheet();
        setRecyclerView();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_layout_with_recycler_view, menu);
        Utilities.Activities.setSearchMenuItem(requireActivity(), menu, R.id.action_search_menu_layout_with_recycler_view,
                R.id.secondary_group_menu_layout_with_recycler_view, new Callbacks.SearchActionCallback() {
                    @Override
                    public void onQueryTextChange(String newText) {
                        if(wordsViewModel.getWordsAdapter() != null){
                            wordsViewModel.getWordsAdapter().getFilter().filter(newText);
                        }
                    }
                });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_filter_menu_layout_with_recycler_view){
            ((NotebookActivity)requireActivity()).showFilterOptionsDialog(WordsFragment.this, new Callbacks.FragmentFilterOptionsCallback() {
                @Override
                public void onAZFilter() {

                }
            });

            return true;
        }

        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        Utilities.Activities.resetToolbarTitle((AppCompatActivity) requireActivity(),getResources().getString(R.string.words));
        ((NotebookActivity)requireActivity()).showBottomNavigationMenu();
        sharedViewModel.setSelectedWordId(NotebookSharedViewModel.NO_ITEM_SELECTED);
    }


    private void setLayoutUtilities(){
        // set views
        includeRVLayout = binding.includeLayoutRecyclerViewFragmentWords.parentLayoutIncludeLayoutRecyclerViewWithBottomNav;
        floatingBtnAddWord = binding.includeLayoutRecyclerViewFragmentWords.floatingBtnAddIncludeLayoutRecyclerViewWithBottomNav;
        recyclerView = binding.includeLayoutRecyclerViewFragmentWords.rvIncludeLayoutRecyclerViewWithBottomNav;
        tvNoItem = binding.includeLayoutRecyclerViewFragmentWords.tvNoItemIncludeLayoutRecyclerViewWithBottomNav;
        guestBottomSheetLayout = binding.includeLayoutActionModeGuestFragmentWords.parentLayoutIncludeLayoutActionModeGuest;
        userBottomSheetLayout = binding.includeLayoutActionModeUserFragmentWords.parentLayoutIncludeLayoutActionModeUser;

        // set listeners

        // persistent bottom sheet include layout listeners
        binding.includeLayoutActionModeGuestFragmentWords.btnSelectIncludeLayoutActionModeGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wordsViewModel.setAllItemsAreSelected(!wordsViewModel.isAllItemsAreSelected());
                wordsViewModel.getWordsService().updateSelectAll(wordsViewModel.isAllItemsAreSelected(), sharedViewModel.getSelectedLessonId());
                Utilities.Activities.changeSelectAllButtonStatus(wordsViewModel.isAllItemsAreSelected(),
                        binding.includeLayoutActionModeGuestFragmentWords.btnSelectIncludeLayoutActionModeGuest);
            }
        });

        binding.includeLayoutActionModeGuestFragmentWords.btnDeleteIncludeLayoutActionModeGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wordsViewModel.getWordsService().deleteSelectedItems(sharedViewModel.getSelectedLessonId());
            }
        });


        // recycler view include layout listeners
        floatingBtnAddWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void setPersistentBottomSheet(){
        guestBottomSheetBehavior = Utilities.Activities.setPersistentBottomSheet(guestBottomSheetLayout);
        userBottomSheetBehavior = Utilities.Activities.setPersistentBottomSheet(userBottomSheetLayout);
    }

    private void setRecyclerView(){
        Utilities.Activities.initializeRecyclerView(requireContext(), recyclerView, 20);
        recyclerView.setAdapter(wordsViewModel.getWordsAdapter());
    }

    private void setViewModel(){
        // set shared view model
        sharedViewModel = new ViewModelProvider(requireActivity()).get(NotebookSharedViewModel.class);

        // set fragment view model
        wordsViewModel = new ViewModelProvider(this).get(WordsViewModel.class);
        // set fragment view model adapter
        wordsViewModel.setWordsAdapter(new WordsAdapter(new Callbacks.FragmentGeneralCallback<WordsFragment>() {
            @Override
            public WordsFragment getFragment() {
                return WordsFragment.this;
            }
        }));

        // set observers
        wordsViewModel.getLiveToastMessage().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                GeneralUtilities.showShortToastMessage(requireContext(), s);
            }
        });


        wordsViewModel.getWordsService().getLiveSelectedItemsCount(sharedViewModel.getSelectedLessonId()).observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(WordsFragment.this.getActionMode() != null){
                    WordsFragment.this.getActionMode().setTitle(getString(R.string.selected) + " " + integer);
                }
            }
        });

        wordsViewModel.getWordsService().getCurrentLessonLiveWords(sharedViewModel.getSelectedLessonId()).observe(this, new Observer<List<Word>>() {
            @Override
            public void onChanged(List<Word> words) {
                Utilities.Activities.changeTextViewStatus(words.isEmpty(), tvNoItem);
                if(wordsViewModel.getWordsAdapter() != null){
                    wordsViewModel.getWordsAdapter().setItems(words);
                }
            }
        });

    }

    public void startActionMode() {
        ((NotebookActivity)requireActivity()).startActionMode(floatingBtnAddWord, includeRVLayout, guestBottomSheetLayout,
                guestBottomSheetBehavior, userBottomSheetLayout, userBottomSheetBehavior, new Callbacks.ActionModeCustomCallback() {
                    @Override
                    public void onCreateActionMode() {
                        ((NotebookActivity)requireActivity()).hideBottomNavigationMenu();

                        // Use this to prevent any previous selection. If an error occurred and
                        // action mode could not be closed then items could not be disabled and will
                        // hang as selected.  FIXME: try yo find a better way to do that
                        wordsViewModel.getWordsService().updateSelectAll(false, sharedViewModel.getSelectedLessonId());
                    }

                    @Override
                    public void onDestroyActionMode() {
                        ((NotebookActivity)requireActivity()).showBottomNavigationMenu();

                        // use this to disable all selection
                        wordsViewModel.getWordsService().updateSelectAll(false, sharedViewModel.getSelectedLessonId());
                    }
                });
    }

    public void goToHomeWordFragment(Word word){
        if(word == null || word.getWordId() == NotebookSharedViewModel.NO_ITEM_SELECTED){
            GeneralUtilities.showShortToastMessage(this.requireContext(),getString(R.string.error_word_can_not_be_opened));
            return;
        }

        // First set current lesson id (lesson which is clicked) on the shared view model and
        // then you can navigate.
        sharedViewModel.setSelectedWordId(word.getWordId());
        ((NotebookActivity)requireActivity()).goToHomeWordFragment();
    }

    public ActionMode getActionMode() {
        return ((NotebookActivity)requireActivity()).getActionMode();
    }
}














