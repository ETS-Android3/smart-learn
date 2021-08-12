package com.smart_learn.data.room.entities.helpers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.smart_learn.core.utilities.CoreUtilities;
import com.smart_learn.data.entities.Statistics;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
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

    private long id;

    @NonNull @NotNull
    private String translation;
    @NonNull @NotNull
    private String phonetic;
    @NonNull @NotNull
    private String language;
    @NonNull @NotNull
    private Statistics statistics;

    public Translation(long id, String translation, String phonetic, String language) {
        this.id = id;
        this.translation = translation == null ? "" : translation;
        this.phonetic = phonetic == null ? "" : phonetic;
        this.language = language == null ? "" : language;

        // every new constructed translation will have an empty statistic
        statistics = new Statistics();
    }

    public boolean areItemsTheSame(Translation newItem) {
        if(newItem == null){
            return false;
        }
        return this.id == newItem.getId();
    }

    public boolean areContentsTheSame(Translation newItem){
        if(newItem == null){
            return false;
        }
        return CoreUtilities.General.areObjectsTheSame(this.translation, newItem.getTranslation()) &&
                CoreUtilities.General.areObjectsTheSame(this.phonetic, newItem.getPhonetic()) &&
                CoreUtilities.General.areObjectsTheSame(this.language, newItem.getLanguage()) &&
                CoreUtilities.General.areObjectsTheSame(this.statistics, newItem.getStatistics());
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
        return new Translation(CoreUtilities.General.generateUniqueId(), "", "", "");
    }

    public void setTranslation(String translation) {
        this.translation = translation == null ? "" : translation;
    }

    public void setPhonetic(String phonetic) {
        this.phonetic = phonetic == null ? "" : phonetic;
    }

    public void setLanguage(String language) {
        this.language = language == null ? "" : language;
    }

    public void setStatistics(Statistics statistics) {
        this.statistics = statistics == null ? new Statistics() : statistics;
    }

    @NonNull @NotNull
    public static ArrayList<Translation> makeDeepCopy(List<Translation> array){
        if(array == null){
            return new ArrayList<>();
        }
        ArrayList<Translation> tmp = new ArrayList<>();
        for(Translation item : array){
            Translation copy = new Translation(item.getId(), item.getTranslation(), item.getPhonetic(), item.getLanguage());
            copy.setStatistics(item.getStatistics().makeDeepCopy());

            tmp.add(copy);
        }
        return tmp;
    }

    @NonNull @NotNull
    public static String fromListToJson(ArrayList<Translation> value) {
        if (value == null) {
            value = new ArrayList<>();
        }
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Translation>>() {}.getType();
        String conversion = gson.toJson(value, type);
        return conversion == null ? "" : conversion;
    }

    @NonNull @NotNull
    public static ArrayList<Translation> fromJsonToList(String value) {
        if (value== null) {
            return new ArrayList<>();
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Translation>>() {}.getType();
        ArrayList<Translation> conversion = gson.fromJson(value, type);
        return conversion == null ? new ArrayList<>() : conversion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Translation)) return false;

        Translation that = (Translation) o;

        if (getId() != that.getId()) return false;
        if (!getTranslation().equals(that.getTranslation())) return false;
        if (!getPhonetic().equals(that.getPhonetic())) return false;
        if (!getLanguage().equals(that.getLanguage())) return false;
        return getStatistics().equals(that.getStatistics());
    }

    @Override
    public int hashCode() {
        int result = (int) (getId() ^ (getId() >>> 32));
        result = 31 * result + getTranslation().hashCode();
        result = 31 * result + getPhonetic().hashCode();
        result = 31 * result + getLanguage().hashCode();
        result = 31 * result + getStatistics().hashCode();
        return result;
    }
}
