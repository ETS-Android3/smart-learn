package com.smart_learn.presenter.helpers.adapters.words;


import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;

import com.smart_learn.R;
import com.smart_learn.core.services.GuestWordService;
import com.smart_learn.core.utilities.CoreUtilities;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.data.room.entities.Word;
import com.smart_learn.databinding.LayoutCardViewWordBinding;
import com.smart_learn.presenter.helpers.Utilities;
import com.smart_learn.presenter.helpers.adapters.helpers.BasicListAdapter;
import com.smart_learn.presenter.helpers.adapters.helpers.BasicViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class GuestWordsAdapter extends BasicListAdapter<Word, GuestWordsAdapter.WordViewHolder, GuestWordsAdapter.Callback> implements Filterable {

    private final int currentLessonId;

    public GuestWordsAdapter(int currentLessonId, @NonNull @NotNull GuestWordsAdapter.Callback adapterCallback) {
        super(adapterCallback);
        this.currentLessonId = currentLessonId;
    }

    @NonNull
    @Override
    public GuestWordsAdapter.WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // set data binding
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        LayoutCardViewWordBinding viewHolderBinding = DataBindingUtil.inflate(layoutInflater ,R.layout.layout_card_view_word, parent, false);
        viewHolderBinding.setLifecycleOwner(adapterCallback.getFragment());

        // set binding variable
        viewHolderBinding.setLiveIsActionModeActive(getLiveIsSelectionModeActive());

        return new WordViewHolder(viewHolderBinding);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            /** Run on background thread. Background thread is created automatically. */
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                isFiltering = true;
                filteringValue = constraint.toString().toLowerCase();

                // For filtering mode we search always in all db.
                List<Word> allItems = GuestWordService.getInstance().getCurrentLessonSampleWords(currentLessonId);
                List<Word> filteredItems;

                if (filteringValue.isEmpty()) {
                    isFiltering = false;
                    filteredItems = allItems;
                }
                else {
                    filteredItems = allItems.stream()
                            .filter(it -> it.getWord().toLowerCase().contains(filteringValue))
                            .collect(Collectors.toList());
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredItems;
                return filterResults;
            }

            /** runs on a UI thread */
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                /*
                 When filtering, search is made in all database and objects returned from db will
                 have same address as objects from current recycler view list. So any change which
                 will be made at filtered items will be reflected in recycler view list also.

                 By this cause DiffUtilCallback will not work because he will see NO change and by
                 this cause, for him, after calling submitList(...) method (and areContentsTheSame(...)
                 method will be called automatically), newList and oldList will be identical.
                 In order to show new changes notifyDataSetChanged() must be called in order to call
                 onBindViewHolder().

                 To avoid this call while filtering, you must make a deep copy for items from
                 database in order to be different from current recycler view list but time will be
                 increased because copies must be processed.
                 */

                // FIXME: make a check for this cast
                submitList((List<Word>) results.values);

                // this call is made in order to show a colorful spanned text
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public void loadMoreData() {
        // no action needed here
    }


    public final class WordViewHolder extends BasicViewHolder<Word, LayoutCardViewWordBinding> {

        private final MutableLiveData<SpannableString> liveSpannedWord;
        private final MutableLiveData<Boolean> liveIsSelected;
        private final AtomicBoolean isDeletingActive;

        public WordViewHolder(@NonNull LayoutCardViewWordBinding viewHolderBinding) {
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
        protected Word getEmptyLiveItemInfo() {
            return Word.generateEmptyObject();
        }

        @Override
        protected void bind(@NonNull @NotNull Word item, int position) {

            if (isSelectionModeActive()) {
                liveSpannedWord.setValue(new SpannableString(item.getWord()));
                boolean isSelected = isSelected(item);
                liveIsSelected.setValue(isSelected);
                viewHolderBinding.cvLayoutCardViewWord.setChecked(isSelected);
                return;
            }

            liveIsSelected.setValue(false);
            viewHolderBinding.cvLayoutCardViewWord.setChecked(false);

            if(isFiltering){
                liveSpannedWord.setValue(Utilities.Activities.generateSpannedString(
                        CoreUtilities.General.getSubstringIndexes(item.getWord().toLowerCase(), filteringValue), item.getWord()));
            }
            else {
                liveSpannedWord.setValue(new SpannableString(item.getWord()));
            }

        }


        private void setListeners(){

            if(adapterCallback.showToolbar()){
                setToolbarListener();
            }

            // simple click action
            viewHolderBinding.cvLayoutCardViewWord.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(!Utilities.Adapters.isGoodAdapterPosition(position)){
                        return;
                    }

                    Word word = getItem(position);
                    if(!CoreUtilities.General.isItemNotNull(word)){
                        return;
                    }

                    if(isSelectionModeActive()){
                        markItem(position, word);
                        return;
                    }

                    adapterCallback.onSimpleClick(word);
                }
            });

            // long click is used for launching action mode
            viewHolderBinding.cvLayoutCardViewWord.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // if filtering is active nothing will happen
                    if(isFiltering){
                        return true;
                    }

                    if(!isSelectionModeActive()) {
                        int position = getAdapterPosition();
                        if(!Utilities.Adapters.isGoodAdapterPosition(position)){
                            return true;
                        }

                        Word word = getItem(position);
                        if(!CoreUtilities.General.isItemNotNull(word)){
                            return true;
                        }

                        adapterCallback.onLongClick(word);
                        // by default clicked item is selected
                        markItem(position, word);
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

                    Word word = getItem(position);
                    if(!CoreUtilities.General.isItemNotNull(word)){
                        return true;
                    }

                    int id = item.getItemId();
                    if(id == R.id.action_delete_menu_card_view_word){
                        // avoid multiple press until operation is finished
                        if(isDeletingActive.get()){
                            return true;
                        }
                        isDeletingActive.set(true);
                        onDeletePressed(word);
                        return true;
                    }
                    return true;
                }
            });
        }

        private void onDeletePressed(Word word){
            GuestWordService.getInstance().delete(word, new DataCallbacks.General() {
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

    public interface Callback extends BasicListAdapter.Callback<Word> {

    }
}

