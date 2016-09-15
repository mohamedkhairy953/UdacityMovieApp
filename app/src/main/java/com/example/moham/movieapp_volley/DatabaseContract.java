package com.example.moham.movieapp_volley;

import android.provider.BaseColumns;

/**
 * Created by moham on 8/29/2016.
 */
public class DatabaseContract {
    public static class MovieTable implements BaseColumns{

        public final static String TABLE="movies";
        public final static String col_backDropPath="backdrop_path";
        public final static String col_OriginalTitle="original_title";
        public final static String col_overview="overview";
        public final static String col_release_date="release_date";
        public final static String col_poster_path="poster_path";
        public final static String col_popularity="popularity";
        public final static String col_title="title";
        public final static String col_vote_average="vote_average";
        public final static String col_vote_count="vote_count";

    }
}
