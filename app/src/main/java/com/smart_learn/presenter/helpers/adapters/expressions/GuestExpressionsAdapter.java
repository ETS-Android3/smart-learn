package com.smart_learn.presenter.helpers.adapters.expressions;

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
import com.smart_learn.core.helpers.CoreUtilities;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.data.room.entities.Expression;
import com.smart_learn.databinding.LayoutCardViewExpressionBinding;
import com.smart_learn.presenter.helpers.PresenterUtilities;
import com.smart_learn.presenter.helpers.adapters.helpers.BasicListAdapter;
import com.smart_learn.presenter.helpers.adapters.helpers.BasicViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;


public class GuestExpressionsAdapter extends BasicListAdapter<Expression, GuestExpressionsAdapter.ExpressionViewHolder, GuestExpressionsAdapter.Callback> implements Filterable {

    private static final int MAX_FILTER_LINES = 25;
    private static final int MAX_NO_FILTER_LINES = 4;

    private final int currentLessonId;

    public GuestExpressionsAdapter(int currentLessonId, @NonNull @NotNull GuestExpressionsAdapter.Callback adapterCallback) {
        super(adapterCallback);
        this.currentLessonId = currentLessonId;
    }


    @NonNull
    @Override
    public ExpressionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // set data binding
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        LayoutCardViewExpressionBinding viewHolderBinding = DataBindingUtil.inflate(layoutInflater,
                R.layout.layout_card_view_expression, parent, false);
        viewHolderBinding.setLifecycleOwner(adapterCallback.getFragment());

        // set binding variable
        viewHolderBinding.setLiveIsActionModeActive(getLiveIsSelectionModeActive());

        return new ExpressionViewHolder(viewHolderBinding);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            /** Run on background thread. Background thread is created automatically. */
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                isFiltering = true;
                filteringValue = CoreUtilities.General.trimAndRemoveAdjacentSpacesAndBreakLines(constraint.toString()).toLowerCase();

                // For filtering mode we search always in all db.
                List<Expression> allItems = GuestExpressionService.getInstance()
                        .getCurrentLessonSampleExpressions(currentLessonId);
                List<Expression> filteredItems;

