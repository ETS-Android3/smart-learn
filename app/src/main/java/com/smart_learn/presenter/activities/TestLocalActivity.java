package com.smart_learn.presenter.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.smart_learn.R;
import com.smart_learn.databinding.ActivityTestBinding;
import com.smart_learn.presenter.helpers.dialogs.SettingsDialog;
import com.smart_learn.presenter.view_models.TestLocalViewModel;

public class TestLocalActivity extends AppCompatActivity {

    private TestLocalViewModel localTestViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set data binding
        ActivityTestBinding activityBinding = DataBindingUtil.setContentView(this,
                R.layout.activity_test);
        activityBinding.setLifecycleOwner(this);

        // TODO: check this in order to avoid using findViewById
        // set up toolbar
        setSupportActionBar(findViewById(R.id.toolbarLessons));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setTitle("Test Local");

            // show back button on toolbar
            // on back button press, it will navigate to parent activity
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        setListeners(activityBinding);

        setViewModel();

        // link data binding with view model
        activityBinding.setViewModel(localTestViewModel);
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


    private void setListeners(ActivityTestBinding activityBinding){
        /*
        activityBinding.btnAddNewWord.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showAddEntranceDialog();
            }
        });

         */
    }

    private void setViewModel(){
        localTestViewModel = new ViewModelProvider(this).get(TestLocalViewModel.class);

        // set observers
        localTestViewModel.getLiveToastMessage().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Toast.makeText(TestLocalActivity.this, s, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showSettingsDialog(){
        DialogFragment dialogFragment = new SettingsDialog(true, R.string.settings, R.layout.dialog_settings);
        dialogFragment.show(getSupportFragmentManager(), "TestActivity");
    }
}