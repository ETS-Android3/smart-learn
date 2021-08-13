package com.smart_learn.presenter.helpers.adapters.lessons;

import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.smart_learn.R;
import com.smart_learn.core.services.SettingsService;
import com.smart_learn.core.services.ThreadExecutorService;
import com.smart_learn.core.services.UserLessonService;
import com.smart_learn.core.services.UserService;
import com.smart_learn.core.utilities.CoreUtilities;
import com.smart_learn.data.firebase.firestore.entities.LessonDocument;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.databinding.LayoutCardViewLessonBinding;
import com.smart_learn.presenter.helpers.PresenterUtilities;
import com.smart_learn.presenter.helpers.adapters.helpers.BasicFirestoreRecyclerAdapter;
import com.smart_learn.presenter.helpers.adapters.helpers.BasicViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;

import timber.log.Timber;

public class UserLessonsAdapter extends BasicFirestoreRecyclerAdapter<LessonDocument, UserLessonsAdapter.LessonViewHolder, UserLessonsAdapter.Callback> {

    private static final int INITIAL_ADAPTER_CAPACITY = 20;
    private static final int LOADING_STEP = 10;

    public UserLessonsAdapter(@NonNull @NotNull UserLessonsAdapter.Callback adapterCallback) {
        super(adapterCallback, getInitialAdapterOptions(adapterCallback.getFragment()), INITIAL_ADAPTER_CAPACITY, LOADING_STEP);
    }

    @NonNull
    @NotNull
    @Override
    public LessonViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        // set data binding
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        LayoutCardViewLessonBinding viewHolderBinding = DataBindingUtil.inflate(layoutInflater,
                R.layout.layout_card_view_lesson, parent, false);
        viewHolderBinding.setLifecycleOwner(adapterCallback.getFragment());

