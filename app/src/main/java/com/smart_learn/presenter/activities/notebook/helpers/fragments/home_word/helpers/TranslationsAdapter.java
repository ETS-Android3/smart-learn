package com.smart_learn.presenter.activities.notebook.helpers.fragments.home_word.helpers;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.smart_learn.R;
import com.smart_learn.core.utilities.CoreUtilities;
import com.smart_learn.core.utilities.GeneralUtilities;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.data.room.entities.helpers.Translation;
import com.smart_learn.databinding.LayoutCardViewTranslationBinding;
import com.smart_learn.presenter.helpers.Utilities;
import com.smart_learn.presenter.helpers.adapters.helpers.BasicViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class TranslationsAdapter extends ListAdapter<Translation, TranslationsAdapter.TranslationViewHolder> {

    private final TranslationsAdapter.Callback callback;

    public TranslationsAdapter(@NonNull TranslationsAdapter.Callback callback) {
        super(new DiffUtil.ItemCallback<Translation>(){
            @Override
            public boolean areItemsTheSame(@NonNull Translation oldItem, @NonNull Translation newItem) {
                return oldItem.areItemsTheSame(newItem);
            }
            @Override
            public boolean areContentsTheSame(@NonNull Translation oldItem, @NonNull Translation newItem) {
                return oldItem.areContentsTheSame(newItem);
            }
        });

        this.callback = callback;
    }

    public void setItems(List<Translation> items) {
        // TODO: when you link this adapter with Room or Firestore, modify this
        submitList(Translation.makeDeepCopy(items));
    }

    @NonNull
    @Override
    public TranslationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // set data binding
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        LayoutCardViewTranslationBinding viewHolderBinding = DataBindingUtil.inflate(layoutInflater,
                R.layout.layout_card_view_translation, parent, false);
        viewHolderBinding.setLifecycleOwner((LifecycleOwner) parent.getContext());

        // link data binding layout with view holder
        TranslationViewHolder viewHolder = new TranslationViewHolder(viewHolderBinding);
        viewHolderBinding.setViewHolder(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TranslationViewHolder holder, int position) {
        if(!Utilities.Adapters.isGoodAdapterPosition(position)){
            return;
        }

        Translation translation = getItem(position);
        if(!CoreUtilities.General.isItemNotNull(translation)){
            return;
        }

        holder.bind(translation, position);
    }


    public final class TranslationViewHolder extends BasicViewHolder<Translation, LayoutCardViewTranslationBinding> {

        private final AtomicBoolean isDeleting;

        public TranslationViewHolder(@NonNull LayoutCardViewTranslationBinding viewHolderBinding) {
            super(viewHolderBinding);
            isDeleting = new AtomicBoolean(false);

            // for expressions phonetic will not be shown
            if(callback.isForExpression()){
                viewHolderBinding.tvPhoneticLayoutCardViewTranslation.setVisibility(View.GONE);
            }

            if(!callback.isOwner()){
                viewHolderBinding.toolbarLayoutCardViewTranslation.setVisibility(View.GONE);
            }

            setListeners();
        }

        @Override
        protected Translation getEmptyLiveItemInfo() {
            return Translation.generateEmptyObject();
        }

        @Override
        protected void bind(@NonNull @NotNull Translation item, int position) {
           liveItemInfo.setValue(item);
        }

        private void setListeners(){

            setToolbarListeners();

            // simple click action
            viewHolderBinding.cvLayoutCardViewTranslation.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(!Utilities.Adapters.isGoodAdapterPosition(position)){
                        return;
                    }

                    Translation translation = getItem(position);
                    if(!CoreUtilities.General.isItemNotNull(translation)){
                        return;
                    }

                    // open dialog to see translation
                    callback.onSimpleClick(translation);
                }
            });
        }

        private void setToolbarListeners(){
            viewHolderBinding.toolbarLayoutCardViewTranslation.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    // only for translation owner toolbar is activated
                    if(!callback.isOwner()){
                        return true;
                    }

                    int position = getAdapterPosition();
                    if(!Utilities.Adapters.isGoodAdapterPosition(position)){
                        return true;
                    }

                    Translation translation = getItem(position);
                    if(!CoreUtilities.General.isItemNotNull(translation)){
                        return true;
                    }

                    int id = item.getItemId();
                    if(id == R.id.action_delete_menu_card_view_translation){
                        // avoid multiple press until operation is finished
                        if(isDeleting.get()){
                            return true;
                        }
                        isDeleting.set(true);
                        onDeletePressed(translation);
                        return true;
                    }
                    return true;
                }
            });
        }

        private void onDeletePressed(Translation translation){
            callback.onDelete(translation, new DataCallbacks.General() {
                @Override
                public void onSuccess() {
                    callback.getActivity().runOnUiThread(() -> {
                        GeneralUtilities.showShortToastMessage(callback.getActivity(),
                                callback.getActivity().getString(R.string.success_deleting_translation));
                    });
                    isDeleting.set(false);
                }
                @Override
                public void onFailure() {
                    callback.getActivity().runOnUiThread(() -> {
                        GeneralUtilities.showShortToastMessage(callback.getActivity(),
                                callback.getActivity().getString(R.string.error_deleting_translation));
                    });
                    isDeleting.set(false);
                }
            });
        }
    }

    public interface Callback {
        boolean isForExpression();
        AppCompatActivity getActivity();
        void onSimpleClick(@NonNull @NotNull Translation translation);
        void onDelete(Translation translation, @NonNull @NotNull DataCallbacks.General callback);
        boolean isOwner();
    }
}

