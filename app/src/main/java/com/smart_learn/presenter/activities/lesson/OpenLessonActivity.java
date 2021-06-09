package com.smart_learn.presenter.activities.lesson;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.smart_learn.R;
import com.smart_learn.databinding.ActivityOpenLessonBinding;
import com.smart_learn.presenter.activities.dialogs.SettingsDialog;

public class OpenLessonActivity extends AppCompatActivity {

    private OpenLessonViewModel openLessonViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set data binding
        ActivityOpenLessonBinding activityBinding = DataBindingUtil.setContentView(this,
                R.layout.activity_open_lesson);
        activityBinding.setLifecycleOwner(this);

        setSupportActionBar(activityBinding.toolbarLessons);

        setListeners(activityBinding);

        setViewModel();

        // link data binding with view model
        activityBinding.setViewModel(openLessonViewModel);
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

    private void setListeners(ActivityOpenLessonBinding activityBinding){
        activityBinding.btnNewLesson.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showAddLessonDialog();
            }
        });

        activityBinding.btnOpenLesson.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               startLessonRVActivity();
            }
        });
    }

    private void setViewModel(){
        openLessonViewModel = new ViewModelProvider(this).get(OpenLessonViewModel.class);

        // set observers
        openLessonViewModel.getLiveToastMessage().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Toast.makeText(OpenLessonActivity.this, s, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void startLessonRVActivity(){
        Intent intent = new Intent(this, LessonRVActivity.class);
        startActivity(intent);
    }

    private void showSettingsDialog(){
        DialogFragment dialogFragment = new SettingsDialog(true, R.string.settings, R.layout.dialog_settings);
        dialogFragment.show(getSupportFragmentManager(), "OpenLessonActivity");
    }

    private void showAddLessonDialog(){
        DialogFragment dialogFragment = openLessonViewModel.prepareLessonDialog(false, null);
        dialogFragment.show(getSupportFragmentManager(), "OpenLessonActivity");
    }
}