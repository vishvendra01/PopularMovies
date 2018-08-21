package com.popmovies.utilities;

import android.database.Cursor;
import android.support.annotation.Nullable;

import com.popmovies.data.MovieContract;
import com.popmovies.model.MovieModel;

public final class CursorUtils {
    private CursorUtils() {
    }

    @Nullable
    public static MovieModel[] getFavoritesMoviesArrayFromCursor(Cursor cursor) {
        MovieModel[] favoriteMovies = null;


        if (cursor.moveToFirst()) {
            favoriteMovies = new MovieModel[cursor.getCount()];

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

                favoriteMovies[cursor.getPosition()] = new MovieModel(_id, title, posterUrl, synopsis, ratings, releaseDate);
            } while (cursor.moveToNext());
        }

        return favoriteMovies;
    }
}
