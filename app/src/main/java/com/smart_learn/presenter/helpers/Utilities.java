package com.smart_learn.presenter.helpers;

import androidx.navigation.NavOptions;

import com.smart_learn.R;

public abstract class Utilities {

    /**
     * Use this function in order to add custom animations for actions from NavigationGraph.
     *
     * If you do not use this function you should set this animations manually for every action from
     * the NavigationGraph.
     * */
    public static NavOptions getNavOptions() {
        // https://stackoverflow.com/questions/50482095/how-do-i-define-default-animations-for-navigation-actions/52413868#52413868
        return new NavOptions.Builder()
                .setEnterAnim(R.anim.slide_in_right)
                .setExitAnim(R.anim.slide_out_left)
                .setPopEnterAnim(R.anim.slide_in_left)
                .setPopExitAnim(R.anim.slide_out_right)
                .build();
    }
}
