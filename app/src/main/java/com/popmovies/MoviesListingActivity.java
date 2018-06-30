package com.popmovies;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.popmovies.model.MovieModel;
import com.popmovies.utilities.NetworkUtils;
import com.popmovies.utilities.TmdbJsonUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Author : Vishvendra
 * Version: 1.0
 * 6/29/2018
 */

public class MoviesListingActivity extends AppCompatActivity implements MovieAdapter.OnItemClickListener {
    private static final int GRID_SPAN_COUNT = 3;
    private static final String SORT_BY_POPULARITY = "popularity";
    private static final String SORT_BY_TOP_RATED = "top_rated";

    private RecyclerView mMoviesRecyclerView;
    private ProgressBar mProgressBar;
    private TextView mErrorMessageTextView;
    private Button mRetryButton;

    private MovieAdapter mMoviesAdapter;
    private String sortBy = SORT_BY_POPULARITY;
    private MoviesLoadTask mMovieLoadTask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_listing);
        init();
        getMoviesListFromTmdb();
    }

    private void init() {
        mMoviesRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);
        mMoviesRecyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, GRID_SPAN_COUNT);
        mMoviesRecyclerView.setLayoutManager(gridLayoutManager);

        mMoviesAdapter = new MovieAdapter(this);
        mMoviesRecyclerView.setAdapter(mMoviesAdapter);

        mProgressBar = (ProgressBar) findViewById(R.id.pb_loading);
        mErrorMessageTextView = (TextView) findViewById(R.id.tv_error_message);
        mRetryButton = (Button) findViewById(R.id.btn_retry);

        mRetryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMoviesListFromTmdb();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // cancel our asynctask if it is running to avoid crashes
        if (mMovieLoadTask != null) {
            mMovieLoadTask.cancel(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_filter) {
            showFilterDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showFilterDialog() {
        final int POPULAR_INDEX = 0;
        final int TOP_RATED_INDEX = 1;

        new AlertDialog.Builder(this)
                .setTitle(R.string.fileter_dialog_title)
                .setItems(R.array.filter_options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == POPULAR_INDEX) {
                            sortBy = SORT_BY_POPULARITY;
                        } else if (which == TOP_RATED_INDEX) {
                            sortBy = SORT_BY_TOP_RATED;
                        }
                        getMoviesListFromTmdb();
                    }
                }).show();
    }

    private void getMoviesListFromTmdb() {
        if (mMovieLoadTask != null) {
            mMovieLoadTask.cancel(true);
        }
        mMovieLoadTask = new MoviesLoadTask();

        if (sortBy.equals(SORT_BY_POPULARITY)) {
            URL popularMoviesUrl = NetworkUtils.buildPopularMoviesUrl();
            mMovieLoadTask.execute(popularMoviesUrl);
        } else if (sortBy.equals(SORT_BY_TOP_RATED)) {
            URL topRatedMoviesUrl = NetworkUtils.buildTopRatedMoviesUrl();
            mMovieLoadTask.execute(topRatedMoviesUrl);
        }
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
        i.putExtra(MovieDetailActivity.ARG_MOVIE_TITLE, clickedMovie.getMovieTitle());
        i.putExtra(MovieDetailActivity.ARG_MOVIE_RELEASE_DATE, clickedMovie.getReleaseRate());
        i.putExtra(MovieDetailActivity.ARG_MOVIE_RATING, clickedMovie.getUserRatings());
        i.putExtra(MovieDetailActivity.ARG_MOVIE_SYNOPSIS, clickedMovie.getMovieSynopsis());
        i.putExtra(MovieDetailActivity.ARG_MOVIE_POSTER_URL, clickedMovie.getMoviePosterUrl());

        startActivity(i);
    }


    private class MoviesLoadTask extends AsyncTask<URL, Void, MovieModel[]> {

        @Override
        protected void onPreExecute() {
            mMoviesAdapter.setMovies(null); // reset movies adapter

            hideErrorView();
            showProgressView();
        }

        @Override
        protected MovieModel[] doInBackground(URL... params) {
            // just in case if i forget to put url
            if (params.length == 0) {
                return null;
            }

            try {
                String moviesResponseJsonStr = NetworkUtils.getResponseFromHttpUrl(params[0]);
                MovieModel[] parsedMoviesModelsArray = TmdbJsonUtils.getMoviesListFromJson(moviesResponseJsonStr);
                return parsedMoviesModelsArray;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(MovieModel[] moviesArray) {
            hideProgressView();

            if (moviesArray == null) {
                showErrorView();
            } else {
                mMoviesAdapter.setMovies(moviesArray);
            }
        }
    }
}
