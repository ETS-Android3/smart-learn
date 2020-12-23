package com.smart_learn.activities.ui.test;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.smart_learn.R;
import com.smart_learn.utilities.Logs;

import lombok.Getter;
import lombok.Setter;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class TestFragmentAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2, R.string.tab_text_3};
    private final Context mContext;

    @Getter
    @Setter
    private TestFragment testFragment;
    @Getter
    @Setter
    private ChatFragment chatFragment;
    @Getter
    @Setter
    private ParticipantsFragment participantsFragment;

    public TestFragmentAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Log.i(Logs.INFO,Logs.FUNCTION + "[getItem] position is [" + position + "]");
        // getItem is called to instantiate the fragment for the given page.
        Fragment fragment = null;
        switch (position){
            case 0:
                fragment = TestFragment.newInstance("TEST..", "test");
                testFragment = (TestFragment)fragment;
                testFragment.setAdapter(this);
                Log.i(Logs.INFO,"create test fragment");
                break;
            case 1:
                fragment = ChatFragment.newInstance("CHAT..", "chat");
                chatFragment = (ChatFragment)fragment;
                chatFragment.setAdapter(this);
                Log.i(Logs.INFO,"create chat fragment");
                break;
            case 2:
                fragment = ParticipantsFragment.newInstance("PARTICIPANT..", "participant");
                participantsFragment = (ParticipantsFragment)fragment;
                participantsFragment.setAdapter(this);
                Log.i(Logs.INFO,"create participants fragment");
                break;
            default:
                Log.e(Logs.UNEXPECTED_ERROR,Logs.FUNCTION + "[getItem] position [" + position + "] is not good");
        }

        assert fragment != null;
        return fragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }

}
