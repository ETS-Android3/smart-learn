package com.smart_learn.presenter.user.fragments.common.tests.schedule.standard;

import com.smart_learn.presenter.user.fragments.common.tests.schedule.UserBasicScheduledTestsFragment;


public abstract class UserStandardScheduledTestsFragment <VM extends UserStandardScheduledTestsViewModel> extends UserBasicScheduledTestsFragment<VM> {

    @Override
    protected boolean showFloatingActionButton() {
        return true;
    }

    @Override
    protected boolean onAdapterShowOptionsToolbar() {
        return true;
    }
}