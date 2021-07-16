package com.smart_learn.presenter.activities.notebook.guest.fragments.expressions.helpers;

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
import com.smart_learn.core.utilities.CoreUtilities;
import com.smart_learn.core.utilities.GeneralUtilities;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.data.room.entities.Expression;
import com.smart_learn.data.room.entities.helpers.BasicInfo;
import com.smart_learn.databinding.LayoutCardViewExpressionBinding;
import com.smart_learn.presenter.activities.notebook.guest.fragments.expressions.GuestExpressionsFragment;
import com.smart_learn.presenter.helpers.Callbacks;
import com.smart_learn.presenter.helpers.PresenterHelpers;
import com.smart_learn.presenter.helpers.Utilities;
import com.smart_learn.presenter.helpers.adapters.BasicViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;


public class ExpressionsAdapter extends ListAdapter<Expression, ExpressionsAdapter.ExpressionViewHolder> implements Filterable, PresenterHelpers.AdapterHelper {

    private static final int MAX_FILTER_LINES = 25;
    private static final int MAX_NO_FILTER_LINES = 4;

    private final MutableLiveData<Boolean> liveIsActionModeActive;
    private boolean isFiltering;
    private String filteringValue;

    private final Callbacks.FragmentGeneralCallback<GuestExpressionsFragment> fragmentCallback;

