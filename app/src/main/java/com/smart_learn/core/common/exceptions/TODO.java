package com.smart_learn.core.common.exceptions;

public class TODO extends UnsupportedOperationException {
    /** Used to exit from the application with an exception when a method is not implemented.
     * Should be used in order to prevent the call of default methods that provide no implementation
     * in order to avoid logical errors in the program. */
    public TODO(String message) {
        super(message);
    }
}
