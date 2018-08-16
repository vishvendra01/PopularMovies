package com.popmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.popmovies.data.MovieDbHelper;
import com.popmovies.model.MovieModel;

import java.util.List;


public class FavoriteMoviesListingActivity extends AppCompatActivity
        implements MovieAdapter.OnItemClickListener, LoaderManager.LoaderCallbacks<MovieModel[]> {
    private static final int FAVORITE_LOADER_ID = 2355;

    private RecyclerView mMoviesRecyclerView;
    private ProgressBar mProgressBar;
    private TextView mErrorMessageTextView;
    private Button mRetryButton;

    private MovieAdapter mMoviesAdapter;

    private MovieDbHelper mDbHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_listing);
        init();

        if (getSupportActionBar() != null) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle(R.string.favorites_activity_title);

            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mDbHelper = new MovieDbHelper(this);

        getSupportLoaderManager().initLoader(FAVORITE_LOADER_ID, null, this);
    }

    private void init() {
        mMoviesRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);
        mMoviesRecyclerView.setHasFixedSize(true);
/*        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, GRID_SPAN_COUNT);
        mMoviesRecyclerView.setLayoutManager(gridLayoutManager);*/

        mMoviesAdapter = new MovieAdapter(this);
        mMoviesRecyclerView.setAdapter(mMoviesAdapter);

        mProgressBar = (ProgressBar) findViewById(R.id.pb_loading);
        mErrorMessageTextView = (TextView) findViewById(R.id.tv_error_message);
        mRetryButton = (Button) findViewById(R.id.btn_retry);

        mRetryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);

        return true;
    }


    private void showErrorView() {
        mRetryButton.setVisibility(View.VISIBLE);
        mErrorMessageTextView.setVisibility(View.VISIBLE);
    }

    private void hideErrorView() {
        mRetryButton.setVisibility(View.INVISIBLE);
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
    }

    private void showProgressView() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressView() {
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onItemClick(MovieModel clickedMovie) {
        Intent i = new Intent(this, MovieDetailActivity.class);
        i.putExtra(MovieDetailActivity.ARG_MOVIE_DATA, clickedMovie);

        startActivity(i);
    }

    @Override
    public Loader<MovieModel[]> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<MovieModel[]>(this) {
            @Override
            public MovieModel[] loadInBackground() {
                final List<MovieModel> moviesList = mDbHelper.getFavoriteMoviesFromDb();
                return moviesList.toArray(new MovieModel[moviesList.size()]);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<MovieModel[]> loader, MovieModel[] data) {
        if (data != null) {
            mMoviesAdapter.setMovies(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<MovieModel[]> loader) {

    }
}
