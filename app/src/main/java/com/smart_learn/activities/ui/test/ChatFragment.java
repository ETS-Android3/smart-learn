package com.smart_learn.activities.ui.test;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.material.tabs.TabLayout;
import com.smart_learn.R;
import com.smart_learn.config.CurrentConfig;
import com.smart_learn.remote.test.RemotePlay;
import com.smart_learn.utilities.Logs;

import java.util.ArrayList;
import java.util.List;


import lombok.Getter;
import lombok.Setter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {


    private List<ChatMessageModel> chatMessageModelList =  new ArrayList<>();
    private RecyclerView recyclerView;
    private ChatMessageRVAdapter adapter ;

    private EditText etMessage;
    private ImageView btnSendMessage;

    @Getter
    @Setter
    private int unreadMessages = 0;
    @Getter
    @Setter
    private boolean unselected = true;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private TestFragmentAdapter testFragmentAdapter;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public void setAdapter(TestFragmentAdapter adapter){
        testFragmentAdapter = adapter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        testFragmentAdapter.setChatFragment(this);
        Log.i(Logs.INFO,Logs.FUNCTION + "[onCreate] create CHAT fragment");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        Log.i(Logs.INFO,Logs.FUNCTION + "[onCreateView] create CHAT view fragment");

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_chat, container, false);

        // get GUI elements
        etMessage = view.findViewById(R.id.etMessageChatFragment);
        btnSendMessage = view.findViewById(R.id.sendBtnChatFragment);

        // add test data
        //addTestInfo();

        // init recycler view
        initRecyclerView(view);

        // set listeners
        btnSendMessage.setOnClickListener(v -> {

            String msg = etMessage.getText().toString();

            ChatMessageModel chatMessageModel = new ChatMessageModel(
                    "guest",
                    msg,
                    "10:00 PM", // get system time
                    ChatMessageRVAdapter.VIEW_TYPE_MESSAGE_SENT
            );

            CurrentConfig.getCurrentConfigInstance().currentActivity.runOnUiThread(() -> {
                chatMessageModelList.add(chatMessageModel);
                recyclerView.smoothScrollToPosition(chatMessageModelList.size());
                adapter.notifyDataSetChanged();
                etMessage.setText("");
            });

            // send message to all
            ((RemotePlay)CurrentConfig.getCurrentConfigInstance().currentActivity).sendChatMessage(chatMessageModel);

        });

        return view;
    }

    public void addMessage(ChatMessageModel chatMessageModel){

        CurrentConfig.getCurrentConfigInstance().currentActivity.runOnUiThread(() -> {
            chatMessageModelList.add(chatMessageModel);
            recyclerView.smoothScrollToPosition(chatMessageModelList.size());
            adapter.notifyDataSetChanged();

            TabLayout.Tab tab = ((RemotePlay)CurrentConfig.getCurrentConfigInstance().currentActivity)
                    .getTab(1);

            // if current fragment is not visible mark some messages as unread
            if (unselected) {
                unreadMessages++;
                tab.setText(testFragmentAdapter.getPageTitle(1) + " (" + unreadMessages + ")");
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

        ChatMessageModel model1 = new ChatMessageModel(
                "guest",
                "Hello. How are you today?",
                "10:00 PM",
                ChatMessageRVAdapter.VIEW_TYPE_MESSAGE_SENT
        );
        ChatMessageModel model2 = new ChatMessageModel(
                "guest",
                "Hey! I'm fine. Thanks for asking!",
                "10:00 PM",
                ChatMessageRVAdapter.VIEW_TYPE_MESSAGE_RECEIVED
        );
        ChatMessageModel model3 = new ChatMessageModel(
                "guest",
                "Sweet! So, what do you wanna do today?",
                "10:00 PM",
                ChatMessageRVAdapter.VIEW_TYPE_MESSAGE_SENT
        );
        ChatMessageModel model4 = new ChatMessageModel(
                "guest",
                "Nah, I dunno. Play soccer.. or learn more coding perhaps?",
                "10:00 PM",
                ChatMessageRVAdapter.VIEW_TYPE_MESSAGE_RECEIVED
        );


        chatMessageModelList.add(model1);
        chatMessageModelList.add(model2);
        chatMessageModelList.add(model3);
        chatMessageModelList.add(model4);
        chatMessageModelList.add(model1);
        chatMessageModelList.add(model2);
        chatMessageModelList.add(model3);
        chatMessageModelList.add(model4);
        chatMessageModelList.add(model1);
        chatMessageModelList.add(model2);
        chatMessageModelList.add(model3);
        chatMessageModelList.add(model4);

    }

    private void initRecyclerView(View view){

        recyclerView = view.findViewById(R.id.rvChatFragment);
        LinearLayoutManager manager = new LinearLayoutManager(CurrentConfig.getCurrentConfigInstance().currentContext,
                RecyclerView.VERTICAL, false);

        recyclerView.setLayoutManager(manager);

        recyclerView.smoothScrollToPosition(chatMessageModelList.size());
        adapter = new ChatMessageRVAdapter(chatMessageModelList, CurrentConfig.getCurrentConfigInstance().currentContext);
        recyclerView.setAdapter(adapter);
    }
}