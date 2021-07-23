package com.smart_learn.presenter.helpers.fragments.recycler_view_with_bottom_menu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.smart_learn.R;
import com.smart_learn.databinding.FragmentBasicForRecyclerViewBinding;
import com.smart_learn.presenter.helpers.Callbacks;
import com.smart_learn.presenter.helpers.ItemDecoration;
import com.smart_learn.presenter.helpers.PresenterHelpers;
import com.smart_learn.presenter.helpers.Utilities;
import com.smart_learn.presenter.helpers.fragments.helpers.BasicFragment;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import timber.log.Timber;

/**
 * The main fragment from which all the fragments of the application which contains a standard
 * layout recycler view must be extended.
 *
 * <br><br>
 *
 * A standard recycler view layout is a layout that includes:
 *          - a RecyclerView
 *          - a FloatingActionButton for adding new items
 *          - a TextView which serves as empty label info (when are no items in the RecyclerView
 *            info will be displayed)
 *  <br><br>
 *
 *  The FloatingActionButton is set to 'View.GONE' by default, but can be shown if necessary
 *  (showFloatingActionButton() method must be overridden).
 *
 *  <br><br>
 *
 *  For TextView label specific description can be added by overriding the
 *  getEmptyLabelDescriptionResourceId() method.
 *
 * @param <VM> A ViewModel class that extends BasicViewModelForRecyclerView<>, that will be used by
 *             the fragment as main view model.
 * */
