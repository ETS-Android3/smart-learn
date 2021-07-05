package com.smart_learn.data.helpers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

/**
 * Used to keep all callbacks for data layer.
 * */
public interface DataCallbacks {

    /**
     * Use to manage insert operations on repository.
     * */
    interface InsertCallback<T> {
        void onSuccess(@NonNull @NotNull T value);
        void onFailure(@Nullable T value);
    }

    /**
     * Use to manage update operations on repository.
     * */
    interface UpdateCallback<T> {
        void onSuccess(@NonNull @NotNull T value);
        void onFailure(@NonNull @NotNull T value);
    }

    /**
     * Use to manage delete operations on repository.
     * */
    interface DeleteCallback<T> {
        void onSuccess(@Nullable T value);
        void onFailure(@NonNull @NotNull T value);
    }

}
