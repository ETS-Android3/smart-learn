package com.smart_learn.presenter.helpers.adapters.test.schedule;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.smart_learn.R;
import com.smart_learn.core.services.test.TestService;
import com.smart_learn.core.utilities.ConnexionChecker;
import com.smart_learn.core.utilities.CoreUtilities;
import com.smart_learn.data.entities.Test;
import com.smart_learn.data.firebase.firestore.entities.TestDocument;
import com.smart_learn.data.helpers.DataCallbacks;
import com.smart_learn.databinding.LayoutCardViewTestScheduleBinding;
import com.smart_learn.presenter.helpers.Utilities;
import com.smart_learn.presenter.helpers.adapters.helpers.BasicFirestoreRecyclerAdapter;
import com.smart_learn.presenter.helpers.adapters.helpers.BasicViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;


public class UserScheduledTestsAdapter extends BasicFirestoreRecyclerAdapter<TestDocument, UserScheduledTestsAdapter.TestViewHolder, UserScheduledTestsAdapter.Callback> {

    private static final int INITIAL_ADAPTER_CAPACITY = 20;
    private static final int LOADING_STEP = 10;

    public UserScheduledTestsAdapter(@NonNull @NotNull UserScheduledTestsAdapter.Callback adapterCallback) {
        super(adapterCallback, getInitialAdapterOptions(adapterCallback.getFragment()), INITIAL_ADAPTER_CAPACITY, LOADING_STEP);
    }

    @NonNull
    @NotNull
    @Override
    public TestViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        // set data binding
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        LayoutCardViewTestScheduleBinding viewHolderBinding = DataBindingUtil.inflate(layoutInflater,
                R.layout.layout_card_view_test_schedule, parent, false);
        viewHolderBinding.setLifecycleOwner(adapterCallback.getFragment());

