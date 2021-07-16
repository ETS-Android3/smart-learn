package com.smart_learn.data.room.entities.helpers;

import androidx.room.ColumnInfo;

import com.smart_learn.core.utilities.CoreUtilities;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NonNull;
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

    public boolean areItemsTheSame(Translation newItem) {
        if(newItem == null){
            return false;
        }
        return CoreUtilities.General.areObjectsTheSame(this.translation, newItem.getTranslation());
    }

    public boolean areContentsTheSame(Translation newItem){
        if(newItem == null){
            return false;
        }
        return CoreUtilities.General.areObjectsTheSame(this.phonetic, newItem.getPhonetic()) &&
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

    public static Translation generateEmptyObject(){
        return new Translation("", "", "");
    }

    @NonNull
    @NotNull
    public static ArrayList<Translation> makeDeepCopy(List<Translation> array){
        if(array == null){
            return new ArrayList<>();
        }
        ArrayList<Translation> tmp = new ArrayList<>();
        for(Translation item : array){
            tmp.add(new Translation(item.getTranslation(), item.getPhonetic(), item.getLanguage()));
        }
        return tmp;
    }
}
