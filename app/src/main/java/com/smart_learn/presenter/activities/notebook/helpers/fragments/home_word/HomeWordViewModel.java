package com.smart_learn.presenter.activities.notebook.helpers.fragments.home_word;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.smart_learn.data.room.entities.Word;
import com.smart_learn.presenter.helpers.view_models.BasicAndroidViewModel;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;

public abstract class HomeWordViewModel extends BasicAndroidViewModel {

    @Getter
    private final MutableLiveData<Word> liveWord;

    public HomeWordViewModel(@NonNull @NotNull Application application) {
        super(application);
        // FIXME: get standard new word
        //liveWord = new MutableLiveData<>(new Word(0,0,0,false,
               // new Translation("",""),""));
        liveWord = new MutableLiveData<>();
    }

    public void setLiveWord(Word word){
        liveWord.setValue(word);
    }
}

