package com.smart_learn.presenter.activities.notebook.guest.fragments.expressions;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.R;
import com.smart_learn.core.services.GuestExpressionService;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.data.room.entities.Expression;
import com.smart_learn.data.room.entities.helpers.BasicInfo;
import com.smart_learn.data.room.entities.helpers.Translation;
import com.smart_learn.presenter.activities.notebook.guest.fragments.GuestNotebookSharedViewModel;
import com.smart_learn.presenter.activities.notebook.guest.fragments.expressions.helpers.ExpressionsAdapter;
import com.smart_learn.presenter.activities.notebook.helpers.fragments.expressions.ExpressionsViewModel;
import com.smart_learn.presenter.helpers.ApplicationController;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

public class GuestExpressionsViewModel extends ExpressionsViewModel<ExpressionsAdapter> {

    @Getter
    @Setter
    private int currentLessonId;
    @Getter
    @Setter
    private boolean allItemsAreSelected;

    public GuestExpressionsViewModel(@NonNull @NotNull Application application) {
        super(application);
        allItemsAreSelected = false;
        currentLessonId = GuestNotebookSharedViewModel.NO_ITEM_SELECTED;
    }

    @Override
    public void addExpression(String expressionValue, String notes, ArrayList<Translation> translations) {
        if(currentLessonId == GuestNotebookSharedViewModel.NO_ITEM_SELECTED){
            liveToastMessage.setValue(ApplicationController.getInstance().getString(R.string.error_adding_expression));
            return;
        }

        Expression newExpression = new Expression(
                notes,
                false,
                new BasicInfo(System.currentTimeMillis()),
                currentLessonId,
                false,
                "",
                translations,
                expressionValue
        );

        GuestExpressionService.getInstance().insert(newExpression, new DataCallbacks.General() {
            @Override
            public void onSuccess() {
                liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.success_adding_expression));
            }

            @Override
            public void onFailure() {
                liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.error_adding_expression));
            }
        });
    }
}
