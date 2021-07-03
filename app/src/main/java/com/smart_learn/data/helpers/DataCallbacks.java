package com.smart_learn.data.helpers;

import androidx.annotation.Nullable;

/**
 * Used to keep all callbacks for data layer.
 * */
public interface DataCallbacks {

    /**
     * Use to manage insert, update, delete operations on repository.
     * */
    interface InsertUpdateDeleteCallback<T> {
        void onSuccess(@Nullable T value);
        void onFailure(@Nullable T value);
    }

}
