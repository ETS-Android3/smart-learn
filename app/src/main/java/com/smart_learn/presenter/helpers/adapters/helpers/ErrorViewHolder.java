package com.smart_learn.presenter.helpers.adapters.helpers;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smart_learn.databinding.LayoutCardViewErrorBinding;

import org.jetbrains.annotations.NotNull;

public class ErrorViewHolder extends RecyclerView.ViewHolder {

    protected final LayoutCardViewErrorBinding viewHolderBinding;

    public ErrorViewHolder(@NonNull @NotNull LayoutCardViewErrorBinding viewHolderBinding) {
        super(viewHolderBinding.getRoot());
        this.viewHolderBinding = viewHolderBinding;
    }

    public void bind(String error){
        viewHolderBinding.setError(error);
    }
}
