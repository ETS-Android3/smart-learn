package com.smart_learn.presenter.activities.lesson;

import android.content.Intent;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smart_learn.R;
import com.smart_learn.core.config.CurrentConfig;
import com.smart_learn.data.models.room.entities.Lesson;
import com.smart_learn.databinding.ActivityRvLessonsBinding;
import com.smart_learn.presenter.activities.dialogs.SettingsDialog;
import com.smart_learn.presenter.activities.lesson.recycler_view.ActionModeRVCallback;
import com.smart_learn.presenter.activities.lesson.recycler_view.adapters.ItemDecoration;
import com.smart_learn.presenter.activities.lesson.recycler_view.adapters.LessonRVAdapter;
import com.smart_learn.presenter.view_models.ActivityRVUtilitiesCallback;
import com.smart_learn.presenter.view_models.LessonRVViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LessonRVActivity extends AppCompatActivity {

    public static final String LESSON_ID = "LESSON_ID";

    private LessonRVViewModel lessonRVViewModel;
    private LessonRVAdapter lessonRVAdapter;
    private RecyclerView recyclerView;

    private ActionModeRVCallback<Lesson> actionModeRVCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set data binding
        ActivityRvLessonsBinding activityBinding = DataBindingUtil.setContentView(this,
                R.layout.activity_rv_lessons);
        activityBinding.setLifecycleOwner(this);

        // TODO: check this in order to avoid using findViewById
        // set up toolbar
        setSupportActionBar(findViewById(R.id.toolbarLessons));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setTitle("Lessons");

            // show back button on toolbar
            // on back button press, it will navigate to parent activity
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        setListeners(activityBinding);

        initRecyclerView();

        setViewModel();

        // link data binding with view model
        activityBinding.setViewModel(lessonRVViewModel);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rv_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.actionSearch);

        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                lessonRVAdapter.getFilter().filter(newText);
                return true;
            }
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.btnSettingsRV) {
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


    private void showSettingsDialog(){
        DialogFragment dialogFragment = new SettingsDialog(true, R.string.settings, R.layout.dialog_settings);
        dialogFragment.show(getSupportFragmentManager(), "LessonRVActivity");
    }

    public void startLessonActivity(long lessonId){
        Intent intent = new Intent(this, LessonActivity.class);
        intent.putExtra(LESSON_ID,lessonId);
        startActivity(intent);
    }

    private void setListeners(ActivityRvLessonsBinding activityBinding){
        activityBinding.btnAddLessonRV.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showAddLessonDialog();
            }
        });
    }

    private void initRecyclerView(){
        recyclerView = findViewById(R.id.rvLessons);
        LinearLayoutManager manager = new LinearLayoutManager(CurrentConfig.getCurrentConfigInstance().currentContext,
                RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.addItemDecoration(new ItemDecoration(10));

        lessonRVAdapter = new LessonRVAdapter(new ActivityRVUtilitiesCallback<Lesson>() {
            @NotNull
            @Override
            public AppCompatActivity getActivity() {
                return LessonRVActivity.this;
            }

            @NonNull
            @Override
            public AndroidViewModel getViewModel() {
                return lessonRVViewModel;
            }

            @NotNull
            @Override
            public List<Lesson> getAllItemsFromDatabase() {
                return lessonRVViewModel.getAllSampleLesson();
            }

            @Override
            public void startActionMode() {

                // set this to true in order to load another layout
                lessonRVAdapter.setActionMode(true);

                actionModeRVCallback = new ActionModeRVCallback<>(new ActivityRVUtilitiesCallback<Lesson>() {
                    @Override
                    public void setToastMessage(@NonNull String message) {
                        Toast.makeText(LessonRVActivity.this, message, Toast.LENGTH_LONG).show();
                    }

                    @NotNull
                    @Override
                    public LiveData<Integer> getLiveSelectedItemsCount() {
                        return lessonRVViewModel.getLiveSelectedItemsCount();
                    }

                    @NotNull
                    @Override
                    public LiveData<Integer> getLiveItemsNumber() {
                        return lessonRVViewModel.getLiveItemsNumber();
                    }

                    @Override
                    public void selectAll() {
                        lessonRVViewModel.updateSelectAll(true);
                    }

                    @Override
                    public void deselectAll() {
                        lessonRVViewModel.updateSelectAll(false);
                    }

                    @Override
                    public void deleteSelectedItems() {
                        lessonRVViewModel.deleteSelectedItems();
                    }

                    @Override
                    public void destroyActionMode() {
                        // Mark that action mode is stopped. This is used in viewBinder in recycler
                        // view adapter to show a specific background.
                        actionModeRVCallback = null;

                        // set this to false in order to load another layout
                        lessonRVAdapter.setActionMode(false);
                    }

                    @Override
                    public void notifyToChange() {
                        lessonRVAdapter.notifyDataSetChanged();
                    }
                },
                        LessonRVActivity.this, recyclerView, R.menu.action_mode_menu, "Selected", "");
            }

            @Nullable
            @Override
            public ActionMode.Callback getActionModeCallback() {
                return actionModeRVCallback;
            }

            @Override
            public void update(Lesson item) {
                lessonRVViewModel.update(item);
            }

            @Override
            public void delete(Lesson item) {
                lessonRVViewModel.delete(item);
            }

            @Override
            public void onSwipeForUpdate(Lesson item) {
                showUpdateLessonDialog(item);
            }

            @Override
            public void onSwipeForDelete(Lesson item) {
                lessonRVViewModel.delete(item);
            }
        });

        recyclerView.setAdapter(lessonRVAdapter);
    }

    private void setViewModel(){
        // set ViewModel
        lessonRVViewModel = new ViewModelProvider(this).get(LessonRVViewModel.class);

        // set observers
        lessonRVViewModel.getLiveToastMessage().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Toast.makeText(LessonRVActivity.this, s, Toast.LENGTH_LONG).show();
            }
        });

        lessonRVViewModel.getAllLiveSampleLessons().observe(this, new Observer<List<Lesson>>() {
            @Override
            public void onChanged(List<Lesson> lessons) {
                lessonRVAdapter.setItems(lessons);
                lessonRVViewModel.checkEmptyMode(lessons);
            }
        });
    }

    private void showAddLessonDialog(){
        DialogFragment dialogFragment = lessonRVViewModel.prepareLessonDialog(false, null);
        dialogFragment.show(getSupportFragmentManager(), "LessonRVActivity");
    }

    private void showUpdateLessonDialog(Lesson lesson){
        DialogFragment dialogFragment = lessonRVViewModel.prepareLessonDialog(true,lesson);
        dialogFragment.show(getSupportFragmentManager(), "LessonRVActivity");
    }
}