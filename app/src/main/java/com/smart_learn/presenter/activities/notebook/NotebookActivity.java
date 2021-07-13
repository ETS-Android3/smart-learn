package com.smart_learn.presenter.activities.notebook;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.smart_learn.R;
import com.smart_learn.core.utilities.GeneralUtilities;
import com.smart_learn.databinding.ActivityNotebookBinding;
import com.smart_learn.databinding.LayoutBottomSheetFilterOptionsBinding;
import com.smart_learn.presenter.helpers.Callbacks;
import com.smart_learn.presenter.helpers.Utilities;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;

public class NotebookActivity extends AppCompatActivity {

    private ActivityNotebookBinding binding;
    private NotebookSharedViewModel sharedViewModel;
    @Getter
    private ActionMode actionMode;
    private NavController navController;

    private BottomSheetBehavior<LinearLayoutCompat> bottomSheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notebook);
        binding.setLifecycleOwner(this);
        setSupportActionBar(binding.toolbarActivityNotebook);

       setLayoutUtilities();

        // if navigation graph cannot be set, then go back from activity
        if(navController == null){
            GeneralUtilities.showShortToastMessage(this, getString(R.string.error_loading_screen));
            onBackPressed();
            this.finish();
            return;
        }

        setViewModel();
    }


    private void setLayoutUtilities(){
        // set bottom sheet behaviour for bottom navigation menu layout
        bottomSheetBehavior = Utilities.Activities.setPersistentBottomSheet(binding.layoutLinearNavigationActivityNotebook);

        // try to set navigation graph
        navController = Utilities.Activities.setNavigationGraphWithBottomMenu(this, R.id.nav_host_fragment_activity_notebook,
                binding.bottomNavigationActivityNotebook, new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @SuppressLint("NonConstantResourceId")
                    @Override
                    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                        switch (item.getItemId()){
                            // fragment with bottom navigation view VISIBLE
                            case R.id.home_lesson_fragment_nav_graph_activity_notebook:
                                navController.navigate(R.id.home_lesson_fragment_nav_graph_activity_notebook,null,
                                        Utilities.Activities.getVisibleBottomMenuNavOptions(R.id.guest_lessons_fragment_nav_graph_activity_notebook));
                                return true;
                            // fragment with bottom navigation view VISIBLE
                            case R.id.words_fragment_nav_graph_activity_notebook:
                                navController.navigate(R.id.words_fragment_nav_graph_activity_notebook,null,
                                        Utilities.Activities.getVisibleBottomMenuNavOptions(R.id.guest_lessons_fragment_nav_graph_activity_notebook));
                                return true;
                        }
                        return false;
                    }
                }, null);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setViewModel(){
        sharedViewModel = new ViewModelProvider(this).get(NotebookSharedViewModel.class);
        sharedViewModel.getLiveToastMessage().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                GeneralUtilities.showShortToastMessage(NotebookActivity.this, s);
            }
        });
    }

    public void hideBottomNavigationMenu(){
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    public void showBottomNavigationMenu(){
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    /** Fragment has bottom navigation view HIDDEN. */
    public void goToHomeLessonFragment(){
        navController.navigate(R.id.action_lessons_fragment_to_lesson_home_fragment_nav_graph_activity_notebook,null,
                Utilities.Activities.getEnterBottomMenuNavOptions(R.id.home_lesson_fragment_nav_graph_activity_notebook));
    }

    /** Fragment has bottom navigation view HIDDEN. */
    public void goToHomeWordFragment(){
        navController.navigate(R.id.action_words_fragment_nav_graph_activity_notebook_to_home_word_fragment_nav_graph_activity_notebook,null,
                Utilities.Activities.getExitBottomMenuNavOptions(R.id.home_word_fragment_nav_graph_activity_notebook));
    }


    /**
     * Define what to do when action mode is started from this activity fragments.
     *
     * Same behaviour is applied to fragments: LessonsFragment, WordsFragment ... TODO: complete this
     *
     * @param button The floating button for add action (this will be hidden in action mode).
     * @param mainLayout The main layout which is shown where bottom sheet is HIDDEN.
     * @param guestSheetLayout The layout for guest bottom sheet (shown when user is not logged in).
     * @param userSheetLayout The layout for user bottom sheet (shown when user is logged in).
     * @param guestSheetBehaviour The behaviour object for guest bottom sheet.
     * @param userSheetBehaviour The behaviour object for user bottom sheet.
     * @param actionModeCustomCallback Callback which will manage custom action mode actions.
     * */
    public void startActionMode(@NonNull FloatingActionButton button, @NonNull CoordinatorLayout mainLayout,
                                @NonNull LinearLayoutCompat guestSheetLayout, @NonNull BottomSheetBehavior<LinearLayoutCompat> guestSheetBehaviour,
                                @NonNull LinearLayoutCompat userSheetLayout, @NonNull BottomSheetBehavior<LinearLayoutCompat> userSheetBehaviour,
                                @NonNull Callbacks.ActionModeCustomCallback actionModeCustomCallback){

        actionMode = startSupportActionMode(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                if(Utilities.Auth.isUserLoggedIn()){
                    Utilities.Activities.showPersistentBottomSheet(button,mainLayout,userSheetLayout,userSheetBehaviour);
                }
                else{
                    Utilities.Activities.showPersistentBottomSheet(button,mainLayout,guestSheetLayout,guestSheetBehaviour);
                }
                actionModeCustomCallback.onCreateActionMode();
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                mode.finish();
                actionMode = null;
                if(Utilities.Auth.isUserLoggedIn()){
                    Utilities.Activities.hidePersistentBottomSheet(button,mainLayout,userSheetBehaviour);
                }
                else{
                    Utilities.Activities.hidePersistentBottomSheet(button,mainLayout,guestSheetBehaviour);
                }
                actionModeCustomCallback.onDestroyActionMode();
            }
        });
    }



    /**
     * This will show a modal bottom sheet dialog for filter options.
     *
     * Same behaviour is applied to fragments: LessonsFragment, WordsFragment ... TODO: complete this
     *
     * @param fragment Fragment where modal bottom sheet will appear.
     * @param fragmentCallback Callback that will manage filter options.
     * */
    public void showFilterOptionsDialog(@NonNull Fragment fragment, @NonNull Callbacks.FragmentFilterOptionsCallback fragmentCallback){

        // create dialog and load layout
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(fragment.requireContext(), R.style.AppTheme_BottomSheetDialogTheme);

        LayoutBottomSheetFilterOptionsBinding bottomSheetBinding = DataBindingUtil.inflate(LayoutInflater.from(fragment.requireContext()),
                R.layout.layout_bottom_sheet_filter_options,null, false);
        bottomSheetBinding.setLifecycleOwner(fragment);

        bottomSheetDialog.setContentView(bottomSheetBinding.getRoot());
        bottomSheetDialog.show();

        // set button listeners
        bottomSheetBinding.btnFilterAZLayoutBottomSheetFilterOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentCallback.onAZFilter();
            }
        });
    }


}