package com.berkaysenkoylu.artbook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DBHandler extends SQLiteOpenHelper {
    private static final String DB_NAME = "artdb";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "arts";

    public DBHandler(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + "'id' INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "'artname' TEXT,"
                + "'paintername' TEXT,"
                + "'imageUri' TEXT,"
                + "'year' TEXT)";

        db.execSQL(query);
    }

    public long addNewArt(String art_name, String painter_name, String img_uri, String year) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("artname", art_name);
        values.put("paintername", painter_name);
        values.put("imageUri", img_uri);
        values.put("year", year);

        long id = db.insert(TABLE_NAME, null, values);

        db.close();

        return id;
    }

    public void editArt(int artId, String art_name, String painter_name, String img_uri, String year) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("artname", art_name);
        values.put("paintername", painter_name);
        values.put("imageUri", img_uri);
        values.put("year", year);

        db.update(TABLE_NAME, values, "id = " + artId, null);
    }

    public int deleteArtWithId(int artId) {
        SQLiteDatabase db = this.getWritableDatabase();

        int deletedItem = db.delete(TABLE_NAME, "id = " + artId, null);

        return deletedItem;
    }

    public ArrayList<Art> getArtList() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursorArt = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        ArrayList<Art> artArrayList = new ArrayList<Art>();

        if (cursorArt.moveToFirst()) {
            do {
                // on below line we are adding the data from cursor to our array list.
                artArrayList.add(new Art(cursorArt.getInt(0),
                         cursorArt.getString(1),
                        cursorArt.getString(2),
                        cursorArt.getString(3),
                        cursorArt.getString(4)));
            } while (cursorArt.moveToNext());

            cursorArt.close();
        }

        return artArrayList;
    }

    public Art getOneArtWithName(String artName) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursorArt = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE artname = '" + artName + "'", null);

        if (cursorArt.moveToFirst()) {
            Art fetchedArt = new Art(cursorArt.getInt(0),
                    cursorArt.getString(1),
                    cursorArt.getString(2),
                    cursorArt.getString(3),
                    cursorArt.getString(4));
            return fetchedArt;
        }

        cursorArt.close();

        // if no art piece is found
        return null;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this method is called to check if the table exists already.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
