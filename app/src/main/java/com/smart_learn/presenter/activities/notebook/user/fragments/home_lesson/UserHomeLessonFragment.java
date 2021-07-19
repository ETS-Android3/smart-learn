package com.smart_learn.presenter.activities.notebook.user.fragments.home_lesson;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.smart_learn.R;
import com.smart_learn.data.firebase.firestore.entities.LessonDocument;
import com.smart_learn.presenter.activities.notebook.helpers.fragments.home_lesson.HomeLessonFragment;
import com.smart_learn.presenter.activities.notebook.user.fragments.UserNotebookSharedViewModel;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import timber.log.Timber;


public class UserHomeLessonFragment extends HomeLessonFragment<UserHomeLessonViewModel> {

    @Getter
    private UserNotebookSharedViewModel sharedViewModel;

    @NonNull
    @Override
    protected @NotNull Class<UserHomeLessonViewModel> getModelClassForViewModel() {
        return UserHomeLessonViewModel.class;
    }


    @Override
    protected void setLayoutUtilities() {
        super.setLayoutUtilities();

        // for user lesson this is enabled
        binding.layoutExtraInfoUserLessonFragmentHomeLesson.setVisibility(View.VISIBLE);
    }

    @Override
    protected void setViewModel(){
        super.setViewModel();
        // set shared view model
        sharedViewModel = new ViewModelProvider(requireActivity()).get(UserNotebookSharedViewModel.class);

        viewModel.getLiveLesson().observe(this, new Observer<LessonDocument>() {
            @Override
            public void onChanged(LessonDocument lessonDocument) {
                if(lessonDocument != null){
                    switch (lessonDocument.getType()){
                        case LessonDocument.Types.RECEIVED:
                            viewModel.setLiveExtraInfo(" " + getString(R.string.received) + " (" + getString(R.string.from) + " " +
                                    lessonDocument.getFromDisplayName() + ")");
                            break;
                        case LessonDocument.Types.SHARED:
                            viewModel.setLiveExtraInfo(" " + getString(R.string.shared) + " " + getString(R.string.string_with_other_users));
                            break;
                        case LessonDocument.Types.LOCAL:
                        default:
                            viewModel.setLiveExtraInfo(getString(R.string.personal));
                            break;
                    }
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        // set listener
        // https://www.youtube.com/watch?v=LfkhFCDnkS0&ab_channel=CodinginFlow
        sharedViewModel
                .getSelectedLesson()
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

                        LessonDocument lessonDocument = value.toObject(LessonDocument.class);
                        if(lessonDocument == null){
                            Timber.i("lessonDocument is null");
                            return;
                        }
                        viewModel.setLiveLesson(value, lessonDocument);
                        viewModel.setLiveNumberOfWords(lessonDocument.getNrOfWords());
                        viewModel.setLiveNumberOfExpressions(lessonDocument.getNrOfExpressions());
                    }
                });
    }
}