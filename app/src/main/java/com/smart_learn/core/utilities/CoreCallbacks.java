package com.smart_learn.core.utilities;

import androidx.annotation.Nullable;


/**
 * Used to keep all callbacks for core layer.
 * */
public interface CoreCallbacks {

    /**
     * Use to manage insert, update, delete operations on services.
     * */
    interface InsertUpdateDeleteCallback<T> {
        void onSuccess(@Nullable T value);
        void onFailure(@Nullable T value);
    }

}
