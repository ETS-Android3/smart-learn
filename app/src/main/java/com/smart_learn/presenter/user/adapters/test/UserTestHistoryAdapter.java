package com.smart_learn.presenter.user.adapters.test;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.smart_learn.R;
import com.smart_learn.core.common.services.SettingsService;
import com.smart_learn.core.user.services.UserService;
import com.smart_learn.core.common.services.TestService;
import com.smart_learn.core.common.helpers.CoreUtilities;
import com.smart_learn.data.common.entities.Test;
import com.smart_learn.data.user.firebase.firestore.entities.TestDocument;
import com.smart_learn.databinding.LayoutCardViewTestHistoryBinding;
import com.smart_learn.presenter.common.helpers.PresenterUtilities;
import com.smart_learn.presenter.user.adapters.helpers.BasicFirestoreRecyclerAdapter;
import com.smart_learn.presenter.common.adapters.helpers.BasicViewHolder;

import org.jetbrains.annotations.NotNull;

import timber.log.Timber;


public class UserTestHistoryAdapter extends BasicFirestoreRecyclerAdapter<TestDocument, UserTestHistoryAdapter.TestViewHolder, UserTestHistoryAdapter.Callback> {

    private static final int INITIAL_ADAPTER_CAPACITY = 20;
    private static final int LOADING_STEP = 10;

    public UserTestHistoryAdapter(@NonNull @NotNull UserTestHistoryAdapter.Callback adapterCallback) {
        super(adapterCallback, getInitialAdapterOptions(adapterCallback.getFragment()), INITIAL_ADAPTER_CAPACITY, LOADING_STEP);
    }

    @NonNull
    @NotNull
    @Override
    public TestViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        // set data binding
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        LayoutCardViewTestHistoryBinding viewHolderBinding = DataBindingUtil.inflate(layoutInflater,
                R.layout.layout_card_view_test_history, parent, false);
        viewHolderBinding.setLifecycleOwner(adapterCallback.getFragment());

        return new TestViewHolder(viewHolderBinding);
    }

    @Override
    public void loadMoreData() {
        Query query = TestService.getInstance().getQueryForTests(currentLoad + loadingStep,
                    SettingsService.getInstance().getUserTestFilterOption());
        super.loadData(query, TestDocument.class, adapterCallback.getFragment());
    }


    private static FirestoreRecyclerOptions<TestDocument> getInitialAdapterOptions(@NonNull @NotNull Fragment fragment) {
        Query query = TestService.getInstance().getQueryForTests(INITIAL_ADAPTER_CAPACITY, SettingsService.getInstance().getUserTestFilterOption());
        return new FirestoreRecyclerOptions.Builder<TestDocument>()
                .setLifecycleOwner(fragment)
                .setQuery(query, TestDocument.class)
                .build();
    }

    public void refreshData(@NonNull @NotNull Fragment fragment){
        updateOptions(getInitialAdapterOptions(fragment));
    }

    public final class TestViewHolder extends BasicViewHolder<TestDocument, LayoutCardViewTestHistoryBinding> {

        private final MutableLiveData<Test> liveTest;
        private final MutableLiveData<String> liveExtraDescription;

        public TestViewHolder(@NonNull @NotNull LayoutCardViewTestHistoryBinding viewHolderBinding) {
            super(viewHolderBinding);
            liveTest = new MutableLiveData<>(getEmptyLiveItemInfo());
            liveExtraDescription = new MutableLiveData<>("");

            makeStandardSetup(viewHolderBinding.toolbarLayoutCardViewTestHistory, viewHolderBinding.cvLayoutCardViewTestHistory);

            // set guest menu to invisible
            viewHolderBinding.toolbarLayoutCardViewTestHistory.getMenu().setGroupVisible(R.id.guest_group_menu_card_view_test_history, false);

            // link binding with variables
            viewHolderBinding.setLiveTest(liveTest);
            viewHolderBinding.setLiveExtraDescription(liveExtraDescription);

            setListeners();
        }

        @Override
        public TestDocument getEmptyLiveItemInfo() {
            return new TestDocument();
        }

        @Override
        public void bind(@NonNull @NotNull TestDocument item, int position) {
            if(!item.isOnline()){
                viewHolderBinding.toolbarLayoutCardViewTestHistory.setVisibility(View.VISIBLE);

                liveTest.setValue(item);

                if(item.isFinished()){
                    liveExtraDescription.setValue(CoreUtilities.General.formatFloatValue(item.getSuccessRate()) + " %");
                    return;
                }

                // here item is in progress
                liveExtraDescription.setValue(item.getAnsweredQuestions() + "/" + item.getTotalQuestions());
                return;
            }

            // Here is online test, so extract user test from test container participants collection
            // and use that data to update views.
            viewHolderBinding.toolbarLayoutCardViewTestHistory.setVisibility(View.GONE);

            TestService.getInstance()
                    .getOnlineTestParticipantsCollectionReference(getSnapshots().getSnapshot(position).getId())
                    .document(UserService.getInstance().getUserUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                            if(!task.isSuccessful() || task.getResult() == null){
                                Timber.w("result is not valid ==> can not load participant test");
                                // if value fails to be loaded use container test values
                                liveItemInfo.postValue(item);
                                liveExtraDescription.postValue("");
                                return;
                            }

                            TestDocument participantTest = task.getResult().toObject(TestDocument.class);
                            if(participantTest == null){
                                Timber.w("participantTest is null ==> can not load participant test");
                                // if value fails to be loaded use container test values
                                liveItemInfo.postValue(item);
                                liveExtraDescription.postValue("");
                                return;
                            }

                            liveTest.postValue(participantTest);

                            if(participantTest.isFinished()){
                                liveExtraDescription.postValue(CoreUtilities.General.formatFloatValue(participantTest.getSuccessRate()) + " %");
                                return;
                            }

                            // here item is in progress
                            liveExtraDescription.postValue(participantTest.getAnsweredQuestions() + "/" + participantTest.getTotalQuestions());
                        }
                    });

        }

        private void setListeners(){

            if(adapterCallback.showToolbar()){
                setToolbarListeners();
            }

            // simple click action
            viewHolderBinding.cvLayoutCardViewTestHistory.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(!PresenterUtilities.Adapters.isGoodAdapterPosition(position)){
                        return;
                    }

                    adapterCallback.onSimpleClick(getSnapshots().getSnapshot(position));
                }
            });

        }

        private void setToolbarListeners(){
            viewHolderBinding.toolbarLayoutCardViewTestHistory.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int position = getAdapterPosition();
                    if(!PresenterUtilities.Adapters.isGoodAdapterPosition(position)){
                        return true;
                    }

                    int id = item.getItemId();
                    if(id == R.id.action_user_hide_menu_card_view_test_history){
                        TestDocument test = getSnapshots().getSnapshot(position).toObject(TestDocument.class);
                        if(test == null || test.isOnline()){
                            // for online test hide is disabled
                            return true;
                        }
                        TestService.getInstance().markAsHidden(getSnapshots().getSnapshot(position), null);
                        return true;
                    }
                    return true;
                }
            });
        }

    }

    public interface Callback extends BasicFirestoreRecyclerAdapter.Callback  {

    }

}
