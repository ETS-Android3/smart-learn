package com.smart_learn.presenter.activities.notebook.user.fragments.words.helpers;

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
import com.smart_learn.core.services.UserWordService;
import com.smart_learn.core.utilities.CoreUtilities;
import com.smart_learn.core.utilities.GeneralUtilities;
import com.smart_learn.data.firebase.firestore.entities.WordDocument;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.databinding.LayoutCardViewWordBinding;
import com.smart_learn.presenter.activities.notebook.user.fragments.words.UserWordsFragment;
import com.smart_learn.presenter.helpers.Utilities;
import com.smart_learn.presenter.helpers.adapters.BasicFirestoreRecyclerAdapter;
import com.smart_learn.presenter.helpers.adapters.BasicViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.Getter;

public class WordsAdapter extends BasicFirestoreRecyclerAdapter<WordDocument, WordsAdapter.WordViewHolder> {

    private static final int INITIAL_ADAPTER_CAPACITY = 20;
    private static final int LOADING_STEP = 10;

    protected final WordsAdapter.Callback<UserWordsFragment> adapterCallback;
    private final MutableLiveData<Boolean> liveIsActionModeActive;

    private final DocumentSnapshot currentLessonSnapshot;
    @Getter
    @NonNull
    @NotNull
    private final ArrayList<DocumentSnapshot> selectedWords;

    // if filter is on
    private boolean isFiltering;
    // current filter value if filter is on
    private String filteringValue;

    public WordsAdapter(@NonNull @NotNull DocumentSnapshot currentLessonSnapshot,
                        @NonNull @NotNull WordsAdapter.Callback<UserWordsFragment> adapterCallback) {
        super(adapterCallback.getFragment(), getInitialAdapterOptions(currentLessonSnapshot, adapterCallback.getFragment()),
                INITIAL_ADAPTER_CAPACITY, LOADING_STEP);
        this.currentLessonSnapshot = currentLessonSnapshot;
        this.adapterCallback = adapterCallback;

        // set initial values
        this.liveIsActionModeActive = new MutableLiveData<>(false);
        this.selectedWords = new ArrayList<>();
        this.isFiltering = false;
        this.filteringValue = "";
    }

    public void setLiveActionMode(boolean value) {
        liveIsActionModeActive.setValue(value);
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
        viewHolderBinding.setLiveIsActionModeActive(liveIsActionModeActive);

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

    public void resetSelectedItems(){
        selectedWords.clear();
        adapterCallback.getFragment().showSelectedItems(selectedWords.size());
    }

    public final class WordViewHolder extends BasicViewHolder<WordDocument, LayoutCardViewWordBinding> {

        private final MutableLiveData<SpannableString> liveSpannedWord;
        private final MutableLiveData<Boolean> liveIsSelected;
        private final AtomicBoolean isDeleting;

        public WordViewHolder(@NonNull @NotNull LayoutCardViewWordBinding viewHolderBinding) {
            super(viewHolderBinding);
            liveSpannedWord = new MutableLiveData<>(new SpannableString(""));
            liveIsSelected = new MutableLiveData<>(false);
            isDeleting = new AtomicBoolean(false);

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

            if (adapterCallback.getFragment().getActionMode() != null) {
                liveSpannedWord.setValue(new SpannableString(item.getWord()));
                liveIsSelected.setValue(viewHolderBinding.cvLayoutCardViewWord.isChecked());
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

            setToolbarListener();

            // simple click action
            viewHolderBinding.cvLayoutCardViewWord.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(!Utilities.Adapters.isGoodAdapterPosition(position)){
                        return;
                    }

                    // If action mode is on then user can select/deselect items.
                    if(adapterCallback.getFragment().getActionMode() != null) {
                        mark(position);
                        return;
                    }

                    // If action mode is disabled then simple click will navigate user to the
                    // HomeWordFragment using selected word.
                    adapterCallback.getFragment().goToUserWordContainerFragment(getSnapshots().getSnapshot(position));
                }
            });

            // long click is used for launching action mode
            viewHolderBinding.cvLayoutCardViewWord.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(adapterCallback.getFragment().getActionMode() == null) {
                        int position = getAdapterPosition();
                        if(!Utilities.Adapters.isGoodAdapterPosition(position)){
                            return true;
                        }

                        adapterCallback.getFragment().startFragmentActionMode();
                        // by default clicked item is selected
                        mark(position);
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
                        if(isDeleting.get()){
                            return true;
                        }
                        isDeleting.set(true);
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
                    adapterCallback.getFragment().requireActivity().runOnUiThread(() -> {
                        GeneralUtilities.showShortToastMessage(adapterCallback.getFragment().requireContext(),
                                adapterCallback.getFragment().getString(R.string.success_deleting_word));
                    });
                    isDeleting.set(false);
                }

                @Override
                public void onFailure() {
                    adapterCallback.getFragment().requireActivity().runOnUiThread(() -> {
                        GeneralUtilities.showShortToastMessage(adapterCallback.getFragment().requireContext(),
                                adapterCallback.getFragment().getString(R.string.error_deleting_word));
                    });
                    isDeleting.set(false);
                }
            });
        }

        private void mark(int position){
            String currentDocId = getSnapshots().getSnapshot(position).getId();

            // if is checked, remove item and then uncheck it
            if(viewHolderBinding.cvLayoutCardViewWord.isChecked()){
                // mark as unchecked
                viewHolderBinding.cvLayoutCardViewWord.setChecked(false);
                liveIsSelected.setValue(false);

                // and remove checked item from list
                int lim = selectedWords.size();
                for(int i = 0; i < lim; i++){
                    if(selectedWords.get(i).getId().equals(currentDocId)){
                        selectedWords.remove(i);
                        break;
                    }
                }

                adapterCallback.getFragment().showSelectedItems(selectedWords.size());
                return;
            }

            // if is unchecked the mark as checked
            viewHolderBinding.cvLayoutCardViewWord.setChecked(true);
            liveIsSelected.setValue(true);

            // and add item only if does not exists
            boolean exists = false;
            for(DocumentSnapshot item : selectedWords){
                if(item.getId().equals(currentDocId)){
                    exists = true;
                    break;
                }
            }

            if(!exists){
                selectedWords.add(getSnapshots().getSnapshot(position));
            }

            adapterCallback.getFragment().showSelectedItems(selectedWords.size());

        }
    }

    public interface Callback <T> {
        T getFragment();
    }

}