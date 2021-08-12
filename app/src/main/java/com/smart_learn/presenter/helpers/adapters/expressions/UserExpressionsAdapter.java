package com.smart_learn.presenter.helpers.adapters.expressions;

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
import com.smart_learn.core.services.UserService;
import com.smart_learn.core.utilities.CoreUtilities;
import com.smart_learn.data.firebase.firestore.entities.ExpressionDocument;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.databinding.LayoutCardViewExpressionBinding;
import com.smart_learn.presenter.helpers.Utilities;
import com.smart_learn.presenter.helpers.adapters.helpers.BasicFirestoreRecyclerAdapter;
import com.smart_learn.presenter.helpers.adapters.helpers.BasicViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;

public class UserExpressionsAdapter extends BasicFirestoreRecyclerAdapter<ExpressionDocument, UserExpressionsAdapter.ExpressionViewHolder, UserExpressionsAdapter.Callback> {

    private static final int MAX_FILTER_LINES = 25;
    private static final int MAX_NO_FILTER_LINES = 4;

    private static final int INITIAL_ADAPTER_CAPACITY = 20;
    private static final int LOADING_STEP = 10;

    private final DocumentSnapshot currentLessonSnapshot;

    public UserExpressionsAdapter(@NonNull @NotNull DocumentSnapshot currentLessonSnapshot,
                                  @NonNull @NotNull UserExpressionsAdapter.Callback adapterCallback) {
        super(adapterCallback, getInitialAdapterOptions(currentLessonSnapshot, adapterCallback.isSharedLessonSelected(), adapterCallback.getFragment()),
                INITIAL_ADAPTER_CAPACITY, LOADING_STEP);
        this.currentLessonSnapshot = currentLessonSnapshot;
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
        viewHolderBinding.setLiveIsActionModeActive(getLiveIsSelectionModeActive());

        return new ExpressionViewHolder(viewHolderBinding);
    }

    @Override
    public void loadMoreData() {
        Query query;
        if(isFiltering){
            query = UserExpressionService.getInstance().getQueryForFilter(currentLessonSnapshot.getId(), currentLoad + loadingStep,
                    adapterCallback.isSharedLessonSelected(), filteringValue);
        }
        else{
            query = UserExpressionService.getInstance().getQueryForAllLessonExpressions(currentLessonSnapshot.getId(),
                    currentLoad + loadingStep, adapterCallback.isSharedLessonSelected());
        }
        super.loadData(query, ExpressionDocument.class, adapterCallback.getFragment());
    }

    private static FirestoreRecyclerOptions<ExpressionDocument> getInitialAdapterOptions(@NonNull @NotNull DocumentSnapshot currentLessonSnapshot,
                                                                                         boolean isSharedLessonSelected,
                                                                                         @NonNull @NotNull Fragment fragment) {
        Query query = UserExpressionService.getInstance().getQueryForAllLessonExpressions(currentLessonSnapshot.getId(), INITIAL_ADAPTER_CAPACITY, isSharedLessonSelected);
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
        updateOptions(getInitialAdapterOptions(currentLessonSnapshot, adapterCallback.isSharedLessonSelected(), fragment));
        // Update values here in order to avoid to remove selected items if selection mode was active
        // while filtering.
        filteringValue = "";
        isFiltering = false;
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
        Query query = UserExpressionService.getInstance().getQueryForFilter(currentLessonSnapshot.getId(), INITIAL_ADAPTER_CAPACITY,
                adapterCallback.isSharedLessonSelected(), filteringValue);
        FirestoreRecyclerOptions<ExpressionDocument> newOptions = new FirestoreRecyclerOptions.Builder<ExpressionDocument>()
                .setLifecycleOwner(fragment)
                .setQuery(query, ExpressionDocument.class)
                .build();

        updateOptions(newOptions);
    }

    public final class ExpressionViewHolder extends BasicViewHolder<ExpressionDocument, LayoutCardViewExpressionBinding> {

        private final MutableLiveData<SpannableString> liveSpannedExpression;
        private final MutableLiveData<Boolean> liveIsSelected;
        private final MutableLiveData<Boolean> liveIsOwner;
        private final AtomicBoolean isDeletingActive;

        public ExpressionViewHolder(@NonNull @NotNull LayoutCardViewExpressionBinding viewHolderBinding) {
            super(viewHolderBinding);
            liveSpannedExpression = new MutableLiveData<>(new SpannableString(""));
            liveIsSelected = new MutableLiveData<>(false);
            liveIsOwner = new MutableLiveData<>(false);
            isDeletingActive = new AtomicBoolean(false);

            makeStandardSetup(viewHolderBinding.toolbarLayoutCardViewExpression, viewHolderBinding.cvLayoutCardViewExpression);

            // link binding with variables
            viewHolderBinding.setLiveSpannedExpression(liveSpannedExpression);
            viewHolderBinding.setLiveIsSelected(liveIsSelected);
            viewHolderBinding.setLiveIsOwner(liveIsOwner);

            setListeners();
        }


        @Override
        protected ExpressionDocument getEmptyLiveItemInfo() {
            return new ExpressionDocument();
        }

        @Override
        protected void bind(@NonNull @NotNull ExpressionDocument item, int position){
            liveIsOwner.setValue(item.getDocumentMetadata().getOwner().equals(UserService.getInstance().getUserUid()));

            if (isSelectionModeActive()) {
                liveSpannedExpression.setValue(new SpannableString(item.getExpression()));
                boolean isSelected = isSelected(getSnapshots().getSnapshot(position));
                liveIsSelected.setValue(isSelected);
                viewHolderBinding.cvLayoutCardViewExpression.setChecked(isSelected);
                return;
            }

            // selection mode is not active so items must be unchecked
            liveIsSelected.setValue(false);
            viewHolderBinding.cvLayoutCardViewExpression.setChecked(false);

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

        }

        private void setListeners(){

            if(adapterCallback.showToolbar()){
                setToolbarListener();
            }

            viewHolderBinding.cvLayoutCardViewExpression.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(!Utilities.Adapters.isGoodAdapterPosition(position)){
                        return;
                    }

                    if(isSelectionModeActive()){
                        if(adapterCallback.isSelectedItemValid(getSnapshots().getSnapshot(position))){
                            markItem(position, getSnapshots().getSnapshot(position));
                        }
                        return;
                    }

                    adapterCallback.onSimpleClick(getSnapshots().getSnapshot(position));
                }
            });

            viewHolderBinding.cvLayoutCardViewExpression.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // selection mode is available only for non-shared lessons
                    if(adapterCallback.isSharedLessonSelected()){
                        return true;
                    }

                    // if filtering is active nothing will happen
                    if(isFiltering){
                        return true;
                    }

                    if(!isSelectionModeActive()) {
                        int position = getAdapterPosition();
                        if(!Utilities.Adapters.isGoodAdapterPosition(position)){
                            return true;
                        }

                        adapterCallback.onLongClick(getSnapshots().getSnapshot(position));
                        // by default clicked item is selected
                        markItem(position, getSnapshots().getSnapshot(position));
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
                        // only owner can delete his word
                        if(liveIsOwner.getValue() == null || !liveIsOwner.getValue()){
                            return true;
                        }

                        if(isDeletingActive.get()){
                            return true;
                        }
                        isDeletingActive.set(true);
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

    public interface Callback extends BasicFirestoreRecyclerAdapter.Callback {
        boolean isSharedLessonSelected();
        boolean isSelectedItemValid(@NonNull @NotNull DocumentSnapshot item);
    }

}