    public ExpressionsAdapter(@NonNull Callbacks.FragmentGeneralCallback<GuestExpressionsFragment> fragmentCallback) {
        super(new DiffUtil.ItemCallback<Expression>(){
            @Override
            public boolean areItemsTheSame(@NonNull Expression oldItem, @NonNull Expression newItem) {
                return oldItem.areItemsTheSame(newItem);
            }
            @Override
            public boolean areContentsTheSame(@NonNull Expression oldItem, @NonNull Expression newItem) {
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

    public void setItems(List<Expression> items) {
        submitList(items);
    }

    @NonNull
    @Override
    public ExpressionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // set data binding
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        LayoutCardViewExpressionBinding viewHolderBinding = DataBindingUtil.inflate(layoutInflater,
                R.layout.layout_card_view_expression, parent, false);
        viewHolderBinding.setLifecycleOwner(fragmentCallback.getFragment());

        // set binding variable
        viewHolderBinding.setLiveIsActionModeActive(liveIsActionModeActive);

        return new ExpressionViewHolder(viewHolderBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpressionViewHolder holder, int position) {
        if(!Utilities.Adapters.isGoodAdapterPosition(position)){
            return;
        }

        Expression expression = getItem(position);
        if(!CoreUtilities.General.isItemNotNull(expression)){
            return;
        }

        holder.bind(expression, position);
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
                List<Expression> allItems = GuestExpressionService.getInstance()
                        .getCurrentLessonSampleExpressions(fragmentCallback.getFragment().getViewModel().getCurrentLessonId());
                List<Expression> filteredItems;

                if (filteringValue.isEmpty()) {
                    isFiltering = false;
                    filteredItems = allItems;
                }
                else {
                    filteredItems = allItems.stream()
                            .filter(it -> it.getExpression().toLowerCase().contains(filteringValue))
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
        private final MutableLiveData<Boolean> liveIsSelected;
        private final AtomicBoolean isDeleting;

        public ExpressionViewHolder(@NonNull LayoutCardViewExpressionBinding viewHolderBinding) {
            super(viewHolderBinding);
            liveSpannedExpression = new MutableLiveData<>(new SpannableString(""));
            liveIsSelected = new MutableLiveData<>(false);
            isDeleting = new AtomicBoolean(false);

            // link binding with variables
            viewHolderBinding.setLiveSpannedExpression(liveSpannedExpression);
            viewHolderBinding.setLiveIsSelected(liveIsSelected);

            setListeners();
        }

        @Override
        protected Expression getEmptyLiveItemInfo() {
            return Expression.generateEmptyObject();
        }

        @Override
        protected void bind(@NonNull @NotNull Expression item, int position) {
            if (fragmentCallback.getFragment().getActionMode() != null) {
                liveSpannedExpression.setValue(new SpannableString(item.getExpression()));
                liveIsSelected.setValue(item.isSelected());
                viewHolderBinding.cvLayoutCardViewExpression.setChecked(item.isSelected());
                return;
            }

            if(isFiltering){
                liveSpannedExpression.setValue(Utilities.Activities.generateSpannedString(
                        CoreUtilities.General.getSubstringIndexes(item.getExpression().toLowerCase(), filteringValue), item.getExpression()));
                // TODO: try to show more lines only if value is on the hidden lines
                viewHolderBinding.tvSpannedExpressionLayoutCardViewExpression.setMaxLines(MAX_FILTER_LINES);
            }
            else {
                liveSpannedExpression.setValue(new SpannableString(item.getExpression()));
                viewHolderBinding.tvSpannedExpressionLayoutCardViewExpression.setMaxLines(MAX_NO_FILTER_LINES);
            }

            liveIsSelected.setValue(false);
            viewHolderBinding.cvLayoutCardViewExpression.setChecked(false);
        }


        private void setListeners(){

            setToolbarListener();

            // simple click action
            viewHolderBinding.cvLayoutCardViewExpression.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(!Utilities.Adapters.isGoodAdapterPosition(position)){
                        return;
                    }

                    Expression expression = getItem(position);
                    if(!CoreUtilities.General.isItemNotNull(expression)){
                        return;
                    }

                    // If action mode is on then user can select/deselect items.
                    if(fragmentCallback.getFragment().getActionMode() != null) {
                        markItem(expression,!expression.isSelected());
                        return;
                    }

                    // If action mode is disabled then simple click will navigate user to the
                    // HomeExpressionFragment using selected expression.
                    fragmentCallback.getFragment().goToGuestHomeExpressionFragment(expression);
                }
            });

            // long click is used for launching action mode
            viewHolderBinding.cvLayoutCardViewExpression.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(fragmentCallback.getFragment().getActionMode() == null) {
                        int position = getAdapterPosition();
                        if(!Utilities.Adapters.isGoodAdapterPosition(position)){
                            return true;
                        }

                        Expression expression = getItem(position);
                        if(!CoreUtilities.General.isItemNotNull(expression)){
                            return true;
                        }

                        fragmentCallback.getFragment().startFragmentActionMode();
                        // by default clicked item is selected
                        markItem(expression,true);
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
                    if(!Utilities.Adapters.isGoodAdapterPosition(position)){
                        return true;
                    }

                    Expression expression = getItem(position);
                    if(!CoreUtilities.General.isItemNotNull(expression)){
                        return true;
                    }

                    int id = item.getItemId();
                    if(id == R.id.action_delete_menu_card_view_expression){
                        // avoid multiple press until operation is finished
                        if(isDeleting.get()){
                            return true;
                        }
                        isDeleting.set(true);
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
                    fragmentCallback.getFragment().requireActivity().runOnUiThread(() -> {
                        GeneralUtilities.showShortToastMessage(fragmentCallback.getFragment().requireContext(),
                                fragmentCallback.getFragment().getString(R.string.success_deleting_expression));
                    });
                    isDeleting.set(false);
                }

                @Override
                public void onFailure() {
                    fragmentCallback.getFragment().requireActivity().runOnUiThread(() -> {
                        GeneralUtilities.showShortToastMessage(fragmentCallback.getFragment().requireContext(),
                                fragmentCallback.getFragment().getString(R.string.error_deleting_expression));
                    });
                    isDeleting.set(false);
                }
            });
        }

        private void markItem(Expression expression, boolean isSelected) {
            // TODO: make a specific method for deep copy on Basic info entity
            BasicInfo basicInfo = new BasicInfo(expression.getBasicInfo().getCreatedAt());
            basicInfo.setModifiedAt(expression.getBasicInfo().getModifiedAt());

            // TODO: make a specific method for deep copy on Expression entity
            Expression tmp = new Expression(
                    expression.getNotes(),
                    expression.isSelected(),
                    basicInfo,
                    expression.getFkLessonId(),
                    expression.isFavourite(),
                    expression.getLanguage(),
                    new ArrayList<>(expression.getTranslations()),
                    expression.getExpression()
            );

            tmp.setExpressionId(expression.getExpressionId());
            tmp.setSelected(isSelected);
            GuestExpressionService.getInstance().update(tmp, null);
        }
    }

}
