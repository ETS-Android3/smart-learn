package com.smart_learn.presenter.common.activities.test.fragments.scheduled_test_info;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.google.android.material.textfield.TextInputLayout;
import com.smart_learn.R;
import com.smart_learn.core.common.helpers.CoreUtilities;
import com.smart_learn.data.common.entities.Test;
import com.smart_learn.data.common.helpers.DataUtilities;
import com.smart_learn.databinding.FragmentScheduledTestInfoBinding;
import com.smart_learn.presenter.common.activities.test.TestSharedViewModel;
import com.smart_learn.presenter.common.helpers.PresenterUtilities;
import com.smart_learn.presenter.common.dialogs.SingleLineEditableLayoutDialog;
import com.smart_learn.presenter.common.fragments.helpers.BasicFragment;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import timber.log.Timber;


public abstract class ScheduledTestInfoFragment <VM extends ScheduledTestInfoViewModel> extends BasicFragment<VM> {

    // if fragment is open for update test id will be transmitted
    public static String TEST_ID_KEY = "TEST_ID_KEY";

    protected FragmentScheduledTestInfoBinding binding;
    @Getter
    protected TestSharedViewModel sharedViewModel;

    protected abstract void navigateToSelectLessonFragment();
    protected abstract void updateTest(@NonNull @NotNull Test newTest);

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = FragmentScheduledTestInfoBinding.inflate(inflater);
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setLayoutUtilities();
    }

    @Override
    public void onResume() {
        super.onResume();
        PresenterUtilities.Activities.resetToolbarTitle((AppCompatActivity) requireActivity(), getString(R.string.test_info));
    }

    @Override
    protected void setViewModel() {
        super.setViewModel();

        // if arguments exists then fragment was opened for update so extract testId
        if(getArguments() != null){
            viewModel.setForUpdate(true);
            String testId = getArguments().getString(TEST_ID_KEY);
            if(testId == null || testId.isEmpty()){
                Timber.w("testId is not selected");
                goBack();
                return;
            }
            viewModel.setTestId(testId);
        }
        else{
            viewModel.setForUpdate(false);
        }

        // set checkboxes observers
        viewModel.getLiveIsRepeatMonday().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                viewModel.setRepeatValuesDescription();
            }
        });

        viewModel.getLiveIsRepeatTuesday().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                viewModel.setRepeatValuesDescription();
            }
        });

        viewModel.getLiveIsRepeatWednesday().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                viewModel.setRepeatValuesDescription();
            }
        });

        viewModel.getLiveIsRepeatThursday().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                viewModel.setRepeatValuesDescription();
            }
        });

        viewModel.getLiveIsRepeatFriday().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                viewModel.setRepeatValuesDescription();
            }
        });

        viewModel.getLiveIsRepeatSaturday().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                viewModel.setRepeatValuesDescription();
            }
        });

        viewModel.getLiveIsRepeatSunday().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                viewModel.setRepeatValuesDescription();
            }
        });

    }

    protected void setLayoutUtilities(){
        if(viewModel.isForUpdate()){
            binding.btnNextFragmentScheduledTestInfo.setVisibility(View.INVISIBLE);
            binding.linearLayoutTestInfoFragmentScheduledInfo.setVisibility(View.VISIBLE);
            binding.btnSaveFragmentScheduledTestInfo.setVisibility(View.VISIBLE);
        }
        else{
            binding.btnNextFragmentScheduledTestInfo.setVisibility(View.VISIBLE);
            binding.linearLayoutTestInfoFragmentScheduledInfo.setVisibility(View.INVISIBLE);
            binding.btnSaveFragmentScheduledTestInfo.setVisibility(View.INVISIBLE);
        }

        setListeners();
    }

    private void setListeners(){
        binding.btnChooseDateFragmentScheduledTestInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // https://stackoverflow.com/questions/15291112/how-to-get-date-from-date-picker-in-android/33003251
                // https://stackoverflow.com/questions/26310750/how-to-set-a-specific-date-in-date-picker-in-android/55222443#55222443

                // set listener
                DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        viewModel.onDateSetClick(year, month, dayOfMonth);
                    }
                };

                // set dialog
                DatePickerDialog datePickerDialog;
                if(viewModel.isForUpdate() && viewModel.isDateSet()){
                    // if exists set custom date
                    datePickerDialog = new DatePickerDialog(ScheduledTestInfoFragment.this.requireContext(), listener,
                            viewModel.getYear(), viewModel.getMonth(), viewModel.getDayOfMonth());
                }
                else {
                    // if custom date does not exist set a minim of date selection starting from current time
                    datePickerDialog = new DatePickerDialog(ScheduledTestInfoFragment.this.requireContext());
                    datePickerDialog.setOnDateSetListener(listener);
                    // current time - 10 seconds
                    datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 10000);
                }

                // show dialog
                datePickerDialog.show();
            }
        });

        binding.btnChooseTimeFragmentScheduledTestInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = CoreUtilities.General.getCurrentHour();
                int minute = CoreUtilities.General.getCurrentMinute();
                if(viewModel.isForUpdate() && viewModel.isTimeSet()){
                    // set custom start hour and minute
                    hour = viewModel.getHour();
                    minute = viewModel.getMinute();
                }

                TimePickerDialog timePickerDialog = new TimePickerDialog(ScheduledTestInfoFragment.this.requireContext(), new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            viewModel.onTimeSetClick(hourOfDay, minute);
                        }
                }, hour, minute, true);

                timePickerDialog.show();
            }
        });

        binding.btnNextFragmentScheduledTestInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewModel.isForUpdate()){
                    showMessage(R.string.error_can_not_process_selection);
                    Timber.w("isForUpdate is true, and should be false");
                    return;
                }
                viewModel.processSelections(ScheduledTestInfoFragment.this, false);
            }
        });

        binding.btnSaveFragmentScheduledTestInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!viewModel.isForUpdate()){
                    showMessage(R.string.error_can_not_process_selection);
                    Timber.w("isForUpdate is false, and should be true");
                    return;
                }
                viewModel.processSelections(ScheduledTestInfoFragment.this, true);
            }
        });

        binding.btnUpdateCustomNameFragmentScheduledTestInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String initialValue = "";
                if(viewModel.isForUpdate() && viewModel.getCustomName() != null){
                    initialValue = viewModel.getCustomName();
                }

                SingleLineEditableLayoutDialog dialog = new SingleLineEditableLayoutDialog(
                        getString(R.string.custom_name),
                        initialValue,
                        getString(R.string.test_name_optional),
                        DataUtilities.Limits.MAX_TEST_CUSTOM_NAME,
                        new SingleLineEditableLayoutDialog.Callback() {
                            @Override
                            public void onUpdate(String oldValue, String newValue, @NonNull @NotNull TextInputLayout textInputLayout,
                                                 @NonNull @NotNull SingleLineEditableLayoutDialog.Listener listener) {
                                viewModel.updateCustomName(oldValue, newValue, textInputLayout, listener);
                            }
                        });
                dialog.show(requireActivity().getSupportFragmentManager(), "ScheduledTestInfoFragment");
            }
        });

    }


    protected void goBack(){
        showMessage(R.string.error_can_not_continue);
        requireActivity().onBackPressed();
    }
}