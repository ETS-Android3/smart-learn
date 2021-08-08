package com.smart_learn.presenter.activities.test;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.smart_learn.R;
import com.smart_learn.core.utilities.GeneralUtilities;
import com.smart_learn.databinding.ActivityTest2Binding;
import com.smart_learn.presenter.helpers.BasicActivity;
import com.smart_learn.presenter.helpers.Utilities;

import org.jetbrains.annotations.NotNull;

public abstract class TestActivity <VM extends TestSharedViewModel> extends BasicActivity {

    public static final String CALLED_BY_SCHEDULED_TEST_KEY = "CALLED_BY_SCHEDULED_TEST_KEY";
    public static final String SCHEDULED_TEST_ID_KEY = "SCHEDULED_TEST_ID_KEY";

    protected ActivityTest2Binding binding;
    protected VM sharedViewModel;

    protected NavController navController;
    protected BottomSheetBehavior<LinearLayoutCompat> bottomSheetBehavior;

    protected FloatingActionButton fabNewTest;

    protected abstract void onFabClick();
    protected abstract void processScheduledTestNotification(@NonNull @NotNull String scheduledTestId);


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

        // first extract arguments if any
        Bundle args = getIntent().getExtras();
        boolean calledByScheduledTest = false;
        String scheduledTestId = "";
        if(args != null){
            calledByScheduledTest = args.getBoolean(CALLED_BY_SCHEDULED_TEST_KEY);
            if(calledByScheduledTest){
                scheduledTestId = args.getString(SCHEDULED_TEST_ID_KEY);
                if(scheduledTestId == null || scheduledTestId.isEmpty()){
                    GeneralUtilities.showShortToastMessage(this, getString(R.string.error_can_not_continue));
                    onBackPressed();
                    this.finish();
                    return;
                }
            }
        }

        // set data binding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_test2);
        binding.setLifecycleOwner(this);
        setSupportActionBar(binding.toolbarActivityTest);

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

        sharedViewModel.setCalledByScheduledTest(calledByScheduledTest);
        if(calledByScheduledTest){
            processScheduledTestNotification(scheduledTestId);
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    protected void setLayoutUtilities(){
        fabNewTest = binding.fabNewTestActivityTest;

        // set specific bottom menu
        // https://stackoverflow.com/questions/43084085/inflate-bottom-navigation-view-menu-programmatically
        binding.bottomNavigationActivityTest.getMenu().clear();
        binding.bottomNavigationActivityTest.inflateMenu(getMenuResourceId());

        // set bottom sheet behaviour for bottom navigation menu layout
        bottomSheetBehavior = Utilities.Activities.setPersistentBottomSheet(binding.layoutLinearNavigationActivityTest);

        fabNewTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFabClick();
            }
        });
    }

    protected void setViewModel(){
        sharedViewModel = new ViewModelProvider(this).get(getModelClassForViewModel());
        sharedViewModel.getLiveToastMessage().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                GeneralUtilities.showShortToastMessage(TestActivity.this, s);
            }
        });
    }

    public void hideBottomNavigationMenu(){
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    public void showBottomNavigationMenu(){
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    public void hideToolbar(){
        binding.toolbarActivityTest.setVisibility(View.GONE);
    }

    public void showToolbar(){
        binding.toolbarActivityTest.setVisibility(View.VISIBLE);
    }


}