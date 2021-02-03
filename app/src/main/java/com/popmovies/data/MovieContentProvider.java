package com.popmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MovieContentProvider extends ContentProvider {
    private static final int FAVORITES = 100;
    private static final int FAVORITE_WITH_ID = 101;

    private UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mDbHelper;

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_FAVORITE, FAVORITES);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_FAVORITE + "/#", FAVORITE_WITH_ID);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);
        Cursor retCursor;

        switch (match) {
            case FAVORITES:

                retCursor = db.query(
                        MovieContract.FavoriteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case FAVORITE_WITH_ID:
                String id = uri.getPathSegments().get(1);
                retCursor = db.query(
                        MovieContract.FavoriteEntry.TABLE_NAME,
                        projection,
                        MovieContract.FavoriteEntry._ID + "=?",
                        new String[]{id},
                        null,
                        null,
                        sortOrder
                );

                break;
            default:
                throw new UnsupportedOperationException("Uri not supported : " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case FAVORITES:
                long id = db.insert(MovieContract.FavoriteEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(MovieContract.FavoriteEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into : " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri : " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int rowsDeleted;
        switch (match) {
            case FAVORITE_WITH_ID:
                String id = uri.getPathSegments().get(1);
                rowsDeleted = db.delete(MovieContract.FavoriteEntry.TABLE_NAME, MovieContract.FavoriteEntry._ID + "=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri : " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case FAVORITE_WITH_ID:
                String id = uri.getPathSegments().get(1);
                rowsUpdated = db.update(MovieContract.FavoriteEntry.TABLE_NAME, values, MovieContract.FavoriteEntry._ID + "=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri : " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }
}
