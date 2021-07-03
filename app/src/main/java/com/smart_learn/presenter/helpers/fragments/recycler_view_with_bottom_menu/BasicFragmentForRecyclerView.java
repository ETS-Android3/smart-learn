package com.smart_learn.presenter.helpers.fragments.recycler_view_with_bottom_menu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.smart_learn.R;
import com.smart_learn.databinding.FragmentBasicForRecyclerViewBinding;
import com.smart_learn.presenter.helpers.ItemDecoration;
import com.smart_learn.presenter.helpers.PresenterHelpers;
import com.smart_learn.presenter.helpers.Utilities;
import com.smart_learn.presenter.helpers.fragments.helpers.BasicFragment;

import org.jetbrains.annotations.NotNull;

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

    protected FragmentBasicForRecyclerViewBinding binding;

    protected SwipeRefreshLayout swipeRefreshLayout;
    protected RecyclerView recyclerView;
    protected FloatingActionButton floatingActionButton;
    protected TextView emptyLabel;


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


    @Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = FragmentBasicForRecyclerViewBinding.inflate(inflater);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setLayoutUtilities();
    }

    @Override
    public void onResume() {
        super.onResume();
        Utilities.Activities.resetToolbarTitle((AppCompatActivity) requireActivity(),getResources().getString(getToolbarTitle()));
    }

    protected void setLayoutUtilities(){
        // set views
        swipeRefreshLayout = binding.includeLayoutRecyclerViewFragmentBasicForRecyclerView.swipeRefreshLayoutRecyclerViewWithBottomNav;
        recyclerView = binding.includeLayoutRecyclerViewFragmentBasicForRecyclerView.rvIncludeLayoutRecyclerViewWithBottomNav;
        floatingActionButton = binding.includeLayoutRecyclerViewFragmentBasicForRecyclerView.floatingBtnAddIncludeLayoutRecyclerViewWithBottomNav;
        emptyLabel = binding.includeLayoutRecyclerViewFragmentBasicForRecyclerView.tvNoItemIncludeLayoutRecyclerViewWithBottomNav;

        // fragments can choose to hide the floating action button
        if(!showFloatingActionButton()){
            floatingActionButton.setVisibility(View.GONE);
        }

        // fragments can choose specific description when are no items in the recycler view
        emptyLabel.setText(getEmptyLabelDescriptionResourceId());

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
}