package com.smart_learn.data.room.converters;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.smart_learn.data.room.entities.helpers.Translation;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class TranslationConverter {
    @TypeConverter
    public static String fromListToString(ArrayList<Translation> value) {
        if (value== null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Translation>>() {}.getType();
        return gson.toJson(value, type);
    }

    @TypeConverter
    public static ArrayList<Translation> fromStringToList(String value) {
        if (value== null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Translation>>() {}.getType();
        return gson.fromJson(value, type);
    }
}

