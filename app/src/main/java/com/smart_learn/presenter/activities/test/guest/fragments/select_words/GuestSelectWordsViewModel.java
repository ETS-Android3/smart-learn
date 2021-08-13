package com.smart_learn.presenter.activities.test.guest.fragments.select_words;

import android.app.Application;

import androidx.annotation.NonNull;

import com.smart_learn.R;
import com.smart_learn.core.services.test.TestService;
import com.smart_learn.data.entities.Test;
import com.smart_learn.data.room.entities.Word;
import com.smart_learn.presenter.helpers.PresenterUtilities;
import com.smart_learn.presenter.helpers.fragments.words.guest.select.GuestBasicSelectWordsViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import timber.log.Timber;

public class GuestSelectWordsViewModel extends GuestBasicSelectWordsViewModel {

    public GuestSelectWordsViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

    protected void generateTest(@NonNull @NotNull GuestSelectWordsFragment fragment, Test test){
        if(adapter == null){
            liveToastMessage.setValue(fragment.getString(R.string.error_can_not_continue));
            Timber.w("adapter is null");
            return;
        }

        if(test == null){
            liveToastMessage.setValue(fragment.getString(R.string.error_can_not_continue));
            Timber.w("test is null");
            return;
        }

        ArrayList<Word> selectedWords = adapter.getSelectedValues();
        if(selectedWords.isEmpty()){
            liveToastMessage.setValue(fragment.getString(R.string.no_selected_word));
            return;
        }

        fragment.showProgressDialog("", fragment.getString(R.string.generating_test));

        TestService.getInstance().generateGuestWordTest(selectedWords, test, new TestService.TestGenerationCallback() {
            @Override
            public void onSuccess(@NonNull @NotNull String testId) {
                fragment.requireActivity().runOnUiThread(() -> {
                    fragment.closeProgressDialog();

                    // if test is schedule job is finished
                    if(test.isScheduled()){
                        PresenterUtilities.General.showShortToastMessage(fragment.requireContext(), fragment.getString(R.string.success_test_generated));
                        fragment.navigateToGuestScheduledTestsFragment();
                        return;
                    }

                    // otherwise go to test fragment
                    int testIdInteger;
                    try {
                        testIdInteger = Integer.parseInt(testId);
                    }
                    catch (NumberFormatException ex){
                        Timber.w(ex);
                        liveToastMessage.setValue(fragment.getString(R.string.error_can_not_continue));
                        return;
                    }
                    fragment.navigateToTestFragment(test.getType(), testIdInteger);
                });
            }

            @Override
            public void onFailure(@NonNull @NotNull String error) {
                fragment.requireActivity().runOnUiThread(() -> {
                    fragment.closeProgressDialog();
                    liveToastMessage.setValue(error);
                });
            }
        });
    }
}
