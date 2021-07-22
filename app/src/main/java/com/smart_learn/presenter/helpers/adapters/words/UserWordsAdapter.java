package com.smart_learn.presenter.helpers.adapters.words;

import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.util.Pair;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.smart_learn.R;
import com.smart_learn.core.services.UserWordService;
import com.smart_learn.core.utilities.CoreUtilities;
import com.smart_learn.data.firebase.firestore.entities.WordDocument;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.databinding.LayoutCardViewWordBinding;
import com.smart_learn.presenter.helpers.Utilities;
import com.smart_learn.presenter.helpers.adapters.helpers.BasicFirestoreRecyclerAdapter;
import com.smart_learn.presenter.helpers.adapters.helpers.BasicViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;

public class UserWordsAdapter extends BasicFirestoreRecyclerAdapter<WordDocument, UserWordsAdapter.WordViewHolder, UserWordsAdapter.Callback> {

    private static final int INITIAL_ADAPTER_CAPACITY = 20;
    private static final int LOADING_STEP = 10;

    private final DocumentSnapshot currentLessonSnapshot;

    public UserWordsAdapter(@NonNull @NotNull DocumentSnapshot currentLessonSnapshot,
                            @NonNull @NotNull UserWordsAdapter.Callback adapterCallback) {
        super(adapterCallback, getInitialAdapterOptions(currentLessonSnapshot, adapterCallback.getFragment()), INITIAL_ADAPTER_CAPACITY, LOADING_STEP);
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
            query = UserWordService.getInstance().getQueryForFilter(currentLessonSnapshot.getId(), currentLoad + loadingStep, filteringValue);
        }
        else{
            query = UserWordService.getInstance().getQueryForAllLessonWords(currentLessonSnapshot.getId(),currentLoad + loadingStep);
        }
        super.loadData(query, WordDocument.class, adapterCallback.getFragment());
    }

    private static FirestoreRecyclerOptions<WordDocument> getInitialAdapterOptions(@NonNull @NotNull DocumentSnapshot currentLessonSnapshot,
                                                                                   @NonNull @NotNull Fragment fragment) {
        Query query = UserWordService.getInstance().getQueryForAllLessonWords(currentLessonSnapshot.getId(), INITIAL_ADAPTER_CAPACITY);
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
        filteringValue = "";
        isFiltering = false;
        updateOptions(getInitialAdapterOptions(currentLessonSnapshot, fragment));
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
        Query query = UserWordService.getInstance().getQueryForFilter(currentLessonSnapshot.getId(), INITIAL_ADAPTER_CAPACITY, filteringValue);
        FirestoreRecyclerOptions<WordDocument> newOptions = new FirestoreRecyclerOptions.Builder<WordDocument>()
                .setLifecycleOwner(fragment)
                .setQuery(query, WordDocument.class)
                .build();

        updateOptions(newOptions);
    }

    public final class WordViewHolder extends BasicViewHolder<WordDocument, LayoutCardViewWordBinding> {

        private final MutableLiveData<SpannableString> liveSpannedWord;
        private final MutableLiveData<Boolean> liveIsSelected;
        private final AtomicBoolean isDeletingActive;

        public WordViewHolder(@NonNull @NotNull LayoutCardViewWordBinding viewHolderBinding) {
            super(viewHolderBinding);
            liveSpannedWord = new MutableLiveData<>(new SpannableString(""));
            liveIsSelected = new MutableLiveData<>(false);
            isDeletingActive = new AtomicBoolean(false);

            makeStandardSetup(viewHolderBinding.toolbarLayoutCardViewWord, viewHolderBinding.cvLayoutCardViewWord);

            // link binding with variables
            viewHolderBinding.setLiveSpannedWord(liveSpannedWord);
            viewHolderBinding.setLiveIsSelected(liveIsSelected);

            setListeners();
        }


        @Override
        protected WordDocument getEmptyLiveItemInfo() {
            return new WordDocument();
        }

        @Override
        protected void bind(@NonNull @NotNull WordDocument item, int position){

            if (isSelectionModeActive()) {
                liveSpannedWord.setValue(new SpannableString(item.getWord()));
                return;
            }

            if(isFiltering){
                liveSpannedWord.setValue(Utilities.Activities.generateSpannedString(
                        CoreUtilities.General.getSubstringIndexes(item.getWord().toLowerCase(), filteringValue), item.getWord()));
            }
            else {
                liveSpannedWord.setValue(new SpannableString(item.getWord()));
            }

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
                    if(!Utilities.Adapters.isGoodAdapterPosition(position)){
                        return;
                    }

                    if(isSelectionModeActive()){
                        markItem(new Pair<>(getSnapshots().getSnapshot(position), new Pair<>(viewHolderBinding.cvLayoutCardViewWord, liveIsSelected)));
                        return;
                    }

                    adapterCallback.onSimpleClick(getSnapshots().getSnapshot(position));
                }
            });

            viewHolderBinding.cvLayoutCardViewWord.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(!isSelectionModeActive()) {
                        int position = getAdapterPosition();
                        if(!Utilities.Adapters.isGoodAdapterPosition(position)){
                            return true;
                        }

                        adapterCallback.onLongClick(getSnapshots().getSnapshot(position));
                        // by default clicked item is selected
                        markItem(new Pair<>(getSnapshots().getSnapshot(position), new Pair<>(viewHolderBinding.cvLayoutCardViewWord, liveIsSelected)));
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
                    if(!Utilities.Adapters.isGoodAdapterPosition(position)){
                        return true;
                    }

                    int id = item.getItemId();
                    if(id == R.id.action_delete_menu_card_view_word){
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

    }

}