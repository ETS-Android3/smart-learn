package com.smart_learn.presenter.activities.notebook.user.fragments.expressions.helpers;

import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.smart_learn.R;
import com.smart_learn.core.services.UserExpressionService;
import com.smart_learn.core.utilities.CoreUtilities;
import com.smart_learn.core.utilities.GeneralUtilities;
import com.smart_learn.data.firebase.firestore.entities.ExpressionDocument;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.databinding.LayoutCardViewExpressionBinding;
import com.smart_learn.presenter.activities.notebook.user.fragments.expressions.UserExpressionsFragment;
import com.smart_learn.presenter.helpers.Utilities;
import com.smart_learn.presenter.helpers.adapters.BasicFirestoreRecyclerAdapter;
import com.smart_learn.presenter.helpers.adapters.BasicViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.Getter;

public class ExpressionsAdapter extends BasicFirestoreRecyclerAdapter<ExpressionDocument, ExpressionsAdapter.ExpressionViewHolder> {

    private static final int MAX_FILTER_LINES = 25;
    private static final int MAX_NO_FILTER_LINES = 4;

    private static final int INITIAL_ADAPTER_CAPACITY = 20;
    private static final int LOADING_STEP = 10;

    protected final ExpressionsAdapter.Callback<UserExpressionsFragment> adapterCallback;
    private final MutableLiveData<Boolean> liveIsActionModeActive;

    private final DocumentSnapshot currentLessonSnapshot;
    @Getter
    @NonNull
    @NotNull
    private final ArrayList<DocumentSnapshot> selectedExpressions;

    // if filter is on
    private boolean isFiltering;
    // current filter value if filter is on
    private String filteringValue;

    public ExpressionsAdapter(@NonNull @NotNull DocumentSnapshot currentLessonSnapshot,
                              @NonNull @NotNull ExpressionsAdapter.Callback<UserExpressionsFragment> adapterCallback) {
        super(adapterCallback.getFragment(), getInitialAdapterOptions(currentLessonSnapshot, adapterCallback.getFragment()),
                INITIAL_ADAPTER_CAPACITY, LOADING_STEP);
        this.currentLessonSnapshot = currentLessonSnapshot;
        this.adapterCallback = adapterCallback;

        // set initial values
        this.liveIsActionModeActive = new MutableLiveData<>(false);
        this.selectedExpressions = new ArrayList<>();
        this.isFiltering = false;
        this.filteringValue = "";
    }

    public void setLiveActionMode(boolean value) {
        liveIsActionModeActive.setValue(value);
    }

    @NonNull
    @NotNull
    @Override
    public ExpressionViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        // set data binding
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        LayoutCardViewExpressionBinding viewHolderBinding = DataBindingUtil.inflate(layoutInflater,
                R.layout.layout_card_view_expression, parent, false);
        viewHolderBinding.setLifecycleOwner(adapterCallback.getFragment());

        // set binding variable
        viewHolderBinding.setLiveIsActionModeActive(liveIsActionModeActive);

