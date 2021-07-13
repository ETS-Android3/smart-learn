package com.smart_learn.presenter.activities.notebook;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import com.smart_learn.presenter.helpers.Callbacks;
import com.smart_learn.presenter.helpers.Utilities;

import org.jetbrains.annotations.NotNull;

public abstract class NotebookActivity <VM extends NotebookSharedViewModel> extends AppCompatActivity {

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


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    protected void setLayoutUtilities(){
        // set bottom sheet behaviour for bottom navigation menu layout
        bottomSheetBehavior = Utilities.Activities.setPersistentBottomSheet(binding.layoutLinearNavigationActivityNotebook);
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