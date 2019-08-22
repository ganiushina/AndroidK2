package ru.geekbrains.projectandroid2.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WeatherTable {
    private final static String TABLE_NAME = "Weather";
    private final static String COLUMN_ID = "_id";
    private final static String COLUMN_CITY_ID = "city_id";
    public final static String COLUMN_PRESSURE = "pressure";
    public final static String COLUMN_HUMIDITY = "humidity";
    public final static String COLUMN_WEATHER = "weather";
    private static String COLUMN_NEW = "";


    static void createTable(SQLiteDatabase database) {
        database.execSQL("CREATE TABLE " + TABLE_NAME + " (" + COLUMN_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_CITY_ID + " INTEGER, "
                + COLUMN_WEATHER + " FLOAT, " + COLUMN_HUMIDITY + " FLOAT, " + COLUMN_PRESSURE + " FLOAT);");
    }

    static void onUpgrade(SQLiteDatabase database) {
        database.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_NEW
                + " TEXT DEFAULT 'Default title'");
    }

    public static void addWeather(int city_id, float pressure, float weathers, float humidity, SQLiteDatabase database) {
       ContentValues values = new ContentValues();
       values.put(COLUMN_CITY_ID, city_id);
       values.put(COLUMN_PRESSURE, pressure);
       values.put(COLUMN_HUMIDITY, humidity);
       values.put(COLUMN_WEATHER, weathers);
       database.insert(TABLE_NAME, null, values);
    }

    public static void editWeather(int city_id, float pressure, float weathers, float humidity, SQLiteDatabase database) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_CITY_ID, city_id);
        values.put(COLUMN_PRESSURE, pressure);
        values.put(COLUMN_HUMIDITY, humidity);
        values.put(COLUMN_WEATHER, weathers);
        String cityId = String.valueOf(city_id);
        database.update(TABLE_NAME, values, COLUMN_CITY_ID + " = ?", new String[] {cityId});
    }

    public static int findWeatherCity(int city_id, SQLiteDatabase database) {
        String cityId = (String.valueOf(city_id));
        Cursor cursor = database.query(TABLE_NAME, null, COLUMN_CITY_ID + " = ?", new String[] {cityId},
                null, null, null);
        int result = 0 ;
        if ((cursor != null) && (cursor.getCount() > 0)){
            cursor.moveToFirst();
            result = cursor.getInt(cursor.getColumnIndex(COLUMN_CITY_ID));
            cursor.close();
        }
        return result;
    }

    public static HashMap<String, String> getCityWeather(int city_id, SQLiteDatabase database) {
        
        Cursor cursor = database.query(TABLE_NAME, null, COLUMN_CITY_ID + " = ?", new String[] {String.valueOf(city_id)},
                null, null, null);
        HashMap<String, String> weather = new HashMap<>();

        if(cursor != null && cursor.moveToFirst()) {

            int weather_temperature = cursor.getColumnIndex(COLUMN_WEATHER);
            int weather_humidity = cursor.getColumnIndex(COLUMN_HUMIDITY);
            int weather_pressure = cursor.getColumnIndex(COLUMN_PRESSURE);            
            do {
                weather.put(COLUMN_WEATHER, cursor.getString(weather_temperature));
                weather.put(COLUMN_HUMIDITY, cursor.getString(weather_humidity));
                weather.put(COLUMN_PRESSURE, cursor.getString(weather_pressure));
            } while (cursor.moveToNext());
        }
       
        return weather;
    }


    public static void deleteWeather(int city_id, SQLiteDatabase database) {
        database.delete(TABLE_NAME, COLUMN_CITY_ID + " = " + city_id, null);
    }

    public static void deleteAll(SQLiteDatabase database) {
        database.delete(TABLE_NAME, null, null);
    }

    public static List<Integer> getAllNotes(SQLiteDatabase database) {
        Cursor cursor = database.query(TABLE_NAME, null, null, null,
                null, null, null);
        return getResultFromCursor(cursor);
    }

    private static List<Integer> getResultFromCursor(Cursor cursor) {
        List<Integer> result = null;

        if(cursor != null && cursor.moveToFirst()) {//попали на первую запись, плюс вернулось true, если запись есть
            result = new ArrayList<>(cursor.getCount());

            int noteIdx = cursor.getColumnIndex(COLUMN_CITY_ID);
            do {
                result.add(cursor.getInt(noteIdx));
            } while (cursor.moveToNext());
        }

        try { cursor.close(); } catch (Exception ignored) {}
        return result == null ? new ArrayList<Integer>(0) : result;
    }
}
