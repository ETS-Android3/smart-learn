package com.smart_learn.data.guest.room.converters;

import androidx.room.TypeConverter;

import com.smart_learn.data.common.helpers.DataUtilities;

import java.util.ArrayList;

public class BooleanConverter {

    @TypeConverter
    public static String fromListToJson(ArrayList<Boolean> value) {
        return DataUtilities.General.fromListToJson(value);
    }

    @TypeConverter
    public static ArrayList<Boolean> fromStringToList(String value) {
        return DataUtilities.General.fromJsonToList(value);
    }

}