        return new ExpressionViewHolder(viewHolderBinding);
    }

    @Override
    public void loadMoreData() {
        Query query;
        if(isFiltering){
            query = UserExpressionService.getInstance().getQueryForFilter(currentLessonSnapshot.getId(), currentLoad + loadingStep, filteringValue);
        }
        else{
            query = UserExpressionService.getInstance().getQueryForAllLessonExpressions(currentLessonSnapshot.getId(),currentLoad + loadingStep);
        }
        super.loadData(query, ExpressionDocument.class, adapterCallback.getFragment());
    }

    private static FirestoreRecyclerOptions<ExpressionDocument> getInitialAdapterOptions(@NonNull @NotNull DocumentSnapshot currentLessonSnapshot,
                                                                                         @NonNull @NotNull Fragment fragment) {
        Query query = UserExpressionService.getInstance().getQueryForAllLessonExpressions(currentLessonSnapshot.getId(), INITIAL_ADAPTER_CAPACITY);
        return new FirestoreRecyclerOptions.Builder<ExpressionDocument>()
                .setLifecycleOwner(fragment)
                .setQuery(query, ExpressionDocument.class)
                .build();
    }

    /**
     * Use to set initial query on the adapter.
     *
     * @param fragment Fragment where adapter must be shown.
     * */
    public void setInitialOption(@NonNull @NotNull Fragment fragment){
        filteringValue = "";
        isFiltering = false;
        updateOptions(getInitialAdapterOptions(currentLessonSnapshot, fragment));
    }


    /**
     * Use to set filtering query on the adapter.
     *
     * @param fragment Fragment where adapter must be shown.
     * @param value Value to be search.
     * */
    public void setFilterOption(@NonNull @NotNull Fragment fragment, @NonNull @NotNull String value){
        filteringValue = value.toLowerCase();
        isFiltering = true;
        Query query = UserExpressionService.getInstance().getQueryForFilter(currentLessonSnapshot.getId(), INITIAL_ADAPTER_CAPACITY, filteringValue);
        FirestoreRecyclerOptions<ExpressionDocument> newOptions = new FirestoreRecyclerOptions.Builder<ExpressionDocument>()
                .setLifecycleOwner(fragment)
                .setQuery(query, ExpressionDocument.class)
                .build();

        updateOptions(newOptions);
    }

    public void resetSelectedItems(){
        selectedExpressions.clear();
        adapterCallback.getFragment().showSelectedItems(selectedExpressions.size());
    }

    public final class ExpressionViewHolder extends BasicViewHolder<ExpressionDocument, LayoutCardViewExpressionBinding> {

        private final MutableLiveData<SpannableString> liveSpannedExpression;
        private final MutableLiveData<Boolean> liveIsSelected;
        private final AtomicBoolean isDeleting;

        public ExpressionViewHolder(@NonNull @NotNull LayoutCardViewExpressionBinding viewHolderBinding) {
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
        protected ExpressionDocument getEmptyLiveItemInfo() {
            return new ExpressionDocument();
        }

        @Override
        protected void bind(@NonNull @NotNull ExpressionDocument item, int position){
            if (adapterCallback.getFragment().getActionMode() != null) {
                liveSpannedExpression.setValue(new SpannableString(item.getExpression()));
                liveIsSelected.setValue(viewHolderBinding.cvLayoutCardViewExpression.isChecked());
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

                    // If action mode is on then user can select/deselect items.
                    if(adapterCallback.getFragment().getActionMode() != null) {
                        mark(position);
                        return;
                    }

                    // If action mode is disabled then simple click will navigate user to the
                    // HomeExpressionFragment using selected expression.
                    adapterCallback.getFragment().goToUserHomeExpressionFragment(getSnapshots().getSnapshot(position));
                }
            });

            // long click is used for launching action mode
            viewHolderBinding.cvLayoutCardViewExpression.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(adapterCallback.getFragment().getActionMode() == null) {
                        int position = getAdapterPosition();
                        if(!Utilities.Adapters.isGoodAdapterPosition(position)){
                            return true;
                        }

                        adapterCallback.getFragment().startFragmentActionMode();
                        // by default clicked item is selected
                        mark(position);
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

                    int id = item.getItemId();
                    if(id == R.id.action_delete_menu_card_view_expression){
                        // avoid multiple press until operation is finished
                        if(isDeleting.get()){
                            return true;
                        }
                        isDeleting.set(true);
                        onDeletePressed(getSnapshots().getSnapshot(position));
                        return true;
                    }
                    return true;
                }
            });
        }

        private void onDeletePressed(DocumentSnapshot expressionSnapshot){
            UserExpressionService.getInstance().deleteExpression(currentLessonSnapshot, expressionSnapshot, new DataCallbacks.General() {
                @Override
                public void onSuccess() {
                    adapterCallback.getFragment().requireActivity().runOnUiThread(() -> {
                        GeneralUtilities.showShortToastMessage(adapterCallback.getFragment().requireContext(),
                                adapterCallback.getFragment().getString(R.string.success_deleting_expression));
                    });
                    isDeleting.set(false);
                }

                @Override
                public void onFailure() {
                    adapterCallback.getFragment().requireActivity().runOnUiThread(() -> {
                        GeneralUtilities.showShortToastMessage(adapterCallback.getFragment().requireContext(),
                                adapterCallback.getFragment().getString(R.string.error_deleting_expression));
                    });
                    isDeleting.set(false);
                }
            });
        }

        private void mark(int position){
            String currentDocId = getSnapshots().getSnapshot(position).getId();

            // if is checked, remove item and then uncheck it
            if(viewHolderBinding.cvLayoutCardViewExpression.isChecked()){
                // mark as unchecked
                viewHolderBinding.cvLayoutCardViewExpression.setChecked(false);
                liveIsSelected.setValue(false);

                // and remove checked item from list
                int lim = selectedExpressions.size();
                for(int i = 0; i < lim; i++){
                    if(selectedExpressions.get(i).getId().equals(currentDocId)){
                        selectedExpressions.remove(i);
                        break;
                    }
                }

                adapterCallback.getFragment().showSelectedItems(selectedExpressions.size());
                return;
            }

            // if is unchecked the mark as checked
            viewHolderBinding.cvLayoutCardViewExpression.setChecked(true);
            liveIsSelected.setValue(true);

            // and add item only if does not exists
            boolean exists = false;
            for(DocumentSnapshot item : selectedExpressions){
                if(item.getId().equals(currentDocId)){
                    exists = true;
                    break;
                }
            }

            if(!exists){
                selectedExpressions.add(getSnapshots().getSnapshot(position));
            }

            adapterCallback.getFragment().showSelectedItems(selectedExpressions.size());

        }
    }

    public interface Callback <T> {
        T getFragment();
    }

}