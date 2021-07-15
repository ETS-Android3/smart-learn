package com.smart_learn.data.room.entities.helpers;

import androidx.room.ColumnInfo;

import com.smart_learn.core.utilities.CoreUtilities;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
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

    public boolean areContentsTheSame(Translation newItem){
        if(newItem == null){
            return false;
        }
        return CoreUtilities.General.areObjectsTheSame(this.translation, newItem.getTranslation()) &&
                CoreUtilities.General.areObjectsTheSame(this.phonetic, newItem.getPhonetic()) &&
                CoreUtilities.General.areObjectsTheSame(this.language, newItem.getLanguage());
    }

    public static boolean areContentsTheSame(ArrayList<Translation> arrayA, ArrayList<Translation> arrayB){
        if(arrayA == null && arrayB == null){
            return true;
        }

        if(arrayA == null || arrayB == null){
            return false;
        }

        if(arrayA.size() != arrayB.size()){
            return false;
        }

        int lim = arrayA.size();
        for(int i = 0; i < lim; i++){
            if(!arrayA.get(i).areContentsTheSame(arrayB.get(i))){
                return false;
            }
        }

        return true;
    }
}
