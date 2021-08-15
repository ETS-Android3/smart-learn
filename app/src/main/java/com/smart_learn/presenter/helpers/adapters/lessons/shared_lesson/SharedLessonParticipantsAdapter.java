package com.smart_learn.presenter.helpers.adapters.lessons.shared_lesson;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.smart_learn.R;
import com.smart_learn.core.services.lesson.UserLessonService;
import com.smart_learn.data.firebase.firestore.entities.UserDocument;
import com.smart_learn.databinding.LayoutCardViewSharedLessonParticipantBinding;
import com.smart_learn.presenter.helpers.PresenterUtilities;
import com.smart_learn.presenter.helpers.adapters.helpers.BasicFirestoreRecyclerAdapter;
import com.smart_learn.presenter.helpers.adapters.helpers.BasicViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class SharedLessonParticipantsAdapter extends BasicFirestoreRecyclerAdapter<UserDocument, SharedLessonParticipantsAdapter.ParticipantViewHolder,
        SharedLessonParticipantsAdapter.Callback> {

    private static final int INITIAL_ADAPTER_CAPACITY = 20;
    private static final int LOADING_STEP = 10;

    private final ArrayList<String> sharedLessonParticipants;

    public SharedLessonParticipantsAdapter(@NonNull @NotNull ArrayList<String> sharedLessonParticipants,
                                           @NonNull @NotNull SharedLessonParticipantsAdapter.Callback adapterCallback) {
        super(adapterCallback, getInitialAdapterOptions(sharedLessonParticipants, adapterCallback.getFragment()), INITIAL_ADAPTER_CAPACITY, LOADING_STEP);
        this.sharedLessonParticipants = sharedLessonParticipants;
    }

    @NonNull
    @NotNull
    @Override
    public ParticipantViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        // set data binding
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        LayoutCardViewSharedLessonParticipantBinding viewHolderBinding = DataBindingUtil.inflate(layoutInflater,
                R.layout.layout_card_view_shared_lesson_participant, parent, false);
        viewHolderBinding.setLifecycleOwner(adapterCallback.getFragment());

        // link data binding layout with view holder
        ParticipantViewHolder viewHolder = new ParticipantViewHolder(viewHolderBinding);
        viewHolderBinding.setViewHolder(viewHolder);
        return viewHolder;
    }

    @Override
    public void loadMoreData() {
        Query query = UserLessonService.getInstance().getQueryForSharedLessonParticipants(sharedLessonParticipants,currentLoad + loadingStep);
        super.loadData(query, UserDocument.class, adapterCallback.getFragment());
    }

    private static FirestoreRecyclerOptions<UserDocument> getInitialAdapterOptions(@NonNull @NotNull ArrayList<String> sharedLessonParticipants,
                                                                                   @NonNull @NotNull Fragment fragment) {
        Query query = UserLessonService.getInstance().getQueryForSharedLessonParticipants(sharedLessonParticipants, INITIAL_ADAPTER_CAPACITY);
        return new FirestoreRecyclerOptions.Builder<UserDocument>()
                .setLifecycleOwner(fragment)
                .setQuery(query, UserDocument.class)
                .build();
    }

    public static final class ParticipantViewHolder extends BasicViewHolder<UserDocument, LayoutCardViewSharedLessonParticipantBinding> {

        public ParticipantViewHolder(@NonNull @NotNull LayoutCardViewSharedLessonParticipantBinding viewHolderBinding) {
            super(viewHolderBinding);
        }

        @Override
        protected UserDocument getEmptyLiveItemInfo() {
            return new UserDocument();
        }

        @Override
        protected void bind(@NonNull @NotNull UserDocument item, int position){
            PresenterUtilities.Activities.loadProfileImage(item.getProfilePhotoUrl(), viewHolderBinding.ivProfileLayoutCardViewSharedLessonParticipant);
            liveItemInfo.setValue(item);
        }
    }

    public interface Callback extends BasicFirestoreRecyclerAdapter.Callback {

    }

}