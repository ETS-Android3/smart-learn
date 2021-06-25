package com.smart_learn.data.room.entities.helpers;

import androidx.room.ColumnInfo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Translation {

    @ColumnInfo(name = "translation")
    private String translation;

    @ColumnInfo(name = "phonetic")
    private String phonetic;

    @ColumnInfo(name = "language")
    private String language;

    public Translation(String translation, String phonetic, String language) {
        this.translation = translation;
        this.phonetic = phonetic;
        this.language = language;
    }
}
