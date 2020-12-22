package com.licenta.smart_learn.activities.ui.game_a;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.licenta.smart_learn.R;
import com.licenta.smart_learn.utilities.Logs;

import lombok.Getter;
import lombok.Setter;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class GameFragmentAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2};
    private final Context mContext;

    @Getter
    @Setter
    private GameFragment gameFragment;
    @Getter
    @Setter
    private ChatFragment chatFragment;

    public GameFragmentAdapter(Context context, FragmentManager fm) {
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
                fragment = GameFragment.newInstance("GAME..", "game");
                ((GameFragment)fragment).setAdapter(this);
                break;
            case 1:
                fragment = ChatFragment.newInstance("CHAT..", "chat");
                ((ChatFragment)fragment).setAdapter(this);
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
        // Show 2 total pages.
        return 2;
    }

}
