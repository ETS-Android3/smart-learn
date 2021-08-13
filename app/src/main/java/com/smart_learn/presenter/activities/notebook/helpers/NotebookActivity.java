package com.smart_learn.presenter.activities.notebook.helpers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.smart_learn.R;
import com.smart_learn.core.utilities.GeneralUtilities;
import com.smart_learn.databinding.ActivityNotebookBinding;
import com.smart_learn.databinding.LayoutBottomSheetFilterOptionsBinding;
import com.smart_learn.presenter.helpers.BasicActivity;
import com.smart_learn.presenter.helpers.Callbacks;
import com.smart_learn.presenter.helpers.PresenterUtilities;

import org.jetbrains.annotations.NotNull;

public abstract class NotebookActivity <VM extends NotebookSharedViewModel> extends BasicActivity {

    protected ActivityNotebookBinding binding;
    protected VM sharedViewModel;

    protected NavController navController;
    protected BottomSheetBehavior<LinearLayoutCompat> bottomSheetBehavior;


    /**
     *  Used to instantiate ViewModel. ViewModelProvider needs something like 'ViewModelClassName.class'.
     *
     * @return ViewModelClassName.class
     */
    @NonNull
    @NotNull
    protected abstract Class <VM> getModelClassForViewModel();

    /**
     *  Used to set a navigation graph resource.
     *
     * @return Navigation resource id.
     */
    protected abstract int getNavigationGraphResource();

    /**
     *  Used to set a bottom menu for the navigation graph.
     *
     * @return Menu resource id.
     */
    protected abstract int getMenuResourceId();

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

        // set specific navigation graph
        // https://stackoverflow.com/questions/50898996/setting-navgraph-programmatically
        navController.setGraph(getNavigationGraphResource());

        setViewModel();
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    protected void setLayoutUtilities(){
        // set specific bottom menu
        // https://stackoverflow.com/questions/43084085/inflate-bottom-navigation-view-menu-programmatically
        binding.bottomNavigationActivityNotebook.getMenu().clear();
        binding.bottomNavigationActivityNotebook.inflateMenu(getMenuResourceId());

        // set bottom sheet behaviour for bottom navigation menu layout
        bottomSheetBehavior = PresenterUtilities.Activities.setPersistentBottomSheet(binding.layoutLinearNavigationActivityNotebook);
    }

    protected void setViewModel(){
        sharedViewModel = new ViewModelProvider(this).get(getModelClassForViewModel());
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