package com.smart_learn.presenter.helpers.fragments.recycler_view_with_bottom_menu;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.smart_learn.presenter.helpers.PresenterHelpers;
import com.smart_learn.presenter.helpers.view_models.BasicAndroidViewModel;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.Setter;

/**
 * The main ViewModel class from which all the ViewModels of the application which are related with
 * fragments extended from BasicFragmentForRecyclerView<>, must be extended.
 *
 * @param <AD> An Adapter class that extends RecyclerView.Adapter<> & PresenterHelpers.AdapterHelper.
 */
public abstract class BasicViewModelForRecyclerView <AD extends RecyclerView.Adapter <?> & PresenterHelpers.AdapterHelper>
        extends BasicAndroidViewModel {

    @Getter
    @Setter
    @Nullable
    protected AD adapter;

    public BasicViewModelForRecyclerView(@NonNull @NotNull Application application) {
        super(application);
    }
}
