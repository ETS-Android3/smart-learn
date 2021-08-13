package com.smart_learn.core.helpers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import timber.log.Timber;

// https://stackoverflow.com/questions/38689399/log-method-name-and-line-number-in-timber/38689400#38689400
public class CustomTimberDebugTree extends Timber.DebugTree {
    @Override
    protected @Nullable String createStackElementTag(@NotNull StackTraceElement element) {
        return String.format("[L:%s] [M:%s] [C:%s]",
                element.getLineNumber(),
                element.getMethodName(),
                super.createStackElementTag(element));
    }
}
