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
import com.smart_learn.core.services.GuestExpressionService;
import com.smart_learn.core.services.GuestLessonService;
import com.smart_learn.core.services.GuestWordService;
import com.smart_learn.core.services.ThreadExecutorService;
import com.smart_learn.core.utilities.CoreUtilities;
import com.smart_learn.core.utilities.GeneralUtilities;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.data.room.entities.Lesson;
import com.smart_learn.databinding.LayoutCardViewLessonBinding;
import com.smart_learn.presenter.activities.notebook.guest.fragments.lessons.GuestLessonsFragment;
import com.smart_learn.presenter.helpers.Callbacks;
import com.smart_learn.presenter.helpers.PresenterHelpers;
import com.smart_learn.presenter.helpers.Utilities;
import com.smart_learn.presenter.helpers.adapters.BasicViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * For ListAdapter https://www.youtube.com/watch?v=xPPMygGxiEo
 * */
public class LessonsAdapter extends ListAdapter <Lesson, LessonsAdapter.LessonViewHolder> implements Filterable, PresenterHelpers.AdapterHelper {

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

        this.isFiltering = false;
        this.filteringValue = "";
    }

    public void setItems(List<Lesson> items) {
        submitList(items);
    }

    @NonNull
    @Override
    public LessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // set data binding
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        LayoutCardViewLessonBinding viewHolderBinding = DataBindingUtil.inflate(layoutInflater,
                R.layout.layout_card_view_lesson, parent, false);
        viewHolderBinding.setLifecycleOwner(fragmentCallback.getFragment());

        return new LessonViewHolder(viewHolderBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull LessonsAdapter.LessonViewHolder holder, int position) {
        if(!Utilities.Adapters.isGoodAdapterPosition(position)){
            return;
        }

        Lesson lesson = getItem(position);
        if(!CoreUtilities.General.isItemNotNull(lesson)){
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
                List<Lesson> allItems = GuestLessonService.getInstance().getAllSampleLesson();
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
        private final MutableLiveData<String> liveExtraInfo;
        private final AtomicBoolean isDeleting;

        public LessonViewHolder(@NonNull LayoutCardViewLessonBinding viewHolderBinding) {
            super(viewHolderBinding);
            liveLessonSpannedName = new MutableLiveData<>(new SpannableString(""));
            liveExtraInfo = new MutableLiveData<>("");
            isDeleting = new AtomicBoolean(false);

            // set user menu to invisible
            viewHolderBinding.toolbarLayoutCardViewLesson.getMenu().setGroupVisible(R.id.user_group_menu_card_view_lesson, false);

            // link binding with variables
            viewHolderBinding.setLiveLessonSpannedName(liveLessonSpannedName);
            viewHolderBinding.setLiveExtraInfo(liveExtraInfo);

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

            liveExtraInfo.setValue("1 Day ago");
        }

        private void setListeners(){

            setToolbarListeners();

            // simple click action
            viewHolderBinding.cvLayoutCardViewLesson.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(!Utilities.Adapters.isGoodAdapterPosition(position)){
                        return;
                    }

                    Lesson lesson = getItem(position);
                    if(!CoreUtilities.General.isItemNotNull(lesson)){
                        return;
                    }

                    // Navigate user to the GuestHomeLessonFragment using selected lesson.
                    fragmentCallback.getFragment().goToGuestHomeLessonFragment(lesson);
                }
            });

        }

        private void setToolbarListeners(){
            viewHolderBinding.toolbarLayoutCardViewLesson.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int position = getAdapterPosition();
                    if(!Utilities.Adapters.isGoodAdapterPosition(position)){
                        return true;
                    }

                    Lesson lesson = getItem(position);
                    if(!CoreUtilities.General.isItemNotNull(lesson)){
                        return true;
                    }

                    int id = item.getItemId();
                    if(id == R.id.action_guest_delete_menu_card_view_lesson){
                        // avoid multiple press until operation is finished
                        if(isDeleting.get()){
                            return true;
                        }
                        isDeleting.set(true);
                        ThreadExecutorService.getInstance().execute(() -> onDeletePressed(lesson));
                        return true;
                    }
                    return true;
                }
            });
        }

        private void onDeletePressed(Lesson lesson){
            int wordsNr = GuestWordService.getInstance().getNumberOfWordsForSpecificLesson(lesson.getLessonId());
            int expressionsNr = GuestExpressionService.getInstance().getNumberOfExpressionsForSpecificLesson(lesson.getLessonId());

            if(wordsNr < 0 || expressionsNr < 0){
                fragmentCallback.getFragment().requireActivity().runOnUiThread(() -> {
                    GeneralUtilities.showShortToastMessage(fragmentCallback.getFragment().requireContext(),
                            fragmentCallback.getFragment().getString(R.string.error_deleting_lesson));
                });
                isDeleting.set(false);
                return;
            }

            // delete lesson if has no entries
            if(wordsNr == 0 && expressionsNr == 0){
                GuestLessonService.getInstance().delete(lesson, new DataCallbacks.General() {
                    @Override
                    public void onSuccess() {
                        fragmentCallback.getFragment().requireActivity().runOnUiThread(() -> {
                            GeneralUtilities.showShortToastMessage(fragmentCallback.getFragment().requireContext(),
                                    fragmentCallback.getFragment().getString(R.string.success_deleting_lesson));
                        });
                        isDeleting.set(false);
                    }

                    @Override
                    public void onFailure() {
                        fragmentCallback.getFragment().requireActivity().runOnUiThread(() -> {
                            GeneralUtilities.showShortToastMessage(fragmentCallback.getFragment().requireContext(),
                                    fragmentCallback.getFragment().getString(R.string.error_deleting_lesson));
                        });
                        isDeleting.set(false);
                    }
                });
                return;
            }

            // show an alert dialog in order to block deletion until lesson entries are not deleted
            fragmentCallback.getFragment().requireActivity().runOnUiThread(() -> {
                fragmentCallback.getFragment().deleteLessonAlert(wordsNr, expressionsNr);
                isDeleting.set(false);
            });
        }

    }
}

