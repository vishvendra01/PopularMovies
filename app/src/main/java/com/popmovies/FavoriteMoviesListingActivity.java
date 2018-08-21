package com.popmovies;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.popmovies.data.MovieContract;
import com.popmovies.databinding.ActivityFavoriteMoviesListingBinding;
import com.popmovies.model.MovieModel;
import com.popmovies.utilities.CursorUtils;


public class FavoriteMoviesListingActivity extends AppCompatActivity
        implements MovieAdapter.OnItemClickListener, LoaderManager.LoaderCallbacks<MovieModel[]> {
    private static final int FAVORITE_LOADER_ID = 2355;

    private MovieAdapter mMoviesAdapter;

    private ActivityFavoriteMoviesListingBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_favorite_movies_listing);

        init();

        if (getSupportActionBar() != null) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle(R.string.favorites_activity_title);

            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        getSupportLoaderManager().initLoader(FAVORITE_LOADER_ID, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        getSupportLoaderManager().restartLoader(FAVORITE_LOADER_ID, null, this);
    }

    private void init() {
        mBinding.rvMovies.setHasFixedSize(true);

        mMoviesAdapter = new MovieAdapter(this);
        mBinding.rvMovies.setAdapter(mMoviesAdapter);

        // hide empty list message text view initially
        mBinding.tvEmptyMessage.setVisibility(View.GONE);

        // hide progress view too
        mBinding.pbLoading.setVisibility(View.GONE);
    }

    @Override
    public void onItemClick(MovieModel clickedMovie) {
        Intent i = new Intent(this, MovieDetailActivity.class);
        i.putExtra(MovieDetailActivity.ARG_MOVIE_DATA, clickedMovie);

        startActivity(i);
    }

    private void setMoviesList(MovieModel[] movies) {
        if (movies != null && movies.length > 0) {
            mMoviesAdapter.setMovies(movies);
        } else {
            mBinding.tvEmptyMessage.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<MovieModel[]> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<MovieModel[]>(this) {
            MovieModel[] cachedMovies = null;

            @Override
            protected void onStartLoading() {
                if (cachedMovies != null) {
                    deliverResult(cachedMovies);
                } else {
                    mBinding.pbLoading.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            @Override
            public MovieModel[] loadInBackground() {
                Cursor cursor = getContentResolver().query(MovieContract.FavoriteEntry.CONTENT_URI,
                        null, null, null, null);
                if (cursor != null) {
                    final MovieModel[] movieArray = CursorUtils.getFavoritesMoviesArrayFromCursor(cursor);
                    cursor.close();
                    return movieArray;
                } else {
                    return null;
                }
            }

            @Override
            public void deliverResult(MovieModel[] data) {
                cachedMovies = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<MovieModel[]> loader, MovieModel[] data) {
        mBinding.pbLoading.setVisibility(View.GONE);

        setMoviesList(data);
    }

    @Override
    public void onLoaderReset(Loader<MovieModel[]> loader) {
        mMoviesAdapter.setMovies(null);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
