package com.smart_learn.presenter.helpers.adapters.test.schedule;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;

import com.smart_learn.R;
import com.smart_learn.core.services.test.TestService;
import com.smart_learn.core.utilities.CoreUtilities;
import com.smart_learn.data.entities.Test;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.data.room.entities.RoomTest;
import com.smart_learn.databinding.LayoutCardViewTestScheduleBinding;
import com.smart_learn.presenter.helpers.PresenterHelpers;
import com.smart_learn.presenter.helpers.Utilities;
import com.smart_learn.presenter.helpers.adapters.helpers.BasicListAdapter;
import com.smart_learn.presenter.helpers.adapters.helpers.BasicViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;

import timber.log.Timber;

public class GuestScheduledTestsAdapter extends BasicListAdapter<RoomTest, GuestScheduledTestsAdapter.TestViewHolder, GuestScheduledTestsAdapter.Callback>
        implements PresenterHelpers.AdapterHelper {

    public GuestScheduledTestsAdapter(@NonNull @NotNull GuestScheduledTestsAdapter.Callback adapterCallback) {
        super(adapterCallback);
    }

    @NonNull
    @Override
    public TestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // set data binding
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        LayoutCardViewTestScheduleBinding viewHolderBinding = DataBindingUtil.inflate(layoutInflater,
                R.layout.layout_card_view_test_schedule, parent, false);
        viewHolderBinding.setLifecycleOwner(adapterCallback.getFragment());

        return new TestViewHolder(viewHolderBinding);
    }

    public final class TestViewHolder extends BasicViewHolder<RoomTest, LayoutCardViewTestScheduleBinding> {

        private final MutableLiveData<Test> liveTest;
        private final MutableLiveData<String> liveTimeDescription;
        private final MutableLiveData<String> liveDateDescription;
        private final AtomicBoolean isDeletingActive;

        public TestViewHolder(@NonNull LayoutCardViewTestScheduleBinding viewHolderBinding) {
            super(viewHolderBinding);
            liveTest = new MutableLiveData<>(getEmptyLiveItemInfo());
            liveTimeDescription = new MutableLiveData<>("");
            liveDateDescription = new MutableLiveData<>("");
            isDeletingActive = new AtomicBoolean(false);

            makeStandardSetup(viewHolderBinding.toolbarLayoutCardViewTestSchedule, viewHolderBinding.cvLayoutCardViewTestSchedule);

            // set user menu to invisible
            viewHolderBinding.toolbarLayoutCardViewTestSchedule.getMenu().setGroupVisible(R.id.user_group_menu_card_view_test_schedule, false);

            // link binding with variables
            viewHolderBinding.setLiveTest(liveTest);
            viewHolderBinding.setLiveTimeDescription(liveTimeDescription);
            viewHolderBinding.setLiveDateDescription(liveDateDescription);

            setListeners();
        }

        @Override
        protected RoomTest getEmptyLiveItemInfo() {
            return RoomTest.generateEmptyObject();
        }

        @Override
        protected void bind(@NonNull @NotNull RoomTest item, int position) {
            String hourString = item.getHour() < 10 ? "0" + item.getHour() : String.valueOf(item.getHour());
            String minuteString = item.getMinute() < 10 ? "0" + item.getMinute() : String.valueOf(item.getMinute());
            liveTimeDescription.setValue(hourString + ":" + minuteString);

            if(item.isOneTime()){
                liveDateDescription.setValue(CoreUtilities.General.getDateStringValue(item.getDayOfMonth(), item.getMonth(), item.getYear(), " "));
            }
            else{
                liveDateDescription.setValue(item.getRepeatValuesDescription());
            }

            liveTest.setValue(item);
            // set also live item info in order to avoid errors when extracting data from liveItemInfo
            liveItemInfo.setValue(item);
        }

        private void setListeners(){

            if(adapterCallback.showToolbar()){
                setToolbarListeners();
            }

            viewHolderBinding.switchLayoutCardViewTestSchedule.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    RoomTest roomTest = (RoomTest) liveTest.getValue();
                    if(roomTest == null){
                       Timber.w("roomTest is null");
                       return;
                    }

                    // is nothing new
                    if(roomTest.isScheduleActive() == isChecked){
                        return;
                    }

                    // if test is not active and should be activated check if date is not in past,
                    // only if is a one time test (scheduled by date, with no repeat)
                    if(isChecked && !roomTest.isScheduleActive() && roomTest.isOneTime()){
                       if(!CoreUtilities.General.isDateAndTimeInFuture(roomTest.getHour(), roomTest.getMinute(),
                               roomTest.getDayOfMonth(), roomTest.getMonth(), roomTest.getYear())){
                           showMessage(R.string.error_can_not_set_schedule_because_date_is_in_past);
                           buttonView.setChecked(false);
                           return;
                       }
                    }

                    roomTest.setScheduleActive(isChecked);
                    TestService.getInstance().update(roomTest, new DataCallbacks.General() {
                       @Override
                       public void onSuccess() {
                           // no action needed here
                       }

                       @Override
                       public void onFailure() {
                            showMessage(R.string.error_can_not_set_schedule);
                       }
                    });
                }
            });

            // simple click action
            viewHolderBinding.cvLayoutCardViewTestSchedule.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(!Utilities.Adapters.isGoodAdapterPosition(position)){
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
            viewHolderBinding.toolbarLayoutCardViewTestSchedule.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int position = getAdapterPosition();
                    if(!Utilities.Adapters.isGoodAdapterPosition(position)){
                        return true;
                    }

                    RoomTest test = getItem(position);
                    if(!CoreUtilities.General.isItemNotNull(test)){
                        return true;
                    }

                    int id = item.getItemId();
                    if(id == R.id.action_guest_delete_menu_card_view_test_schedule){
                        if(isDeletingActive.get()){
                            return true;
                        }
                        isDeletingActive.set(true);
                        deleteItem(test);
                        return true;
                    }
                    return true;
                }
            });
        }

        private void deleteItem(RoomTest test){
            TestService.getInstance().delete(test, new DataCallbacks.General() {
                @Override
                public void onSuccess() {
                    showMessage(R.string.succes_delete_test);
                    isDeletingActive.set(false);
                }

                @Override
                public void onFailure() {
                    showMessage(R.string.error_delete_test);
                    isDeletingActive.set(false);
                }
            });
        }

    }

    public interface Callback extends BasicListAdapter.Callback<RoomTest> {

    }
}
