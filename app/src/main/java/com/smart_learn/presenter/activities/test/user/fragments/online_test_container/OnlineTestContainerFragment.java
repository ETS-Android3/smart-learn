package com.smart_learn.presenter.activities.test.user.fragments.online_test_container;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.smart_learn.R;
import com.smart_learn.core.services.UserService;
import com.smart_learn.data.entities.Question;
import com.smart_learn.data.entities.Test;
import com.smart_learn.databinding.FragmentOnlineTestContainerBinding;
import com.smart_learn.presenter.activities.test.TestActivity;
import com.smart_learn.presenter.activities.test.helpers.fragments.test_questions.TestQuestionsFragment;
import com.smart_learn.presenter.activities.test.user.UserTestActivity;
import com.smart_learn.presenter.activities.test.user.UserTestSharedViewModel;
import com.smart_learn.presenter.activities.test.user.fragments.online_test_container.fragments.test_questions.UserOnlineTestQuestionsFragment;
import com.smart_learn.presenter.helpers.PresenterUtilities;
import com.smart_learn.presenter.helpers.fragments.helpers.BasicFragment;
import com.smart_learn.presenter.helpers.fragments.test_finalize.FinalizeTestFragment;
import com.smart_learn.presenter.helpers.fragments.test_types.BasicTestTypeFragment;
import com.smart_learn.presenter.helpers.fragments.test_types.mixed.MixedTestFragment;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import timber.log.Timber;


public class OnlineTestContainerFragment extends BasicFragment<OnlineTestContainerViewModel> {

    @Getter
    protected FragmentOnlineTestContainerBinding binding;
    protected UserTestSharedViewModel sharedViewModel;
    private NavController nestedNavController;

    private Snackbar snackbar;
    private IntentFilter intentFilter;
    private BroadcastReceiver broadcastReceiver;

    @NonNull
    @Override
    protected @NotNull Class<OnlineTestContainerViewModel> getModelClassForViewModel() {
        return OnlineTestContainerViewModel.class;
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setViewModel();
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = FragmentOnlineTestContainerBinding.inflate(inflater);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // try to set navigation graph
        // https://stackoverflow.com/questions/52540303/android-jetpack-navigation-with-viewpager-and-tablayout/62530288#62530288
        NavHostFragment nestedNavHostFragment = (NavHostFragment) getChildFragmentManager().findFragmentById(R.id.nested_nav_host_fragment_online_test_container);
        if(nestedNavHostFragment != null){
            nestedNavController = nestedNavHostFragment.getNavController();
        }

        // if navigation graph cannot be set, then stop activity
        if(nestedNavController == null){
            PresenterUtilities.General.showShortToastMessage(this.requireContext(), getString(R.string.error_loading_screen));
            // If navigation cannot be opened finish, because navigation cannot be done.
            requireActivity().onBackPressed();
            return;
        }

        setLayoutUtilities();
        setBroadcastReceiver();

    }

