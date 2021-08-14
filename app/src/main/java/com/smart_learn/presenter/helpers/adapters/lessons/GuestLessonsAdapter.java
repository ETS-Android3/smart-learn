package com.smart_learn.presenter.helpers.adapters.lessons;


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
import com.smart_learn.core.services.expression.GuestExpressionService;
import com.smart_learn.core.services.lesson.GuestLessonService;
import com.smart_learn.core.services.word.GuestWordService;
import com.smart_learn.core.services.helpers.ThreadExecutorService;
import com.smart_learn.core.helpers.CoreUtilities;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.data.room.entities.Lesson;
import com.smart_learn.databinding.LayoutCardViewLessonBinding;
import com.smart_learn.presenter.helpers.PresenterHelpers;
import com.smart_learn.presenter.helpers.PresenterUtilities;
import com.smart_learn.presenter.helpers.adapters.helpers.BasicListAdapter;
import com.smart_learn.presenter.helpers.adapters.helpers.BasicViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class GuestLessonsAdapter extends BasicListAdapter<Lesson, GuestLessonsAdapter.LessonViewHolder, GuestLessonsAdapter.Callback> implements Filterable, PresenterHelpers.AdapterHelper {

    public GuestLessonsAdapter(@NonNull @NotNull GuestLessonsAdapter.Callback adapterCallback) {
        super(adapterCallback);
    }

    @NonNull
    @Override
    public LessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // set data binding
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        LayoutCardViewLessonBinding viewHolderBinding = DataBindingUtil.inflate(layoutInflater,
                R.layout.layout_card_view_lesson, parent, false);
        viewHolderBinding.setLifecycleOwner(adapterCallback.getFragment());

        return new LessonViewHolder(viewHolderBinding);
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
                filteringValue = CoreUtilities.General.trimAndRemoveAdjacentSpacesAndBreakLines(constraint.toString()).toLowerCase();

                // For filtering mode we search always in all db.
                List<Lesson> allItems = GuestLessonService.getInstance().getAllSampleLesson();
                List<Lesson> filteredItems;

                if (filteringValue.isEmpty()) {
                    isFiltering = false;
                    filteredItems = allItems;
                }
                else {
                    filteredItems = allItems.stream()
                            .filter(it -> CoreUtilities.General
                                            .trimAndRemoveAdjacentSpacesAndBreakLines(it.getName())
                                            .toLowerCase()
                                            .contains(filteringValue))
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

    public final class LessonViewHolder extends BasicViewHolder<Lesson, LayoutCardViewLessonBinding> {

        private final MutableLiveData<SpannableString> liveLessonSpannedName;
        private final MutableLiveData<String> liveExtraInfo;
        private final AtomicBoolean isDeletingActive;

        public LessonViewHolder(@NonNull LayoutCardViewLessonBinding viewHolderBinding) {
            super(viewHolderBinding);
            liveLessonSpannedName = new MutableLiveData<>(new SpannableString(""));
            liveExtraInfo = new MutableLiveData<>("");
            isDeletingActive = new AtomicBoolean(false);

            makeStandardSetup(viewHolderBinding.toolbarLayoutCardViewLesson, viewHolderBinding.cvLayoutCardViewLesson);

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
                String lessonName = CoreUtilities.General.trimAndRemoveAdjacentSpacesAndBreakLines(item.getName());
                liveLessonSpannedName.setValue(PresenterUtilities.Activities.generateSpannedString(
                        CoreUtilities.General.getSubstringIndexes(lessonName.toLowerCase(), filteringValue), lessonName));
            }
            else {
                liveLessonSpannedName.setValue(new SpannableString(item.getName()));
            }

            String extraDescription = adapterCallback.getFragment().getString(R.string.added) + " " +
                    CoreUtilities.General.getFormattedTimeDifferenceFromPastToPresent(item.getBasicInfo().getCreatedAt());
            liveExtraInfo.setValue(extraDescription);
        }

        private void setListeners(){

            if(adapterCallback.showToolbar()){
                setToolbarListeners();
            }

            // simple click action
            viewHolderBinding.cvLayoutCardViewLesson.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(!PresenterUtilities.Adapters.isGoodAdapterPosition(position)){
                        return;
                    }

                    Lesson lesson = getItem(position);
                    if(!CoreUtilities.General.isItemNotNull(lesson)){
                        return;
                    }

                    adapterCallback.onSimpleClick(lesson);
                }
            });

        }

        private void setToolbarListeners(){
            viewHolderBinding.toolbarLayoutCardViewLesson.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int position = getAdapterPosition();
                    if(!PresenterUtilities.Adapters.isGoodAdapterPosition(position)){
                        return true;
                    }

                    Lesson lesson = getItem(position);
                    if(!CoreUtilities.General.isItemNotNull(lesson)){
                        return true;
                    }

                    int id = item.getItemId();
                    if(id == R.id.action_guest_delete_menu_card_view_lesson){
                        // avoid multiple press until operation is finished
                        if(isDeletingActive.get()){
                            return true;
                        }
                        isDeletingActive.set(true);
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
                showMessage(R.string.error_deleting_lesson);
                isDeletingActive.set(false);
                return;
            }

            // delete lesson if has no entries
            if(wordsNr == 0 && expressionsNr == 0){
                GuestLessonService.getInstance().delete(lesson, new DataCallbacks.General() {
                    @Override
                    public void onSuccess() {
                        showMessage(R.string.success_deleting_lesson);
                        isDeletingActive.set(false);
                    }

                    @Override
                    public void onFailure() {
                        showMessage(R.string.error_deleting_lesson);
                        isDeletingActive.set(false);
                    }
                });
                return;
            }

            // show an alert dialog in order to block deletion until lesson entries are not deleted
            adapterCallback.getFragment().requireActivity().runOnUiThread(() -> adapterCallback.onDeleteLessonAlert(wordsNr, expressionsNr));
            isDeletingActive.set(false);
        }
    }

    public interface Callback extends BasicListAdapter.Callback<Lesson> {
        void onDeleteLessonAlert(int wordsNr, int expressionsNr);
    }
}

