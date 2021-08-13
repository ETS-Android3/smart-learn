package com.smart_learn.presenter.helpers.adapters.test.questions;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.smart_learn.R;
import com.smart_learn.core.utilities.CoreUtilities;
import com.smart_learn.data.entities.Question;
import com.smart_learn.data.entities.QuestionFullWrite;
import com.smart_learn.data.entities.QuestionMixed;
import com.smart_learn.data.entities.QuestionQuiz;
import com.smart_learn.data.entities.QuestionTrueOrFalse;
import com.smart_learn.databinding.LayoutCardViewErrorBinding;
import com.smart_learn.databinding.LayoutCardViewQuestionFullWriteBinding;
import com.smart_learn.databinding.LayoutCardViewQuestionMixedBinding;
import com.smart_learn.databinding.LayoutCardViewQuestionQuizBinding;
import com.smart_learn.databinding.LayoutCardViewQuestionTrueOrFalseBinding;
import com.smart_learn.presenter.helpers.ApplicationController;
import com.smart_learn.presenter.helpers.PresenterHelpers;
import com.smart_learn.presenter.helpers.PresenterUtilities;
import com.smart_learn.presenter.helpers.adapters.helpers.BasicViewHolder;
import com.smart_learn.presenter.helpers.adapters.helpers.ErrorViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import timber.log.Timber;


public class QuestionsAdapter extends ListAdapter<Question, RecyclerView.ViewHolder> implements PresenterHelpers.AdapterHelper {

    private final int questionType;
    private final QuestionsAdapter.Callback adapterCallback;

    public QuestionsAdapter(int questionType, @NonNull @NotNull QuestionsAdapter.Callback adapterCallback) {
        super(new DiffUtil.ItemCallback<Question>(){
            @Override
            public boolean areItemsTheSame(@NonNull Question oldItem, @NonNull Question newItem) {
                return oldItem.areItemsTheSame(newItem);
            }
            @Override
            public boolean areContentsTheSame(@NonNull Question oldItem, @NonNull Question newItem) {
                switch (questionType){
                    case Question.Types.QUESTION_FULL_WRITE:
                        return ((QuestionFullWrite)oldItem).areContentsTheSame((QuestionFullWrite) newItem);
                    case Question.Types.QUESTION_QUIZ:
                        return ((QuestionQuiz)oldItem).areContentsTheSame((QuestionQuiz) newItem);
                    case Question.Types.QUESTION_TRUE_OR_FALSE:
                        return ((QuestionTrueOrFalse)oldItem).areContentsTheSame((QuestionTrueOrFalse) newItem);
                    case Question.Types.QUESTION_MIXED:
                        return ((QuestionMixed)oldItem).areContentsTheSame((QuestionMixed) newItem);
                    default:
                        return oldItem.areContentsTheSame(newItem);
                }
            }
        });
        this.questionType = questionType;
        this.adapterCallback = adapterCallback;
    }

