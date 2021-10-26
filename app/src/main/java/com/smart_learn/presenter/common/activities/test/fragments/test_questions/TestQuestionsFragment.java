package com.smart_learn.presenter.common.activities.test.fragments.test_questions;

import com.smart_learn.R;
import com.smart_learn.data.common.entities.question.Question;
import com.smart_learn.presenter.common.fragments.helpers.recycler_view.BasicFragmentForRecyclerView;

import timber.log.Timber;


public abstract class TestQuestionsFragment <VM extends TestQuestionsViewModel> extends BasicFragmentForRecyclerView<VM> {

    public static String SELECTED_TEST_KEY = "SELECTED_TEST_KEY";
    public static String QUESTION_TYPE_KEY = "QUESTION_TYPE_KEY";

    protected int questionType = Question.Types.NO_TYPE;
    protected String currentTestId = "";

    @Override
    protected boolean isFragmentWithBottomNav() {
        return false;
    }

    @Override
    protected int getEmptyLabelDescriptionResourceId() {
        return R.string.no_questions;
    }

    @Override
    protected int getToolbarTitle() {
        return R.string.results;
    }

    @Override
    protected void setLayoutUtilities() {
        super.setLayoutUtilities();

        // this fragment does not need refreshing neither for user or guest
        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    protected void setViewModel() {
        super.setViewModel();

        // try to link fragment with lesson
        if(getArguments() == null){
            Timber.w("getArguments() is null");
            goBack();
            return;
        }

        currentTestId = getArguments().getString(SELECTED_TEST_KEY);
        if(currentTestId == null || currentTestId.isEmpty()){
            Timber.w("currentTestId is not selected");
            goBack();
            return;
        }

        questionType = getArguments().getInt(QUESTION_TYPE_KEY);
        if(questionType == Question.Types.NO_TYPE){
            Timber.w("questionType is not selected");
            goBack();
        }
    }

    private void goBack(){
        showMessage(R.string.error_can_not_continue);
        requireActivity().onBackPressed();
    }

}