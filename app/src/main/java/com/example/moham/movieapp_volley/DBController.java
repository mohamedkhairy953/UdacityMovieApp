package com.example.moham.movieapp_volley;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by moham on 8/29/2016.
 */
public class DBController {
    DbHelper helper;
    SQLiteDatabase db;

    public DBController(Context context) {
        this.helper = new DbHelper(context);
    }

    private void open() {
        db = helper.getWritableDatabase();
    }

    public long insert_movie(Movie_model model) {
        open();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.MovieTable._ID, model.getId());
        values.put(DatabaseContract.MovieTable.col_backDropPath, model.getBackdrop_path());
        values.put(DatabaseContract.MovieTable.col_OriginalTitle, model.getOriginal_title());
        values.put(DatabaseContract.MovieTable.col_overview, model.getOverview());
        values.put(DatabaseContract.MovieTable.col_popularity, model.getPopularity());
        values.put(DatabaseContract.MovieTable.col_poster_path, model.getPoster_path());
        values.put(DatabaseContract.MovieTable.col_release_date, model.getRelease_date());
        values.put(DatabaseContract.MovieTable.col_vote_average, model.getVote_average());
        values.put(DatabaseContract.MovieTable.col_vote_count, model.getVote_count());
        values.put(DatabaseContract.MovieTable.col_title, model.getTitle());
        long insert = db.insert(DatabaseContract.MovieTable.TABLE, null, values);
        helper.close();
        return insert;
    }

    public ArrayList<Movie_model> get_all_fav_movies() {
        open();
        ArrayList<Movie_model> movies_arraylist = new ArrayList<>();
        Cursor query = db.query(DatabaseContract.MovieTable.TABLE,
                null,
                null,
                null,
                null,
                null,
                null
        );
        if (query.moveToFirst()) {
            do {
                Movie_model model = new Movie_model();
                model.setBackdrop_path(query.getString(query.getColumnIndex(DatabaseContract.MovieTable.col_backDropPath)));
                model.setOriginal_title(query.getString(query.getColumnIndex(DatabaseContract.MovieTable.col_OriginalTitle)));
                model.setOverview(query.getString(query.getColumnIndex(DatabaseContract.MovieTable.col_overview)));
                model.setPopularity(query.getDouble(query.getColumnIndex(DatabaseContract.MovieTable.col_popularity)));
                model.setRelease_date(query.getString(query.getColumnIndex(DatabaseContract.MovieTable.col_release_date)));
                model.setPoster_path(query.getString(query.getColumnIndex(DatabaseContract.MovieTable.col_poster_path)));
                model.setTitle(query.getString(query.getColumnIndex(DatabaseContract.MovieTable.col_title)));
                model.setVote_average(query.getInt(query.getColumnIndex(DatabaseContract.MovieTable.col_vote_average)));
                model.setVote_count(query.getInt(query.getColumnIndex(DatabaseContract.MovieTable.col_vote_count)));
                model.setId(query.getInt(query.getColumnIndex(DatabaseContract.MovieTable._ID)));
                movies_arraylist.add(model);
            } while (query.moveToNext());
        }
        return movies_arraylist;
    }

    public boolean is_In_MyFav(int id) {
        open();
        Cursor query = db.query(DatabaseContract.MovieTable.TABLE, null, DatabaseContract.MovieTable._ID + "= ?", new String[]{String.valueOf(id)}, null, null, null);
        if (query.moveToFirst()) {
            return true;
        }
        return false;
    }
}
