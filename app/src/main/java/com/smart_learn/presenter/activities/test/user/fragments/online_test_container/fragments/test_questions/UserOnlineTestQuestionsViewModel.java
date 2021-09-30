package com.smart_learn.presenter.activities.test.user.fragments.online_test_container.fragments.test_questions;

import android.app.Application;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.smart_learn.core.services.test.TestService;
import com.smart_learn.data.entities.Question;
import com.smart_learn.data.entities.QuestionFullWrite;
import com.smart_learn.data.entities.QuestionMixed;
import com.smart_learn.data.entities.QuestionQuiz;
import com.smart_learn.data.entities.QuestionTrueOrFalse;
import com.smart_learn.data.firebase.firestore.entities.TestDocument;
import com.smart_learn.presenter.activities.test.helpers.fragments.test_questions.TestQuestionsViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import timber.log.Timber;


public class UserOnlineTestQuestionsViewModel extends TestQuestionsViewModel {

    public UserOnlineTestQuestionsViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

    protected void setAdapterQuestions(String containerTestId, String participantTestId, int questionType){

        TestService.getInstance()
                .getOnlineTestParticipantsCollectionReference(containerTestId)
                .document(participantTestId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                        if(!task.isSuccessful() || task.getResult() == null){
                            Timber.w("result is not valid");
                            return;
                        }

                        TestDocument test = task.getResult().toObject(TestDocument.class);
                        if(test == null){
                            Timber.w("test is null");
                            return;
                        }

                        ArrayList<Question> questions = new ArrayList<>();
                        switch (questionType){
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
                                Timber.w("type [" + questionType + "] is not valid");
                                return;
                        }
                        if(adapter != null){
                            adapter.setItems(new ArrayList<>(questions));
                        }
                    }
                });
    }
}