        return new TestViewHolder(viewHolderBinding);
    }

    @Override
    public void loadMoreData() {
        Query query = TestService.getInstance().getQueryForTests(currentLoad + loadingStep, TestService.SHOW_ONLY_LOCAL_SCHEDULED_TESTS);
        super.loadData(query, TestDocument.class, adapterCallback.getFragment());
    }

    private static FirestoreRecyclerOptions<TestDocument> getInitialAdapterOptions(@NonNull @NotNull Fragment fragment) {
        Query query = TestService.getInstance().getQueryForTests(INITIAL_ADAPTER_CAPACITY, TestService.SHOW_ONLY_LOCAL_SCHEDULED_TESTS);
        return new FirestoreRecyclerOptions.Builder<TestDocument>()
                .setLifecycleOwner(fragment)
                .setQuery(query, TestDocument.class)
                .build();
    }

    public final class TestViewHolder extends BasicViewHolder<TestDocument, LayoutCardViewTestScheduleBinding> {

        private final MutableLiveData<Test> liveTest;
        private final MutableLiveData<String> liveTimeDescription;
        private final MutableLiveData<String> liveDateDescription;
        private final AtomicBoolean isDeletingActive;
        private final AtomicBoolean isLaunchingActive;

        public TestViewHolder(@NonNull @NotNull LayoutCardViewTestScheduleBinding viewHolderBinding) {
            super(viewHolderBinding);
            liveTest = new MutableLiveData<>(getEmptyLiveItemInfo());
            liveTimeDescription = new MutableLiveData<>("");
            liveDateDescription = new MutableLiveData<>("");
            isDeletingActive = new AtomicBoolean(false);
            isLaunchingActive = new AtomicBoolean(false);

            makeStandardSetup(viewHolderBinding.toolbarLayoutCardViewTestSchedule, viewHolderBinding.cvLayoutCardViewTestSchedule);

            // set guest menu to invisible
            viewHolderBinding.toolbarLayoutCardViewTestSchedule.getMenu().setGroupVisible(R.id.guest_group_menu_card_view_test_schedule, false);

            // link binding with variables
            viewHolderBinding.setLiveTest(liveTest);
            viewHolderBinding.setLiveTimeDescription(liveTimeDescription);
            viewHolderBinding.setLiveDateDescription(liveDateDescription);

            setListeners();
        }

        @Override
        protected TestDocument getEmptyLiveItemInfo() {
            return new TestDocument();
        }

        @Override
        protected void bind(@NonNull @NotNull TestDocument item, int position) {
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
                    int position = getAdapterPosition();
                    if(!Utilities.Adapters.isGoodAdapterPosition(position)){
                        return;
                    }

                    TestDocument testDocument = getSnapshots().get(position);
                    // is nothing new
                    if(testDocument.isScheduleActive() == isChecked){
                        return;
                    }

                    // if test is not active and should be activated check if date is not in past,
                    // only if is a one time test (scheduled by date, with no repeat)
                    if(isChecked && !testDocument.isScheduleActive() && testDocument.isOneTime()){
                        if(!CoreUtilities.General.isDateAndTimeInFuture(testDocument.getHour(), testDocument.getMinute(),
                                testDocument.getDayOfMonth(), testDocument.getMonth(), testDocument.getYear())){
                            showMessage(R.string.error_can_not_set_schedule_because_date_is_in_past);
                            buttonView.setChecked(false);
                            return;
                        }
                    }

                    if(isChecked){
                        testDocument.setAlarm(getSnapshots().getSnapshot(position).getId(), true);
                    }
                    else{
                        testDocument.cancelAlarm(getSnapshots().getSnapshot(position).getId(), true);
                    }

                    TestService.getInstance().updateDocument(TestDocument.convertDocumentToHashMap(testDocument), getSnapshots().getSnapshot(position),
                            new DataCallbacks.General() {
                                @Override
                                public void onSuccess() {
                                    if(testDocument.isScheduleActive()){
                                        showMessage(R.string.success_set_schedule);
                                    }
                                    else{
                                        showMessage(R.string.success_cancel_schedule);
                                    }
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

                    adapterCallback.onSimpleClick(getSnapshots().getSnapshot(position));
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

                    int id = item.getItemId();
                    if(id == R.id.action_user_delete_menu_card_view_test_schedule){
                        if(isDeletingActive.get()){
                            return true;
                        }
                        isDeletingActive.set(true);
                        deleteItem(getSnapshots().get(position), getSnapshots().getSnapshot(position));
                        return true;
                    }
                    if(id == R.id.action_user_launch_now_menu_card_view_test_schedule){
                        if(isLaunchingActive.get()){
                            return true;
                        }
                        isLaunchingActive.set(true);
                        launchScheduledTest(getSnapshots().get(position), getSnapshots().getSnapshot(position));
                        return true;
                    }
                    return true;
                }
            });
        }

        private void deleteItem(TestDocument testDocument, DocumentSnapshot testSnapshot){
            // first deactivate alarm
            testDocument.cancelAlarm(testSnapshot.getId(), true);

            // and then you can delete scheduled test
            TestService.getInstance().deleteScheduledTest(testSnapshot, new DataCallbacks.General() {
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

        private void launchScheduledTest(TestDocument scheduledTest, DocumentSnapshot scheduledTestSnapshot){
            adapterCallback.getFragment().showProgressDialog("", adapterCallback.getFragment().getString(R.string.preparing_test));

            if(scheduledTest.isGenerated()) {
                createTestFromScheduledTest(scheduledTest);
                return;
            }

            // here test is not generated so internet must be available in order to generate it
            new ConnexionChecker(new ConnexionChecker.Callback() {
                @Override
                public void isConnected() {
                    createTestFromScheduledTest(scheduledTest);
                }

                @Override
                public void networkDisabled() {
                    showMessage(R.string.error_no_network);
                }

                @Override
                public void internetNotAvailable() {
                    showMessage(R.string.error_no_internet_connection);
                }
                @Override
                public void notConnected() {
                    adapterCallback.getFragment().requireActivity().runOnUiThread(adapterCallback.getFragment()::closeProgressDialog);
                    isLaunchingActive.set(false);
                }
            }).check();
        }

        private void createTestFromScheduledTest(TestDocument scheduledTest){
            TestService.getInstance().createTestFromScheduledTest(scheduledTest, true, new TestService.TestGenerationCallback() {
                @Override
                public void onComplete(@NonNull @NotNull String testId) {
                    adapterCallback.getFragment().requireActivity().runOnUiThread(() -> {
                        adapterCallback.getFragment().closeProgressDialog();
                        isLaunchingActive.set(false);

                        if(testId.equals(TestService.NO_TEST_ID)){
                            showMessage(R.string.error_can_not_continue);
                            return;
                        }
                        adapterCallback.onCompleteCreateLocalTestFromScheduledTest(scheduledTest.getType(), testId);
                    });
                }
            });
        }
    }

    public interface Callback extends BasicFirestoreRecyclerAdapter.Callback  {
        void onCompleteCreateLocalTestFromScheduledTest(int type, @NonNull @NotNull String testId);
    }

}