        return new LessonViewHolder(viewHolderBinding);
    }

    @Override
    public void loadMoreData() {
        Query query;
        if(isFiltering){
            query = UserLessonService.getInstance().getQueryForFilter(currentLoad + loadingStep,
                    filteringValue, SettingsService.getInstance().getUserLessonShowOption());
        }
        else{
            query = UserLessonService.getInstance().getQueryForLessons(currentLoad + loadingStep,
                    SettingsService.getInstance().getUserLessonShowOption());
        }
        super.loadData(query, LessonDocument.class, adapterCallback.getFragment());
    }


    private static FirestoreRecyclerOptions<LessonDocument> getInitialAdapterOptions(@NonNull @NotNull Fragment fragment) {
        Query query = UserLessonService.getInstance().getQueryForLessons(INITIAL_ADAPTER_CAPACITY, SettingsService.getInstance().getUserLessonShowOption());
        return new FirestoreRecyclerOptions.Builder<LessonDocument>()
                .setLifecycleOwner(fragment)
                .setQuery(query, LessonDocument.class)
                .build();
    }

    /**
     * Use to set initial query on the adapter.
     *
     * @param fragment Fragment where adapter must be shown.
     * */
    public void setInitialOption(@NonNull @NotNull Fragment fragment){
        updateOptions(getInitialAdapterOptions(fragment));
        // Update values here in order to avoid to remove selected items if selection mode was active
        // while filtering.
        filteringValue = "";
        isFiltering = false;
    }

    /**
     * Use to set filtering query on the adapter.
     *
     * @param fragment Fragment where adapter must be shown.
     * @param value Value to be search.
     * */
    public void setFilterOption(@NonNull @NotNull Fragment fragment, @NonNull @NotNull String value){
        filteringValue = value.toLowerCase();
        isFiltering = true;
        Query query = UserLessonService.getInstance().getQueryForFilter(INITIAL_ADAPTER_CAPACITY, filteringValue,
                SettingsService.getInstance().getUserLessonShowOption());
        FirestoreRecyclerOptions<LessonDocument> newOptions = new FirestoreRecyclerOptions.Builder<LessonDocument>()
                .setLifecycleOwner(fragment)
                .setQuery(query, LessonDocument.class)
                .build();

        updateOptions(newOptions);
    }

    public final class LessonViewHolder extends BasicViewHolder<LessonDocument, LayoutCardViewLessonBinding> {

        private final MutableLiveData<SpannableString> liveLessonSpannedName;
        private final MutableLiveData<String> liveExtraInfo;
        private final AtomicBoolean isDeletingActive;
        private boolean isLessonOwner;

        public LessonViewHolder(@NonNull @NotNull LayoutCardViewLessonBinding viewHolderBinding) {
            super(viewHolderBinding);
            liveLessonSpannedName = new MutableLiveData<>(new SpannableString(""));
            liveExtraInfo = new MutableLiveData<>("");
            isDeletingActive = new AtomicBoolean(false);
            isLessonOwner = false;

            makeStandardSetup(viewHolderBinding.toolbarLayoutCardViewLesson, viewHolderBinding.cvLayoutCardViewLesson);

            // set guest menu to invisible
            viewHolderBinding.toolbarLayoutCardViewLesson.getMenu().setGroupVisible(R.id.guest_group_menu_card_view_lesson, false);

            // link binding with variables
            viewHolderBinding.setLiveLessonSpannedName(liveLessonSpannedName);
            viewHolderBinding.setLiveExtraInfo(liveExtraInfo);

            setListeners();
        }

        @Override
        protected LessonDocument getEmptyLiveItemInfo() {
            return new LessonDocument();
        }

        @Override
        protected void bind(@NonNull @NotNull LessonDocument item, int position){
            isLessonOwner = item.getDocumentMetadata().getOwner().equals(UserService.getInstance().getUserUid());
            if(!isLessonOwner){
                // Show delete menu option only for owner (in that case toolbar contains only delete,
                // so hide toolbar completely).
                viewHolderBinding.toolbarLayoutCardViewLesson.setVisibility(View.GONE);
            }

            if(item.getType() == LessonDocument.Types.SHARED){
                // if is shared lesson hide share button
                MenuItem menuItem = viewHolderBinding.toolbarLayoutCardViewLesson.getMenu().findItem(R.id.action_user_share_menu_card_view_lesson);
                if(menuItem != null){
                    menuItem.setVisible(false);
                }
            }

            if(isFiltering){
                liveLessonSpannedName.setValue(PresenterUtilities.Activities.generateSpannedString(
                        CoreUtilities.General.getSubstringIndexes(item.getName().toLowerCase(), filteringValue), item.getName()));
            }
            else {
                liveLessonSpannedName.setValue(new SpannableString(item.getName()));
            }

            String extraDescription = LessonDocument.generateLessonTypeTitle(item.getType()) + " - " +
                    adapterCallback.getFragment().getString(R.string.added) + " " +
                    CoreUtilities.General.getFormattedTimeDifferenceFromPastToPresent(item.getDocumentMetadata().getCreatedAt());
            liveExtraInfo.setValue(extraDescription);
        }

        private void setListeners(){

            // If toolbar is not hidden the enable listeners.
            if(adapterCallback.showToolbar()){
                setToolbarListener();
            }

            // simple click action on recycler view item
            viewHolderBinding.cvLayoutCardViewLesson.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(!PresenterUtilities.Adapters.isGoodAdapterPosition(position)){
                        return;
                    }

                    adapterCallback.onSimpleClick(getSnapshots().getSnapshot(position));
                }
            });

        }

        private void setToolbarListener(){
            viewHolderBinding.toolbarLayoutCardViewLesson.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    // only lesson owner can do these actions
                    if(!isLessonOwner){
                        return true;
                    }

                    int position = getAdapterPosition();
                    if(!PresenterUtilities.Adapters.isGoodAdapterPosition(position)){
                        return true;
                    }

                    DocumentSnapshot snapshot = getSnapshots().getSnapshot(position);
                    LessonDocument lesson = snapshot.toObject(LessonDocument.class);
                    if(lesson == null){
                        Timber.w("lesson is null");
                        return true;
                    }

                    int id = item.getItemId();
                    if(id == R.id.action_user_delete_menu_card_view_lesson){
                        // avoid multiple press until operation is finished
                        if(isDeletingActive.get()){
                            return true;
                        }
                        isDeletingActive.set(true);
                        ThreadExecutorService.getInstance().execute(() -> onDeletePressed(lesson, snapshot));
                        return true;
                    }

                    if(id == R.id.action_user_share_menu_card_view_lesson){
                        adapterCallback.onShareLessonClick(snapshot);
                        return true;
                    }

                    return true;
                }
            });
        }

        private void onDeletePressed(LessonDocument lesson, DocumentSnapshot snapshot){
            int wordsNr = lesson.getNrOfWords();
            int expressionsNr = lesson.getNrOfExpressions();

            if(wordsNr < 0 || expressionsNr < 0){
                showMessage(R.string.error_deleting_lesson);
                isDeletingActive.set(false);
                return;
            }

            // delete lesson if has no entries
            if(wordsNr == 0 && expressionsNr == 0){
                UserLessonService.getInstance().deleteLesson(snapshot, new DataCallbacks.General() {
                    @Override
                    public void onSuccess() {
                        showMessage(R.string.success_deleting_lesson);
                        isDeletingActive.set(false);
                    }

                    @Override
                    public void onFailure() {
                        showMessage(R.string.error_deleting_lesson);
                        isDeletingActive.set(false);
                    }
                });
                return;
            }

            // show an alert dialog in order to block deletion until lesson entries are not deleted
            adapterCallback.getFragment().requireActivity().runOnUiThread(() -> adapterCallback.onDeleteLessonAlert(wordsNr, expressionsNr));
            isDeletingActive.set(false);
        }

    }

    public interface Callback extends BasicFirestoreRecyclerAdapter.Callback  {
        void onDeleteLessonAlert(int wordsNr, int expressionsNr);
        void onShareLessonClick(@NonNull @NotNull DocumentSnapshot lessonSnapshot);
    }

}