                if (filteringValue.isEmpty()) {
                    isFiltering = false;
                    filteredItems = allItems;
                }
                else {
                    filteredItems = allItems.stream()
                            .filter(it -> CoreUtilities.General
                                    .trimAndRemoveAdjacentSpacesAndBreakLines(it.getExpression())
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
                submitList((List<Expression>) results.values);

                // this call is made in order to show a colorful spanned text
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public void loadMoreData() {
        // no action needed here
    }


    public final class ExpressionViewHolder extends BasicViewHolder<Expression, LayoutCardViewExpressionBinding> {

        private final MutableLiveData<SpannableString> liveSpannedExpression;
        private final MutableLiveData<String> liveDateDifferenceDescription;
        private final MutableLiveData<Boolean> liveIsSelected;
        private final AtomicBoolean isDeletingActive;

        public ExpressionViewHolder(@NonNull LayoutCardViewExpressionBinding viewHolderBinding) {
            super(viewHolderBinding);
            liveSpannedExpression = new MutableLiveData<>(new SpannableString(""));
            liveDateDifferenceDescription = new MutableLiveData<>("");
            liveIsSelected = new MutableLiveData<>(false);
            isDeletingActive = new AtomicBoolean(false);

            makeStandardSetup(viewHolderBinding.toolbarLayoutCardViewExpression, viewHolderBinding.cvLayoutCardViewExpression);

            // link binding with variables
            viewHolderBinding.setLiveSpannedExpression(liveSpannedExpression);
            viewHolderBinding.setLiveDateDifferenceDescription(liveDateDifferenceDescription);
            viewHolderBinding.setLiveIsSelected(liveIsSelected);
            // guest user is always the owner
            viewHolderBinding.setLiveIsOwner(new MutableLiveData<>(true));

            setListeners();
        }

        @Override
        protected Expression getEmptyLiveItemInfo() {
            return Expression.generateEmptyObject();
        }

        @Override
        protected void bind(@NonNull @NotNull Expression item, int position) {
            String dateDifferenceDescription = adapterCallback.getFragment().getString(R.string.expression_added) + " " +
                    CoreUtilities.General.getFormattedTimeDifferenceFromPastToPresent(item.getBasicInfo().getCreatedAt());
            liveDateDifferenceDescription.setValue(dateDifferenceDescription);

            if(isFiltering){
                String expression = CoreUtilities.General.trimAndRemoveAdjacentSpacesAndBreakLines(item.getExpression());
                liveSpannedExpression.setValue(PresenterUtilities.Activities.generateSpannedString(
                        CoreUtilities.General.getSubstringIndexes(expression.toLowerCase(), filteringValue), expression));
                // TODO: try to show more lines only if value is on the hidden lines
                viewHolderBinding.tvSpannedExpressionLayoutCardViewExpression.setMaxLines(MAX_FILTER_LINES);
            }
            else {
                liveSpannedExpression.setValue(new SpannableString(item.getExpression()));
                viewHolderBinding.tvSpannedExpressionLayoutCardViewExpression.setMaxLines(MAX_NO_FILTER_LINES);
            }

            if (isSelectionModeActive()) {
                boolean isSelected = isSelected(item);
                liveIsSelected.setValue(isSelected);
                viewHolderBinding.cvLayoutCardViewExpression.setChecked(isSelected);
                return;
            }

            liveIsSelected.setValue(false);
            viewHolderBinding.cvLayoutCardViewExpression.setChecked(false);
        }


        private void setListeners(){

            if(adapterCallback.showToolbar()){
                setToolbarListener();
            }

            // simple click action
            viewHolderBinding.cvLayoutCardViewExpression.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(!PresenterUtilities.Adapters.isGoodAdapterPosition(position)){
                        return;
                    }

                    Expression expression = getItem(position);
                    if(!CoreUtilities.General.isItemNotNull(expression)){
                        return;
                    }

                    if(isSelectionModeActive()){
                        if(adapterCallback.isSelectedItemValid(expression)){
                            markItem(position, expression);
                        }
                        return;
                    }

                    adapterCallback.onSimpleClick(expression);
                }
            });

            // long click is used for launching action mode
            viewHolderBinding.cvLayoutCardViewExpression.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // if filtering is active nothing will happen
                    if(isFiltering){
                        return true;
                    }

                    if(!isSelectionModeActive()) {
                        int position = getAdapterPosition();
                        if(!PresenterUtilities.Adapters.isGoodAdapterPosition(position)){
                            return true;
                        }

                        Expression expression = getItem(position);
                        if(!CoreUtilities.General.isItemNotNull(expression)){
                            return true;
                        }

                        adapterCallback.onLongClick(expression);
                        // by default clicked item is selected
                        markItem(position, expression);
                    }

                    return true;
                }
            });

        }

        private void setToolbarListener(){
            viewHolderBinding.toolbarLayoutCardViewExpression.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int position = getAdapterPosition();
                    if(!PresenterUtilities.Adapters.isGoodAdapterPosition(position)){
                        return true;
                    }

                    Expression expression = getItem(position);
                    if(!CoreUtilities.General.isItemNotNull(expression)){
                        return true;
                    }

                    int id = item.getItemId();
                    if(id == R.id.action_delete_menu_card_view_expression){
                        if(isDeletingActive.get()){
                            return true;
                        }
                        isDeletingActive.set(true);
                        onDeletePressed(expression);
                        return true;
                    }
                    return true;
                }
            });
        }

        private void onDeletePressed(Expression expression){
            GuestExpressionService.getInstance().delete(expression, new DataCallbacks.General() {
                @Override
                public void onSuccess() {
                    showMessage(R.string.success_deleting_expression);
                    isDeletingActive.set(false);
                }

                @Override
                public void onFailure() {
                    showMessage(R.string.error_deleting_expression);
                    isDeletingActive.set(false);
                }
            });
        }

    }

    public interface Callback extends BasicListAdapter.Callback<Expression> {
        boolean isSelectedItemValid(@NonNull @NotNull Expression item);
    }

}
