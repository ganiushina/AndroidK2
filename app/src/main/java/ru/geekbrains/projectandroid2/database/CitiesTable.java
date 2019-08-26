package ru.geekbrains.projectandroid2.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class CitiesTable {
    private final static String TABLE_NAME = "Cities";
    private final static String COLUMN_ID = "_id";
    private final static String COLUMN_CITY = "city";
    private static String COLUMN_NEW = "";


    static void createTable(SQLiteDatabase database) {
        database.execSQL("CREATE TABLE " + TABLE_NAME + " (" + COLUMN_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_CITY + " TEXT );");
    }

    static void onUpgrade(SQLiteDatabase database) {
        database.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_NEW
                + " TEXT DEFAULT 'Default title'");
    }

    public static void addCity(String city, SQLiteDatabase database) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_CITY, city);
        database.insert(TABLE_NAME, null, values);
    }

    public static void editCity(String cityToEdit, String newCity, SQLiteDatabase database) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_CITY, newCity);
        database.execSQL("UPDATE " + TABLE_NAME + " set " + COLUMN_CITY + " = " + newCity + "WHERE "
                + COLUMN_CITY + " = " + cityToEdit + ";");
    }

    public static void deleteCity(String city, SQLiteDatabase database) {
        database.delete(TABLE_NAME, COLUMN_CITY + " = " + city, null);
    }

    public static int findCity(String city, SQLiteDatabase database) {
        Cursor cursor = database.query(TABLE_NAME, null, COLUMN_CITY + " = ?", new String[] {city},
                null, null, null);
        int result = 0 ;
        if ((cursor != null) && (cursor.getCount() > 0)){
            cursor.moveToFirst();
            result = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
            cursor.close();
        }
        return result;
    }

    public static void deleteAll(SQLiteDatabase database) {
        database.delete(TABLE_NAME, null, null);
    }

    public static List<String> getAllCities(SQLiteDatabase database) {
        Cursor cursor = database.query(TABLE_NAME, null, null, null,
                null, null, null);
        return getResultFromCursor(cursor);
    }

    private static List<String> getResultFromCursor(Cursor cursor) {
        List<String> result = null;

        if(cursor != null && cursor.moveToFirst()) {//попали на первую запись, плюс вернулось true, если запись есть
            result = new ArrayList<>(cursor.getCount());

            int cityName = cursor.getColumnIndex(COLUMN_CITY);
            do {
                result.add(cursor.getString(cityName));
            } while (cursor.moveToNext());
        }

        try {
            assert cursor != null;
            cursor.close(); } catch (Exception ignored) {}
        return result == null ? new ArrayList<String>(0) : result;
    }
}
