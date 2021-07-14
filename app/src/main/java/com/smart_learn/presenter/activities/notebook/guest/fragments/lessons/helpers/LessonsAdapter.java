package com.smart_learn.presenter.activities.notebook.guest.fragments.lessons.helpers;


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
import com.smart_learn.core.utilities.CoreUtilities;
import com.smart_learn.data.room.entities.Lesson;
import com.smart_learn.databinding.LayoutCardViewLessonBinding;
import com.smart_learn.presenter.activities.notebook.guest.fragments.lessons.GuestLessonsFragment;
import com.smart_learn.presenter.helpers.Callbacks;
import com.smart_learn.presenter.helpers.PresenterHelpers;
import com.smart_learn.presenter.helpers.Utilities;
import com.smart_learn.presenter.helpers.adapters.BasicViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

import timber.log.Timber;

import static androidx.recyclerview.widget.RecyclerView.NO_POSITION;

/**
 * For ListAdapter https://www.youtube.com/watch?v=xPPMygGxiEo
 * */
public class LessonsAdapter extends ListAdapter <Lesson, LessonsAdapter.LessonViewHolder> implements Filterable, PresenterHelpers.AdapterHelper {

    private final MutableLiveData<Boolean> liveIsActionModeActive;
    private boolean isFiltering;
    private String filteringValue;

    private final Callbacks.FragmentGeneralCallback<GuestLessonsFragment> fragmentCallback;

    public LessonsAdapter(@NonNull Callbacks.FragmentGeneralCallback<GuestLessonsFragment> fragmentCallback) {
        super(new DiffUtil.ItemCallback<Lesson>(){
            @Override
            public boolean areItemsTheSame(@NonNull Lesson oldItem, @NonNull Lesson newItem) {
                return oldItem.areItemsTheSame(newItem);
            }
            @Override
            public boolean areContentsTheSame(@NonNull Lesson oldItem, @NonNull Lesson newItem) {
                return oldItem.areContentsTheSame(newItem);
            }
        });

        this.fragmentCallback = fragmentCallback;

        this.liveIsActionModeActive = new MutableLiveData<>(false);
        this.isFiltering = false;
        this.filteringValue = "";
    }

    public void setItems(List<Lesson> items) {
        submitList(items);
    }

    public void setLiveActionMode(boolean value) {
         liveIsActionModeActive.setValue(value);
    }

    @NonNull
    @Override
    public LessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // set data binding
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        LayoutCardViewLessonBinding viewHolderBinding = DataBindingUtil.inflate(layoutInflater,
                R.layout.layout_card_view_lesson, parent, false);
        viewHolderBinding.setLifecycleOwner(fragmentCallback.getFragment());

        // set binding variable
        viewHolderBinding.setLiveIsActionModeActive(liveIsActionModeActive);

        return new LessonViewHolder(viewHolderBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull LessonsAdapter.LessonViewHolder holder, int position) {
        if(position == NO_POSITION){
            Timber.w("position is set to NO_POSITION");
            return;
        }

        Lesson lesson = getItem(position);
        if(lesson == null){
            Timber.w("item is null ==> can not create bind");
            return;
        }

        holder.bind(lesson, position);
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
                isFiltering = true;
                filteringValue = constraint.toString();

                // For filtering mode we search always in all db.
                List<Lesson> allItems = fragmentCallback.getFragment().getViewModel().getGuestLessonService().getAllSampleLesson();
                List<Lesson> filteredItems;

                if (filteringValue.isEmpty()) {
                    isFiltering = false;
                    filteredItems = allItems;
                }
                else {
                    filteredItems = allItems.stream()
                            .filter(it -> it.getName().toLowerCase().contains(filteringValue.toLowerCase()))
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
                submitList((List<Lesson>) results.values);

                // this call is made in order to show a colorful spanned text
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public void loadMoreData() {
        // no action needed here
    }


    public final class LessonViewHolder extends BasicViewHolder<Lesson, LayoutCardViewLessonBinding> {

        private final MutableLiveData<SpannableString> liveLessonSpannedName;
        private final MutableLiveData<Boolean> liveIsSelected;

        public LessonViewHolder(@NonNull LayoutCardViewLessonBinding viewHolderBinding) {
            super(viewHolderBinding);
            liveLessonSpannedName = new MutableLiveData<>(new SpannableString(""));
            liveIsSelected = new MutableLiveData<>(false);

            // link binding with variables
            viewHolderBinding.setLiveLessonSpannedName(liveLessonSpannedName);
            viewHolderBinding.setLiveIsSelected(liveIsSelected);

            setListeners();
        }

        @Override
        protected Lesson getEmptyLiveItemInfo() {
            return Lesson.generateEmptyObject();
        }

        @Override
        protected void bind(@NonNull @NotNull Lesson item, int position) {
            if(isFiltering){
                liveLessonSpannedName.setValue(Utilities.Activities.generateSpannedString(
                        CoreUtilities.General.getSubstringIndexes(item.getName(), filteringValue), item.getName()));
            }
            else {
                liveLessonSpannedName.setValue(new SpannableString(item.getName()));
            }

            if (fragmentCallback.getFragment().getActionMode() != null) {
                liveIsSelected.setValue(item.isSelected());
                viewHolderBinding.cvLayoutCardViewLesson.setChecked(item.isSelected());
                return;
            }

            liveIsSelected.setValue(false);
            viewHolderBinding.cvLayoutCardViewLesson.setChecked(false);
        }


        private void setListeners(){

            setToolbarListener();

            // simple click action
            viewHolderBinding.cvLayoutCardViewLesson.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position == NO_POSITION){
                        Timber.w("position is set to NO_POSITION");
                        return;
                    }

                    Lesson lesson = getItem(position);
                    if(lesson == null){
                        Timber.w("lesson is null");
                        return;
                    }

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
                        int position = getAdapterPosition();
                        if(position == NO_POSITION){
                            Timber.w("position is set to NO_POSITION");
                            return true;
                        }

                        Lesson lesson = getItem(position);
                        if(lesson == null){
                            Timber.w("lesson is null");
                            return true;
                        }

                        fragmentCallback.getFragment().startFragmentActionMode();
                        // by default clicked item is selected
                        markItem(lesson,true);
                    }
                    return true;
                }
            });

        }

        private void setToolbarListener(){
            viewHolderBinding.toolbarLayoutCardViewLesson.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int position = getAdapterPosition();
                    if(position == NO_POSITION){
                        Timber.w("position is set to NO_POSITION");
                        return true;
                    }

                    Lesson lesson = getItem(position);
                    if(lesson == null){
                        Timber.w("lesson is null");
                        return true;
                    }

                    int id = item.getItemId();
                    if(id == R.id.action_delete_menu_card_view_lesson){
                        fragmentCallback.getFragment().deleteLessonAlert(new Callbacks.StandardAlertDialogCallback() {
                            @Override
                            public void onPositiveButtonPress() {
                                fragmentCallback.getFragment().getViewModel().getGuestLessonService().delete(lesson, null);
                            }
                        });
                        return true;
                    }
                    return true;
                }
            });
        }
    }

    private void markItem(Lesson lesson, boolean isSelected) {
        Lesson tmp = new Lesson(lesson.getNotes(), lesson.isSelected(), lesson.getBasicInfo(), lesson.getName());
        tmp.setLessonId(lesson.getLessonId());
        tmp.setSelected(isSelected);
        fragmentCallback.getFragment().getViewModel().getGuestLessonService().update(tmp, null);
    }
}

