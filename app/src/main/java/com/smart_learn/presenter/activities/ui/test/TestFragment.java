package com.smart_learn.presenter.activities.ui.test;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.smart_learn.R;
import com.smart_learn.config.CurrentConfig;
import com.smart_learn.remote.test.RemotePlay;
import com.smart_learn.utilities.Logs;

import lombok.Getter;
import lombok.Setter;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TestFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TestFragmentAdapter testFragmentAdapter;

    @Getter
    @Setter
    private int unseenMoves = 0;
    @Getter
    @Setter
    private boolean unselected = false;

    public TestFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TestFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TestFragment newInstance(String param1, String param2) {
        TestFragment fragment = new TestFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public void setAdapter(TestFragmentAdapter adapter){
        testFragmentAdapter = adapter;
    }

    public void registerMove(){
        CurrentConfig.getCurrentConfigInstance().currentActivity.runOnUiThread(() -> {

            TabLayout.Tab tab = ((RemotePlay)CurrentConfig.getCurrentConfigInstance().currentActivity)
                    .getTab(0);

            // if current fragment is not visible mark move qas unseen
            if (unselected) {
                unseenMoves++;
                tab.setText(testFragmentAdapter.getPageTitle(0) + " (" + unseenMoves + ")");
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        testFragmentAdapter.setTestFragment(this);
        Log.i(Logs.INFO,Logs.FUNCTION + "[onCreate] create TEST fragment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i(Logs.INFO,Logs.FUNCTION + "[onCreateView] create TEST view fragment");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_test, container, false);

        ((RemotePlay) CurrentConfig.getCurrentConfigInstance().currentActivity).initLayoutElements(view);

        return view;
    }
}