package com.smart_learn.presenter.activities.notebook.user.fragments.home_expression;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.smart_learn.data.firebase.firestore.entities.ExpressionDocument;
import com.smart_learn.presenter.activities.notebook.helpers.fragments.home_expression.HomeExpressionFragment;
import com.smart_learn.presenter.activities.notebook.user.UserNotebookSharedViewModel;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import timber.log.Timber;


public class UserHomeExpressionFragment extends HomeExpressionFragment<UserHomeExpressionViewModel> {

    @Getter
    protected UserNotebookSharedViewModel sharedViewModel;

    @NonNull
    @Override
    protected @NotNull Class<UserHomeExpressionViewModel> getModelClassForViewModel() {
        return UserHomeExpressionViewModel.class;
    }

    @Override
    protected void setViewModel(){
        super.setViewModel();

        // set shared view model
        sharedViewModel = new ViewModelProvider(requireActivity()).get(UserNotebookSharedViewModel.class);
    }

    @Override
    public void onStart() {
        super.onStart();

        // set listener
        // https://www.youtube.com/watch?v=LfkhFCDnkS0&ab_channel=CodinginFlow
        sharedViewModel
                .getSelectedExpression()
                .getReference()
                .addSnapshotListener(this.requireActivity(), new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot value,
                                        @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Timber.e(error);
                            return;
                        }

                        if(value == null){
                            Timber.i("value is null");
                            return;
                        }

                        ExpressionDocument expressionDocument = value.toObject(ExpressionDocument.class);
                        if(expressionDocument == null){
                            Timber.i("expressionDocument is null");
                            return;
                        }

                        viewModel.setLiveExpression(value, expressionDocument);
                    }
                });
    }
}