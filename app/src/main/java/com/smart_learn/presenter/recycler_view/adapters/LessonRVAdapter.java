package com.smart_learn.presenter.recycler_view.adapters;

import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.smart_learn.R;
import com.smart_learn.data.models.room.entities.Lesson;
import com.smart_learn.data.models.room.entities.helpers.IndexRange;
import com.smart_learn.databinding.LayoutLessonDetailsBinding;
import com.smart_learn.presenter.activities.lesson.LessonRVActivity;
import com.smart_learn.presenter.view_models.ActivityRVUtilitiesCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * For ListAdapter https://www.youtube.com/watch?v=xPPMygGxiEo
 * */
public class LessonRVAdapter extends ListAdapter <Lesson, LessonRVAdapter.LessonViewHolder> implements Filterable {

    // used for showing different layouts
    private final MutableLiveData<Boolean> liveActionModeIsActive = new MutableLiveData<>(false);

    // For a swipe and show a new layout. Check this info:
    // https://github.com/chthai64/SwipeRevealLayout
    // https://www.youtube.com/watch?v=hnuMyuCWwwU&t
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    protected final ActivityRVUtilitiesCallback<Lesson> activityCallback;

    private static final DiffUtil.ItemCallback<Lesson> DIFF_UTIL_CALLBACK = new DiffUtil.ItemCallback<Lesson>(){
        @Override
        public boolean areItemsTheSame(@NonNull Lesson oldItem, @NonNull Lesson newItem) {
            return oldItem.getLessonId() == newItem.getLessonId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Lesson oldItem, @NonNull Lesson newItem) {
            return oldItem.getName().equals(newItem.getName()) &&
                    oldItem.getCreatedAt() == newItem.getCreatedAt() &&
                    oldItem.getModifiedAt() == newItem.getModifiedAt() &&
                    oldItem.isSelected() == newItem.isSelected();
        }
    };

    public LessonRVAdapter(ActivityRVUtilitiesCallback<Lesson> activityCallback) {
        super(DIFF_UTIL_CALLBACK);
        this.activityCallback = activityCallback;
        viewBinderHelper.setOpenOnlyOne(true);
    }

    /** Load initial data in recycler view */
    public void setItems(List<Lesson> items) {
        submitList(items);
    }

    @NonNull
    @Override
    public LessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // View itemView = LayoutInflater.from(parent.getContext())
                //.inflate(R.layout.layout_lesson_details,parent,false);

        // make data binding
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        LayoutLessonDetailsBinding viewHolderBinding = DataBindingUtil.inflate(layoutInflater,
                R.layout.layout_lesson_details, parent, false);
        viewHolderBinding.setLifecycleOwner(activityCallback.getActivity());

        // link data binding layout with view holder
        LessonViewHolder lessonViewHolder = new LessonViewHolder(viewHolderBinding);
        viewHolderBinding.setViewHolder(lessonViewHolder);

