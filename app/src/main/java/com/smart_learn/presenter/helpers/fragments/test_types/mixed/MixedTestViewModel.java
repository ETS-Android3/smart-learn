package com.smart_learn.presenter.helpers.fragments.test_types.mixed;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.smart_learn.data.entities.Question;
import com.smart_learn.data.entities.QuestionMixed;
import com.smart_learn.data.entities.Test;
import com.smart_learn.data.helpers.DataUtilities;
import com.smart_learn.presenter.helpers.adapters.mixed_question.MixedQuestionOptionsAdapter;
import com.smart_learn.presenter.helpers.fragments.test_types.BasicTestTypeViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import lombok.Getter;


public abstract class MixedTestViewModel extends BasicTestTypeViewModel {

    private boolean isMixedLettersTest;
    // at this moment is not used in view but keep it for debug
    private final MutableLiveData<String> liveCurrentOrder;

    @NonNull
    @NotNull
    @Getter
    private final MixedQuestionOptionsAdapter adapter;

    public MixedTestViewModel(@NonNull @NotNull Application application) {
        super(application);
        isMixedLettersTest = false;
        liveCurrentOrder = new MutableLiveData<>("");
        adapter = new MixedQuestionOptionsAdapter();
    }

    public LiveData<String> getLiveCurrentOrder(){
        return liveCurrentOrder;
    }

    protected void setMixedLettersTest(boolean value){
        isMixedLettersTest = value;
        adapter.setLiveIsMixedLetterTest(isMixedLettersTest);
    }

    @Override
    protected ArrayList<Question> getAllTestQuestions(@NonNull @NotNull Test test) {
        return new ArrayList<>(QuestionMixed.fromJsonToList(test.getQuestionsJson()));
    }

    @Override
    protected void showCustomValues(@NonNull @NotNull Question currentQuestion, boolean isReversed) {
        adapter.setAdapterOrderList(((QuestionMixed) currentQuestion).getStartOrder());
        liveCurrentOrder.setValue(QuestionMixed.convertOrderToString(adapter.getAdapterOrderList(), isMixedLettersTest));
    }

    @Override
    protected Question getProcessedQuestion(@NonNull @NotNull Question question, boolean isReversed) {
        ((QuestionMixed)question).setUserAnswer(adapter.getAdapterOrderList());
        adapter.setAdapterOrderList(new ArrayList<>());
        return question;
    }

    @Override
    protected String getQuestionsJson(@NonNull @NotNull ArrayList<Question> questions) {
        ArrayList<QuestionMixed> tmp = new ArrayList<>();
        for(Question item : questions){
            tmp.add((QuestionMixed)item);
        }
        return DataUtilities.General.fromListToJson(tmp);
    }

    protected void swapAdapterOptionsItems(int fromPosition, int toPosition, RecyclerView recyclerView){
        // notify that move is made (this is used to show drag animation)
        adapter.notifyItemMoved(fromPosition, toPosition);
        // Update adapter list when move is made, because notifyItemMoved() does not update list. Only
        // move view holders to one position to another.
        refreshCurrentOrder(recyclerView);
    }

    protected void refreshCurrentOrder(RecyclerView recyclerView){
        // Update adapter list, by getting all view holders in order and set new information on adapter
        // list.
        // FIXME: Find another way to update in moment/after drag moves, because if will be to much
        //  view holders in recycler view then this refresh wil not be correct because only VISIBLE
        //  view holders will be considered. So if hidden view holders exist then adapter list will
        //  not be updated correctly because positions for hidden adapters will be passed, because no
        //  view holder will be found (being INVISIBLE ==> do not exist in recycler view).
        // https://stackoverflow.com/questions/58159346/how-to-create-recyclerview-drag-and-drop-swap-2-item-positions-version
        // https://stackoverflow.com/questions/34742272/recyclerview-get-all-existing-views-viewholders/34742514#34742514
        int lim = recyclerView.getChildCount();
        for(int i = 0; i < lim; i++){
            MixedQuestionOptionsAdapter.OptionViewHolder viewHolder =
                    (MixedQuestionOptionsAdapter.OptionViewHolder) recyclerView.getChildViewHolder(recyclerView.getChildAt(i));
            adapter.getAdapterOrderList().set(i, viewHolder.getOptionValue());
        }
        liveCurrentOrder.setValue(QuestionMixed.convertOrderToString(adapter.getAdapterOrderList(), isMixedLettersTest));
    }

}
