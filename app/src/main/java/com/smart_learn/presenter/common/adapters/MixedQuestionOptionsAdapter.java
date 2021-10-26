package com.smart_learn.presenter.common.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.smart_learn.R;
import com.smart_learn.databinding.LayoutCardViewMixedQuestionOptionBinding;
import com.smart_learn.presenter.common.helpers.PresenterUtilities;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import lombok.Getter;

public class MixedQuestionOptionsAdapter extends RecyclerView.Adapter<MixedQuestionOptionsAdapter.OptionViewHolder> {

    @Getter
    @NonNull
    @NotNull
    private ArrayList<String> adapterOrderList;
    private final MutableLiveData<Boolean> liveIsMixedLetterTest;

    public MixedQuestionOptionsAdapter() {
        adapterOrderList = new ArrayList<>();
        liveIsMixedLetterTest = new MutableLiveData<>(false);
    }

    @NonNull
    @NotNull
    @Override
    public OptionViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        // set data binding
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        LayoutCardViewMixedQuestionOptionBinding viewHolderBinding = DataBindingUtil.inflate(layoutInflater,
                R.layout.layout_card_view_mixed_question_option, parent, false);
        viewHolderBinding.setLifecycleOwner((LifecycleOwner)parent.getContext());

        // link data binding layout with view holder
        OptionViewHolder viewHolder = new OptionViewHolder(viewHolderBinding);
        viewHolderBinding.setViewHolder(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull OptionViewHolder holder, int position) {
        if(!PresenterUtilities.Adapters.isGoodAdapterPosition(position)){
            return;
        }

        String value = adapterOrderList.get(position);
        holder.bind(value == null ? "" : value);
    }

    @Override
    public int getItemCount() {
        return adapterOrderList.size();
    }


    public void setAdapterOrderList(ArrayList<String> newList){
        if(newList == null){
            newList = new ArrayList<>();
        }
        adapterOrderList = newList;
        notifyDataSetChanged();
    }

    public void setLiveIsMixedLetterTest(boolean value){
        liveIsMixedLetterTest.setValue(value);
    }

    public final class OptionViewHolder extends RecyclerView.ViewHolder {

        private final MutableLiveData<String> liveOptionValue;

        public OptionViewHolder(@NonNull @NotNull LayoutCardViewMixedQuestionOptionBinding viewHolder) {
            super(viewHolder.getRoot());
            liveOptionValue = new MutableLiveData<>();
        }

        public LiveData<String> getLiveOptionValue(){
            return liveOptionValue;
        }

        public LiveData<Boolean> getLiveIsMixedLetterTest(){
            return liveIsMixedLetterTest;
        }

        public String getOptionValue(){
            return liveOptionValue.getValue() == null ? "" : liveOptionValue.getValue();
        }

        protected void bind(String value){
            liveOptionValue.setValue(value);
        }
    }
}
