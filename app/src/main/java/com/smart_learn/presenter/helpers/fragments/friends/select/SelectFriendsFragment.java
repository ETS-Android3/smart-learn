package com.smart_learn.presenter.helpers.fragments.friends.select;

import android.view.View;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;
import com.smart_learn.R;
import com.smart_learn.presenter.helpers.fragments.friends.BasicFriendsFragment;

import org.jetbrains.annotations.NotNull;


public abstract class SelectFriendsFragment<VM extends SelectFriendsViewModel> extends BasicFriendsFragment<VM> {

    protected abstract int getFloatingActionButtonIconResourceId();
    protected abstract void onFloatingActionButtonPress();
    protected abstract void showSelectedItemsCounter(int value);

    @Override
    protected boolean showFloatingActionButton() {
        return true;
    }

    @Override
    protected int getToolbarTitle() {
        return R.string.selected_friends_point;
    }

    @Override
    protected boolean useToolbarMenu() {
        return true;
    }

    @Override
    protected void onAdapterRemoveFriend(@NonNull @NotNull DocumentSnapshot friendSnapshot) {

    }

    @Override
    protected boolean onAdapterShowCheckedIcon() {
        return true;
    }

    @Override
    protected boolean onAdapterShowOptionsToolbar() {
        return false;
    }

    @Override
    protected void onAdapterSimpleClick(@NonNull @NotNull DocumentSnapshot item) {

    }

    @Override
    protected void onAdapterLongClick(@NonNull @NotNull DocumentSnapshot item) {

    }

    @Override
    protected void onAdapterUpdateSelectedItemsCounter(int value) {
        showSelectedItemsCounter(value);
    }

    @Override
    protected void setViewModel() {
        super.setViewModel();

        // here will be used only selection mode
        if(viewModel.getAdapter() != null){
            viewModel.getAdapter().setSelectionModeActive(true);
        }
    }

    @Override
    protected void setLayoutUtilities() {
        super.setLayoutUtilities();

        floatingActionButton.setImageResource(getFloatingActionButtonIconResourceId());

        // set listeners
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFloatingActionButtonPress();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if(viewModel.getAdapter() != null){
            showSelectedItemsCounter(viewModel.getAdapter().getSelectedValues().size());
        }
    }

}