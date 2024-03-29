package com.smart_learn.presenter.user.activities.test;

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
import com.google.android.material.textfield.TextInputLayout;
import com.smart_learn.R;
import com.smart_learn.core.user.services.UserService;
import com.smart_learn.data.common.entities.question.Question;
import com.smart_learn.data.common.entities.Test;
import com.smart_learn.data.user.firebase.firestore.entities.TestDocument;
import com.smart_learn.data.user.firebase.firestore.entities.helpers.DocumentMetadata;
import com.smart_learn.data.common.helpers.DataUtilities;
import com.smart_learn.databinding.LayoutBottomSheetShowUserTestOptionsBinding;
import com.smart_learn.presenter.user.activities.main.MainActivity;
import com.smart_learn.presenter.common.activities.test.TestActivity;
import com.smart_learn.presenter.common.activities.test.fragments.scheduled_test_info.ScheduledTestInfoFragment;
import com.smart_learn.presenter.common.activities.test.fragments.test_questions.TestQuestionsFragment;
import com.smart_learn.presenter.user.activities.test.fragments.online_test_container.fragments.test_questions.UserOnlineTestQuestionsFragment;
import com.smart_learn.presenter.common.helpers.PresenterUtilities;
import com.smart_learn.presenter.common.dialogs.SingleLineEditableLayoutDialog;
import com.smart_learn.presenter.common.fragments.expressions.BasicExpressionsFragment;
import com.smart_learn.presenter.user.fragments.common.expressions.UserBasicExpressionsFragment;
import com.smart_learn.presenter.common.fragments.test.test_finalize.FinalizeTestFragment;
import com.smart_learn.presenter.common.fragments.test.test_types.BasicTestTypeFragment;
import com.smart_learn.presenter.common.fragments.test.test_types.mixed.MixedTestFragment;
import com.smart_learn.presenter.common.fragments.words.BasicWordsFragment;
import com.smart_learn.presenter.user.fragments.common.words.UserBasicWordsFragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import timber.log.Timber;


public class UserTestActivity extends TestActivity<UserTestSharedViewModel> {

    @NonNull
    @Override
    protected @NotNull Class<UserTestSharedViewModel> getModelClassForViewModel() {
        return UserTestSharedViewModel.class;
    }

    @Override
    protected int getNavigationGraphResource() {
        return R.navigation.nav_graph_activity_user_test;
    }

    @Override
    protected int getMenuResourceId() {
        return R.menu.menu_bottom_navigation_activity_user_test;
    }

    @Override
    protected void onFabClick() {
        showAddNewTestOptions();
    }

    @Override
    protected void processScheduledTestNotification(@NonNull @NotNull String scheduledTestId) {
        sharedViewModel.processScheduledTestNotification(UserTestActivity.this, scheduledTestId);
    }

    public void goToMainActivity(){
        // When going back to MainActivity clear backstack and finish current activity,
        // because MainActivity is central point when user is logged in.
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        this.finish();
    }

