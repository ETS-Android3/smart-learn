package com.smart_learn.presenter.guest.fragments.common.expressions.standard;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.R;
import com.smart_learn.core.guest.services.GuestExpressionService;
import com.smart_learn.data.common.helpers.DataCallbacks;
import com.smart_learn.data.guest.room.entitites.Expression;
import com.smart_learn.data.guest.room.entitites.helpers.BasicInfo;
import com.smart_learn.data.common.entities.Translation;
import com.smart_learn.presenter.guest.activities.notebook.GuestNotebookSharedViewModel;
import com.smart_learn.core.common.helpers.ApplicationController;
import com.smart_learn.presenter.guest.fragments.common.expressions.GuestBasicExpressionsViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.Getter;
import lombok.Setter;
import timber.log.Timber;

public abstract class GuestStandardExpressionsViewModel extends GuestBasicExpressionsViewModel {

    @Getter
    @Setter
    private boolean allItemsAreSelected;
    private final AtomicBoolean isDeletingActive;

    public GuestStandardExpressionsViewModel(@NonNull @NotNull Application application) {
        super(application);
        allItemsAreSelected = false;
        isDeletingActive = new AtomicBoolean(false);
    }

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

    protected void deleteSelectedExpressions(){
        if(isDeletingActive.get()){
            return;
        }
        isDeletingActive.set(true);

        if(currentLessonId == GuestNotebookSharedViewModel.NO_ITEM_SELECTED){
            liveToastMessage.setValue(ApplicationController.getInstance().getString(R.string.error_deleting_expressions));
            Timber.w("currentLessonId is not set");
            isDeletingActive.set(false);
            return;
        }

        if(allItemsAreSelected){
            GuestExpressionService.getInstance().deleteAll(currentLessonId, new DataCallbacks.General() {
                @Override
                public void onSuccess() {
                    liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.success_deleting_expressions));
                    if (adapter != null) {
                        adapter.resetSelectedItems();
                    }
                    isDeletingActive.set(false);
                }

                @Override
                public void onFailure() {
                    liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.error_deleting_expressions));
                    isDeletingActive.set(false);
                }
            });
            return;
        }

        if(adapter == null){
            liveToastMessage.setValue(ApplicationController.getInstance().getString(R.string.error_deleting_expressions));
            Timber.w("adapter is null");
            isDeletingActive.set(false);
            return;
        }

        ArrayList<Expression> selectedExpressions = adapter.getSelectedValues();
        if(selectedExpressions.isEmpty()){
            liveToastMessage.setValue(ApplicationController.getInstance().getString(R.string.no_selected_expression));
            isDeletingActive.set(false);
            return;
        }

        GuestExpressionService.getInstance().deleteAll(selectedExpressions, new DataCallbacks.General() {
            @Override
            public void onSuccess() {
                liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.success_deleting_expressions));
                if (adapter != null) {
                    adapter.resetSelectedItems();
                }
                isDeletingActive.set(false);
            }

            @Override
            public void onFailure() {
                liveToastMessage.postValue(ApplicationController.getInstance().getString(R.string.error_deleting_expressions));
                isDeletingActive.set(false);
            }
        });
    }

}