    protected void setLayoutUtilities(){
        // set tab listener
        binding.tabLayoutFragmentOnlineTestContainer.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                switch (position){
                    case 0: // fot participants
                        nestedNavController.navigate(R.id.online_test_participants_fragment_nested_nav_graph_fragment_online_test_container, null);
                        break;
                    case 1: // for chat
                        nestedNavController.navigate(R.id.online_test_messages_container_fragment_nested_nav_graph_fragment_online_test_container, null);
                        break;
                    case 2: // for test progress
                        if(sharedViewModel.isSelectedOnlineTestFinished()){
                            // if test is finished then is current user test so give directly his UID as participantTestId
                            goToUserTestResultsFragment(UserService.getInstance().getUserUid(), sharedViewModel.getSelectedOnlineTestType(), false);
                        }
                        else{
                            goToTestTypeFragment(sharedViewModel.getSelectedOnlineContainerTestId());
                        }
                        break;
                    default:
                        Timber.w("Position " + position + " is not set");
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // no action needed here
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // no action needed here
            }
        });
    }

    private void setBroadcastReceiver(){
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                viewModel.onSignalReceived(OnlineTestContainerFragment.this);
            }
        };
        intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((UserTestActivity)requireActivity()).hideBottomNavigationMenu();
        ((TestActivity<?>)requireActivity()).hideToolbar();
        sharedViewModel.setOnlineTestContainerFragmentActive(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireActivity().registerReceiver(broadcastReceiver, intentFilter);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        ((TestActivity<?>)requireActivity()).showToolbar();
        sharedViewModel.setOnlineTestContainerFragmentActive(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireActivity().unregisterReceiver(broadcastReceiver);
        }
    }

    @Override
    protected void setViewModel(){
        super.setViewModel();
        sharedViewModel = new ViewModelProvider(requireActivity()).get(UserTestSharedViewModel.class);
    }

    private void goToTestTypeFragment(String containerTestId){
        if(containerTestId == null || containerTestId.isEmpty()){
            Timber.w("participantTestId can not be null or empty");
            PresenterUtilities.General.showShortToastMessage(requireContext(), getString(R.string.error_page_can_not_be_opened));
            return;
        }

        Bundle args = new Bundle();
        args.putString(BasicTestTypeFragment.TEST_ID_KEY, containerTestId);

        int type = sharedViewModel.getSelectedOnlineTestType();
        switch (type){
            case Test.Types.WORD_WRITE:
                nestedNavController.navigate(R.id.user_full_write_online_test_fragment_nested_nav_graph_fragment_online_test_container, args);
                break;
            case Test.Types.WORD_QUIZ:
                nestedNavController.navigate(R.id.user_quiz_online_test_fragment_nested_nav_graph_fragment_online_test_container, args);
                break;
            case Test.Types.WORD_MIXED_LETTERS:
                args.putBoolean(MixedTestFragment.IS_MIXED_LETTERS_TEST_KEY, true);
                nestedNavController.navigate(R.id.user_mixed_online_test_fragment_nested_nav_graph_fragment_online_test_container, args);
                break;
            case Test.Types.EXPRESSION_MIXED_WORDS:
                args.putBoolean(MixedTestFragment.IS_MIXED_LETTERS_TEST_KEY, false);
                nestedNavController.navigate(R.id.user_mixed_online_test_fragment_nested_nav_graph_fragment_online_test_container, args);
                break;
            case Test.Types.EXPRESSION_TRUE_OR_FALSE:
                nestedNavController.navigate(R.id.user_true_or_false_online_test_fragment_nested_nav_graph_fragment_online_test_container, args);
                break;
            default:
                PresenterUtilities.General.showShortToastMessage(requireContext(), getString(R.string.error_page_can_not_be_opened));
        }
    }

    public void goToFinalizeTestFragment(String participantTestId, int testType, int correctAnsweredQuestions, int totalQuestions){
        if (totalQuestions <= 0) {
            Timber.w("totalQuestions [" + totalQuestions + "] is not valid");
            PresenterUtilities.General.showShortToastMessage(requireContext(), getString(R.string.error_can_not_continue));
            return;
        }

        if (correctAnsweredQuestions < 0 || correctAnsweredQuestions > totalQuestions) {
            Timber.w("correctAnsweredQuestions [" + correctAnsweredQuestions + "] is not valid");
            PresenterUtilities.General.showShortToastMessage(requireContext(), getString(R.string.error_can_not_continue));
            return;
        }

        switch (testType){
            case Test.Types.WORD_WRITE:
            case Test.Types.WORD_QUIZ:
            case Test.Types.WORD_MIXED_LETTERS:
            case Test.Types.EXPRESSION_MIXED_WORDS:
            case Test.Types.EXPRESSION_TRUE_OR_FALSE:
                break;
            default:
                Timber.w("testType [" + testType + "] is not valid");
                PresenterUtilities.General.showShortToastMessage(requireContext(), getString(R.string.error_can_not_continue));
                return;
        }

        // mark that test is finalized
        sharedViewModel.setSelectedOnlineTestFinished(true);

        Bundle args = new Bundle();
        args.putString(FinalizeTestFragment.TEST_ID_KEY, participantTestId);
        args.putInt(FinalizeTestFragment.TEST_TYPE_KEY, testType);
        args.putInt(FinalizeTestFragment.TEST_TOTAL_QUESTIONS_KEY, totalQuestions);
        args.putInt(FinalizeTestFragment.TEST_CORRECT_ANSWERS_KEY, correctAnsweredQuestions);
        nestedNavController.navigate(R.id.user_finalize_online_test_fragment_nested_nav_graph_fragment_online_test_container, args);
    }

    public void goToUserTestResultsFragment(String participantTestId, int testType, boolean openAsNewFragment){
        if(openAsNewFragment){
            ((UserTestActivity)requireActivity()).goToUserTestResultsFragment(sharedViewModel.getSelectedOnlineContainerTestId(), participantTestId,
                    testType, true);
            return;
        }

        Bundle args = new Bundle();
        //  sharedViewModel.getSelectedOnlineTestId() is container test id
        args.putString(TestQuestionsFragment.SELECTED_TEST_KEY, sharedViewModel.getSelectedOnlineContainerTestId());
        args.putString(UserOnlineTestQuestionsFragment.PARTICIPANT_TEST_ONLINE_KEY_ID, participantTestId);
        switch (testType){
            case Test.Types.WORD_WRITE:
                args.putInt(TestQuestionsFragment.QUESTION_TYPE_KEY, Question.Types.QUESTION_FULL_WRITE);
                break;
            case Test.Types.WORD_QUIZ:
                args.putInt(TestQuestionsFragment.QUESTION_TYPE_KEY, Question.Types.QUESTION_QUIZ);
                break;
            case Test.Types.EXPRESSION_TRUE_OR_FALSE:
                args.putInt(TestQuestionsFragment.QUESTION_TYPE_KEY, Question.Types.QUESTION_TRUE_OR_FALSE);
                break;
            // both WORD_MIXED_LETTERS and EXPRESSION_MIXED_WORDS will have same question type
            case Test.Types.WORD_MIXED_LETTERS:
            case Test.Types.EXPRESSION_MIXED_WORDS:
                args.putInt(TestQuestionsFragment.QUESTION_TYPE_KEY, Question.Types.QUESTION_MIXED);
                break;

            default:
                PresenterUtilities.General.showShortToastMessage(requireContext(), getString(R.string.error_can_not_continue));
                Timber.w("test type [" + testType + "] is not valid");
                return;
        }

        nestedNavController.navigate(R.id.user_online_test_questions_fragment_nested_nav_graph_fragment_online_test_container, args);
    }

    protected void showSnackBar(int messageId){

        snackbar = Snackbar.make(binding.getRoot(), messageId, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.close, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // no action needed here
                    }
                });
        snackbar.show();
    }

    protected void disableSnackBar(){
        if (snackbar != null){
            snackbar.dismiss();
            snackbar = null;
        }
    }
}