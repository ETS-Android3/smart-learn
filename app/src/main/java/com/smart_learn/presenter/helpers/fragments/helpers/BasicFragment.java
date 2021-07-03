package com.smart_learn.presenter.helpers.fragments.helpers;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.smart_learn.core.utilities.GeneralUtilities;
import com.smart_learn.presenter.helpers.view_models.BasicAndroidViewModel;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;

/**
 * The main fragment from which all the fragments of the application must be extended.
 *
 * @param <VM> A ViewModel class that extends BasicAndroidViewModel that will be used by the fragment
 *             as main view model.
 * */
public abstract class BasicFragment <VM extends BasicAndroidViewModel> extends Fragment {

    @Getter
    protected VM viewModel;

    /**
     *  Used to instantiate ViewModel. ViewModelProvider needs something like 'ViewModelClassName.class'.
     *
     * @return ViewModelClassName.class
     */
    @NonNull
    @NotNull
    protected abstract Class <VM> getModelClassForViewModel();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setViewModel();
    }

    protected void setViewModel(){
        // Set view model (getModelClassForViewModel() is something like 'ViewModelClassName.class').
        viewModel = new ViewModelProvider(this).get(getModelClassForViewModel());

        // set observers
        viewModel.getLiveToastMessage().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                GeneralUtilities.showShortToastMessage(requireContext(), s);
            }
        });
    }

}