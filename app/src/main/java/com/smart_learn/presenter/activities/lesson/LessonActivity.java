package com.smart_learn.presenter.activities.lesson;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.smart_learn.R;
import com.smart_learn.data.models.room.entities.Lesson;
import com.smart_learn.databinding.ActivityLessonBinding;
import com.smart_learn.presenter.activities.TestLocalActivity;
import com.smart_learn.presenter.activities.dialogs.SettingsDialog;
import com.smart_learn.presenter.view_models.BasicLessonEntranceViewModel;
import com.smart_learn.presenter.view_models.LessonViewModel;

public class LessonActivity extends AppCompatActivity {

    private long currentLessonId;
    private LessonViewModel lessonViewModel;
    private BasicLessonEntranceViewModel basicLessonEntranceViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get transmitted lesson id from the previous activity
        Intent intent = getIntent();
        currentLessonId = intent.getLongExtra(LessonRVActivity.LESSON_ID,0);

        // set data binding
        ActivityLessonBinding activityBinding = DataBindingUtil.setContentView(this,
                R.layout.activity_lesson);
        activityBinding.setLifecycleOwner(this);

        // TODO: check this in order to avoid using findViewById
        // set up toolbar
        setSupportActionBar(findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setTitle("Lesson");

            // show back button on toolbar
            // on back button press, it will navigate to parent activity
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        setListeners(activityBinding);

        setViewModel();

        // link data binding with view model
        activityBinding.setViewModel(lessonViewModel);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.btnSettings) {
            showSettingsDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    private void setListeners(ActivityLessonBinding activityBinding){
        activityBinding.btnAddNewWord.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showAddEntranceDialog();
            }
        });

        activityBinding.btnTestLocal.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(LessonActivity.this, TestLocalActivity.class);
                startActivity(intent);
            }
        });

        activityBinding.btnTestOnline.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO: remove this when refactoring is made
                //TestService.getTestServiceInstance().currentTestMode.set(TestService.REMOTE_MODE_TEST.toInt())
                //lessonActivityK.startTestGenerationActivity()
            }
        });

        activityBinding.btnDisplayWords.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startLessonEntriesRVActivity();
            }
        });

        activityBinding.btnDeleteLesson.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // delete current lesson
                lessonViewModel.deleteCurrentLesson();

                // go to previous activity which already exist on stack
                onBackPressed();
            }
        });

        activityBinding.btnUpdateLesson.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showUpdateLessonDialog(lessonViewModel.getLiveCurrentLesson().getValue());
            }
        });
    }

    private void setViewModel(){
        // view model to open lesson entrance dialog
        basicLessonEntranceViewModel = new ViewModelProvider(this).get(BasicLessonEntranceViewModel.class);

        lessonViewModel = new ViewModelProvider(this).get(LessonViewModel.class);

        // set current lesson
        lessonViewModel.setLiveCurrentLesson(currentLessonId);

        // set observers
        lessonViewModel.getLiveToastMessage().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Toast.makeText(LessonActivity.this, s, Toast.LENGTH_LONG).show();
            }
        });

        basicLessonEntranceViewModel.getLiveToastMessage().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Toast.makeText(LessonActivity.this, s, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showSettingsDialog(){
        DialogFragment dialogFragment = new SettingsDialog(true, R.string.settings, R.layout.dialog_settings);
        dialogFragment.show(getSupportFragmentManager(), "LessonActivity");
    }

    private void showUpdateLessonDialog(Lesson lesson){
        DialogFragment dialogFragment = lessonViewModel.prepareLessonDialog(true,lesson);
        dialogFragment.show(getSupportFragmentManager(), "LessonActivity");
    }

    private void showAddEntranceDialog(){
        DialogFragment dialogFragment = basicLessonEntranceViewModel.prepareEntranceDialog(false, null, currentLessonId);
        dialogFragment.show(getSupportFragmentManager(), "LessonActivity");
    }

    /** Show entries from the selected lesson */
    private void startLessonEntriesRVActivity(){
        Intent intent = new Intent(this, LessonEntriesRVActivity.class);
        intent.putExtra(LessonRVActivity.LESSON_ID,currentLessonId);
        startActivity(intent);
    }
}