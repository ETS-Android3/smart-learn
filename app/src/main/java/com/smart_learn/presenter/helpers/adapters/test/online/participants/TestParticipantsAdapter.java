package com.smart_learn.presenter.helpers.adapters.test.online.participants;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.smart_learn.R;
import com.smart_learn.core.services.test.TestService;
import com.smart_learn.core.helpers.CoreUtilities;
import com.smart_learn.data.firebase.firestore.entities.TestDocument;
import com.smart_learn.databinding.LayoutCardViewTestParticipantBinding;
import com.smart_learn.core.helpers.ApplicationController;
import com.smart_learn.presenter.helpers.PresenterUtilities;
import com.smart_learn.presenter.helpers.adapters.helpers.BasicFirestoreRecyclerAdapter;
import com.smart_learn.presenter.helpers.adapters.helpers.BasicViewHolder;

import org.jetbrains.annotations.NotNull;


public class TestParticipantsAdapter extends BasicFirestoreRecyclerAdapter<TestDocument, TestParticipantsAdapter.ParticipantViewHolder, TestParticipantsAdapter.Callback> {

    private static final int INITIAL_ADAPTER_CAPACITY = 20;
    private static final int LOADING_STEP = 10;

    private final String currentTestId;

    public TestParticipantsAdapter(@NonNull @NotNull String currentTestId,
                                   @NonNull @NotNull TestParticipantsAdapter.Callback adapterCallback) {
        super(adapterCallback, getInitialAdapterOptions(currentTestId, adapterCallback.getFragment()), INITIAL_ADAPTER_CAPACITY, LOADING_STEP);
        this.currentTestId = currentTestId;
    }

    @NonNull
    @NotNull
    @Override
    public ParticipantViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        // set data binding
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        LayoutCardViewTestParticipantBinding viewHolderBinding = DataBindingUtil.inflate(layoutInflater,
                R.layout.layout_card_view_test_participant, parent, false);
        viewHolderBinding.setLifecycleOwner(adapterCallback.getFragment());

        // link data binding layout with view holder
        ParticipantViewHolder viewHolder = new ParticipantViewHolder(viewHolderBinding);
        viewHolderBinding.setViewHolder(viewHolder);
        return viewHolder;
    }

    @Override
    public void loadMoreData() {
        Query query = TestService.getInstance().getQueryForOnlineTestParticipantsRanking(currentTestId,currentLoad + loadingStep);
        super.loadData(query, TestDocument.class, adapterCallback.getFragment());
    }

    private static FirestoreRecyclerOptions<TestDocument> getInitialAdapterOptions(@NonNull @NotNull String currentTestId,
                                                                                   @NonNull @NotNull Fragment fragment) {
        Query query = TestService.getInstance().getQueryForOnlineTestParticipantsRanking(currentTestId, INITIAL_ADAPTER_CAPACITY);
        return new FirestoreRecyclerOptions.Builder<TestDocument>()
                .setLifecycleOwner(fragment)
                .setQuery(query, TestDocument.class)
                .build();
    }

    public final class ParticipantViewHolder extends BasicViewHolder<TestDocument, LayoutCardViewTestParticipantBinding> {

        private final MutableLiveData<String> liveProgressDescription;
        private final MutableLiveData<String> liveExtraProgressDescription;
        private final MutableLiveData<String> liveTotalTestTimeDescription;

        public ParticipantViewHolder(@NonNull @NotNull LayoutCardViewTestParticipantBinding viewHolderBinding) {
            super(viewHolderBinding);
            liveProgressDescription = new MutableLiveData<>("");
            liveExtraProgressDescription = new MutableLiveData<>("");
            liveTotalTestTimeDescription = new MutableLiveData<>("");
            setListeners();
        }

        public LiveData<String> getLiveProgressDescription() {
            return liveProgressDescription;
        }

        public LiveData<String> getLiveExtraProgressDescription() {
            return liveExtraProgressDescription;
        }

        public LiveData<String> getLiveTotalTestTimeDescription() {
            return liveTotalTestTimeDescription;
        }

        @Override
        protected TestDocument getEmptyLiveItemInfo() {
            return new TestDocument();
        }

        @Override
        protected void bind(@NonNull @NotNull TestDocument item, int position){
            PresenterUtilities.Activities.loadProfileImage(item.getUserProfilePhotoUrl(), viewHolderBinding.ivProfileLayoutCardViewTestParticipant);

            liveItemInfo.setValue(item);

            liveTotalTestTimeDescription.setValue(item.getTotalTimeDescription());

            if(item.getTotalQuestions() != 0){
                if(item.isFinished()){
                    liveProgressDescription.setValue(CoreUtilities.General.formatFloatValue(item.getSuccessRate()) + " %");
                    liveExtraProgressDescription.setValue(item.getCorrectAnswers() + " " + ApplicationController.getInstance().getString(R.string.from) +
                            " " + item.getTotalQuestions());
                }
                else{
                    liveProgressDescription.setValue(item.getAnsweredQuestions() + "/" + item.getTotalQuestions());
                    liveExtraProgressDescription.setValue(CoreUtilities.General.formatFloatValue(item.getSuccessRate()) + " %");
                }
            }
            else{
                liveProgressDescription.setValue("");
                liveExtraProgressDescription.setValue("");
            }
        }

        private void setListeners(){
            // simple click action
            viewHolderBinding.cvLayoutCardViewTestParticipant.setOnClickListener(new View.OnClickListener(){
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
    }

    public interface Callback extends BasicFirestoreRecyclerAdapter.Callback {

    }

}