public abstract class BasicFragmentForRecyclerView<VM extends BasicViewModelForRecyclerView<?>> extends BasicFragment<VM>
        implements PresenterHelpers.FragmentRecyclerViewHelper {

    private static final int NO_RESOURCE_SELECTED = -1;

    protected FragmentBasicForRecyclerViewBinding binding;

    protected CoordinatorLayout includeRVLayout;
    protected SwipeRefreshLayout swipeRefreshLayout;
    protected RecyclerView recyclerView;
    protected FloatingActionButton floatingActionButton;
    protected TextView emptyLabel;

    @Getter
    protected ActionMode actionMode;

    // These values will be used when action mode is active if a specific bottom sheet was specified.
    protected LinearLayoutCompat bottomSheetLayout;
    protected BottomSheetBehavior<LinearLayoutCompat> bottomSheetBehavior;

    // adapter general
    protected boolean onAdapterShowCheckedIcon(){
        return false;
    }
    protected boolean onAdapterShowOptionsToolbar(){
        return false;
    }
    protected void onAdapterUpdateSelectedItemsCounter(int value){}

    // general
    protected boolean useToolbarMenu(){
        return false;
    }
    protected int getMenuResourceId(){
        return R.menu.menu_layout_with_recycler_view;
    }
    protected boolean useSearchOnMenu(){
        return false;
    }
    protected int getActionSearchId(){
        return R.id.action_search_menu_layout_with_recycler_view;
    }
    protected boolean useSecondaryGroupOnMenu(){
        return false;
    }
    protected int getSecondaryGroupId(){
        return R.id.secondary_group_menu_layout_with_recycler_view;
    }
    protected int getFloatingActionButtonIconResourceId(){
        return R.drawable.ic_baseline_plus_24;
    }
    protected void onFloatingActionButtonPress(){}
    protected void onFilter(String newText){}
    protected void onSearchActionExpand(){}
    protected void onSearchActionCollapse(){}

    /**
     * Override if you want to use a bottom sheet when action mode is active.
     *
     * If you set this to true, methods 'getBottomSheetLayout()' and 'getParentBottomSheetLayoutId()'
     * must be overridden also in order to specify the bottom sheet.
     *
     * @return true to use, false otherwise (default behaviour).
     * */
    protected boolean isBottomSheetUsed() {
        return false;
    }


    /**
     * Override if you want to use a specific bottom sheet when action mode is active.
     *
     * @return Resource layout of the bottom sheet.
     * */
    protected int getBottomSheetLayout() {
        return NO_RESOURCE_SELECTED;
    }


    /**
     * Override if you want to use a specific bottom sheet when action mode is active.
     *
     * @return Resource id of the parent bottom sheet layout.
     * */
    protected int getParentBottomSheetLayoutId() {
        return NO_RESOURCE_SELECTED;
    }


    /**
     * Override if you want to show the FloatingActionButton. By default View.GONE is set.
     *
     * @return true to show, false to hide (default behaviour).
     * */
    protected boolean showFloatingActionButton() {
        return false;
    }


    /**
     * Override if you want to show specific description when are no items in the recycler view.
     * By default 'R.string.no_items' is set.
     *
     * @return String resource id for the chosen description.
     * */
    protected int getEmptyLabelDescriptionResourceId() {
        return R.string.no_items;
    }


    /**
     * Override if you want to add a specific ItemDecoration to the recycler view.
     * By default ItemDecoration(20) is set.
     *
     * @return ItemDecoration object to be added to the recycler view.
     * */
    protected ItemDecoration getRecyclerViewItemDecoration(){
        return new ItemDecoration(20);
    }


    /**
     * Override if you want to show specific toolbar title when fragment is active.
     * By default 'R.string.empty' is set.
     *
     * @return String resource id for the chosen title.
     * */
    protected int getToolbarTitle() {
        return R.string.empty;
    }

    /**
     * Override if fragment does not use bottom navigation. By default 'true' is set.
     *
     * @return true if fragment use bottom navigation, or false otherwise.
     * */
    protected boolean isFragmentWithBottomNav() {
        return true;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = FragmentBasicForRecyclerViewBinding.inflate(inflater);
        binding.setLifecycleOwner(this);
        if(useToolbarMenu()){
            // use this to set toolbar menu inside fragment
            // https://stackoverflow.com/questions/15653737/oncreateoptionsmenu-inside-fragments/31360073#31360073
            setHasOptionsMenu(true);
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setLayoutUtilities();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if(!useToolbarMenu()){
            return;
        }

        inflater.inflate(getMenuResourceId(), menu);

        if(!useSecondaryGroupOnMenu()){
            menu.setGroupVisible(getSecondaryGroupId(), false);
        }

        if(!useSearchOnMenu()){
            return;
        }

        Utilities.Activities.setSearchMenuItem(menu, getActionSearchId(), new Callbacks.SearchActionCallback() {
                    @Override
                    public void onQueryTextChange(String newText) {
                        onFilter(newText);
                    }
                });

        MenuItem searchItem = menu.findItem(getActionSearchId());
        if(searchItem == null){
            Timber.w("searchItem is null ==> search is not functionally");
            return;
        }

        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                menu.setGroupVisible(getSecondaryGroupId(), false);
                unsetValueFromEmptyLabel();
                onSearchActionExpand();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                if(useSecondaryGroupOnMenu()){
                    menu.setGroupVisible(getSecondaryGroupId(), true);
                }
                resetValueFromEmptyLabel();
                onSearchActionCollapse();
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Utilities.Activities.resetToolbarTitle((AppCompatActivity) requireActivity(),getResources().getString(getToolbarTitle()));
        if(isCreated){
            isCreated = false;
        }
        else{
            // for fragment which are not recreated hide loading icon
            stopRefreshing();
        }
    }

    protected void setLayoutUtilities(){
        // set views
        if(isFragmentWithBottomNav()){
            // hide RecyclerView with no bottom nav and show RecyclerView with bottom nav
            binding.noBottomNavIncludeLayoutRvFragmentBasicForRecyclerView.parentLayoutIncludeLayoutRecyclerView.setVisibility(View.GONE);
            binding.withBottomNavIncludeLayoutRvFragmentBasicForRecyclerView.parentLayoutIncludeLayoutRecyclerView.setVisibility(View.VISIBLE);

            // set current views extracted from include layout RecyclerView with bottom nav
            includeRVLayout = binding.withBottomNavIncludeLayoutRvFragmentBasicForRecyclerView.parentLayoutIncludeLayoutRecyclerView;
            swipeRefreshLayout = binding.withBottomNavIncludeLayoutRvFragmentBasicForRecyclerView.swipeRefreshIncludeLayoutRecyclerView;
            recyclerView = binding.withBottomNavIncludeLayoutRvFragmentBasicForRecyclerView.rvIncludeLayoutRecyclerView;
            floatingActionButton = binding.withBottomNavIncludeLayoutRvFragmentBasicForRecyclerView.floatingBtnAddIncludeLayoutRecyclerView;
            emptyLabel = binding.withBottomNavIncludeLayoutRvFragmentBasicForRecyclerView.tvNoItemIncludeLayoutRecyclerView;
        }
        else {
            // show RecyclerView with no bottom nav and hide RecyclerView with bottom nav
            binding.noBottomNavIncludeLayoutRvFragmentBasicForRecyclerView.parentLayoutIncludeLayoutRecyclerView.setVisibility(View.VISIBLE);
            binding.withBottomNavIncludeLayoutRvFragmentBasicForRecyclerView.parentLayoutIncludeLayoutRecyclerView.setVisibility(View.GONE);

            // set current views extracted from include layout RecyclerView with no bottom nav
            includeRVLayout = binding.noBottomNavIncludeLayoutRvFragmentBasicForRecyclerView.parentLayoutIncludeLayoutRecyclerView;
            swipeRefreshLayout = binding.noBottomNavIncludeLayoutRvFragmentBasicForRecyclerView.swipeRefreshIncludeLayoutRecyclerView;
            recyclerView = binding.noBottomNavIncludeLayoutRvFragmentBasicForRecyclerView.rvIncludeLayoutRecyclerView;
            floatingActionButton = binding.noBottomNavIncludeLayoutRvFragmentBasicForRecyclerView.floatingBtnAddIncludeLayoutRecyclerView;
            emptyLabel = binding.noBottomNavIncludeLayoutRvFragmentBasicForRecyclerView.tvNoItemIncludeLayoutRecyclerView;
        }


        // fragments can choose to hide the floating action button
        if(!showFloatingActionButton()){
            floatingActionButton.setVisibility(View.GONE);
        }
        else{
            floatingActionButton.setImageResource(getFloatingActionButtonIconResourceId());

            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onFloatingActionButtonPress();
                }
            });
        }

        // fragments can choose specific description when are no items in the recycler view
        emptyLabel.setText(getEmptyLabelDescriptionResourceId());

        // set values for bottom sheet is this is set
        if(isBottomSheetUsed()){
            if(getBottomSheetLayout() == NO_RESOURCE_SELECTED || getParentBottomSheetLayoutId() == NO_RESOURCE_SELECTED){
                throw new UnsupportedOperationException("You must give a specific layout, if you want to" +
                        " use the bottom sheet");
            }
            ViewStub viewStub = (ViewStub) requireActivity().findViewById(R.id.view_stub_fragment_basic_for_recycler_view);
            viewStub.setLayoutResource(getBottomSheetLayout());
            viewStub.inflate();
            bottomSheetLayout = (LinearLayoutCompat) requireActivity().findViewById(getParentBottomSheetLayoutId());
            bottomSheetBehavior = Utilities.Activities.setPersistentBottomSheet(bottomSheetLayout);
        }

        setSwipeRefreshLayout();

        setRecyclerView();
    }

    protected void setSwipeRefreshLayout(){
        Utilities.Activities.setSwipeRefreshLayout(swipeRefreshLayout, true, true, null);
    }

    protected void setRecyclerView(){
        Utilities.Activities.initializeRecyclerView(requireContext(), recyclerView, getRecyclerViewItemDecoration(),
                viewModel.getAdapter(), new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull @NotNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                // https://stackoverflow.com/questions/29024058/recyclerview-scrolled-up-down-listener
                // https://stackoverflow.com/questions/40561474/recyclerview-onscrollstatechanged-and-onscrolled
                // https://stackoverflow.com/questions/33454609/detect-start-scroll-and-end-scroll-in-recyclerview/33454785

                // Check if user has reached the end of the list.
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // If so, then load more data.
                    if(viewModel.getAdapter() != null){
                        viewModel.getAdapter().loadMoreData();
                    }
                }
            }
        });
    }

    @Override
    public void startRefreshing(){
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void stopRefreshing(){
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showOrHideEmptyLabel(boolean value) {
        Utilities.Activities.changeTextViewStatus(value, emptyLabel);
    }

    @Override
    public void scrollToPosition(int position) {
        // https://stackoverflow.com/questions/30805262/how-to-get-recyclerview-to-view-a-certain-position-without-scrolling/30805540#30805540
        recyclerView.scrollToPosition(position);
    }


    protected void startActionMode(@NonNull Callbacks.ActionModeCustomCallback actionModeCustomCallback){
        actionMode = ((AppCompatActivity)requireActivity()).startSupportActionMode(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                if(!showFloatingActionButton()){
                    Utilities.Activities.showPersistentBottomSheet(isFragmentWithBottomNav(),null, includeRVLayout, bottomSheetLayout, bottomSheetBehavior);
                }
                else{
                    Utilities.Activities.showPersistentBottomSheet(isFragmentWithBottomNav(), floatingActionButton, includeRVLayout, bottomSheetLayout, bottomSheetBehavior);
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
                if(!showFloatingActionButton()){
                    Utilities.Activities.hidePersistentBottomSheet(isFragmentWithBottomNav(),null, includeRVLayout, bottomSheetBehavior);
                }
                else{
                    Utilities.Activities.hidePersistentBottomSheet(isFragmentWithBottomNav(), floatingActionButton, includeRVLayout, bottomSheetBehavior);
                }
                actionModeCustomCallback.onDestroyActionMode();
            }
        });
    }

    protected void unsetValueFromEmptyLabel(){
        emptyLabel.setText("");
    }

    protected void resetValueFromEmptyLabel(){
        emptyLabel.setText(getEmptyLabelDescriptionResourceId());
    }
}