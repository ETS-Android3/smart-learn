package com.smart_learn.presenter.helpers.fragments.tests.schedule.user.standard;

import com.smart_learn.presenter.helpers.fragments.tests.schedule.user.UserBasicScheduledTestsFragment;


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