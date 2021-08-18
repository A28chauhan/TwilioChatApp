package com.carematix.twiliochatapp.architecture.converter;

import androidx.room.TypeConverter;


import com.carematix.twiliochatapp.architecture.table.beans.Links;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public
class LinksTypeConverter {

    @TypeConverter
    public List<Links> fromString(String value) {
        Type listType = new TypeToken<List<Links>>() {}.getType();
        List<Links> genres = new Gson().fromJson(value, listType);
        return genres;
    }

    @TypeConverter
    public String fromList(List<Links> genres) {
        return new Gson().toJson(genres);
    }
}