    @Override
    public void onBackPressed() {
        // if this is last activity then go to MainActivity in order to avoid to exit from application
        // https://stackoverflow.com/questions/5975811/how-to-check-if-an-activity-is-the-last-one-in-the-activity-stack-for-an-applica/15664268#15664268
        if(isTaskRoot()){
            goToMainActivity();
            return;
        }

        // From these fragments go back to MainActivity. In order to do that finish current activity.
        if(sharedViewModel.isTestHistoryFragmentActive() || sharedViewModel.isScheduledTestFragmentActive()){
            sharedViewModel.setTestHistoryFragmentActive(false);
            sharedViewModel.setScheduledTestFragmentActive(false);
            this.finish();
            return;
        }

        // From OnlineTestContainerFragment go back only to TestHistoryFragment because it is the
        // main point for this activity. From TestHistoryFragment if going back is activated then
        // this activity will be finished and MainActivity will be launched.
        if(sharedViewModel.isOnlineTestContainerFragmentActive()){
            goToUserTestHistoryFragment();
            sharedViewModel.setOnlineTestContainerFragmentActive(false);
            return;
        }

        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        // if this is last activity then go to MainActivity in order to avoid to exit from application
        // https://stackoverflow.com/questions/5975811/how-to-check-if-an-activity-is-the-last-one-in-the-activity-stack-for-an-applica/15664268#15664268
        if(isTaskRoot()){
            goToMainActivity();
            return true;
        }

        // From these fragments go back to MainActivity. In order to do that finish current activity.
        if(sharedViewModel.isTestHistoryFragmentActive() || sharedViewModel.isScheduledTestFragmentActive()){
            sharedViewModel.setTestHistoryFragmentActive(false);
            sharedViewModel.setScheduledTestFragmentActive(false);
            this.finish();
            return true;
        }

        // From OnlineTestContainerFragment go back only to TestHistoryFragment because it is the
        // main point for this activity. From TestHistoryFragment if going back is activated then
        // this activity will be finished and MainActivity will be launched.
        if(sharedViewModel.isOnlineTestContainerFragmentActive()){
            goToUserTestHistoryFragment();
            sharedViewModel.setOnlineTestContainerFragmentActive(false);
            return true;
        }
        return super.onSupportNavigateUp();
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
                            case R.id.user_test_history_fragment_nav_graph_activity_user_test:
                                navController.navigate(R.id.user_test_history_fragment_nav_graph_activity_user_test,null);
                                return true;
                            case R.id.user_scheduled_tests_fragment_nav_graph_activity_user_test:
                                navController.navigate(R.id.user_scheduled_tests_fragment_nav_graph_activity_user_test, null);
                                return true;
                        }
                        return false;
                    }
                }, null);

    }

    private void showAddNewTestOptions(){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.AppTheme_BottomSheetDialogTheme);

        LayoutBottomSheetShowUserTestOptionsBinding bottomSheetBinding = DataBindingUtil.inflate(LayoutInflater.from(this),
                R.layout.layout_bottom_sheet_show_user_test_options,null, false);
        bottomSheetBinding.setLifecycleOwner(this);

        bottomSheetDialog.setContentView(bottomSheetBinding.getRoot());
        bottomSheetDialog.show();

        // set button listeners
        bottomSheetBinding.btnStartUserLocalTestLayoutBottomSheetShowUserTestOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // prepare a new empty test
                sharedViewModel.setGeneratedTest(new TestDocument(new DocumentMetadata(UserService.getInstance().getUserUid(),
                        System.currentTimeMillis(), new ArrayList<>())));
                bottomSheetDialog.dismiss();
                goToUserSelectLessonFragment();
            }
        });

        bottomSheetBinding.btnStartUserOnlineTestLayoutBottomSheetShowUserTestOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SingleLineEditableLayoutDialog dialog = new SingleLineEditableLayoutDialog(
                        getString(R.string.test_name),
                        "",
                        getString(R.string.name),
                        DataUtilities.Limits.MAX_TEST_CUSTOM_NAME,
                        getString(R.string.continue_value),
                        new SingleLineEditableLayoutDialog.Callback() {
                            @Override
                            public void onUpdate(String oldValue, String newValue,
                                                 @NonNull @NotNull TextInputLayout textInputLayout,
                                                 @NonNull @NotNull SingleLineEditableLayoutDialog.Listener listener) {
                                sharedViewModel.addNewOnlineTest(UserTestActivity.this, newValue, textInputLayout, listener);
                            }
                        }
                );

                dialog.show(getSupportFragmentManager(), "UserTestActivity");
                bottomSheetDialog.dismiss();
            }
        });
    }

    public void goToUserTestResultsFragment(String testId, int testType){
        goToUserTestResultsFragment(testId, "", testType , false);
    }

    public void goToUserTestResultsFragment(String containerTestId, String participantTestId, int testType, boolean useOnlineTestFragment){
        Bundle args = new Bundle();
        args.putString(TestQuestionsFragment.SELECTED_TEST_KEY, containerTestId);
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
                PresenterUtilities.General.showShortToastMessage(this, getString(R.string.error_can_not_continue));
                Timber.w("test type [" + testType + "] is not valid");
                return;
        }

        if(useOnlineTestFragment){
            args.putString(UserOnlineTestQuestionsFragment.PARTICIPANT_TEST_ONLINE_KEY_ID, participantTestId);
            navController.navigate(R.id.user_online_test_questions_fragment_nav_graph_activity_user_test, args);
        }
        else{
            navController.navigate(R.id.user_test_questions_fragment_nav_graph_activity_user_test, args,
                    PresenterUtilities.Activities.getExitBottomMenuNavAnimationsOptions());
        }
    }

    public void goToUserScheduledTestInfoFragment(){
        navController.navigate(R.id.action_user_scheduled_tests_fragment_to_user_scheduled_test_info_fragment_nav_graph_activity_user_test, null,
                PresenterUtilities.Activities.getExitBottomMenuNavAnimationsOptions());
    }

    public void goToUserScheduledTestInfoFragmentForUpdate(String testId){
        Bundle args = new Bundle();
        args.putString(ScheduledTestInfoFragment.TEST_ID_KEY, testId);
        navController.navigate(R.id.action_user_scheduled_tests_fragment_to_user_scheduled_test_info_fragment_nav_graph_activity_user_test, args,
                PresenterUtilities.Activities.getExitBottomMenuNavAnimationsOptions());
    }

    public void goToUserTestTypesFragment(){
        navController.navigate(R.id.action_user_select_lesson_fragment_to_user_select_test_type_fragment_nav_graph_activity_user_test);
    }

    public void goToUserSelectLessonFragment(){
        navController.navigate(R.id.user_select_lesson_fragment_nav_graph_activity_user_test,null,
                PresenterUtilities.Activities.getExitBottomMenuNavAnimationsOptions());
    }

    public void goToUserTestSetupFragment(){
        navController.navigate(R.id.action_user_select_test_type_fragment_to_user_test_setup_fragment_nav_graph_activity_user_test);
    }

    public void goToUserSelectWordsFragment(String lessonId, boolean isSharedLesson) {
        Bundle args = new Bundle();
        args.putString(BasicWordsFragment.SELECTED_LESSON_KEY, lessonId);
        args.putBoolean(UserBasicWordsFragment.IS_SHARED_LESSON_SELECTED, isSharedLesson);
        navController.navigate(R.id.user_select_words_fragment_nav_graph_activity_user_test, args);
    }

    public void goToUserSelectExpressionsFragment(String lessonId, boolean isSharedLesson) {
        Bundle args = new Bundle();
        args.putString(BasicExpressionsFragment.SELECTED_LESSON_KEY, lessonId);
        args.putBoolean(UserBasicExpressionsFragment.IS_SHARED_LESSON_SELECTED, isSharedLesson);
        navController.navigate(R.id.user_select_expressions_fragment_nav_graph_activity_user_test, args);
    }

    public void goToActivateTestFragment(int type, String testId, boolean isOnline){
        if(isOnline){
            // here test will start so can not be finished, so give 'false' to isFinished
            goToUserOnlineTestContainerFragment(type, testId, false);
            return;
        }

        // here will be only local tests
        switch (type){
            case Test.Types.WORD_WRITE:
                goToUserWordsFullWriteLocalTestFragment(testId);
                return;
            case Test.Types.WORD_QUIZ:
                goToUserWordsQuizLocalTestFragment(testId);
                return;
            case Test.Types.WORD_MIXED_LETTERS:
                goToUserWordsMixedLettersLocalTestFragment(testId);
                return;
            case Test.Types.EXPRESSION_MIXED_WORDS:
                goToUserExpressionsMixedLocalTestFragment(testId);
                return;
            case Test.Types.EXPRESSION_TRUE_OR_FALSE:
                goToUserExpressionsTrueOrFalseLocalTestFragment(testId);
                return;
            default:
                PresenterUtilities.General.showShortToastMessage(this, getString(R.string.error_can_not_continue));
        }
    }

    private void goToUserWordsFullWriteLocalTestFragment(String testId) {
        Bundle args = new Bundle();
        args.putString(BasicTestTypeFragment.TEST_ID_KEY, String.valueOf(testId));
        navController.navigate(R.id.user_full_write_test_fragment_nav_graph_activity_user_test, args);
    }

    private void goToUserWordsQuizLocalTestFragment(String testId) {
        Bundle args = new Bundle();
        args.putString(BasicTestTypeFragment.TEST_ID_KEY, testId);
        navController.navigate(R.id.user_quiz_test_fragment_nav_graph_activity_user_test, args);
    }

    private void goToUserExpressionsTrueOrFalseLocalTestFragment(String testId) {
        Bundle args = new Bundle();
        args.putString(BasicTestTypeFragment.TEST_ID_KEY, testId);
        navController.navigate(R.id.user_true_or_false_test_fragment_nav_graph_activity_user_test, args);
    }

    private void goToUserExpressionsMixedLocalTestFragment(String testId) {
        Bundle args = new Bundle();
        args.putString(BasicTestTypeFragment.TEST_ID_KEY, testId);
        args.putBoolean(MixedTestFragment.IS_MIXED_LETTERS_TEST_KEY, false);
        navController.navigate(R.id.user_mixed_test_fragment_nav_graph_activity_user_test, args);
    }

    private void goToUserWordsMixedLettersLocalTestFragment(String testId) {
        Bundle args = new Bundle();
        args.putString(BasicTestTypeFragment.TEST_ID_KEY, testId);
        args.putBoolean(MixedTestFragment.IS_MIXED_LETTERS_TEST_KEY, true);
        navController.navigate(R.id.user_mixed_test_fragment_nav_graph_activity_user_test, args);
    }

    public void goToUserScheduledTestsFragment(){
        navController.navigate(R.id.user_scheduled_tests_fragment_nav_graph_activity_user_test, null);
    }

    public void goToUserTestHistoryFragment(){
        navController.navigate(R.id.user_test_history_fragment_nav_graph_activity_user_test, null);
    }

    public void goToUserFinalizeTestFragment(String testId, int testType, int correctAnsweredQuestions, int totalQuestions){
        if (totalQuestions <= 0) {
            Timber.w("totalQuestions [" + totalQuestions + "] is not valid");
            PresenterUtilities.General.showShortToastMessage(this, getString(R.string.error_can_not_continue));
            return;
        }

        if (correctAnsweredQuestions < 0 || correctAnsweredQuestions > totalQuestions) {
            Timber.w("correctAnsweredQuestions [" + correctAnsweredQuestions + "] is not valid");
            PresenterUtilities.General.showShortToastMessage(this, getString(R.string.error_can_not_continue));
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
                PresenterUtilities.General.showShortToastMessage(this, getString(R.string.error_can_not_continue));
                return;
        }

        Bundle args = new Bundle();
        args.putString(FinalizeTestFragment.TEST_ID_KEY, testId);
        args.putInt(FinalizeTestFragment.TEST_TYPE_KEY, testType);
        args.putInt(FinalizeTestFragment.TEST_TOTAL_QUESTIONS_KEY, totalQuestions);
        args.putInt(FinalizeTestFragment.TEST_CORRECT_ANSWERS_KEY, correctAnsweredQuestions);
        navController.navigate(R.id.user_finalize_test_fragment_nav_graph_activity_user_test, args);
    }

    public void goToUserOnlineTestContainerFragment(int testType, String testId, boolean isFinished){
        sharedViewModel.setSelectedOnlineContainerTestId(testId);
        sharedViewModel.setSelectedOnlineTestType(testType);
        sharedViewModel.setSelectedOnlineTestFinished(isFinished);
        navController.navigate(R.id.online_test_container_fragment_nav_graph_activity_user_test, null);
    }

    public void goToUserSelectFriendsFragment() {
        navController.navigate(R.id.user_select_friends_fragment_nav_graph_activity_user_test,null,
                PresenterUtilities.Activities.getExitBottomMenuNavAnimationsOptions());
    }
}