package com.smart_learn.presenter.helpers.adapters.test.history;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;

import com.smart_learn.R;
import com.smart_learn.core.services.test.TestService;
import com.smart_learn.core.utilities.CoreUtilities;
import com.smart_learn.data.entities.Test;
import com.smart_learn.data.room.entities.RoomTest;
import com.smart_learn.databinding.LayoutCardViewTestHistoryBinding;
import com.smart_learn.presenter.helpers.PresenterHelpers;
import com.smart_learn.presenter.helpers.PresenterUtilities;
import com.smart_learn.presenter.helpers.adapters.helpers.BasicListAdapter;
import com.smart_learn.presenter.helpers.adapters.helpers.BasicViewHolder;

import org.jetbrains.annotations.NotNull;

public class GuestTestHistoryAdapter extends BasicListAdapter<RoomTest, GuestTestHistoryAdapter.TestViewHolder, GuestTestHistoryAdapter.Callback>
        implements PresenterHelpers.AdapterHelper {

    public GuestTestHistoryAdapter(@NonNull @NotNull GuestTestHistoryAdapter.Callback adapterCallback) {
        super(adapterCallback);
    }

    @NonNull
    @Override
    public TestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // set data binding
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        LayoutCardViewTestHistoryBinding viewHolderBinding = DataBindingUtil.inflate(layoutInflater,
                R.layout.layout_card_view_test_history, parent, false);
        viewHolderBinding.setLifecycleOwner(adapterCallback.getFragment());

        return new TestViewHolder(viewHolderBinding);
    }

    public final class TestViewHolder extends BasicViewHolder<RoomTest, LayoutCardViewTestHistoryBinding> {

        private final MutableLiveData<Test> liveTest;
        private final MutableLiveData<String> liveExtraDescription;

        public TestViewHolder(@NonNull LayoutCardViewTestHistoryBinding viewHolderBinding) {
            super(viewHolderBinding);
            liveTest = new MutableLiveData<>(getEmptyLiveItemInfo());
            liveExtraDescription = new MutableLiveData<>("");

            makeStandardSetup(viewHolderBinding.toolbarLayoutCardViewTestHistory, viewHolderBinding.cvLayoutCardViewTestHistory);

            // set user menu to invisible
            viewHolderBinding.toolbarLayoutCardViewTestHistory.getMenu().setGroupVisible(R.id.user_group_menu_card_view_test_history, false);

            // link binding with variables
            viewHolderBinding.setLiveTest(liveTest);
            viewHolderBinding.setLiveExtraDescription(liveExtraDescription);

            setListeners();
        }

        @Override
        protected RoomTest getEmptyLiveItemInfo() {
            return RoomTest.generateEmptyObject();
        }

        @Override
        protected void bind(@NonNull @NotNull RoomTest item, int position) {
            liveTest.setValue(item);

            if(item.isFinished()){
                liveExtraDescription.setValue(CoreUtilities.General.formatFloatValue(item.getSuccessRate()) + " %");
                return;
            }

            // here item is in progress
            liveExtraDescription.setValue(item.getAnsweredQuestions() + "/" + item.getTotalQuestions());
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

                    RoomTest test = getItem(position);
                    if(!CoreUtilities.General.isItemNotNull(test)){
                        return;
                    }

                    adapterCallback.onSimpleClick(test);
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

                    RoomTest test = getItem(position);
                    if(!CoreUtilities.General.isItemNotNull(test)){
                        return true;
                    }

                    int id = item.getItemId();
                    if(id == R.id.action_guest_hide_menu_card_view_test_history){
                        test.setHidden(true);
                        TestService.getInstance().update(test, null);
                        return true;
                    }
                    return true;
                }
            });
        }
    }

    public interface Callback extends BasicListAdapter.Callback<RoomTest> {

    }
}