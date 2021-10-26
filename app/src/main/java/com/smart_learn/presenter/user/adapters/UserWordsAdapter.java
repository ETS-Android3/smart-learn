package com.smart_learn.presenter.user.adapters;

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
import com.smart_learn.core.user.services.UserService;
import com.smart_learn.core.user.services.UserWordService;
import com.smart_learn.core.common.helpers.CoreUtilities;
import com.smart_learn.data.user.firebase.firestore.entities.WordDocument;
import com.smart_learn.data.common.helpers.DataCallbacks;
import com.smart_learn.databinding.LayoutCardViewWordBinding;
import com.smart_learn.presenter.common.helpers.PresenterUtilities;
import com.smart_learn.presenter.user.adapters.helpers.BasicFirestoreRecyclerAdapter;
import com.smart_learn.presenter.common.adapters.helpers.BasicViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;

public class UserWordsAdapter extends BasicFirestoreRecyclerAdapter<WordDocument, UserWordsAdapter.WordViewHolder, UserWordsAdapter.Callback> {

    private static final int INITIAL_ADAPTER_CAPACITY = 20;
    private static final int LOADING_STEP = 10;

    private final DocumentSnapshot currentLessonSnapshot;

    public UserWordsAdapter(@NonNull @NotNull DocumentSnapshot currentLessonSnapshot,
                            @NonNull @NotNull UserWordsAdapter.Callback adapterCallback) {
        super(adapterCallback, getInitialAdapterOptions(currentLessonSnapshot, adapterCallback.isSharedLessonSelected() ,adapterCallback.getFragment()),
                INITIAL_ADAPTER_CAPACITY, LOADING_STEP);
        this.currentLessonSnapshot = currentLessonSnapshot;
    }

    @NonNull
    @NotNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        // set data binding
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        LayoutCardViewWordBinding viewHolderBinding = DataBindingUtil.inflate(layoutInflater,
                R.layout.layout_card_view_word, parent, false);
        viewHolderBinding.setLifecycleOwner(adapterCallback.getFragment());

        // set binding variable
        viewHolderBinding.setLiveIsActionModeActive(getLiveIsSelectionModeActive());

