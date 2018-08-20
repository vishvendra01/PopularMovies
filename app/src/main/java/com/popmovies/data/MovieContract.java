package com.popmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Author : Vishvendra
 * Version: 1.0
 * 8/16/2018
 */

public final class MovieContract {
    public static final String AUTHORITY = "com.popmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_FAVORITE = "favorite";

    public static final class FavoriteEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITE).build();

        public static final String TABLE_NAME = "favorite";

        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER_URL = "poster_url";
        public static final String COLUMN_SYNOPSIS = "synopsis";
        public static final String COLUMN_RATINGS = "ratings";
        public static final String COLUMN_RELEASE_DATE = "release_date";

    }
}
