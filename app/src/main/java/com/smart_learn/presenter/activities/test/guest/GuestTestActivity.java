package com.smart_learn.presenter.activities.test.guest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.smart_learn.R;
import com.smart_learn.core.utilities.GeneralUtilities;
import com.smart_learn.data.entities.Question;
import com.smart_learn.data.entities.Test;
import com.smart_learn.data.room.entities.RoomTest;
import com.smart_learn.databinding.LayoutBottomSheetShowGuestTestOptionsBinding;
import com.smart_learn.presenter.activities.guest.GuestActivity;
import com.smart_learn.presenter.activities.test.TestActivity;
import com.smart_learn.presenter.activities.test.helpers.fragments.scheduled_test_info.ScheduledTestInfoFragment;
import com.smart_learn.presenter.activities.test.helpers.fragments.test_questions.TestQuestionsFragment;
import com.smart_learn.presenter.helpers.PresenterUtilities;
import com.smart_learn.presenter.helpers.fragments.test_finalize.FinalizeTestFragment;
import com.smart_learn.presenter.helpers.fragments.test_types.BasicTestTypeFragment;
import com.smart_learn.presenter.helpers.fragments.test_types.mixed.MixedTestFragment;
import com.smart_learn.presenter.helpers.fragments.words.BasicWordsFragment;

import org.jetbrains.annotations.NotNull;

import timber.log.Timber;

public class GuestTestActivity extends TestActivity<GuestTestSharedViewModel> {

    @NonNull
    @Override
    protected @NotNull Class<GuestTestSharedViewModel> getModelClassForViewModel() {
        return GuestTestSharedViewModel.class;
    }

    @Override
    protected int getNavigationGraphResource() {
        return R.navigation.nav_graph_activity_guest_test;
    }

    @Override
    protected int getMenuResourceId() {
        return R.menu.menu_bottom_navigation_activity_guest_test;
    }

    @Override
    protected void onFabClick() {
        showAddNewTestOptions();
    }

    @Override
    protected void processScheduledTestNotification(@NonNull @NotNull String scheduledTestId) {
        sharedViewModel.processScheduledTestNotification(GuestTestActivity.this, scheduledTestId);
    }

