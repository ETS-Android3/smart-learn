package com.smart_learn.presenter.user.activities.test.fragments.test_questions;

import android.widget.TextView;

import androidx.annotation.NonNull;

import com.smart_learn.presenter.common.activities.test.fragments.test_questions.TestQuestionsFragment;
import com.smart_learn.presenter.user.activities.test.UserTestActivity;
import com.smart_learn.presenter.common.helpers.PresenterUtilities;
import com.smart_learn.presenter.common.adapters.QuestionsAdapter;

import org.jetbrains.annotations.NotNull;


public class UserTestQuestionsFragment extends TestQuestionsFragment<UserTestQuestionsViewModel> {

    @NonNull
    @Override
    protected @NotNull Class<UserTestQuestionsViewModel> getModelClassForViewModel() {
        return UserTestQuestionsViewModel.class;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((UserTestActivity)requireActivity()).hideBottomNavigationMenu();
    }

    @Override
    protected void setViewModel(){
        super.setViewModel();

        viewModel.setAdapter(new QuestionsAdapter(questionType, new QuestionsAdapter.Callback() {
            @Override
            public void onListLoadAction(boolean isEmpty) {
                requireActivity().runOnUiThread(() -> PresenterUtilities.Activities.changeTextViewStatus(isEmpty, emptyLabel));
            }
        }));
        viewModel.setAdapterQuestions(UserTestQuestionsFragment.this, currentTestId, questionType);
    }

    protected TextView getEmptyLabel(){
        return emptyLabel;
    }
}