package com.smart_learn.presenter.user.activities.test.fragments.online_test_container.fragments.test_questions;

import androidx.annotation.NonNull;

import com.smart_learn.R;
import com.smart_learn.presenter.common.activities.test.fragments.test_questions.TestQuestionsFragment;
import com.smart_learn.presenter.common.helpers.PresenterUtilities;
import com.smart_learn.presenter.common.adapters.QuestionsAdapter;

import org.jetbrains.annotations.NotNull;


public class UserOnlineTestQuestionsFragment extends TestQuestionsFragment<UserOnlineTestQuestionsViewModel> {

    public static String PARTICIPANT_TEST_ONLINE_KEY_ID = "PARTICIPANT_TEST_ONLINE_KEY_ID";

    @NonNull
    @Override
    protected @NotNull Class<UserOnlineTestQuestionsViewModel> getModelClassForViewModel() {
        return UserOnlineTestQuestionsViewModel.class;
    }

    @Override
    protected void setViewModel(){
        super.setViewModel();

        // in this fragment current test id will be actually id for test container
        final String containerTestId = currentTestId;
        // this will be id of the actually test
        String participantTestId = null;

        if(getArguments() != null){
            participantTestId = getArguments().getString(PARTICIPANT_TEST_ONLINE_KEY_ID);
        }

        if(participantTestId == null || participantTestId.isEmpty()){
            showMessage(R.string.error_can_not_open_results);
            return;
        }

        viewModel.setAdapter(new QuestionsAdapter(questionType, new QuestionsAdapter.Callback() {
            @Override
            public void onListLoadAction(boolean isEmpty) {
                requireActivity().runOnUiThread(() -> PresenterUtilities.Activities.changeTextViewStatus(isEmpty, emptyLabel));
            }
        }));
        viewModel.setAdapterQuestions(containerTestId, participantTestId, questionType);
    }
}