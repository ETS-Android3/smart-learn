package com.smart_learn.presenter.common.fragments.test.test_types;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.smart_learn.R;
import com.smart_learn.presenter.common.helpers.PresenterUtilities;
import com.smart_learn.presenter.common.fragments.helpers.BasicFragment;

import org.jetbrains.annotations.NotNull;

import timber.log.Timber;

public abstract class BasicTestTypeFragment <VM extends BasicTestTypeViewModel> extends BasicFragment<VM> {

    public static String TEST_ID_KEY = "TEST_ID_KEY";

    protected abstract void goToTestFinalizeFragment(String testId, int testType, int correctQuestions, int totalQuestions);
    protected abstract void customGoBack();
    protected void setLayoutUtilities(){}
    protected boolean useReverseSwitch(){
        return false;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // use this to set toolbar menu inside fragment
        // https://stackoverflow.com/questions/15653737/oncreateoptionsmenu-inside-fragments/31360073#31360073
        setHasOptionsMenu(true);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setLayoutUtilities();
    }

    @Override
    public void onResume() {
        super.onResume();
        PresenterUtilities.Activities.resetToolbarTitle((AppCompatActivity) requireActivity(), getString(R.string.test));
        viewModel.setFragmentIsActive(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        viewModel.setFragmentIsActive(false);
    }

    @Override
    protected void setViewModel() {
        super.setViewModel();

        if (getArguments() == null){
            goBack();
            return;
        }

        // extract test id
        String testId = getArguments().getString(TEST_ID_KEY);
        if (testId == null || testId.isEmpty()) {
            Timber.w("testId is not selected");
            goBack();
            return;
        }
        viewModel.setTestId(testId);
        viewModel.extractTest(BasicTestTypeFragment.this);

        // set switch observer
        if(useReverseSwitch()){
            viewModel.getLiveIsReverseChecked().observe(this, new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean aBoolean) {
                    viewModel.prepareAndShowQuestion(aBoolean != null && aBoolean);
                }
            });
        }
    }

    public void goBack(){
        showMessage(R.string.error_can_not_continue);
        customGoBack();
    }

    public void goBack(int messageId){
        showMessage(messageId);
        customGoBack();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        super.onOptionsItemSelected(item);
        int id = item.getItemId();
        if(id == android.R.id.home){
            customGoBack();
            return true;
        }
        return true;
    }

}