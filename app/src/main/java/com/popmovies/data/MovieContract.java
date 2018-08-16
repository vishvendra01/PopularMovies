package com.popmovies.data;

import android.provider.BaseColumns;

/**
 * Author : Vishvendra
 * Version: 1.0
 * 8/16/2018
 */

public final class MovieContract {
    public static final class FavoriteEntry implements BaseColumns {
        public static final String TABLE_NAME = "favorite";

        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER_URL = "poster_url";
        public static final String COLUMN_SYNOPSIS = "synopsis";
        public static final String COLUMN_RATINGS = "ratings";
        public static final String COLUMN_RELEASE_DATE = "release_date";

    }
}
