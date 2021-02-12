package com.smart_learn.models.room.entities.helpers;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Translation {

    private String translation;
    private String phonetic;

    public Translation(String translation, String phonetic) {
        this.translation = translation;
        this.phonetic = phonetic;
    }
}
