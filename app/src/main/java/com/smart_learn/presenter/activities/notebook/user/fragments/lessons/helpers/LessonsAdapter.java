package com.smart_learn.presenter.activities.notebook.user.fragments.lessons.helpers;

import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.smart_learn.R;
import com.smart_learn.core.services.SettingsService;
import com.smart_learn.core.services.ThreadExecutorService;
import com.smart_learn.core.services.UserLessonService;
import com.smart_learn.core.utilities.CoreUtilities;
import com.smart_learn.core.utilities.GeneralUtilities;
import com.smart_learn.data.firebase.firestore.entities.LessonDocument;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.databinding.LayoutCardViewLessonBinding;
import com.smart_learn.presenter.activities.notebook.user.fragments.lessons.UserLessonsFragment;
import com.smart_learn.presenter.helpers.Utilities;
import com.smart_learn.presenter.helpers.adapters.BasicFirestoreRecyclerAdapter;
import com.smart_learn.presenter.helpers.adapters.BasicViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;

import timber.log.Timber;

public class LessonsAdapter extends BasicFirestoreRecyclerAdapter<LessonDocument, LessonsAdapter.LessonViewHolder> {

    private static final int INITIAL_ADAPTER_CAPACITY = 20;
    private static final int LOADING_STEP = 10;

    protected final LessonsAdapter.Callback<UserLessonsFragment> adapterCallback;

    // if filter is on
    private boolean isFiltering;
    // current filter value if filter is on
    private String filteringValue;

    public LessonsAdapter(@NonNull @NotNull LessonsAdapter.Callback<UserLessonsFragment> adapterCallback) {
        super(adapterCallback.getFragment(), getInitialAdapterOptions(adapterCallback.getFragment()),
                INITIAL_ADAPTER_CAPACITY, LOADING_STEP);
        this.adapterCallback = adapterCallback;

        // set initial values
        this.isFiltering = false;
        this.filteringValue = "";
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


    private static FirestoreRecyclerOptions<LessonDocument> getInitialAdapterOptions(@NonNull @NotNull UserLessonsFragment fragment) {
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
    public void setInitialOption(@NonNull @NotNull UserLessonsFragment fragment){
        filteringValue = "";
        isFiltering = false;
        updateOptions(getInitialAdapterOptions(fragment));
    }

    /**
     * Use to set filtering query on the adapter.
     *
     * @param fragment Fragment where adapter must be shown.
     * @param value Value to be search.
     * */
    public void setFilterOption(@NonNull @NotNull UserLessonsFragment fragment, @NonNull @NotNull String value){
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
        private final AtomicBoolean isDeleting;

        public LessonViewHolder(@NonNull @NotNull LayoutCardViewLessonBinding viewHolderBinding) {
            super(viewHolderBinding);
            liveLessonSpannedName = new MutableLiveData<>(new SpannableString(""));
            liveExtraInfo = new MutableLiveData<>("");
            isDeleting = new AtomicBoolean(false);

            // set guest menu to invisible
            viewHolderBinding.toolbarLayoutCardViewLesson.getMenu().setGroupVisible(R.id.guest_group_menu_card_view_lesson, false);

            // link binding with variables
            viewHolderBinding.setLiveLessonSpannedName(liveLessonSpannedName);
            viewHolderBinding.setLiveExtraInfo(liveExtraInfo);

            // Hide toolbar if not necessary. Fragments which will use the same adapter can choose
            // show/hide toolbar
            if(adapterCallback.hideItemToolbar()){
                viewHolderBinding.toolbarLayoutCardViewLesson.setVisibility(View.GONE);
            }

            // Disable checked icon if it is not necessary. Fragments which will use the same adapter
            // can choose show/disable checked icon.
            if(!adapterCallback.showCheckedIcon()){
                viewHolderBinding.cvLayoutCardViewLesson.setCheckedIcon(null);
            }

            setListeners();
        }

        @Override
        protected LessonDocument getEmptyLiveItemInfo() {
            return new LessonDocument();
        }

        @Override
        protected void bind(@NonNull @NotNull LessonDocument item, int position){
            if(isFiltering){
                liveLessonSpannedName.setValue(Utilities.Activities.generateSpannedString(
                        CoreUtilities.General.getSubstringIndexes(item.getName().toLowerCase(), filteringValue), item.getName()));
            }
            else {
                liveLessonSpannedName.setValue(new SpannableString(item.getName()));
            }

            liveExtraInfo.setValue(LessonDocument.generateLessonTypeTitle(item.getType()) + " - 1 Day Ago");
        }

        private void setListeners(){

            // If toolbar is not hidden the enable listeners.
            if(!adapterCallback.hideItemToolbar()){
                setToolbarListener();
            }

            // simple click action on recycler view item
            viewHolderBinding.cvLayoutCardViewLesson.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(!Utilities.Adapters.isGoodAdapterPosition(position)){
                        return;
                    }

                    adapterCallback.onItemClick(getSnapshots().getSnapshot(position));
                }
            });

        }

        private void setToolbarListener(){
            viewHolderBinding.toolbarLayoutCardViewLesson.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int position = getAdapterPosition();
                    if(!Utilities.Adapters.isGoodAdapterPosition(position)){
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
                        if(isDeleting.get()){
                            return true;
                        }
                        isDeleting.set(true);
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
                adapterCallback.getFragment().requireActivity().runOnUiThread(() -> {
                    GeneralUtilities.showShortToastMessage(adapterCallback.getFragment().requireContext(),
                            adapterCallback.getFragment().getString(R.string.error_deleting_lesson));
                });
                isDeleting.set(false);
                return;
            }

            // delete lesson if has no entries
            if(wordsNr == 0 && expressionsNr == 0){
                UserLessonService.getInstance().deleteDocument(snapshot, new DataCallbacks.General() {
                    @Override
                    public void onSuccess() {
                        adapterCallback.getFragment().requireActivity().runOnUiThread(() -> {
                            GeneralUtilities.showShortToastMessage(adapterCallback.getFragment().requireContext(),
                                    adapterCallback.getFragment().getString(R.string.success_deleting_lesson));
                        });
                        isDeleting.set(false);
                    }

                    @Override
                    public void onFailure() {
                        adapterCallback.getFragment().requireActivity().runOnUiThread(() -> {
                            GeneralUtilities.showShortToastMessage(adapterCallback.getFragment().requireContext(),
                                    adapterCallback.getFragment().getString(R.string.error_deleting_lesson));
                        });
                        isDeleting.set(false);
                    }
                });
                return;
            }

            // show an alert dialog in order to block deletion until lesson entries are not deleted
            adapterCallback.getFragment().requireActivity().runOnUiThread(() -> {
                adapterCallback.getFragment().deleteLessonAlert(wordsNr, expressionsNr);
                isDeleting.set(false);
            });
        }

    }

    public interface Callback <T> {
        boolean hideItemToolbar();
        boolean showCheckedIcon();
        void onItemClick(@NonNull @NotNull DocumentSnapshot documentSnapshot);
        void onShareLessonClick(@NonNull @NotNull DocumentSnapshot documentSnapshot);
        T getFragment();
    }

}
