package com.smart_learn.data.common.entities;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LessonEntrance {

    @NonNull @NotNull
    private String id;

    @NonNull @NotNull
    private String value;
    @NonNull @NotNull
    private ArrayList<Translation> translations;

    // this will be used for generating questions
    @NonNull @NotNull
    private Statistics statistics;

    public LessonEntrance(String id, String value, ArrayList<Translation> translations, Statistics statistics) {
        this.id = id == null ? "" : id;
        this.value = value == null ? "" : value;
        this.translations = translations == null ? new ArrayList<>() : translations;
        this.statistics = statistics == null ? new Statistics() : statistics;
    }

    public void setId(String id) {
        this.id = id == null ? "" : id;
    }

    public void setValue(String value) {
        this.value = value == null ? "" : value;
    }

    public void setTranslations(ArrayList<Translation> translations) {
        this.translations = translations == null ? new ArrayList<>() : translations;
    }

    public void setStatistics(Statistics statistics) {
        this.statistics = statistics == null ? new Statistics() : statistics;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LessonEntrance)) return false;

        LessonEntrance that = (LessonEntrance) o;

        if (!getId().equals(that.getId())) return false;
        if (!getValue().equals(that.getValue())) return false;
        if (!getTranslations().equals(that.getTranslations())) return false;
        return getStatistics().equals(that.getStatistics());
    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + getValue().hashCode();
        result = 31 * result + getTranslations().hashCode();
        result = 31 * result + getStatistics().hashCode();
        return result;
    }
}