        return new WordViewHolder(viewHolderBinding);
    }

    @Override
    public void loadMoreData() {
        Query query;
        if(isFiltering){
            query = UserWordService.getInstance().getQueryForFilter(currentLessonSnapshot.getId(),
                    currentLoad + loadingStep, adapterCallback.isSharedLessonSelected(), filteringValue);
        }
        else{
            query = UserWordService.getInstance().getQueryForAllLessonWords(currentLessonSnapshot.getId(),
                    currentLoad + loadingStep, adapterCallback.isSharedLessonSelected());
        }
        super.loadData(query, WordDocument.class, adapterCallback.getFragment());
    }

    private static FirestoreRecyclerOptions<WordDocument> getInitialAdapterOptions(@NonNull @NotNull DocumentSnapshot currentLessonSnapshot,
                                                                                   boolean isSharedLessonSelected, @NonNull @NotNull Fragment fragment) {
        Query query = UserWordService.getInstance().getQueryForAllLessonWords(currentLessonSnapshot.getId(), INITIAL_ADAPTER_CAPACITY, isSharedLessonSelected);
        return new FirestoreRecyclerOptions.Builder<WordDocument>()
                .setLifecycleOwner(fragment)
                .setQuery(query, WordDocument.class)
                .build();
    }

    /**
     * Use to set initial query on the adapter.
     *
     * @param fragment Fragment where adapter must be shown.
     * */
    public void setInitialOption(@NonNull @NotNull Fragment fragment){
        updateOptions(getInitialAdapterOptions(currentLessonSnapshot, adapterCallback.isSharedLessonSelected(), fragment));
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
        filteringValue = CoreUtilities.General.trimAndRemoveAdjacentSpacesAndBreakLines(value).toLowerCase();
        if(filteringValue.isEmpty()){
            filteringValue = CoreUtilities.General.DEFAULT_VALUE_FOR_SEARCH;
        }
        isFiltering = true;
        Query query = UserWordService.getInstance().getQueryForFilter(currentLessonSnapshot.getId(),
                INITIAL_ADAPTER_CAPACITY, adapterCallback.isSharedLessonSelected(), filteringValue);
        FirestoreRecyclerOptions<WordDocument> newOptions = new FirestoreRecyclerOptions.Builder<WordDocument>()
                .setLifecycleOwner(fragment)
                .setQuery(query, WordDocument.class)
                .build();

        updateOptions(newOptions);
    }

    public final class WordViewHolder extends BasicViewHolder<WordDocument, LayoutCardViewWordBinding> {

        private final MutableLiveData<SpannableString> liveSpannedWord;
        private final MutableLiveData<String> liveDateDifferenceDescription;
        private final MutableLiveData<Boolean> liveIsSelected;
        private final MutableLiveData<Boolean> liveIsOwner;
        private final AtomicBoolean isDeletingActive;


        public WordViewHolder(@NonNull @NotNull LayoutCardViewWordBinding viewHolderBinding) {
            super(viewHolderBinding);
            liveSpannedWord = new MutableLiveData<>(new SpannableString(""));
            liveDateDifferenceDescription = new MutableLiveData<>("");
            liveIsSelected = new MutableLiveData<>(false);
            liveIsOwner = new MutableLiveData<>(false);
            isDeletingActive = new AtomicBoolean(false);

            makeStandardSetup(viewHolderBinding.toolbarLayoutCardViewWord, viewHolderBinding.cvLayoutCardViewWord);

            // link binding with variables
            viewHolderBinding.setLiveSpannedWord(liveSpannedWord);
            viewHolderBinding.setLiveDateDifferenceDescription(liveDateDifferenceDescription);
            viewHolderBinding.setLiveIsSelected(liveIsSelected);
            viewHolderBinding.setLiveIsOwner(liveIsOwner);

            setListeners();
        }


        @Override
        public WordDocument getEmptyLiveItemInfo() {
            return new WordDocument();
        }

        @Override
        public void bind(@NonNull @NotNull WordDocument item, int position){
            liveIsOwner.setValue(item.getDocumentMetadata().getOwner().equals(UserService.getInstance().getUserUid()));

            String dateDifferenceDescription = adapterCallback.getFragment().getString(R.string.word_added) + " " +
                    CoreUtilities.General.getFormattedTimeDifferenceFromPastToPresent(item.getDocumentMetadata().getCreatedAt());
            liveDateDifferenceDescription.setValue(dateDifferenceDescription);

            if(isFiltering){
                String word = CoreUtilities.General.trimAndRemoveAdjacentSpacesAndBreakLines(item.getWord());
                liveSpannedWord.setValue(PresenterUtilities.Activities.generateSpannedString(
                        CoreUtilities.General.getSubstringIndexes(word.toLowerCase(), filteringValue), word));
            }
            else {
                liveSpannedWord.setValue(new SpannableString(item.getWord()));
            }

            if (isSelectionModeActive()) {
                boolean isSelected = isSelected(getSnapshots().getSnapshot(position));
                liveIsSelected.setValue(isSelected);
                viewHolderBinding.cvLayoutCardViewWord.setChecked(isSelected);
                return;
            }

            // selection mode is not active so items must be unchecked
            liveIsSelected.setValue(false);
            viewHolderBinding.cvLayoutCardViewWord.setChecked(false);
        }

        private void setListeners(){

            if(adapterCallback.showToolbar()){
                setToolbarListener();
            }

            viewHolderBinding.cvLayoutCardViewWord.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(!PresenterUtilities.Adapters.isGoodAdapterPosition(position)){
                        return;
                    }

                    if(isSelectionModeActive()){
                        if(adapterCallback.isSelectedItemValid(getSnapshots().getSnapshot(position))){
                            markItem(position, getSnapshots().getSnapshot(position));
                        }
                        return;
                    }

                    adapterCallback.onSimpleClick(getSnapshots().getSnapshot(position));
                }
            });

            viewHolderBinding.cvLayoutCardViewWord.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // selection mode is available only for non-shared lessons
                    if(adapterCallback.isSharedLessonSelected()){
                        return true;
                    }

                    // if filtering is active nothing will happen
                    if(isFiltering){
                        return true;
                    }

                    if(!isSelectionModeActive()) {
                        int position = getAdapterPosition();
                        if(!PresenterUtilities.Adapters.isGoodAdapterPosition(position)){
                            return true;
                        }

                        adapterCallback.onLongClick(getSnapshots().getSnapshot(position));
                        // by default clicked item is selected
                        markItem(position, getSnapshots().getSnapshot(position));
                    }

                    return true;
                }
            });

        }

        private void setToolbarListener(){
            viewHolderBinding.toolbarLayoutCardViewWord.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int position = getAdapterPosition();
                    if(!PresenterUtilities.Adapters.isGoodAdapterPosition(position)){
                        return true;
                    }

                    int id = item.getItemId();
                    if(id == R.id.action_delete_menu_card_view_word){
                        // only owner can delete his word
                        if(liveIsOwner.getValue() == null || !liveIsOwner.getValue()){
                            return true;
                        }

                        // avoid multiple press until operation is finished
                        if(isDeletingActive.get()){
                            return true;
                        }
                        isDeletingActive.set(true);
                        onDeletePressed(getSnapshots().getSnapshot(position));
                        return true;
                    }
                    return true;
                }
            });
        }

        private void onDeletePressed(DocumentSnapshot wordSnapshot){
            UserWordService.getInstance().deleteWord(currentLessonSnapshot, wordSnapshot, new DataCallbacks.General() {
                @Override
                public void onSuccess() {
                    showMessage(R.string.success_deleting_word);
                    isDeletingActive.set(false);
                }

                @Override
                public void onFailure() {
                    showMessage(R.string.error_deleting_word);
                    isDeletingActive.set(false);
                }
            });
        }
    }

    public interface Callback extends BasicFirestoreRecyclerAdapter.Callback {
        boolean isSharedLessonSelected();
        boolean isSelectedItemValid(@NonNull @NotNull DocumentSnapshot item);
    }

}