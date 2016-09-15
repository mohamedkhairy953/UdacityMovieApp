package com.example.moham.movieapp_volley;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by moham on 8/29/2016.
 */
public class DbHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "moviesdb";
    public static final int DB_VERSION = 1;

    public DbHelper(Context c) {

        super(c, DB_NAME, null, DB_VERSION);
    }

    private String CREATE_STAT = "CREATE TABLE " + DatabaseContract.MovieTable.TABLE + "(" +
            DatabaseContract.MovieTable._ID + " INTEGER PRIMARY KEY," +
            DatabaseContract.MovieTable.col_backDropPath + " TEXT NOT NULL," +
            DatabaseContract.MovieTable.col_OriginalTitle + " TEXT NOT NULL," +
            DatabaseContract.MovieTable.col_overview + " TEXT NOT NULL," +
            DatabaseContract.MovieTable.col_popularity + " NUMBER NOT NULL," +
            DatabaseContract.MovieTable.col_poster_path + " TEXT NOT NULL," +
            DatabaseContract.MovieTable.col_title + " TEXT NOT NULL," +
            DatabaseContract.MovieTable.col_vote_average + " INTEGER NOT NULL," +
            DatabaseContract.MovieTable.col_vote_count + " INTEGER NOT NULL," +
            DatabaseContract.MovieTable.col_release_date + " TEXT NOT NULL);";


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_STAT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.MovieTable.TABLE);
        onCreate(db);

    }
}
