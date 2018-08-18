package com.popmovies;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.popmovies.dialogs.FilterDialog;
import com.popmovies.model.MovieModel;
import com.popmovies.utilities.NetworkUtils;
import com.popmovies.utilities.TmdbJsonUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

public class MoviesListingActivity extends AppCompatActivity
        implements MovieAdapter.OnItemClickListener, LoaderManager.LoaderCallbacks<MovieModel[]>, FilterDialog.OptionSelectListener {
    private static final String SORT_TYPE_KEY = "sort-type";
    private static final String MOVIE_DATA_KEY = "movie-data";

    private static final String SORT_BY_POPULARITY = "popularity";
    private static final String SORT_BY_TOP_RATED = "top_rated";

    private static final String ARG_MOVIE_LISTING_URL = "movie_listing_url";
    private static final int LOADER_ID = 2354;

    private RecyclerView mMoviesRecyclerView;
    private ProgressBar mProgressBar;
    private TextView mErrorMessageTextView;
    private Button mRetryButton;

    private MovieModel[] mMoviesListing;

    private MovieAdapter mMoviesAdapter;
    private String mSortBy = SORT_BY_POPULARITY;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_listing);
        init();

        if (savedInstanceState != null && savedInstanceState.containsKey(SORT_TYPE_KEY)) {
            mSortBy = savedInstanceState.getString(SORT_TYPE_KEY);
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(MOVIE_DATA_KEY)) {
            Parcelable[] parcelablesArray = savedInstanceState.getParcelableArray(MOVIE_DATA_KEY);
            mMoviesListing = new MovieModel[parcelablesArray.length];
            System.arraycopy(parcelablesArray, 0, mMoviesListing, 0, parcelablesArray.length);

            mMoviesAdapter.setMovies(mMoviesListing);
        } else {
            URL moviesUrl = null;

            if (mSortBy.equals(SORT_BY_POPULARITY)) {
                moviesUrl = NetworkUtils.buildPopularMoviesUrl();
            } else if (mSortBy.equals(SORT_BY_TOP_RATED)) {
                moviesUrl = NetworkUtils.buildTopRatedMoviesUrl();
            }

            if (moviesUrl == null) {
                return;
            }

            Bundle args = new Bundle();
            args.putSerializable(ARG_MOVIE_LISTING_URL, moviesUrl);

            getSupportLoaderManager().initLoader(LOADER_ID, args, this);
        }

    }

    private void init() {
        mMoviesRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);
        mMoviesRecyclerView.setHasFixedSize(true);

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
        } else if (item.getItemId() == R.id.action_favorite) {
            Intent i = new Intent(this, FavoriteMoviesListingActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mMoviesListing != null) {
            outState.putParcelableArray(MOVIE_DATA_KEY, mMoviesListing);
        }

        outState.putString(SORT_TYPE_KEY, mSortBy);
    }

    private void showFilterDialog() {
        new FilterDialog().show(getSupportFragmentManager(), FilterDialog.class.getSimpleName());
    }

    private void getMoviesListFromTmdb() {
        if (mSortBy.equals(SORT_BY_POPULARITY)) {
            URL popularMoviesUrl = NetworkUtils.buildPopularMoviesUrl();

            Bundle args = new Bundle();
            args.putSerializable(ARG_MOVIE_LISTING_URL, popularMoviesUrl);

            getSupportLoaderManager().restartLoader(LOADER_ID, args, this);

        } else if (mSortBy.equals(SORT_BY_TOP_RATED)) {
            URL topRatedMoviesUrl = NetworkUtils.buildTopRatedMoviesUrl();

            Bundle args = new Bundle();
            args.putSerializable(ARG_MOVIE_LISTING_URL, topRatedMoviesUrl);

            getSupportLoaderManager().restartLoader(LOADER_ID, args, this);
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
        i.putExtra(MovieDetailActivity.ARG_MOVIE_DATA, clickedMovie);

        startActivity(i);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<MovieModel[]> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<MovieModel[]>(this) {
            MovieModel[] cachedMovies;

            @Override
            protected void onStartLoading() {
                mMoviesAdapter.setMovies(null); // reset movies adapter

                hideErrorView();
                showProgressView();

                if (cachedMovies != null) {
                    deliverResult(cachedMovies);
                } else {
                    forceLoad();
                }
            }

            @Override
            public void deliverResult(MovieModel[] data) {
                cachedMovies = data;
                super.deliverResult(data);
            }

            @Override
            public MovieModel[] loadInBackground() {
                if (args == null) {
                    return null;
                }

                if (args.containsKey(ARG_MOVIE_LISTING_URL)) {
                    URL movieUrl = (URL) args.getSerializable(ARG_MOVIE_LISTING_URL);
                    try {
                        String moviesResponseJsonStr = NetworkUtils.getResponseFromHttpUrl(movieUrl);
                        MovieModel[] parsedMoviesModelsArray = TmdbJsonUtils.getMoviesListFromJson(moviesResponseJsonStr);
                        return parsedMoviesModelsArray;
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                return null;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<MovieModel[]> loader, MovieModel[] data) {
        hideProgressView();

        mMoviesListing = data;
        if (data == null) {
            showErrorView();
        } else {
            mMoviesAdapter.setMovies(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<MovieModel[]> loader) {

    }

    @Override
    public void onOptionSelected(int index) {
        if (index == FilterDialog.POPULAR_INDEX) {
            mSortBy = SORT_BY_POPULARITY;
        } else if (index == FilterDialog.TOP_RATED_INDEX) {
            mSortBy = SORT_BY_TOP_RATED;
        }

        getMoviesListFromTmdb();
    }
}
