package com.smart_learn.presenter.helpers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.AndroidViewModel;

import com.smart_learn.core.exceptions.TODO;
import com.smart_learn.core.utilities.Logs;

/** General utilities */
public interface ActivityGeneralUtilitiesCallback {

    /** Override in order to have a getter for AppCompatActivity instance of current activity. */
    @NonNull
    default AppCompatActivity getActivity() { throw new TODO(Logs.NOT_IMPLEMENTED); }

    /** Override in order to have a getter for ViewModel of current activity. */
    @NonNull
    default AndroidViewModel getViewModel() { throw new TODO(Logs.NOT_IMPLEMENTED); }

    /** Override in order to show a toast message in current activity. */
    default void setToastMessage(@NonNull String message) { throw new TODO(Logs.NOT_IMPLEMENTED); }

    /** Override in order to show a toast message in current activity for a certain length.
     *
     * @param length should be Toast.LENGTH_LONG or Toast.LENGTH_SHORT
     * */
    default void setToastMessage(@NonNull String message, int length) { throw new TODO(Logs.NOT_IMPLEMENTED); }
}
