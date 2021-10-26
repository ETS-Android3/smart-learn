package com.smart_learn.data.common.entities.question;

import com.smart_learn.R;
import com.smart_learn.core.common.helpers.CoreUtilities;
import com.smart_learn.core.common.helpers.ApplicationController;
import com.smart_learn.data.common.entities.question.helpers.QuestionMetadata;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public abstract class Question {

    public interface Types {
        int NO_TYPE = 0;
        int QUESTION_FULL_WRITE = 1;
        int QUESTION_QUIZ = 2;
        int QUESTION_TRUE_OR_FALSE = 3;
        int QUESTION_MIXED = 4;
    }

    public static final int NO_ID = -1;

    protected final long id;
    protected final int type;
    @NotNull @NonNull
    protected final QuestionMetadata questionMetadata;

    @NotNull @NonNull
    protected final String questionValue;
    @NotNull @NonNull
    protected final String questionValueReversed;
    protected boolean isReversed;

    protected boolean isAnswered;
    protected boolean isAnswerCorrect;
    protected long answerTimeInMilliseconds;

    public Question(long id, int type, String questionValue, String questionValueReversed, QuestionMetadata questionMetadata) {
        this.id = id;
        this.type = type;
        this.questionValue = questionValue == null ? "" : questionValue.trim();
        this.questionValueReversed = questionValueReversed == null ? "" : questionValueReversed.trim();
        this.questionMetadata = questionMetadata == null ? QuestionMetadata.generateEmptyObject() : questionMetadata;
    }

    public boolean areItemsTheSame(Question newItem) {
        if(newItem == null){
            return false;
        }
        return this.id == newItem.getId();
    }

    public boolean areContentsTheSame(Question newItem) {
        if(newItem == null){
            return false;
        }
        return this.type == newItem.getType() &&
                CoreUtilities.General.areObjectsTheSame(this.questionMetadata, newItem.getQuestionMetadata()) &&
                CoreUtilities.General.areObjectsTheSame(this.questionValue, newItem.getQuestionValue()) &&
                CoreUtilities.General.areObjectsTheSame(this.questionValueReversed, newItem.getQuestionValueReversed()) &&
                this.isReversed == newItem.isReversed() &&
                this.isAnswered == newItem.isAnswered() &&
                this.isAnswerCorrect == newItem.isAnswerCorrect() &&
                this.answerTimeInMilliseconds == newItem.getAnswerTimeInMilliseconds();
    }

    public static String getQuestionDescription(int type){
        switch (type){
            case Types.QUESTION_QUIZ:
                return ApplicationController.getInstance().getString(R.string.question_quiz_description);
            case Types.QUESTION_TRUE_OR_FALSE:
                return ApplicationController.getInstance().getString(R.string.question_true_or_false_description);
            case Types.QUESTION_FULL_WRITE:
                return ApplicationController.getInstance().getString(R.string.question_word_full_write_description);
            case Types.QUESTION_MIXED:
                return ApplicationController.getInstance().getString(R.string.question_mixed_description);
            default:
                return ApplicationController.getInstance().getString(R.string.empty);
        }
    }

}
