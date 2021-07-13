package com.smart_learn.presenter.activities.notebook.guest.fragments.lessons.helpers;


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
import com.smart_learn.data.room.entities.Lesson;
import com.smart_learn.data.room.entities.helpers.IndexRange;
import com.smart_learn.databinding.LayoutCardViewLessonBinding;
import com.smart_learn.presenter.activities.notebook.guest.fragments.lessons.GuestLessonsFragment;
import com.smart_learn.presenter.helpers.Callbacks;
import com.smart_learn.presenter.helpers.PresenterHelpers;
import com.smart_learn.presenter.helpers.Utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import timber.log.Timber;

/**
 * For ListAdapter https://www.youtube.com/watch?v=xPPMygGxiEo
 * */
public class LessonsAdapter extends ListAdapter <Lesson, LessonsAdapter.LessonViewHolder> implements Filterable, PresenterHelpers.AdapterHelper {

    private final Callbacks.FragmentGeneralCallback<GuestLessonsFragment> fragmentCallback;

    public LessonsAdapter(@NonNull Callbacks.FragmentGeneralCallback<GuestLessonsFragment> fragmentCallback) {
        super(new DiffUtil.ItemCallback<Lesson>(){
            @Override
            public boolean areItemsTheSame(@NonNull Lesson oldItem, @NonNull Lesson newItem) {
                return oldItem.getLessonId() == newItem.getLessonId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Lesson oldItem, @NonNull Lesson newItem) {
                return oldItem.getName().equals(newItem.getName()) &&
                        oldItem.getBasicInfo().getCreatedAt() == newItem.getBasicInfo().getCreatedAt() &&
                        oldItem.getBasicInfo().getModifiedAt() == newItem.getBasicInfo().getModifiedAt() &&
                        oldItem.isSelected() == newItem.isSelected();
            }
        });

        this.fragmentCallback = fragmentCallback;
    }

    /** Load data in recycler view */
    public void setItems(List<Lesson> items) {
        submitList(items);
    }

    @NonNull
    @Override
    public LessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // set data binding
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        LayoutCardViewLessonBinding viewHolderBinding = DataBindingUtil.inflate(layoutInflater ,R.layout.layout_card_view_lesson, parent, false);
        viewHolderBinding.setLifecycleOwner(fragmentCallback.getFragment());

        // link data binding layout with view holder
        LessonViewHolder lessonViewHolder = new LessonViewHolder(viewHolderBinding);
        viewHolderBinding.setViewHolder(lessonViewHolder);

        return lessonViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull LessonsAdapter.LessonViewHolder holder, int position) {
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

                String searchValue = constraint.toString();

                // For filtering mode we search always in all db.
                List<Lesson> allItems = fragmentCallback.getFragment().getViewModel().getLessonService().getAllSampleLesson();
                // reset search indexes and spanned name for all items
                allItems.forEach(it -> {
                    it.setSearchIndexes(new ArrayList<>());
                    it.resetSpannedName();
                });

                List<Lesson> filteredItems;

                if (searchValue.isEmpty()) {
                    filteredItems = allItems;
                }
                else {
                    filteredItems = allItems.stream()
                            .filter(it -> it.getName().toLowerCase().contains(searchValue.toLowerCase()))
                            .collect(Collectors.toList());

                    // these indexes are used to show a background color for searchValue while filtering
                    filteredItems.forEach(it -> {
                        int start = it.getName().toLowerCase().indexOf(searchValue.toLowerCase());
                        int end = start + searchValue.length();
                        it.addIndexRange(new IndexRange(start,end));
                    });
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
                submitList((List<Lesson>) results.values);

                // this call is made in order to show a colorful spanned text
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public void loadMoreData() {

    }


    /**
     * Class to specific how an element from recycler view (lesson card) will be shown
     * */
    public final class LessonViewHolder extends RecyclerView.ViewHolder {

        private final LayoutCardViewLessonBinding viewHolderBinding;
        private final MutableLiveData<Lesson> liveLesson;

        public LessonViewHolder(@NonNull LayoutCardViewLessonBinding viewHolderBinding) {
            // this will set itemView in ViewHolder class
            super(viewHolderBinding.getRoot());
            this.viewHolderBinding = viewHolderBinding;

            // avoid a null value for liveLesson.getValue()
            // FIXME: add a standard new empty lesson
            //liveLesson = new MutableLiveData<>(new Lesson("",0,0,false));
            liveLesson = new MutableLiveData<>();

            setListeners();
        }

        public LiveData<Lesson> getLiveLesson(){ return liveLesson; }

        private void setListeners(){
            // simple click action
            viewHolderBinding.cvLayoutCardViewLesson.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Lesson lesson = getItem(getAdapterPosition());
                    // If action mode is on then user can select/deselect items.
                    if(fragmentCallback.getFragment().getActionMode() != null) {
                        markItem(lesson,!lesson.isSelected());
                        return;
                    }

                    // If action mode is disabled then simple click will navigate user to the
                    // HomeLessonFragment using selected lesson.
                    fragmentCallback.getFragment().goToHomeLessonFragment(lesson);
                }
            });

            // long click is used for launching action mode
            viewHolderBinding.cvLayoutCardViewLesson.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(fragmentCallback.getFragment().getActionMode() == null) {
                        fragmentCallback.getFragment().startFragmentActionMode();
                        // by default clicked item is selected
                        markItem(getItem(getAdapterPosition()),true);
                    }
                    return true;
                }
            });

        }

        /** this is how single elements are displayed in recycler view */
        private void bind(Lesson lesson){
            if(lesson == null){
                Timber.w("lesson is null ==> can not create bind");
                return;
            }

            if (fragmentCallback.getFragment().getActionMode() != null) {
                viewHolderBinding.toolbarLayoutCardViewLesson.setVisibility(View.GONE);
                viewHolderBinding.cvLayoutCardViewLesson.setChecked(lesson.isSelected());
                liveLesson.setValue(lesson);
                return;
            }

            viewHolderBinding.toolbarLayoutCardViewLesson.setVisibility(View.VISIBLE);

            if(!lesson.getSearchIndexes().isEmpty()) {
                lesson.setSpannedName(Utilities.Activities.createSpannedText(lesson.getSearchIndexes(), lesson.getName()));
            }
            else {
                // This reset will be made also when a new filtering is made, but to avoid to keep
                // irrelevant info linked to lesson if no filtering will be made, make reset here
                // too.
                lesson.setSearchIndexes(new ArrayList<>());
                lesson.resetSpannedName();
            }

            liveLesson.setValue(lesson);
        }
    }

    private void markItem(Lesson lesson, boolean isSelected) {
        // FIXME: add a standard new empty lesson
        //Lesson tmp = new Lesson(lesson.getName(), lesson.getCreatedAt(), lesson.getModifiedAt(), lesson.isSelected());
       // tmp.setLessonId(lesson.getLessonId());
       // tmp.setSelected(isSelected);
        //fragmentCallback.getFragment().getLessonsViewModel().getLessonService().update(tmp);
    }
}

