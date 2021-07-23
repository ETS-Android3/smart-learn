package com.smart_learn.presenter.helpers.fragments.friends;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;
import com.smart_learn.R;
import com.smart_learn.core.utilities.CoreUtilities;
import com.smart_learn.presenter.helpers.adapters.friends.FriendsAdapter;
import com.smart_learn.presenter.helpers.fragments.recycler_view_with_bottom_menu.BasicFragmentForRecyclerView;

import org.jetbrains.annotations.NotNull;


public abstract class BasicFriendsFragment <VM extends BasicFriendsViewModel> extends BasicFragmentForRecyclerView<VM> {

    protected void onAdapterSimpleClick(@NonNull @NotNull DocumentSnapshot item){}
    protected void onAdapterLongClick(@NonNull @NotNull DocumentSnapshot item){}
    protected void onAdapterRemoveFriend(@NonNull @NotNull DocumentSnapshot friendSnapshot){}

    @Override
    protected int getEmptyLabelDescriptionResourceId() {
        return R.string.no_friends;
    }

    @Override
    protected int getToolbarTitle() {
        return R.string.friends;
    }

    @Override
    protected boolean useToolbarMenu() {
        return true;
    }

    @Override
    protected boolean useSearchOnMenu() {
        return true;
    }

    @Override
    protected void setViewModel(){
        super.setViewModel();

        viewModel.setAdapter(new FriendsAdapter(new FriendsAdapter.Callback() {
            @Override
            public void onRemoveFriend(@NonNull @NotNull DocumentSnapshot friendSnapshot) {
                onAdapterRemoveFriend(friendSnapshot);
            }

            @Override
            public boolean showCheckedIcon() {
                return onAdapterShowCheckedIcon();
            }

            @Override
            public boolean showToolbar() {
                return onAdapterShowOptionsToolbar();
            }

            @Override
            public void onSimpleClick(@NonNull @NotNull DocumentSnapshot item) {
                onAdapterSimpleClick(item);
            }

            @Override
            public void onLongClick(@NonNull @NotNull DocumentSnapshot item) {
                onAdapterLongClick(item);
            }

            @Override
            public void updateSelectedItemsCounter(int value) {
                onAdapterUpdateSelectedItemsCounter(value);
            }

            @NonNull
            @Override
            public @NotNull BasicFragmentForRecyclerView<?> getFragment() {
                return BasicFriendsFragment.this;
            }
        }));
    }

    @Override
    protected void onFilter(String newText) {
        if(viewModel.getAdapter() == null){
            return;
        }

        if(newText == null || newText.isEmpty()){
            newText = CoreUtilities.General.DEFAULT_VALUE_FOR_SEARCH;
        }

        viewModel.getAdapter().setFilterOption(BasicFriendsFragment.this, newText);
    }

    @Override
    protected void onSearchActionCollapse() {
        super.onSearchActionCollapse();
        if(viewModel.getAdapter() != null){
            viewModel.getAdapter().setInitialOption(BasicFriendsFragment.this);
        }
    }
}