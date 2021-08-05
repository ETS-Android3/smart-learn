package com.smart_learn.presenter.activities.notebook.user.fragments.home_lesson;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.smart_learn.presenter.activities.notebook.user.UserNotebookActivity;
import com.smart_learn.presenter.activities.notebook.user.UserNotebookSharedViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import lombok.Getter;
import timber.log.Timber;


public class UserHomeLessonFragment extends HomeLessonFragment<UserHomeLessonViewModel> {

    public static final String IS_SHARED_LESSON_SELECTED = "IS_SHARED_LESSON_SELECTED";

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

        // fo shared lesson show a specific toolbar menu
        boolean isSharedLessonSelected = getArguments() != null && getArguments().getBoolean(IS_SHARED_LESSON_SELECTED);
        if(isSharedLessonSelected){
            // use this to set toolbar menu inside fragment
            // https://stackoverflow.com/questions/15653737/oncreateoptionsmenu-inside-fragments/31360073#31360073
            setHasOptionsMenu(true);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_toolbar_fragment_user_home_lesson, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        int id = item.getItemId();
        if(id == R.id.action_view_participants_menu_toolbar_activity_community){
            viewParticipants();
            return true;
        }
        return false;
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
                            viewModel.setLiveExtraInfo(getString(R.string.received) + " (" + getString(R.string.from) + " " +
                                    lessonDocument.getFromDisplayName() + ")");
                            break;
                        case LessonDocument.Types.SHARED:
                            viewModel.setLiveExtraInfo(getString(R.string.shared) + " " + getString(R.string.string_with_other_users) + " (" +
                            getString(R.string.created_by) + " " +  lessonDocument.getFromDisplayName() + ")");
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

    private void viewParticipants(){
        ArrayList<String> participants = viewModel.getCurrentLessonParticipants();
        if(participants.isEmpty()){
            showMessage(R.string.no_participants);
            return;
        }
        sharedViewModel.setSelectedSharedLessonParticipants(participants);
        ((UserNotebookActivity)requireActivity()).goToSharedLessonParticipantsFragment();
    }
}