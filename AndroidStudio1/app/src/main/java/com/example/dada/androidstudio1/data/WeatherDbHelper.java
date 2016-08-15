package com.example.dada.androidstudio1.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.dada.androidstudio1.data.Contract.LocationColumns;
import com.example.dada.androidstudio1.data.Contract.WeatherColumns;

/**
 * Created by Messi10 on 10-Sep-14.
 */
public class WeatherDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "weather.db";

    public WeatherDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_LOCATION_TABLE="CREATE TABLE" + LocationColumns.TABLE_NAME+" ( " +
                LocationColumns._ID + "INTEGER PRIMARY KEY AUTOINCREMENT," +
                LocationColumns.COLUMN_LOCATION_SETTING + "TEXT UNIQUE NOT NULL" +
                LocationColumns.COLUMN_CITY_NAME + "TEXT NOT NULL" +
                LocationColumns.COLUMN_COORD_LAT +"REAL NOT NULL" +
                LocationColumns.COLUMN_COORD_LONG + "REAL NOT NULL" +
                "(UNIQUE(" + LocationColumns.COLUMN_LOCATION_SETTING + " ) ON CONFLICT IGNORE );";

        final String SQL_CREATE_WEATHER_TABLE = "CREATE TABLE " + WeatherColumns.TABLE_NAME + " (" +
                // Why AutoIncrement here, and not above?
                // Unique keys will be auto-generated in either case.  But for weather
                // forecasting, it's reasonable to assume the user will want information
                // for a certain date and all dates *following*, so the forecast data
                // should be sorted accordingly.
                WeatherColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                // the ID of the location entry associated with this weather data
                WeatherColumns.COLUMN_LOC_KEY + " INTEGER NOT NULL, " +
                WeatherColumns.COLUMN_DATETEXT + " TEXT NOT NULL, " +
                WeatherColumns.COLUMN_SHORT_DESC + " TEXT NOT NULL, " +
                WeatherColumns.COLUMN_WEATHER_ID + " INTEGER NOT NULL," +

                WeatherColumns.COLUMN_MIN_TEMP + " REAL NOT NULL, " +
                WeatherColumns.COLUMN_MAX_TEMP + " REAL NOT NULL, " +

                WeatherColumns.COLUMN_HUMIDITY + " REAL NOT NULL, " +
                WeatherColumns.COLUMN_PRESSURE + " REAL NOT NULL, " +
                WeatherColumns.COLUMN_WIND_SPEED + " REAL NOT NULL, " +
                WeatherColumns.COLUMN_DEGREES + " REAL NOT NULL, " +

                // Set up the location column as a foreign key to location table.
                " FOREIGN KEY (" + WeatherColumns.COLUMN_LOC_KEY + ") REFERENCES " +
                LocationColumns.TABLE_NAME + " (" + LocationColumns._ID + "), " +

                // To assure the application have just one weather entry per day
                // per location, it's created a UNIQUE constraint with REPLACE strategy
                " UNIQUE (" + WeatherColumns.COLUMN_DATETEXT + ", " +
                WeatherColumns.COLUMN_LOC_KEY + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_LOCATION_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_WEATHER_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LocationColumns.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + WeatherColumns.TABLE_NAME);

        onCreate(sqLiteDatabase);
    }
}
