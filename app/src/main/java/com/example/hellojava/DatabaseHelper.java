package com.example.hellojava;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME    = "settings.db";
    private static final int    DATABASE_VERSION = 2;

    // Settings table
    private static final String TABLE_SETTINGS    = "settings";
    private static final String COLUMN_KEY        = "setting_key";
    private static final String COLUMN_VALUE      = "value";

    // History table
    private static final String TABLE_HISTORY     = "connection_history";
    private static final String COL_HISTORY_IP    = "ip";
    private static final String COL_HISTORY_PORT  = "port";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create settings table
        String createSettings =
                "CREATE TABLE " + TABLE_SETTINGS + " ("
                        + COLUMN_KEY + " TEXT PRIMARY KEY, "
                        + COLUMN_VALUE + " TEXT)";
        db.execSQL(createSettings);

        // Create connection history table
        String createHistory =
                "CREATE TABLE " + TABLE_HISTORY + " ("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + COL_HISTORY_IP + " TEXT, "
                        + COL_HISTORY_PORT + " INTEGER, "
                        + "UNIQUE(" + COL_HISTORY_IP + ", " + COL_HISTORY_PORT + "))";
        db.execSQL(createHistory);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        if (oldV < 2) {
            // Add history table in v2
            String createHistory =
                    "CREATE TABLE " + TABLE_HISTORY + " ("
                            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                            + COL_HISTORY_IP + " TEXT, "
                            + COL_HISTORY_PORT + " INTEGER, "
                            + "UNIQUE(" + COL_HISTORY_IP + ", " + COL_HISTORY_PORT + "))";
            db.execSQL(createHistory);
        }
    }

    // --- Settings methods ---
    public void setSetting(String key, String value) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_KEY, key);
        cv.put(COLUMN_VALUE, value);
        db.insertWithOnConflict(TABLE_SETTINGS, null, cv,
                SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public String getSetting(String key) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_SETTINGS,
                new String[]{COLUMN_VALUE},
                COLUMN_KEY + " = ?",
                new String[]{key}, null, null, null
        );
        String value = null;
        if (cursor.moveToFirst()) {
            value = cursor.getString(
                    cursor.getColumnIndexOrThrow(COLUMN_VALUE)
            );
        }
        cursor.close();
        db.close();
        return value;
    }

    public String getLastIpAddress() {
        return getSetting("ip");
    }

    public int getApiPort() {
        String p = getSetting("api_port");
        try {
            return (p != null) ? Integer.parseInt(p) : 7125;
        } catch (NumberFormatException ex) {
            return 7125;
        }
    }

    // --- History methods ---
    public void deleteConnection(String ip) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_HISTORY, COL_HISTORY_IP + " = ?", new String[]{ip});
        db.close();
    }

    public void addConnectionToHistory(String ip, int port) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_HISTORY_IP, ip);
        cv.put(COL_HISTORY_PORT, port);
        db.insertWithOnConflict(TABLE_HISTORY, null, cv,
                SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
    }

    public List<String> getAllHistoryIps() {
        SQLiteDatabase db = getReadableDatabase();
        List<String> list = new ArrayList<>();
        Cursor cursor = db.query(
                TABLE_HISTORY,
                new String[]{COL_HISTORY_IP},
                null,null,null,null,
                "id DESC"
        );
        while (cursor.moveToNext()) {
            list.add(cursor.getString(0));
        }
        cursor.close(); db.close();
        return list;
    }

    public List<Integer> getAllHistoryPorts() {
        SQLiteDatabase db = getReadableDatabase();
        List<Integer> list = new ArrayList<>();
        Cursor cursor = db.query(
                TABLE_HISTORY,
                new String[]{COL_HISTORY_PORT},
                null,null,null,null,
                "id DESC"
        );
        while (cursor.moveToNext()) {
            list.add(cursor.getInt(0));
        }
        cursor.close(); db.close();
        return list;
    }
}