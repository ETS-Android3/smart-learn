package com.smart_learn.presenter.activities.notebook.user.fragments.home_word;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.tabs.TabLayout;
import com.smart_learn.R;
import com.smart_learn.core.utilities.GeneralUtilities;
import com.smart_learn.databinding.FragmentUserWordContainerBinding;
import com.smart_learn.presenter.activities.notebook.helpers.NotebookActivity;
import com.smart_learn.presenter.activities.notebook.user.fragments.UserNotebookSharedViewModel;
import com.smart_learn.presenter.helpers.Utilities;
import com.smart_learn.presenter.helpers.fragments.helpers.WebViewFragment;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import timber.log.Timber;


public class UserWordContainerFragment extends Fragment {

    @Getter
    protected FragmentUserWordContainerBinding binding;
    protected UserNotebookSharedViewModel sharedViewModel;
    private NavController nestedNavController;


    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setViewModel();
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = FragmentUserWordContainerBinding.inflate(inflater);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // try to set navigation graph
        // https://stackoverflow.com/questions/52540303/android-jetpack-navigation-with-viewpager-and-tablayout/62530288#62530288
        NavHostFragment nestedNavHostFragment = (NavHostFragment) getChildFragmentManager().findFragmentById(R.id.nested_nav_host_fragment_user_word_container);
        if(nestedNavHostFragment != null){
            nestedNavController = nestedNavHostFragment.getNavController();
        }

        // if navigation graph cannot be set, then stop activity
        if(nestedNavController == null){
            GeneralUtilities.showShortToastMessage(this.requireContext(), getString(R.string.error_loading_screen));
            // If navigation cannot be opened finish, because navigation cannot be done.
            requireActivity().onBackPressed();
            return;
        }

        setLayoutUtilities();
    }

    protected void setLayoutUtilities(){
        // set tab listener
        binding.tabLayoutFragmentUserWordContainer.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                switch (position){
                    case 0: // for Word overview
                        nestedNavController.navigate(R.id.user_home_word_fragment_nested_nav_graph_fragment_user_word_container);
                        break;
                    case 1: // fot meaning
                        // https://developer.android.com/guide/navigation/navigation-pass-data#java
                        Bundle meaningBundle = new Bundle();
                        meaningBundle.putString(WebViewFragment.URL_KEY, sharedViewModel.getMeaningUrl());
                        nestedNavController.navigate(R.id.meaning_web_view_fragment_nested_nav_graph_fragment_user_word_container, meaningBundle);
                        break;
                    case 2: // for examples
                        // https://developer.android.com/guide/navigation/navigation-pass-data#java
                        Bundle examplesBundle = new Bundle();
                        examplesBundle.putString(WebViewFragment.URL_KEY, sharedViewModel.getExamplesUrl());
                        nestedNavController.navigate(R.id.examples_web_view_fragment_nested_nav_graph_fragment_user_word_container, examplesBundle);
                        break;
                    default:
                        Timber.w("Position " + position + " is not set");
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // no action needed here
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // no action needed here
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Utilities.Activities.resetToolbarTitle((AppCompatActivity) requireActivity(), getString(R.string.word));
        ((NotebookActivity<?>)requireActivity()).hideBottomNavigationMenu();
    }

    private void setViewModel(){
        // set shared view model
        sharedViewModel = new ViewModelProvider(requireActivity()).get(UserNotebookSharedViewModel.class);
    }
}