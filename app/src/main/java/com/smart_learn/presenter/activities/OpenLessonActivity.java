package com.smart_learn.presenter.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.smart_learn.R;
import com.smart_learn.presenter.activities.dialogs.DialogButtonsActionCallback;
import com.smart_learn.presenter.activities.dialogs.DialogDismissCallback;
import com.smart_learn.presenter.activities.dialogs.LessonDialog;
import com.smart_learn.presenter.activities.dialogs.SettingsDialog;
import com.smart_learn.presenter.view_models.OpenLessonViewModel;

public class OpenLessonActivity extends AppCompatActivity {

    private OpenLessonViewModel openLessonViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_lesson);

        setSupportActionBar(findViewById(R.id.toolbarLessons));

        // set ViewModel
        openLessonViewModel = new ViewModelProvider(this).get(OpenLessonViewModel.class);

        // set observers
        openLessonViewModel.getLiveToastMessage().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
               Toast.makeText(OpenLessonActivity.this, s, Toast.LENGTH_LONG).show();
            }
        });



        // get GUI components
        Button btnNewLesson = findViewById(R.id.btnNewLesson);
        Button btnOpenLesson = findViewById(R.id.btnOpenLesson);

        // set listeners
        btnNewLesson.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showAddLessonDialog();
            }
        });

        btnOpenLesson.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //startLessonRVActivity();
            }
        });

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

    public void startLessonRVActivity(){
        //Intent intent = new Intent(this, LessonRVActivity.class);
        //startActivity(intent);
    }

    private void showSettingsDialog(){
        DialogFragment dialogFragment = new SettingsDialog(true, R.string.settings, R.layout.dialog_settings);
        dialogFragment.show(getSupportFragmentManager(), "OpenLessonActivity");
    }

    private void showAddLessonDialog(){

        DialogFragment dialogFragment = new LessonDialog(true, R.string.add_lesson, R.layout.dialog_new_lesson,
                R.string.save, R.string.cancel, new DialogButtonsActionCallback() {

            @Override
            public void onPositiveButtonPressed(View view, DialogInterface dialog, int which) {
                EditText etLessonName = ((AlertDialog) dialog).findViewById(R.id.etLessonName);
                openLessonViewModel.processLessonDialog(etLessonName, new DialogDismissCallback() {
                    @Override
                    public void onDismiss() {
                        dialog.dismiss();
                    }
                });
            }

        });

        dialogFragment.show(getSupportFragmentManager(), "OpenLessonActivity");

    }

}