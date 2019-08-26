package ru.geekbrains.projectandroid2.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelperWeather extends SQLiteOpenHelper {

    private static final String DATABASE_WEATHER_NAME = "weather.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelperWeather(Context context) {
        super(context, DATABASE_WEATHER_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        WeatherTable.createTable(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        WeatherTable.onUpgrade(sqLiteDatabase);
    }
}