    public void goToGuestActivity(){
        // When going back to GuestActivity clear backstack and finish current activity,
        // because GuestActivity is central point when user is not logged in.
        Intent intent = new Intent(this, GuestActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        this.finish();
    }

    @Override
    protected void setLayoutUtilities(){
        super.setLayoutUtilities();

        // try to set navigation graph
        navController = PresenterUtilities.Activities.setNavigationGraphWithBottomMenu(this, R.id.nav_host_fragment_activity_test,
                binding.bottomNavigationActivityTest, new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @SuppressLint("NonConstantResourceId")
                    @Override
                    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.guest_test_history_fragment_nav_graph_activity_guest_test:
                                navController.navigate(R.id.guest_test_history_fragment_nav_graph_activity_guest_test,null);
                                return true;
                            case R.id.guest_scheduled_tests_fragment_nav_graph_activity_guest_test:
                                navController.navigate(R.id.guest_scheduled_tests_fragment_nav_graph_activity_guest_test, null);
                                return true;
                        }
                        return false;
                    }
                }, null);

    }

    private void showAddNewTestOptions(){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.AppTheme_BottomSheetDialogTheme);

        LayoutBottomSheetShowGuestTestOptionsBinding bottomSheetBinding = DataBindingUtil.inflate(LayoutInflater.from(this),
                R.layout.layout_bottom_sheet_show_guest_test_options,null, false);
        bottomSheetBinding.setLifecycleOwner(this);

        bottomSheetDialog.setContentView(bottomSheetBinding.getRoot());
        bottomSheetDialog.show();

        // set button listeners
        bottomSheetBinding.btnStartGuestLocalTestLayoutBottomSheetShowGuestTestOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                // prepare a new empty test
                sharedViewModel.setGeneratedTest(RoomTest.generateEmptyObject());
                goToGuestSelectLessonFragment();
            }
        });
    }

    @Override
    public void onBackPressed() {
        // if this is last activity then go to GuestActivity in order to avoid to exit from application
        // https://stackoverflow.com/questions/5975811/how-to-check-if-an-activity-is-the-last-one-in-the-activity-stack-for-an-applica/15664268#15664268
        if(isTaskRoot()){
            goToGuestActivity();
            return;
        }

        // From these fragments go back to GuestActivity. In order to do that finish current activity.
        if(sharedViewModel.isTestHistoryFragmentActive() || sharedViewModel.isScheduledTestFragmentActive()){
            sharedViewModel.setTestHistoryFragmentActive(false);
            sharedViewModel.setScheduledTestFragmentActive(false);
            this.finish();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        // if this is last activity then go to GuestActivity in order to avoid to exit from application
        // https://stackoverflow.com/questions/5975811/how-to-check-if-an-activity-is-the-last-one-in-the-activity-stack-for-an-applica/15664268#15664268
        if(isTaskRoot()){
            goToGuestActivity();
            return true;
        }

        // From these fragments go back to GuestActivity. In order to do that finish current activity.
        if(sharedViewModel.isTestHistoryFragmentActive() || sharedViewModel.isScheduledTestFragmentActive()){
            sharedViewModel.setTestHistoryFragmentActive(false);
            sharedViewModel.setScheduledTestFragmentActive(false);
            this.finish();
            return true;
        }
        return super.onSupportNavigateUp();
    }

    public void goToGuestTestResultsFragment(int testId, int testType){
        Bundle args = new Bundle();
        args.putString(TestQuestionsFragment.SELECTED_TEST_KEY, String.valueOf(testId));
        switch (testType){
            case Test.Types.WORD_WRITE:
                args.putInt(TestQuestionsFragment.QUESTION_TYPE_KEY, Question.Types.QUESTION_FULL_WRITE);
                break;
            case Test.Types.WORD_QUIZ:
                args.putInt(TestQuestionsFragment.QUESTION_TYPE_KEY, Question.Types.QUESTION_QUIZ);
                break;
            case Test.Types.EXPRESSION_TRUE_OR_FALSE:
                args.putInt(TestQuestionsFragment.QUESTION_TYPE_KEY, Question.Types.QUESTION_TRUE_OR_FALSE);
                break;
            // both WORD_MIXED_LETTERS and EXPRESSION_MIXED_WORDS will have same question type
            case Test.Types.WORD_MIXED_LETTERS:
            case Test.Types.EXPRESSION_MIXED_WORDS:
                args.putInt(TestQuestionsFragment.QUESTION_TYPE_KEY, Question.Types.QUESTION_MIXED);
                break;

            default:
                GeneralUtilities.showShortToastMessage(this, getString(R.string.error_can_not_continue));
                Timber.w("test type [" + testType + "] is not valid");
                return;
        }

        navController.navigate(R.id.guest_test_questions_fragment_nav_graph_activity_guest_test, args,
                PresenterUtilities.Activities.getExitBottomMenuNavAnimationsOptions());
    }

    public void goToGuestScheduledTestInfoFragment(){
        navController.navigate(R.id.action_guest_scheduled_tests_fragment_to_guest_scheduled_test_info_fragment_nav_graph_activity_guest_test, null,
                PresenterUtilities.Activities.getExitBottomMenuNavAnimationsOptions());
    }

    public void goToGuestScheduledTestInfoFragmentForUpdate(int testId){
        Bundle args = new Bundle();
        args.putString(ScheduledTestInfoFragment.TEST_ID_KEY, String.valueOf(testId));
        navController.navigate(R.id.action_guest_scheduled_tests_fragment_to_guest_scheduled_test_info_fragment_nav_graph_activity_guest_test, args,
                PresenterUtilities.Activities.getExitBottomMenuNavAnimationsOptions());
    }

    public void goToGuestTestTypesFragment(){
        navController.navigate(R.id.action_guest_select_lesson_fragment_to_guest_select_test_type_fragment_nav_graph_activity_guest_test);
    }

    public void goToGuestSelectLessonFragment(){
        navController.navigate(R.id.guest_select_lesson_fragment_nav_graph_activity_guest_test,null,
                PresenterUtilities.Activities.getExitBottomMenuNavAnimationsOptions());
    }

    public void goToGuestTestSetupFragment(){
        navController.navigate(R.id.action_guest_select_test_type_fragment_to_guest_test_setup_fragment_nav_graph_activity_guest_test);
    }

    public void goToGuestSelectWordsFragment(int lessonId) {
        Bundle args = new Bundle();
        args.putInt(BasicWordsFragment.SELECTED_LESSON_KEY, lessonId);
        navController.navigate(R.id.action_guest_test_setup_fragment_to_guest_select_words_fragment_nav_graph_activity_guest_test, args);
    }

    public void goToGuestSelectExpressionsFragment(int lessonId) {
        Bundle args = new Bundle();
        args.putInt(BasicWordsFragment.SELECTED_LESSON_KEY, lessonId);
        navController.navigate(R.id.action_guest_test_setup_fragment_to_guest_select_expressions_fragment_nav_graph_activity_guest_test, args);
    }

    public void goToActivateTestFragment(int type, int testId){
        switch (type){
            case Test.Types.WORD_WRITE:
                goToGuestWordsFullWriteTestFragment(testId);
                return;
            case Test.Types.WORD_QUIZ:
                goToGuestWordsQuizTestFragment(testId);
                return;
            case Test.Types.WORD_MIXED_LETTERS:
                goToGuestWordsMixedLettersTestFragment(testId);
                return;
            case Test.Types.EXPRESSION_MIXED_WORDS:
                goToGuestExpressionsMixedTestFragment(testId);
                return;
            case Test.Types.EXPRESSION_TRUE_OR_FALSE:
                goToGuestExpressionsTrueOrFalseTestFragment(testId);
                return;
            default:
                GeneralUtilities.showShortToastMessage(this, getString(R.string.error_can_not_continue));
        }
    }

    private void goToGuestWordsFullWriteTestFragment(int testId) {
        Bundle args = new Bundle();
        args.putString(BasicTestTypeFragment.TEST_ID_KEY, String.valueOf(testId));
        navController.navigate(R.id.guest_full_write_test_fragment_nav_graph_activity_guest_test, args);
    }

    private void goToGuestWordsQuizTestFragment(int testId) {
        Bundle args = new Bundle();
        args.putString(BasicTestTypeFragment.TEST_ID_KEY, String.valueOf(testId));
        navController.navigate(R.id.guest_quiz_test_fragment_nav_graph_activity_guest_test, args);
    }

    private void goToGuestExpressionsTrueOrFalseTestFragment(int testId) {
        Bundle args = new Bundle();
        args.putString(BasicTestTypeFragment.TEST_ID_KEY, String.valueOf(testId));
        navController.navigate(R.id.guest_true_or_false_test_fragment_nav_graph_activity_guest_test, args);
    }

    private void goToGuestExpressionsMixedTestFragment(int testId) {
        Bundle args = new Bundle();
        args.putString(BasicTestTypeFragment.TEST_ID_KEY, String.valueOf(testId));
        args.putBoolean(MixedTestFragment.IS_MIXED_LETTERS_TEST_KEY, false);
        navController.navigate(R.id.guest_mixed_test_fragment_nav_graph_activity_guest_test, args);
    }

    private void goToGuestWordsMixedLettersTestFragment(int testId) {
        Bundle args = new Bundle();
        args.putString(BasicTestTypeFragment.TEST_ID_KEY, String.valueOf(testId));
        args.putBoolean(MixedTestFragment.IS_MIXED_LETTERS_TEST_KEY, true);
        navController.navigate(R.id.guest_mixed_test_fragment_nav_graph_activity_guest_test, args);
    }

    public void goToGuestScheduledTestsFragment(){
       navController.navigate(R.id.guest_scheduled_tests_fragment_nav_graph_activity_guest_test, null);
   }

    public void goToGuestTestHistoryFragment(){
        navController.navigate(R.id.guest_test_history_fragment_nav_graph_activity_guest_test, null);
    }

    public void goToGuestFinalizeTestFragment(int testId, int testType, int correctAnsweredQuestions, int totalQuestions){
        if (totalQuestions <= 0) {
            Timber.w("totalQuestions [" + totalQuestions + "] is not valid");
            GeneralUtilities.showShortToastMessage(this, getString(R.string.error_can_not_continue));
            return;
        }

        if (correctAnsweredQuestions < 0 || correctAnsweredQuestions > totalQuestions) {
            Timber.w("correctAnsweredQuestions [" + correctAnsweredQuestions + "] is not valid");
            GeneralUtilities.showShortToastMessage(this, getString(R.string.error_can_not_continue));
            return;
        }

        switch (testType){
            case Test.Types.WORD_WRITE:
            case Test.Types.WORD_QUIZ:
            case Test.Types.WORD_MIXED_LETTERS:
            case Test.Types.EXPRESSION_MIXED_WORDS:
            case Test.Types.EXPRESSION_TRUE_OR_FALSE:
                break;
            default:
                Timber.w("testType [" + testType + "] is not valid");
                GeneralUtilities.showShortToastMessage(this, getString(R.string.error_can_not_continue));
                return;
        }

        Bundle args = new Bundle();
        args.putString(FinalizeTestFragment.TEST_ID_KEY, String.valueOf(testId));
        args.putInt(FinalizeTestFragment.TEST_TYPE_KEY, testType);
        args.putInt(FinalizeTestFragment.TEST_TOTAL_QUESTIONS_KEY, totalQuestions);
        args.putInt(FinalizeTestFragment.TEST_CORRECT_ANSWERS_KEY, correctAnsweredQuestions);
        navController.navigate(R.id.guest_finalize_test_fragment_nav_graph_activity_guest_test, args);
    }

}