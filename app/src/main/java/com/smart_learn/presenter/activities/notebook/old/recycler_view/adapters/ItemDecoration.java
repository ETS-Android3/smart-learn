package com.smart_learn.presenter.activities.notebook.old.recycler_view.adapters;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/** helper for decoration */
public class ItemDecoration extends RecyclerView.ItemDecoration {

    private final int padding;

    public ItemDecoration(int padding) {
        this.padding = padding;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent,
                               @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.top = padding;
    }
}