        return lessonViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull LessonViewHolder holder, int position) {
        Lesson lesson = getItem(position);

        viewBinderHelper.bind(holder.swipeRevealLayout, String.valueOf(lesson.getLessonId()));
         // Set this in order to close automatically secondary layout after item update.
         viewBinderHelper.closeLayout(String.valueOf(lesson.getLessonId()));

        holder.bind(lesson);
    }


    /** Filter used in search mode
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
                List<Lesson> allItems = activityCallback.getAllItemsFromDatabase();
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


    public void setActionMode(boolean value){liveActionModeIsActive.setValue(value);}

    /**
     * Class to specific how an element from recycler view will be shown
     * */
    public final class LessonViewHolder extends RecyclerView.ViewHolder {

        // avoid a null value for liveLesson.getValue()
        private final MutableLiveData<Lesson> liveLesson =
                new MutableLiveData<>(new Lesson("",0,0,false));

        private final SwipeRevealLayout swipeRevealLayout;

        public LessonViewHolder(LayoutLessonDetailsBinding viewHolderBinding) {
            // this will set itemView in ViewHolder class
            super(viewHolderBinding.getRoot());

            // layout which is VISIBLE when action mode is NOT active
            LinearLayout normalModeLayout = itemView.findViewById(R.id.mainSwipeLayout);

            // layout which is VISIBLE when action mode is active
            LinearLayout actionModeLayout = viewHolderBinding.includeActionModeLayout.actionModeLayout;

            // layout which is VISIBLE when action mode is NOT active and swipe is made
            // FIXME: Find a better way to get 'swipeRevealLayout'
            //  Why findViewById() does not work?
            //  https://stackoverflow.com/questions/51418503/findviewbyid-not-working-for-specific-view/51420912
            swipeRevealLayout = itemView.findViewWithTag("LESSON_SWIPE_REVEAL_LAYOUT_TAG");

            TextView tvDelete = viewHolderBinding.includeNormalModeLayout.getRoot().findViewById(R.id.tvDelete);
            TextView tvUpdate = viewHolderBinding.includeNormalModeLayout.getRoot().findViewById(R.id.tvUpdate);

            // set listeners
            // simple click in action mode is used for selecting items
            actionModeLayout.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if(activityCallback.getActionModeCallback() != null) {
                        int itemPosition = getAdapterPosition();
                        Lesson item = getItem(itemPosition);
                        // If item is selected deselect it, otherwise select it.
                        markItem(item,!item.isSelected(),itemPosition);
                    }
                }
            });

            // simple click in normal mode is used for launching LessonActivity with selected lesson
            normalModeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // if action mode is not set then set selected lesson and launch LessonActivity
                    Lesson item = getItem(getAdapterPosition());
                    ((LessonRVActivity)activityCallback.getActivity()).startLessonActivity(item.getLessonId());
                }
            });

            // long click in normal mode is used for launching action mode
            normalModeLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(activityCallback.getActionModeCallback() == null) {
                        activityCallback.startActionMode();
                    }
                    return true;
                }
            });

            // for swiping options
            tvUpdate.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    activityCallback.onSwipeForUpdate(getItem(getAdapterPosition()));
                }
            });

            tvDelete.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    activityCallback.onSwipeForDelete(getItem(getAdapterPosition()));
                }
            });
        }

        public LiveData<Lesson> getLiveLesson(){ return liveLesson; }

        public LiveData<Boolean> getLiveLiveActionModeIsActive(){ return liveActionModeIsActive; }


        /** this is how single elements are displayed in recycler view */
        private void bind(Lesson lesson){

            if(!lesson.getSearchIndexes().isEmpty()) {
                lesson.setSpannedName(createSpannedText(lesson.getSearchIndexes(), lesson.getName()));
            }
            else {
                // This reset will be made also when a new filtering is made, but to avoid to keep
                // irrelevant info linked to lesson if no filtering will be made, make reset here
                // too.
                lesson.setSearchIndexes(new ArrayList<>());
                lesson.resetSpannedName();
            }

            // this will notify view to change with new values
            liveLesson.setValue(lesson);
        }
    }

    private Spanned createSpannedText(List<IndexRange> indexRangeList, String text){

        if(indexRangeList != null && !indexRangeList.isEmpty()){

            int idxStart = indexRangeList.get(0).getStart();
            int idxEnd = indexRangeList.get(0).getEnd();

            text = text.subSequence(0,idxStart).toString() +
                    "<span style=\"background-color:yellow\">" +
                    text.subSequence(idxStart,idxEnd).toString() +
                    "</span>" +
                    text.subSequence(idxEnd,text.length()).toString();
        }

        return Html.fromHtml(text,Html.FROM_HTML_MODE_LEGACY);
    }

    protected void markItem(Lesson item, boolean isSelected, int itemPosition) {
        item.setSelected(isSelected);
        // Keep info about selected in db also
        activityCallback.update(item);

        notifyItemChanged(itemPosition);
    }
}
