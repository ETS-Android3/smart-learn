package com.smart_learn.presenter.helpers.fragments.tests.schedule.guest.standard;

import com.smart_learn.presenter.helpers.fragments.tests.schedule.guest.GuestBasicScheduledTestsFragment;


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