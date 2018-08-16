package com.popmovies.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.popmovies.model.MovieModel;

import java.util.ArrayList;
import java.util.List;

public class MovieDbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "movies.db";
    private static final int DB_VERSION = 1;

    public MovieDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_FAVORITE_TABLE_SQL =
                "CREATE TABLE " + MovieContract.FavoriteEntry.TABLE_NAME + " (" +
                        MovieContract.FavoriteEntry._ID + " INTEGER PRIMARY KEY, " +
                        MovieContract.FavoriteEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                        MovieContract.FavoriteEntry.COLUMN_POSTER_URL + " TEXT NOT NULL, " +
                        MovieContract.FavoriteEntry.COLUMN_SYNOPSIS + " TEXT NOT NULL, " +
                        MovieContract.FavoriteEntry.COLUMN_RATINGS + " TEXT NOT NULL, " +
                        MovieContract.FavoriteEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                        " UNIQUE (" + MovieContract.FavoriteEntry._ID + " ) ON CONFLICT REPLACE" +
                        ");";

        db.execSQL(CREATE_FAVORITE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.FavoriteEntry.TABLE_NAME);
        onCreate(db);
    }

    public List<MovieModel> getFavoriteMoviesFromDb() {
        List<MovieModel> favoriteMovies = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(MovieContract.FavoriteEntry.TABLE_NAME,
                null, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            final int idColumnIndex = cursor.getColumnIndex(MovieContract.FavoriteEntry._ID);
            final int titleColumnIndex = cursor.getColumnIndex(MovieContract.FavoriteEntry.COLUMN_TITLE);
            final int posterUrlColumnIndex = cursor.getColumnIndex(MovieContract.FavoriteEntry.COLUMN_POSTER_URL);
            final int synopsisColumnIndex = cursor.getColumnIndex(MovieContract.FavoriteEntry.COLUMN_SYNOPSIS);
            final int ratingsColumnIndex = cursor.getColumnIndex(MovieContract.FavoriteEntry.COLUMN_RATINGS);
            final int releaseDateColumnIndex = cursor.getColumnIndex(MovieContract.FavoriteEntry.COLUMN_RELEASE_DATE);

            do {
                int _id = cursor.getInt(idColumnIndex);
                String title = cursor.getString(titleColumnIndex);
                String posterUrl = cursor.getString(posterUrlColumnIndex);
                String synopsis = cursor.getString(synopsisColumnIndex);
                double ratings = cursor.getDouble(ratingsColumnIndex);
                String releaseDate = cursor.getString(releaseDateColumnIndex);

                favoriteMovies.add(new MovieModel(_id, title, posterUrl, synopsis, ratings, releaseDate));

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return favoriteMovies;
    }

    public void addFavoriteMovieToDb(MovieModel favoriteMovie) {
        ContentValues values = new ContentValues();
        values.put(MovieContract.FavoriteEntry._ID, favoriteMovie.getId());
        values.put(MovieContract.FavoriteEntry.COLUMN_TITLE, favoriteMovie.getMovieTitle());
        values.put(MovieContract.FavoriteEntry.COLUMN_POSTER_URL, favoriteMovie.getMoviePosterUrl());
        values.put(MovieContract.FavoriteEntry.COLUMN_SYNOPSIS, favoriteMovie.getMovieSynopsis());
        values.put(MovieContract.FavoriteEntry.COLUMN_RATINGS, favoriteMovie.getUserRatings());
        values.put(MovieContract.FavoriteEntry.COLUMN_RELEASE_DATE, favoriteMovie.getReleaseRate());

        SQLiteDatabase db = getWritableDatabase();
        db.insert(MovieContract.FavoriteEntry.TABLE_NAME, null, values);
        db.close();
    }

    public boolean removeFavoriteMovieFromDb(int id) {
        SQLiteDatabase db = getReadableDatabase();
        return db.delete(MovieContract.FavoriteEntry.TABLE_NAME, MovieContract.FavoriteEntry._ID + "=?",
                new String[]{String.valueOf(id)}) == 1;
    }

    /**
     * check in favorite table if movie with given id exits
     *
     * @param id movie id
     * @return whether or not given movie is favorite
     */
    public boolean isFavoriteMovie(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(MovieContract.FavoriteEntry.TABLE_NAME, null,
                MovieContract.FavoriteEntry._ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count == 1;
    }
}
