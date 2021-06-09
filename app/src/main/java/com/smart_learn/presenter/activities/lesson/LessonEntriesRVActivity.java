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
import com.smart_learn.data.models.room.entities.Word;
import com.smart_learn.databinding.ActivityRvEntrancesBinding;
import com.smart_learn.presenter.activities.dialogs.SettingsDialog;
import com.smart_learn.presenter.activities.lesson.recycler_view.ActionModeRVCallback;
import com.smart_learn.presenter.activities.lesson.recycler_view.adapters.ItemDecoration;
import com.smart_learn.presenter.activities.lesson.recycler_view.adapters.LessonEntriesRVAdapter;
import com.smart_learn.presenter.view_models.ActivityRVUtilitiesCallback;
import com.smart_learn.presenter.view_models.LessonEntriesRVViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class LessonEntriesRVActivity extends AppCompatActivity {

    private long currentLessonId;
    private LessonEntriesRVViewModel lessonEntriesRVViewModel;
    private LessonEntriesRVAdapter lessonEntriesRVAdapter;
    private RecyclerView recyclerView;

    private ActionModeRVCallback<Word> actionModeRVCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get transmitted lesson id from the previous activity
        Intent intent = getIntent();
        currentLessonId = intent.getLongExtra(LessonRVActivity.LESSON_ID,0);

        // set data binding
        ActivityRvEntrancesBinding activityBinding = DataBindingUtil.setContentView(this,
                R.layout.activity_rv_entrances);
        activityBinding.setLifecycleOwner(this);

        // TODO: check this in order to avoid using findViewById
        // set up toolbar
        setSupportActionBar(findViewById(R.id.toolbarLessons));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setTitle("Lesson entries");

            // show back button on toolbar
            // on back button press, it will navigate to parent activity
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        setListeners(activityBinding);

        initRecyclerView();

        setViewModel();

        // link data binding with view model
        activityBinding.setViewModel(lessonEntriesRVViewModel);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rv_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.actionSearch);

        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                lessonEntriesRVAdapter.getFilter().filter(newText);
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
        dialogFragment.show(getSupportFragmentManager(), "LessonEntriesActivity");
    }

    private void setListeners(ActivityRvEntrancesBinding activityBinding){
        activityBinding.btnAddEntrance.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showAddEntryDialog();
            }
        });
    }

    private void initRecyclerView(){
        recyclerView = findViewById(R.id.rvEntrance);
        LinearLayoutManager manager = new LinearLayoutManager(CurrentConfig.getCurrentConfigInstance().currentContext,
                RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.addItemDecoration(new ItemDecoration(10));

        lessonEntriesRVAdapter = new LessonEntriesRVAdapter(new ActivityRVUtilitiesCallback<Word>() {
            @NotNull
            @Override
            public AppCompatActivity getActivity() {
                return LessonEntriesRVActivity.this;
            }

            @NonNull
            @Override
            public AndroidViewModel getViewModel() {
                return lessonEntriesRVViewModel;
            }

            @NotNull
            @Override
            public List<Word> getAllItemsFromDatabase() {
                return lessonEntriesRVViewModel.getCurrentLessonSampleWords(currentLessonId);
            }

            @Override
            public void startActionMode() {

                // set this to true in order to load another layout
                lessonEntriesRVAdapter.setActionMode(true);

                actionModeRVCallback = new ActionModeRVCallback<>(new ActivityRVUtilitiesCallback<Word>() {
                    @Override
                    public void setToastMessage(@NonNull String message) {
                        Toast.makeText(LessonEntriesRVActivity.this, message, Toast.LENGTH_LONG).show();
                    }

                    @NotNull
                    @Override
                    public LiveData<Integer> getLiveSelectedItemsCount() {
                        return lessonEntriesRVViewModel.getLiveSelectedItemsCount(currentLessonId);
                    }

                    @NotNull
                    @Override
                    public LiveData<Integer> getLiveItemsNumber() {
                        return lessonEntriesRVViewModel.getLiveItemsNumber(currentLessonId);
                    }

                    @Override
                    public void selectAll() {
                        lessonEntriesRVViewModel.updateSelectAll(true,currentLessonId);
                    }

                    @Override
                    public void deselectAll() {
                        lessonEntriesRVViewModel.updateSelectAll(false,currentLessonId);
                    }

                    @Override
                    public void deleteSelectedItems() {
                        lessonEntriesRVViewModel.deleteSelectedItems(currentLessonId);
                    }

                    @Override
                    public void destroyActionMode() {
                        // Mark that action mode is stopped. This is used in viewBinder in recycler
                        // view adapter to show a specific background.
                        actionModeRVCallback = null;

                        // set this to false in order to load another layout
                        lessonEntriesRVAdapter.setActionMode(false);
                    }

                    @Override
                    public void notifyToChange() {
                        lessonEntriesRVAdapter.notifyDataSetChanged();
                    }
                },
                        LessonEntriesRVActivity.this, recyclerView, R.menu.action_mode_menu, "Selected", "");
            }

            @Nullable
            @Override
            public ActionMode.Callback getActionModeCallback() {
                return actionModeRVCallback;
            }

            @Override
            public void update(Word item) {
                lessonEntriesRVViewModel.update(item);
            }

            @Override
            public void delete(Word item) {
                lessonEntriesRVViewModel.delete(item);
            }

            @Override
            public void onSwipeForUpdate(Word item) {
                showUpdateEntryDialog(item);
            }

            @Override
            public void onSwipeForDelete(Word item) {
                lessonEntriesRVViewModel.delete(item);
            }
        });

        recyclerView.setAdapter(lessonEntriesRVAdapter);
    }

    private void setViewModel(){
        // set ViewModel
        lessonEntriesRVViewModel = new ViewModelProvider(this).get(LessonEntriesRVViewModel.class);

        // set observers
        lessonEntriesRVViewModel.getLiveToastMessage().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Toast.makeText(LessonEntriesRVActivity.this, s, Toast.LENGTH_LONG).show();
            }
        });

        lessonEntriesRVViewModel.getCurrentLessonLiveWords(currentLessonId).observe(this, new Observer<List<Word>>() {
            @Override
            public void onChanged(List<Word> words) {
                lessonEntriesRVAdapter.setItems(words);
                lessonEntriesRVViewModel.checkEmptyMode(new ArrayList<>(words));
            }
        });
    }

    private void showAddEntryDialog(){
        DialogFragment dialogFragment = lessonEntriesRVViewModel.prepareEntranceDialog(false,
                null, currentLessonId);
        dialogFragment.show(getSupportFragmentManager(), "LessonEntriesRVActivity");
    }

    private void showUpdateEntryDialog(Word word){
        DialogFragment dialogFragment = lessonEntriesRVViewModel.prepareEntranceDialog(true, word, currentLessonId);
        dialogFragment.show(getSupportFragmentManager(), "LessonEntriesRVActivity");
    }
}