package com.smart_learn.presenter.guest.fragments.common.tests.schedule.standard;

import com.smart_learn.presenter.guest.fragments.common.tests.schedule.GuestBasicScheduledTestsFragment;


public abstract class GuestStandardScheduledTestsFragment <VM extends GuestStandardScheduledTestsViewModel> extends GuestBasicScheduledTestsFragment<VM> {

    @Override
    protected boolean showFloatingActionButton() {
        return true;
    }

    @Override
    protected boolean onAdapterShowOptionsToolbar() {
        return true;
    }
}