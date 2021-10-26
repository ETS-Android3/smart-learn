package com.smart_learn.presenter.user.activities.test.fragments.select_test_type;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.smart_learn.R;
import com.smart_learn.data.common.entities.Test;
import com.smart_learn.data.user.firebase.firestore.entities.TestDocument;
import com.smart_learn.presenter.common.activities.test.fragments.select_test_type.SelectTestTypeFragment;
import com.smart_learn.presenter.user.activities.test.UserTestActivity;
import com.smart_learn.presenter.user.activities.test.UserTestSharedViewModel;

import org.jetbrains.annotations.NotNull;

import timber.log.Timber;


public class UserSelectTestTypeFragment extends SelectTestTypeFragment<UserSelectTestTypeViewModel> {

    @NonNull
    @Override
    protected @NotNull Class<UserSelectTestTypeViewModel> getModelClassForViewModel() {
        return UserSelectTestTypeViewModel.class;
    }

    @Override
    protected void navigateToTestSetupFragment() {
        navigateToSpecificFragment();
    }

    @Override
    protected void setViewModel(){
        super.setViewModel();

        // set shared view model
        sharedViewModel = new ViewModelProvider(requireActivity()).get(UserTestSharedViewModel.class);
    }

    private void navigateToSpecificFragment(){
        if(sharedViewModel.getGeneratedTest() == null){
            showMessage(R.string.error_can_not_continue);
            return;
        }

        // if is a user online test, skip test setup than go directly to select words/expressions fragment
        if((sharedViewModel.getGeneratedTest() instanceof TestDocument) && ((TestDocument)sharedViewModel.getGeneratedTest()).isOnline()){
            int type = sharedViewModel.getGeneratedTest().getType();
            String lessonId = sharedViewModel.getGeneratedTest().getLessonId();
            boolean isSharedLesson = sharedViewModel.getGeneratedTest().isSharedLesson();
            switch (type){
                case Test.Types.WORD_MIXED_LETTERS:
                case Test.Types.WORD_QUIZ:
                case Test.Types.WORD_WRITE:
                    ((UserTestActivity)requireActivity()).goToUserSelectWordsFragment(lessonId, isSharedLesson);
                    return;
                case Test.Types.EXPRESSION_MIXED_WORDS:
                case Test.Types.EXPRESSION_TRUE_OR_FALSE:
                    ((UserTestActivity)requireActivity()).goToUserSelectExpressionsFragment(lessonId, isSharedLesson);
                    return;
                default:
                    Timber.w("Type [" + type + "] is not valid");
                    showMessage(R.string.error_can_not_continue);
                    return;
            }
        }

        // if is not an online test go to test setup
        ((UserTestActivity)requireActivity()).goToUserTestSetupFragment();
    }
}