    public void setItems(List<Question> items) {
        // save only questions which are answered
        ArrayList<Question> newList = new ArrayList<>();
        for(Question question : items){
            if(question.isAnswered()){
                newList.add(question);
            }
        }
        submitList(newList);
        adapterCallback.onListLoadAction(newList.isEmpty());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        switch (questionType){
            case Question.Types.QUESTION_FULL_WRITE:
                LayoutCardViewQuestionFullWriteBinding fullWriteHolderBinding = DataBindingUtil.inflate(layoutInflater,
                        R.layout.layout_card_view_question_full_write, parent, false);
                fullWriteHolderBinding.setLifecycleOwner((LifecycleOwner) parent.getContext());

                // link data binding layout with view holder
                QuestionFullWriteViewHolder fullWriteViewHolder = new QuestionFullWriteViewHolder(fullWriteHolderBinding);
                fullWriteHolderBinding.setViewHolder(fullWriteViewHolder);
                return fullWriteViewHolder;

            case Question.Types.QUESTION_QUIZ:
                LayoutCardViewQuestionQuizBinding quizHolderBinding = DataBindingUtil.inflate(layoutInflater,
                        R.layout.layout_card_view_question_quiz, parent, false);
                quizHolderBinding.setLifecycleOwner((LifecycleOwner) parent.getContext());

                // link data binding layout with view holder
                QuestionQuizViewHolder quizViewHolder = new QuestionQuizViewHolder(quizHolderBinding);
                quizHolderBinding.setViewHolder(quizViewHolder);
                return quizViewHolder;

            case Question.Types.QUESTION_TRUE_OR_FALSE:
                LayoutCardViewQuestionTrueOrFalseBinding trueOrFalseHolderBinding = DataBindingUtil.inflate(layoutInflater,
                        R.layout.layout_card_view_question_true_or_false, parent, false);
                trueOrFalseHolderBinding.setLifecycleOwner((LifecycleOwner) parent.getContext());

                // link data binding layout with view holder
                QuestionTrueOrFalseViewHolder trueOrFalseViewHolderViewHolder = new QuestionTrueOrFalseViewHolder(trueOrFalseHolderBinding);
                trueOrFalseHolderBinding.setViewHolder(trueOrFalseViewHolderViewHolder);
                return trueOrFalseViewHolderViewHolder;

            case Question.Types.QUESTION_MIXED:
                LayoutCardViewQuestionMixedBinding mixedHolderBinding = DataBindingUtil.inflate(layoutInflater,
                        R.layout.layout_card_view_question_mixed, parent, false);
                mixedHolderBinding.setLifecycleOwner((LifecycleOwner) parent.getContext());

                // link data binding layout with view holder
                QuestionMixedViewHolder mixedViewHolder = new QuestionMixedViewHolder(mixedHolderBinding);
                mixedHolderBinding.setViewHolder(mixedViewHolder);
                return mixedViewHolder;

            default:
                LayoutCardViewErrorBinding errorHolderBinding = DataBindingUtil.inflate(layoutInflater,
                        R.layout.layout_card_view_error, parent, false);
                errorHolderBinding.setLifecycleOwner((LifecycleOwner) parent.getContext());
                return new ErrorViewHolder(errorHolderBinding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(!PresenterUtilities.Adapters.isGoodAdapterPosition(position)){
            return;
        }

        if(!CoreUtilities.General.isItemNotNull(getItem(position))){
            return;
        }

        switch (questionType){
            case Question.Types.QUESTION_FULL_WRITE:
                ((QuestionFullWriteViewHolder)holder).bind((QuestionFullWrite) getItem(position), position);
                return;
            case Question.Types.QUESTION_QUIZ:
                ((QuestionQuizViewHolder)holder).bind((QuestionQuiz) getItem(position), position);
                return;
            case Question.Types.QUESTION_TRUE_OR_FALSE:
                ((QuestionTrueOrFalseViewHolder)holder).bind((QuestionTrueOrFalse) getItem(position), position);
                return;
            case Question.Types.QUESTION_MIXED:
                ((QuestionMixedViewHolder)holder).bind((QuestionMixed) getItem(position), position);
                return;
            default:
                ((ErrorViewHolder)holder).bind(ApplicationController.getInstance().getString(R.string.error_not_good_question_type));
        }
    }

    @Override
    public void loadMoreData() {
        // no action needed here
    }

    public final class QuestionFullWriteViewHolder extends BasicViewHolder<QuestionFullWrite, LayoutCardViewQuestionFullWriteBinding> {

        private final MutableLiveData<SpannableString> liveQuestionDescription;
        private final MutableLiveData<String> liveCorrectAnswer;
        private final MutableLiveData<String> liveCorrectAnswerReversed;
        private final MutableLiveData<String> liveUserAnswer;

        public QuestionFullWriteViewHolder(@NonNull LayoutCardViewQuestionFullWriteBinding viewHolderBinding) {
            super(viewHolderBinding);
            liveQuestionDescription = new MutableLiveData<>(new SpannableString(""));
            liveCorrectAnswer = new MutableLiveData<>("");
            liveCorrectAnswerReversed = new MutableLiveData<>("");
            liveUserAnswer = new MutableLiveData<>("");
        }

        public LiveData<SpannableString> getLiveQuestionDescription(){
            return liveQuestionDescription;
        }

        public LiveData<String> getLiveUserAnswer(){
            return liveUserAnswer;
        }

        public LiveData<String> getLiveCorrectAnswer() {
            return liveCorrectAnswer;
        }

        public LiveData<String> getLiveCorrectAnswerReversed() {
            return liveCorrectAnswerReversed;
        }

        @Override
        protected QuestionFullWrite getEmptyLiveItemInfo() {
            return QuestionFullWrite.generateEmptyObject();
        }

        @Override
        protected void bind(@NonNull @NotNull QuestionFullWrite item, int position) {
            liveItemInfo.setValue(item);
            if(item.getUserAnswer() == null || item.getUserAnswer().isEmpty()){
                liveUserAnswer.setValue(ApplicationController.getInstance().getString(R.string.no_response_given));
            }
            else{
                liveUserAnswer.setValue(item.getUserAnswer());
            }

            if(item.isReversed()){
                liveQuestionDescription.setValue(getSpecificQuestionDescription(item.getType(), item.getQuestionValueReversed()));

                if(!item.isAnswerCorrect()){
                    // set correct reversed answer only if user did not give a correct response
                    ArrayList<String> correctAnswersReversed = item.getCorrectAnswersReversed();
                    int lim = correctAnswersReversed.size();
                    if(lim < 2){
                        // If is only one answer give answer without index in front. If no answer exist
                        // an empty value will be set.
                        liveCorrectAnswerReversed.setValue(lim < 1 ? "" : correctAnswersReversed.get(0));
                    }
                    else {
                        StringBuilder stringBuilder = new StringBuilder(ApplicationController.getInstance().getString(R.string.any_of_the_followings));
                        stringBuilder.append("\n");
                        for(int i = 0; i < lim; i++){
                            int idx = i + 1;
                            stringBuilder.append((idx)).append(". ").append(correctAnswersReversed.get(i)).append("\n");
                        }
                        liveCorrectAnswerReversed.setValue(stringBuilder.substring(0, stringBuilder.length()).trim());
                    }
                }

            }
            else{
                liveQuestionDescription.setValue(getSpecificQuestionDescription(item.getType(), item.getQuestionValue()));

                if(!item.isAnswerCorrect()){
                    // set correct answer only if user did not give a correct response
                    ArrayList<String> correctAnswers = item.getCorrectAnswers();
                    int lim = correctAnswers.size();
                    if(lim < 2){
                        // If is only one answer give answer without index in front. If no answer exist
                        // an empty value will be set.
                        liveCorrectAnswer.setValue(lim < 1 ? "" : correctAnswers.get(0));
                    }
                    else{
                        StringBuilder stringBuilder = new StringBuilder(ApplicationController.getInstance().getString(R.string.any_of_the_followings));
                        stringBuilder.append("\n");
                        for(int i = 0; i < lim; i++){
                            int idx = i + 1;
                            stringBuilder.append(idx).append(". ").append(correctAnswers.get(i)).append("\n");
                        }
                        liveCorrectAnswer.setValue(stringBuilder.substring(0, stringBuilder.length()).trim());
                    }
                }
            }
        }
    }

    public final class QuestionQuizViewHolder extends BasicViewHolder<QuestionQuiz, LayoutCardViewQuestionQuizBinding> {

        private static final int NEUTRAL = 0;
        private static final int CORRECT = 1;
        private static final int WRONG = 2;

        private final MutableLiveData<SpannableString> liveQuestionDescription;
        private final MutableLiveData<String> liveUserAnswer;
        private final MutableLiveData<String> liveCorrectAnswer;

        private final ArrayList<Button> layoutButtons;
        // these will be used to mark buttons in specific way
        private final ArrayList<Integer> optionsStatus;

        public QuestionQuizViewHolder(@NonNull LayoutCardViewQuestionQuizBinding viewHolderBinding) {
            super(viewHolderBinding);
            liveQuestionDescription = new MutableLiveData<>(new SpannableString(""));
            liveUserAnswer = new MutableLiveData<>("");
            liveCorrectAnswer = new MutableLiveData<>("");


            layoutButtons = new ArrayList<>();
            layoutButtons.add(viewHolderBinding.btnOptionALayoutCardViewQuestionQuiz);
            layoutButtons.add(viewHolderBinding.btnOptionBLayoutCardViewQuestionQuiz);
            layoutButtons.add(viewHolderBinding.btnOptionCLayoutCardViewQuestionQuiz);
            layoutButtons.add(viewHolderBinding.btnOptionDLayoutCardViewQuestionQuiz);

            // by default all options are neutral
            optionsStatus = new ArrayList<>(Collections.nCopies(layoutButtons.size(), NEUTRAL));
        }

        public LiveData<SpannableString> getLiveQuestionDescription(){
            return liveQuestionDescription;
        }

        public LiveData<String> getLiveCorrectAnswer(){
            return liveCorrectAnswer;
        }

        public LiveData<String> getLiveUserAnswer(){
            return liveUserAnswer;
        }

        @Override
        protected QuestionQuiz getEmptyLiveItemInfo() {
            return QuestionQuiz.generateEmptyObject();
        }

        @Override
        protected void bind(@NonNull @NotNull QuestionQuiz item, int position) {
            if(layoutButtons.size() != item.getOptions().size()){
                Timber.e("not enough buttons in layout");
                return;
            }

            int userAnswer = item.getUserAnswer();
            optionsStatus.set(userAnswer, WRONG);
            ArrayList<Integer> correctAnswers = item.isReversed() ? item.getCorrectAnswersReversed() : item.getCorrectAnswers();
            for(Integer answer : correctAnswers){
                optionsStatus.set(answer, CORRECT);
                if(answer.equals(userAnswer)){
                    optionsStatus.set(userAnswer, CORRECT);
                }
            }

            int lim = optionsStatus.size();
            if(lim != layoutButtons.size()){
                Timber.e("not enough buttons in layout");
                return;
            }
            for(int i = 0; i < lim; i++){
                String optionText = item.isReversed() ? item.getOptionsReversed().get(i) : item.getOptions().get(i);
                setButton(layoutButtons.get(i), optionsStatus.get(i), optionText, i);
            }

            liveItemInfo.setValue(item);
            liveUserAnswer.setValue(QuestionQuiz.getStringAnswerValues(new ArrayList<>(Collections.singletonList(item.getUserAnswer()))));
            if(item.isReversed()){
                liveQuestionDescription.setValue(getSpecificQuestionDescription(item.getType(), item.getQuestionValueReversed()));
                liveCorrectAnswer.setValue(QuestionQuiz.getStringAnswerValues(item.getCorrectAnswersReversed()));
            }
            else{
                liveQuestionDescription.setValue(getSpecificQuestionDescription(item.getType(), item.getQuestionValue()));
                liveCorrectAnswer.setValue(QuestionQuiz.getStringAnswerValues(item.getCorrectAnswers()));
            }
        }

        private void setButton(Button button, int option, String text, int buttonIndex) {
            Drawable icon = null;
            switch (buttonIndex){
                case QuestionQuiz.INDEX_OPTION_A:
                    icon = ContextCompat.getDrawable(viewHolderBinding.getRoot().getContext(), R.drawable.img_letter_a_white_16);
                    break;
                case QuestionQuiz.INDEX_OPTION_B:
                    icon = ContextCompat.getDrawable(viewHolderBinding.getRoot().getContext(), R.drawable.img_letter_b_white_16);
                    break;
                case QuestionQuiz.INDEX_OPTION_C:
                    icon = ContextCompat.getDrawable(viewHolderBinding.getRoot().getContext(), R.drawable.img_letter_c_white_16);
                    break;
                case QuestionQuiz.INDEX_OPTION_D:
                    icon = ContextCompat.getDrawable(viewHolderBinding.getRoot().getContext(), R.drawable.img_letter_d_white_16);
                    break;
            }

            button.setText(text);
            switch (option){
                case CORRECT:
                    Drawable correctAnswerIcon = ContextCompat.getDrawable(viewHolderBinding.getRoot().getContext(), R.drawable.ic_baseline_done_24);
                    if(correctAnswerIcon != null){
                        correctAnswerIcon.setTint(Color.WHITE);
                    }
                    button.setCompoundDrawablesWithIntrinsicBounds(icon, null, correctAnswerIcon, null);
                    button.setBackground(ContextCompat.getDrawable(viewHolderBinding.getRoot().getContext(), R.drawable.background_button_quiz_correct_answer));
                    button.setTextColor(ApplicationController.getInstance().getColor(R.color.white));
                    break;
                case WRONG:
                    Drawable wrongAnswerIcon = ContextCompat.getDrawable(viewHolderBinding.getRoot().getContext(), R.drawable.ic_baseline_close_24);
                    if(wrongAnswerIcon != null){
                        wrongAnswerIcon.setTint(Color.WHITE);
                    }
                    button.setCompoundDrawablesWithIntrinsicBounds(icon, null, wrongAnswerIcon, null);
                    button.setBackground(ContextCompat.getDrawable(viewHolderBinding.getRoot().getContext(), R.drawable.background_button_quiz_wrong_answer));
                    button.setTextColor(ApplicationController.getInstance().getColor(R.color.white));
                    break;
                default:
                    button.setBackground(ContextCompat.getDrawable(viewHolderBinding.getRoot().getContext(), R.drawable.background_button_quiz_neutral_option));
                    button.setTextColor(ApplicationController.getInstance().getColor(R.color.black));
            }
        }
    }

    public final class QuestionTrueOrFalseViewHolder extends BasicViewHolder<QuestionTrueOrFalse, LayoutCardViewQuestionTrueOrFalseBinding> {

        private final MutableLiveData<SpannableString> liveQuestionDescription;
        private final MutableLiveData<String> liveUserAnswer;
        private final MutableLiveData<String> liveCorrectAnswer;
        private final MutableLiveData<String> liveOption;

        public QuestionTrueOrFalseViewHolder(@NonNull LayoutCardViewQuestionTrueOrFalseBinding viewHolderBinding) {
            super(viewHolderBinding);
            liveQuestionDescription = new MutableLiveData<>(new SpannableString(""));
            liveUserAnswer = new MutableLiveData<>("");
            liveCorrectAnswer = new MutableLiveData<>("");
            liveOption = new MutableLiveData<>("");
        }

        public LiveData<SpannableString> getLiveQuestionDescription(){
            return liveQuestionDescription;
        }

        public LiveData<String> getLiveCorrectAnswer(){
            return liveCorrectAnswer;
        }

        public LiveData<String> getLiveOption(){
            return liveOption;
        }

        public LiveData<String> getLiveUserAnswer(){
            return liveUserAnswer;
        }

        @Override
        protected QuestionTrueOrFalse getEmptyLiveItemInfo() {
            return QuestionTrueOrFalse.generateEmptyObject();
        }

        @Override
        protected void bind(@NonNull @NotNull QuestionTrueOrFalse item, int position) {
            liveItemInfo.setValue(item);
            liveUserAnswer.setValue(QuestionTrueOrFalse.getStringAnswerValues(item.getUserAnswer()));

            if(item.isReversed()){
                liveQuestionDescription.setValue(getSpecificQuestionDescription(item.getType(), item.getQuestionValueReversed()));
                liveCorrectAnswer.setValue(QuestionTrueOrFalse.getStringAnswerValues(item.getCorrectAnswerReversed()));
                liveOption.setValue(item.getOptionReversed());
            }
            else{
                liveQuestionDescription.setValue(getSpecificQuestionDescription(item.getType(), item.getQuestionValue()));
                liveCorrectAnswer.setValue(QuestionTrueOrFalse.getStringAnswerValues(item.getCorrectAnswer()));
                liveOption.setValue(item.getOption());
            }
        }
    }

    public final class QuestionMixedViewHolder extends BasicViewHolder<QuestionMixed, LayoutCardViewQuestionMixedBinding> {

        private final MutableLiveData<SpannableString> liveQuestionDescription;
        private final MutableLiveData<String> liveUserAnswer;
        private final MutableLiveData<String> liveCorrectAnswer;

        public QuestionMixedViewHolder(@NonNull LayoutCardViewQuestionMixedBinding viewHolderBinding) {
            super(viewHolderBinding);
            liveQuestionDescription = new MutableLiveData<>(new SpannableString(""));
            liveUserAnswer = new MutableLiveData<>("");
            liveCorrectAnswer = new MutableLiveData<>("");
        }

        public LiveData<SpannableString> getLiveQuestionDescription(){
            return liveQuestionDescription;
        }

        public LiveData<String> getLiveCorrectAnswer(){
            return liveCorrectAnswer;
        }

        public LiveData<String> getLiveUserAnswer(){
            return liveUserAnswer;
        }

        @Override
        protected QuestionMixed getEmptyLiveItemInfo() {
            return QuestionMixed.generateEmptyObject();
        }

        @Override
        protected void bind(@NonNull @NotNull QuestionMixed item, int position) {
            liveItemInfo.setValue(item);
            liveQuestionDescription.setValue(getSpecificQuestionDescription(item.getType(), item.getQuestionValue()));
            liveCorrectAnswer.setValue(QuestionMixed.convertOrderToString(item.getCorrectAnswerOrder()));
            liveUserAnswer.setValue(QuestionMixed.convertOrderToString(item.getUserAnswerOrder()));
        }
    }

    private SpannableString getSpecificQuestionDescription(int type, String questionValue){
        if(questionValue == null){
            questionValue = "";
        }
        // https://stackoverflow.com/questions/14371092/how-to-make-a-specific-text-on-textview-bold/14371141#14371141
        SpannableStringBuilder spannedValue = new SpannableStringBuilder(Question.getQuestionDescription(type) + ":");
        spannedValue.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, spannedValue.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        return new SpannableString(spannedValue.append(" ").append(questionValue));
    }

    public interface Callback {
        void onListLoadAction(boolean isEmpty);
    }

}