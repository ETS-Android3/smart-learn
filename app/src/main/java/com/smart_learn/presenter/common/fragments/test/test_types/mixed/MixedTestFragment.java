package com.smart_learn.presenter.common.fragments.test.test_types.mixed;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.smart_learn.databinding.FragmentMixedTestBinding;
import com.smart_learn.presenter.common.fragments.test.test_types.BasicTestTypeFragment;

import org.jetbrains.annotations.NotNull;

import timber.log.Timber;


public abstract class MixedTestFragment <VM extends MixedTestViewModel> extends BasicTestTypeFragment<VM> {

    public static String IS_MIXED_LETTERS_TEST_KEY = "IS_MIXED_LETTERS_TEST_KEY";

    protected FragmentMixedTestBinding binding;
    private RecyclerView rvQuestionOptions;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = FragmentMixedTestBinding.inflate(inflater);
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);
        return binding.getRoot();
    }

    @Override
    protected void setViewModel() {
        super.setViewModel();

        if (getArguments() == null){
            Timber.w("arguments are null");
            goBack();
            return;
        }

        viewModel.setMixedLettersTest(getArguments().getBoolean(IS_MIXED_LETTERS_TEST_KEY));
    }

    @Override
    protected void setLayoutUtilities() {
        super.setLayoutUtilities();
        // get views
        rvQuestionOptions = binding.rvFragmentMixedTest;

        // reverse is not needed here
        binding.boardIncludeLayoutFragmentMixedTest.switchReverseIncludeLayoutTestBaseBoard.setVisibility(View.GONE);

        setRecyclerView();

        binding.btnSubmitAnswerFragmentMixedTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.showNextQuestion(MixedTestFragment.this);
            }
        });
    }

    private void setRecyclerView(){
        // https://github.com/google/flexbox-layout
        // https://stackoverflow.com/questions/39436115/auto-fit-according-to-screen-size-in-grid-layout-android/62279449#62279449
        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(requireContext());
        flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
        flexboxLayoutManager.setJustifyContent(JustifyContent.FLEX_END);
        rvQuestionOptions.setLayoutManager(flexboxLayoutManager);

        rvQuestionOptions.setAdapter(viewModel.getAdapter());

        // https://www.youtube.com/watch?v=H9D_HoOeKWM&ab_channel=yoursTRULY
        // https://stackoverflow.com/questions/58159346/how-to-create-recyclerview-drag-and-drop-swap-2-item-positions-version
        final ItemTouchHelper dragHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END,0) {
            @Override
            public boolean onMove(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull @NotNull RecyclerView.ViewHolder target) {
                viewModel.swapAdapterOptionsItems(viewHolder.getAdapterPosition(), target.getAdapterPosition(), recyclerView);
                return false;
            }

            @Override
            public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int direction) {
                // no action needed here
            }

            @Override
            public void clearView(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                viewModel.refreshCurrentOrder(rvQuestionOptions);
            }
        });

        dragHelper.attachToRecyclerView(rvQuestionOptions);
    }

}