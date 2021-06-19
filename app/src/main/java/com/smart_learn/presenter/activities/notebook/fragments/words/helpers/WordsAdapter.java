package com.smart_learn.presenter.activities.notebook.fragments.words.helpers;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.smart_learn.R;
import com.smart_learn.data.models.room.entities.Word;
import com.smart_learn.data.models.room.entities.helpers.Translation;
import com.smart_learn.databinding.LayoutCardViewWordBinding;
import com.smart_learn.presenter.activities.notebook.fragments.words.WordsFragment;
import com.smart_learn.presenter.helpers.Callbacks;
import com.smart_learn.presenter.helpers.Utilities;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class WordsAdapter extends ListAdapter <Word, WordsAdapter.WordViewHolder> implements Filterable {

    private final Callbacks.FragmentGeneralCallback<WordsFragment> fragmentCallback;

    public WordsAdapter(@NonNull Callbacks.FragmentGeneralCallback<WordsFragment> fragmentCallback) {
        super(new DiffUtil.ItemCallback<Word>(){
            @Override
            public boolean areItemsTheSame(@NonNull Word oldItem, @NonNull Word newItem) {
                return oldItem.getWordId() == newItem.getWordId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Word oldItem, @NonNull Word newItem) {
                return oldItem.getWord().equals(newItem.getWord()) &&
                        oldItem.getCreatedAt() == newItem.getCreatedAt() &&
                        oldItem.getModifiedAt() == newItem.getModifiedAt() &&
                        oldItem.isSelected() == newItem.isSelected() &&
                        oldItem.getTranslation().getTranslation().equals(newItem.getTranslation().getTranslation()) &&
                        oldItem.getTranslation().getPhonetic().equals(newItem.getTranslation().getPhonetic());
            }
        });

        this.fragmentCallback = fragmentCallback;
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

        // link data binding layout with view holder
        WordsAdapter.WordViewHolder lessonViewHolder = new  WordsAdapter.WordViewHolder(viewHolderBinding);
        viewHolderBinding.setViewHolder(lessonViewHolder);

        return lessonViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull WordsAdapter.WordViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    /**
     * Filter used in search mode
     *  https://www.youtube.com/watch?v=CTvzoVtKoJ8&list=WL&index=78&t=285s
     * */
    @Override
    public Filter getFilter() {
        return new Filter() {

            /** Run on background thread. Background thread is created automatically. */
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults filterResults = new FilterResults();
                return filterResults;
            }

            /** runs on a UI thread */
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

            }
        };
    }


    /**
     * Class to specific how an element from recycler view (lesson card) will be shown
     * */
    public final class WordViewHolder extends RecyclerView.ViewHolder {

        private final LayoutCardViewWordBinding viewHolderBinding;
        private final MutableLiveData<Word> liveWord;

        public WordViewHolder(@NonNull LayoutCardViewWordBinding viewHolderBinding) {
            // this will set itemView in ViewHolder class
            super(viewHolderBinding.getRoot());
            this.viewHolderBinding = viewHolderBinding;

            // avoid a null value for liveLesson.getValue()
            liveWord = new MutableLiveData<>(new Word(0,0,0,false,new Translation("",""),""));

            setListeners();
        }

        public LiveData<Word> getLiveWord(){ return liveWord; }

        private void setListeners(){
            // simple click action
            viewHolderBinding.cvLayoutCardViewWord.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Word word = getItem(getAdapterPosition());
                    // If action mode is on then user can select/deselect items.
                    if(fragmentCallback.getFragment().getActionMode() != null) {
                        markItem(word,!word.isSelected());
                        return;
                    }

                    // If action mode is disabled then simple click will navigate user to the
                    // HomeWordFragment using selected word.
                    fragmentCallback.getFragment().goToHomeWordFragment(word);
                }
            });

            // long click is used for launching action mode
            viewHolderBinding.cvLayoutCardViewWord.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(fragmentCallback.getFragment().getActionMode() == null) {
                        fragmentCallback.getFragment().startActionMode();
                        // by default clicked item is selected
                        markItem(getItem(getAdapterPosition()),true);
                    }
                    return true;
                }
            });

        }

        /** this is how single elements are displayed in recycler view */
        private void bind(Word word){
            if(word == null){
                Timber.w("word is null ==> can not create bind");
                return;
            }

            if (fragmentCallback.getFragment().getActionMode() != null) {
                viewHolderBinding.toolbarLayoutCardViewWord.setVisibility(View.GONE);
                viewHolderBinding.cvLayoutCardViewWord.setChecked(word.isSelected());
                liveWord.setValue(word);
                return;
            }

            viewHolderBinding.toolbarLayoutCardViewWord.setVisibility(View.VISIBLE);

            if(!word.getSearchIndexes().isEmpty()) {
                word.setSpannedWord(Utilities.Activities.createSpannedText(word.getSearchIndexes(), word.getWord()));
            }
            else {
                // This reset will be made also when a new filtering is made, but to avoid to keep
                // irrelevant info linked to lesson if no filtering will be made, make reset here
                // too.
                word.setSearchIndexes(new ArrayList<>());
                word.resetSpannedWord();
            }

            liveWord.setValue(word);
        }
    }

    private void markItem(Word word, boolean isSelected) {
        Word tmp = new Word(word.getCreatedAt(),word.getModifiedAt(),word.getFkLessonId(),word.isSelected(),
                new Translation(word.getTranslation().getTranslation(),word.getTranslation().getPhonetic()),word.getWord());
        tmp.setWordId(word.getWordId());
        tmp.setSelected(isSelected);
        fragmentCallback.getFragment().getWordsViewModel().getWordsService().update(tmp);
    }
}

