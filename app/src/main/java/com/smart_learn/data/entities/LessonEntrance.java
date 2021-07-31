package com.smart_learn.data.entities;

import com.smart_learn.data.room.entities.helpers.Translation;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LessonEntrance {

    private String value;
    protected ArrayList<Translation> translations;

    public LessonEntrance(String value, ArrayList<Translation> translations) {
        this.value = value;
        this.translations = translations;
    }
}
