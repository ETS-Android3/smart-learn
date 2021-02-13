package com.smart_learn.presenter.activities.ui.test;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.smart_learn.R;
import com.smart_learn.core.config.CurrentConfig;
import com.smart_learn.core.remote.test.RemotePlay;
import com.smart_learn.core.services.TestService;
import com.smart_learn.core.utilities.Logs;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ParticipantsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ParticipantsFragment extends Fragment {

    private List<ParticipantModel> participantModelList =  new ArrayList<>();
    private RecyclerView recyclerView;
    private ParticipantsRVAdapter adapter;

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
    private int unseenParticipants = 0;
    @Getter
    @Setter
    private boolean unselected = false;

    public ParticipantsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ParticipantsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ParticipantsFragment newInstance(String param1, String param2) {
        ParticipantsFragment fragment = new ParticipantsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        testFragmentAdapter.setParticipantsFragment(this);
        Log.i(Logs.INFO,Logs.FUNCTION + "[onCreate] create PARTICIPANTS fragment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i(Logs.INFO,Logs.FUNCTION + "[onCreateView] create PARTICIPANTS view fragment");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_participants, container, false);

        // add test data
        //addTestInfo();

        // set initial data
        participantModelList = TestService.getTestServiceInstance().participantModelList;

        // init recycler view
        initRecyclerView(view);

        return view;
    }

    public void setAdapter(TestFragmentAdapter adapter){
        testFragmentAdapter = adapter;
    }

    private boolean participantExists(String participantId){
        for(ParticipantModel p: participantModelList){
            if(p.getParticipantId().equals(participantId)){
                return true;
            }
        }
        return false;
    }

    public void addParticipant(ParticipantModel participantModel){

        /*
        if(participantExists(participantModel.getParticipantId())){
            return;
        }

         */

        CurrentConfig.getCurrentConfigInstance().currentActivity.runOnUiThread(() -> {
            participantModelList.add(participantModel);
            recyclerView.smoothScrollToPosition(participantModelList.size());
            adapter.notifyDataSetChanged();

            TabLayout.Tab tab = ((RemotePlay)CurrentConfig.getCurrentConfigInstance().currentActivity)
                    .getTab(2);

            // if current fragment is not visible mark some messages as unread
            if (unselected) {
                unseenParticipants++;
                tab.setText(testFragmentAdapter.getPageTitle(2) + " (" + unseenParticipants + ")");
//                tab.setCustomView(R.layout.notification_badge);
//
//                if(tab.getCustomView() != null){
//                    TextView textView = tab.getCustomView().findViewById(R.id.text);
//                    textView.setText("5");
//                }
            }
        });

    }

    private void addTestInfo(){

        ParticipantModel model1 = new ParticipantModel(
                false,
                "guest abcd",
                ParticipantsRVAdapter.VIEW_TYPE_CONNECTED
        );
//        ParticipantModel model2 = new ParticipantModel(
//                "guest 2",
//                ParticipantsRVAdapter.VIEW_TYPE_CONNECTED
//        );
//        ParticipantModel model3 = new ParticipantModel(
//                "guest 3",
//                ParticipantsRVAdapter.VIEW_TYPE_CONNECTED
//        );
//        ParticipantModel model4 = new ParticipantModel(
//                "guest 4",
//                ParticipantsRVAdapter.VIEW_TYPE_CONNECTED
//        );
//        ParticipantModel model5 = new ParticipantModel(
//                "guest 5",
//                ParticipantsRVAdapter.VIEW_TYPE_CONNECTED
//        );
//        ParticipantModel model6 = new ParticipantModel(
//                "guest 6",
//                ParticipantsRVAdapter.VIEW_TYPE_CONNECTED
//        );



        participantModelList.add(model1);
//        participantModelList.add(model2);
//        participantModelList.add(model3);
//        participantModelList.add(model4);
//        participantModelList.add(model1);
//        participantModelList.add(model2);
//        participantModelList.add(model3);
//        participantModelList.add(model4);
//        participantModelList.add(model1);
//        participantModelList.add(model2);
//        participantModelList.add(model3);
//        participantModelList.add(model4);

    }

    private void initRecyclerView(View view){

        recyclerView = view.findViewById(R.id.rvParticipantsFragment);
        LinearLayoutManager manager = new LinearLayoutManager(CurrentConfig.getCurrentConfigInstance().currentContext,
                RecyclerView.VERTICAL, false);

        recyclerView.setLayoutManager(manager);

        recyclerView.smoothScrollToPosition(participantModelList.size());
        adapter = new ParticipantsRVAdapter(participantModelList, CurrentConfig.getCurrentConfigInstance().currentContext);
        recyclerView.setAdapter(adapter);
    }

}