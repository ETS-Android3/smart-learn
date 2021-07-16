package com.smart_learn.presenter.activities.notebook.guest.fragments.words.helpers;


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
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.smart_learn.R;
import com.smart_learn.core.services.GuestWordService;
import com.smart_learn.core.utilities.CoreUtilities;
import com.smart_learn.core.utilities.GeneralUtilities;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.data.room.entities.Word;
import com.smart_learn.data.room.entities.helpers.BasicInfo;
import com.smart_learn.databinding.LayoutCardViewWordBinding;
import com.smart_learn.presenter.activities.notebook.guest.fragments.words.GuestWordsFragment;
import com.smart_learn.presenter.helpers.Callbacks;
import com.smart_learn.presenter.helpers.PresenterHelpers;
import com.smart_learn.presenter.helpers.Utilities;
import com.smart_learn.presenter.helpers.adapters.BasicViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class WordsAdapter extends ListAdapter <Word, WordsAdapter.WordViewHolder> implements Filterable, PresenterHelpers.AdapterHelper {

    private final MutableLiveData<Boolean> liveIsActionModeActive;
    private boolean isFiltering;
    private String filteringValue;

    private final Callbacks.FragmentGeneralCallback<GuestWordsFragment> fragmentCallback;

    public WordsAdapter(@NonNull Callbacks.FragmentGeneralCallback<GuestWordsFragment> fragmentCallback) {
        super(new DiffUtil.ItemCallback<Word>(){
            @Override
            public boolean areItemsTheSame(@NonNull Word oldItem, @NonNull Word newItem) {
                return oldItem.areItemsTheSame(newItem);
            }
            @Override
            public boolean areContentsTheSame(@NonNull Word oldItem, @NonNull Word newItem) {
                return oldItem.areContentsTheSame(newItem);
            }
        });

        this.fragmentCallback = fragmentCallback;

        this.liveIsActionModeActive = new MutableLiveData<>(false);
        this.isFiltering = false;
        this.filteringValue = "";
    }

    public void setLiveActionMode(boolean value) {
        liveIsActionModeActive.setValue(value);
    }

    /** Load data in recycler view */
    public void setItems(List<Word> items) {
        submitList(items);
    }

    @NonNull
    @Override
    public WordsAdapter.WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // set data binding
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        LayoutCardViewWordBinding viewHolderBinding = DataBindingUtil.inflate(layoutInflater ,R.layout.layout_card_view_word, parent, false);
        viewHolderBinding.setLifecycleOwner(fragmentCallback.getFragment());

        // set binding variable
        viewHolderBinding.setLiveIsActionModeActive(liveIsActionModeActive);

        return new WordViewHolder(viewHolderBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull WordsAdapter.WordViewHolder holder, int position) {
        if(!Utilities.Adapters.isGoodAdapterPosition(position)){
            return;
        }

        Word word = getItem(position);
        if(!CoreUtilities.General.isItemNotNull(word)){
            return;
        }

        holder.bind(word, position);
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
                List<Word> allItems = GuestWordService.getInstance().getCurrentLessonSampleWords(fragmentCallback.getFragment().getViewModel().getCurrentLessonId());
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
        private final AtomicBoolean isDeleting;

        public WordViewHolder(@NonNull LayoutCardViewWordBinding viewHolderBinding) {
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
        protected Word getEmptyLiveItemInfo() {
            return Word.generateEmptyObject();
        }

        @Override
        protected void bind(@NonNull @NotNull Word item, int position) {
            if (fragmentCallback.getFragment().getActionMode() != null) {
                liveSpannedWord.setValue(new SpannableString(item.getWord()));
                liveIsSelected.setValue(item.isSelected());
                viewHolderBinding.cvLayoutCardViewWord.setChecked(item.isSelected());
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

                    Word word = getItem(position);
                    if(!CoreUtilities.General.isItemNotNull(word)){
                        return;
                    }

                    // If action mode is on then user can select/deselect items.
                    if(fragmentCallback.getFragment().getActionMode() != null) {
                        markItem(word,!word.isSelected());
                        return;
                    }

                    // If action mode is disabled then simple click will navigate user to the
                    // HomeWordFragment using selected word.
                    fragmentCallback.getFragment().goToGuestHomeWordFragment(word);
                }
            });

            // long click is used for launching action mode
            viewHolderBinding.cvLayoutCardViewWord.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(fragmentCallback.getFragment().getActionMode() == null) {
                        int position = getAdapterPosition();
                        if(!Utilities.Adapters.isGoodAdapterPosition(position)){
                            return true;
                        }

                        Word word = getItem(position);
                        if(!CoreUtilities.General.isItemNotNull(word)){
                            return true;
                        }

                        fragmentCallback.getFragment().startFragmentActionMode();
                        // by default clicked item is selected
                        markItem(word,true);
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
                        if(isDeleting.get()){
                            return true;
                        }
                        isDeleting.set(true);
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
                    fragmentCallback.getFragment().requireActivity().runOnUiThread(() -> {
                        GeneralUtilities.showShortToastMessage(fragmentCallback.getFragment().requireContext(),
                                fragmentCallback.getFragment().getString(R.string.success_deleting_word));
                    });
                    isDeleting.set(false);
                }

                @Override
                public void onFailure() {
                    fragmentCallback.getFragment().requireActivity().runOnUiThread(() -> {
                        GeneralUtilities.showShortToastMessage(fragmentCallback.getFragment().requireContext(),
                                fragmentCallback.getFragment().getString(R.string.error_deleting_word));
                    });
                    isDeleting.set(false);
                }
            });
        }
    }

    private void markItem(Word word, boolean isSelected) {
        // TODO: make a specific method for deep copy on Basic info entity
        BasicInfo basicInfo = new BasicInfo(word.getBasicInfo().getCreatedAt());
        basicInfo.setModifiedAt(word.getBasicInfo().getModifiedAt());

        // TODO: make a specific method for deep copy on Word entity
        Word tmp = new Word(
                word.getNotes(),
                word.isSelected(),
                basicInfo,
                word.getFkLessonId(),
                word.isFavourite(),
                word.getLanguage(),
                new ArrayList<>(word.getTranslations()),
                word.getWord(),
                word.getPhonetic()
        );

        tmp.setWordId(word.getWordId());
        tmp.setSelected(isSelected);
        GuestWordService.getInstance().update(tmp, null);
    }
}

