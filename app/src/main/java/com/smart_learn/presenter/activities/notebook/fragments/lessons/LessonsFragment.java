package com.smart_learn.presenter.activities.notebook.fragments.lessons;

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
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.smart_learn.R;
import com.smart_learn.core.utilities.GeneralUtilities;
import com.smart_learn.data.models.room.entities.Lesson;
import com.smart_learn.databinding.FragmentLessonsBinding;
import com.smart_learn.presenter.activities.notebook.NotebookActivity;
import com.smart_learn.presenter.activities.notebook.NotebookSharedViewModel;
import com.smart_learn.presenter.activities.notebook.fragments.lessons.helpers.LessonDialog;
import com.smart_learn.presenter.activities.notebook.fragments.lessons.helpers.LessonsAdapter;
import com.smart_learn.presenter.helpers.Callbacks;
import com.smart_learn.presenter.helpers.Utilities;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import lombok.Getter;

public class LessonsFragment extends Fragment {

    @Getter
    private FragmentLessonsBinding binding;
    @Getter
    private LessonsViewModel lessonsViewModel;
    @Getter
    private NotebookSharedViewModel sharedViewModel;

    private CoordinatorLayout includeRVLayout;
    private FloatingActionButton floatingBtnAddLesson;
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
        binding = FragmentLessonsBinding.inflate(inflater);
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
                        if(lessonsViewModel.getLessonsAdapter() != null){
                            lessonsViewModel.getLessonsAdapter().getFilter().filter(newText);
                        }
                    }
                });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_filter_menu_layout_with_recycler_view){
            ((NotebookActivity)requireActivity()).showFilterOptionsDialog(LessonsFragment.this, new Callbacks.FragmentFilterOptionsCallback() {
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
        Utilities.Activities.resetToolbarTitle((AppCompatActivity) requireActivity(),getResources().getString(R.string.lessons));
        ((NotebookActivity)requireActivity()).hideBottomNavigationMenu();
        sharedViewModel.setSelectedLessonId(NotebookSharedViewModel.NO_ITEM_SELECTED);
        sharedViewModel.setSelectedWordId(NotebookSharedViewModel.NO_ITEM_SELECTED);
    }


    private void setLayoutUtilities(){

        // set views
        includeRVLayout = binding.includeLayoutRecyclerViewFragmentLessons.parentLayoutIncludeLayoutRecyclerViewNoBottomNav;
        floatingBtnAddLesson = binding.includeLayoutRecyclerViewFragmentLessons.floatingBtnAddIncludeLayoutRecyclerViewNoBottomNav;
        recyclerView = binding.includeLayoutRecyclerViewFragmentLessons.rvIncludeLayoutRecyclerViewNoBottomNav;
        tvNoItem = binding.includeLayoutRecyclerViewFragmentLessons.tvNoItemIncludeLayoutRecyclerViewNoBottomNav;
        guestBottomSheetLayout = binding.includeLayoutActionModeGuestFragmentLessons.parentLayoutIncludeLayoutActionModeGuest;
        userBottomSheetLayout = binding.includeLayoutActionModeUserFragmentLessons.parentLayoutIncludeLayoutActionModeUser;

        // set listeners

        // persistent bottom sheet include layout listeners
        binding.includeLayoutActionModeGuestFragmentLessons.btnSelectIncludeLayoutActionModeGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lessonsViewModel.setAllItemsAreSelected(!lessonsViewModel.isAllItemsAreSelected());
                lessonsViewModel.getLessonService().updateSelectAll(lessonsViewModel.isAllItemsAreSelected());
                Utilities.Activities.changeSelectAllButtonStatus(lessonsViewModel.isAllItemsAreSelected(),
                        binding.includeLayoutActionModeGuestFragmentLessons.btnSelectIncludeLayoutActionModeGuest);
            }
        });

        binding.includeLayoutActionModeGuestFragmentLessons.btnDeleteIncludeLayoutActionModeGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lessonsViewModel.deleteSelectedItems();
            }
        });


        // recycler view include layout listeners
        floatingBtnAddLesson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddLessonDialog();
            }
        });
    }

    private void setPersistentBottomSheet(){
        guestBottomSheetBehavior = Utilities.Activities.setPersistentBottomSheet(guestBottomSheetLayout);
        userBottomSheetBehavior = Utilities.Activities.setPersistentBottomSheet(userBottomSheetLayout);
    }

    private void setRecyclerView(){
        Utilities.Activities.initializeRecyclerView(requireContext(), recyclerView, 20);
        recyclerView.setAdapter(lessonsViewModel.getLessonsAdapter());
    }

    private void setViewModel(){
        // set shared view model
        sharedViewModel = new ViewModelProvider(requireActivity()).get(NotebookSharedViewModel.class);

        // set fragment view model
        lessonsViewModel = new ViewModelProvider(this).get(LessonsViewModel.class);
        // set fragment view model adapter
        lessonsViewModel.setLessonsAdapter(new LessonsAdapter(new Callbacks.FragmentGeneralCallback<LessonsFragment>() {
            @Override
            public LessonsFragment getFragment() {
                return LessonsFragment.this;
            }
        }));

        // set observers
        lessonsViewModel.getLiveToastMessage().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                GeneralUtilities.showShortToastMessage(requireContext(), s);
            }
        });

        lessonsViewModel.getLessonService().getLiveSelectedItemsCount().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(LessonsFragment.this.getActionMode() != null){
                    LessonsFragment.this.getActionMode().setTitle(getString(R.string.selected) + " " + integer);
                }
            }
        });

        lessonsViewModel.getLessonService().getAllLiveSampleLessons().observe(this, new Observer<List<Lesson>>() {
            @Override
            public void onChanged(List<Lesson> lessons) {
                Utilities.Activities.changeTextViewStatus(lessons.isEmpty(), tvNoItem);
                if(lessonsViewModel.getLessonsAdapter() != null){
                    lessonsViewModel.getLessonsAdapter().setItems(lessons);
                }
            }
        });
    }

    private void showAddLessonDialog(){
        DialogFragment dialogFragment = new LessonDialog(new LessonDialog.Callback() {
            @Override
            public void onAddLesson(@NonNull @NotNull Lesson lesson) {
                GeneralUtilities.showShortToastMessage(LessonsFragment.this.requireContext(),
                        lessonsViewModel.getLessonService().tryToAddOrUpdateNewLesson(lesson, false).getInfo());
            }
        });
        dialogFragment.show(requireActivity().getSupportFragmentManager(), "LessonsActivity");
    }

    public void startActionMode() {
        ((NotebookActivity)requireActivity()).startActionMode(floatingBtnAddLesson, includeRVLayout, guestBottomSheetLayout,
                guestBottomSheetBehavior, userBottomSheetLayout, userBottomSheetBehavior, new Callbacks.ActionModeCustomCallback() {
                    @Override
                    public void onCreateActionMode() {
                        // Use this to prevent any previous selection. If an error occurred and
                        // action mode could not be closed then items could not be disabled and will
                        // hang as selected.  FIXME: try yo find a better way to do that
                        lessonsViewModel.getLessonService().updateSelectAll(false);
                    }

                    @Override
                    public void onDestroyActionMode() {
                        // use this to disable all selection
                        lessonsViewModel.getLessonService().updateSelectAll(false);
                    }
                });
    }

    public void goToHomeLessonFragment(Lesson lesson){
        // when navigation is made a valid lesson id must be set on shared view model
        if(lesson == null || lesson.getLessonId() == NotebookSharedViewModel.NO_ITEM_SELECTED){
            GeneralUtilities.showShortToastMessage(this.requireContext(),getString(R.string.error_lesson_can_not_be_opened));
            return;
        }

        // First set current lesson id (lesson which is clicked) on the shared view model and
        // then you can navigate.
        sharedViewModel.setSelectedLessonId(lesson.getLessonId());
        ((NotebookActivity)requireActivity()).goToHomeLessonFragment();
    }

    public ActionMode getActionMode() {
        return ((NotebookActivity)requireActivity()).getActionMode();
    }

}