package com.smart_learn.presenter.activities.test.guest.fragments.test_questions;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;

import com.smart_learn.core.services.test.TestService;
import com.smart_learn.data.entities.Question;
import com.smart_learn.data.entities.QuestionFullWrite;
import com.smart_learn.data.entities.QuestionMixed;
import com.smart_learn.data.entities.QuestionQuiz;
import com.smart_learn.data.entities.QuestionTrueOrFalse;
import com.smart_learn.data.room.entities.RoomTest;
import com.smart_learn.presenter.activities.test.guest.GuestTestActivity;
import com.smart_learn.presenter.activities.test.helpers.fragments.test_questions.TestQuestionsFragment;
import com.smart_learn.presenter.helpers.Utilities;
import com.smart_learn.presenter.helpers.adapters.test.questions.QuestionsAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import timber.log.Timber;


public class GuestTestQuestionsFragment extends TestQuestionsFragment<GuestTestQuestionsViewModel> {

    @NonNull
    @Override
    protected @NotNull Class<GuestTestQuestionsViewModel> getModelClassForViewModel() {
        return GuestTestQuestionsViewModel.class;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((GuestTestActivity)requireActivity()).hideBottomNavigationMenu();
    }

    @Override
    protected void setViewModel(){
        super.setViewModel();

        int testId;
        try{
            testId = Integer.parseInt(currentTestId);
        } catch (NumberFormatException ex){
            Timber.w(ex);
            return;
        }

        setAdapter(testId, questionType);
    }

    private void setAdapter(int testId, int type){

        viewModel.setAdapter(new QuestionsAdapter(type, new QuestionsAdapter.Callback() {
            @Override
            public void onListLoadAction(boolean isEmpty) {
                requireActivity().runOnUiThread(() -> Utilities.Activities.changeTextViewStatus(isEmpty, emptyLabel));
            }
        }));

        TestService.getInstance().getLiveTest(testId).observe(this, new Observer<RoomTest>() {
            @Override
            public void onChanged(RoomTest test) {
                if(test == null){
                    Timber.w("test is null");
                    return;
                }

                ArrayList<Question> questions = new ArrayList<>();
                switch (type){
                    case Question.Types.QUESTION_FULL_WRITE:
                        questions.addAll(QuestionFullWrite.fromJsonToList(test.getQuestionsJson()));
                        break;
                    case Question.Types.QUESTION_QUIZ:
                        questions.addAll(QuestionQuiz.fromJsonToList(test.getQuestionsJson()));
                        break;
                    case Question.Types.QUESTION_TRUE_OR_FALSE:
                        questions.addAll(QuestionTrueOrFalse.fromJsonToList(test.getQuestionsJson()));
                        break;
                    case Question.Types.QUESTION_MIXED:
                        questions.addAll(QuestionMixed.fromJsonToList(test.getQuestionsJson()));
                        break;
                    default:
                        Timber.w("type [" + type + "] is not valid");
                        return;
                }

                if(viewModel.getAdapter() != null){
                    viewModel.getAdapter().setItems(new ArrayList<>(questions));
                }
            }
        });
    }
}