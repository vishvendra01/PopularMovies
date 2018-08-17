package com.popmovies;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.popmovies.data.MovieDbHelper;
import com.popmovies.databinding.ActivityMovieDetailBinding;
import com.popmovies.model.MovieModel;
import com.popmovies.model.ReviewModel;
import com.popmovies.model.TrailerModel;
import com.popmovies.utilities.NetworkUtils;
import com.popmovies.utilities.TmdbJsonUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

/**
 * Author : Vishvendra
 * Version: 1.0
 * 6/29/2018
 */

public class MovieDetailActivity extends AppCompatActivity {
    public static final String ARG_MOVIE_DATA = "movie";

    private static final String ARG_REVIEWS = "reviews";
    private static final String ARG_TRAILERS = "trailers";
    private static final String ARG_FAVORITE_MOVIE = "favorite_movie";

    public static final String ACTION_FAVORITE_STATUS_CHANGED = "favorite-changed";

    private static final int REVIEW_LOADER_ID = 2355;
    private static final int TRAILER_LOADER_ID = 2545;

    private LoaderManager.LoaderCallbacks<ReviewModel[]> mReviewsLoaderCallbacks;
    private LoaderManager.LoaderCallbacks<TrailerModel[]> mTrailerLoaderCallbacks;

    private ReviewModel[] mReviews;
    private TrailerModel[] mTrailers;

    private MovieReviewsAdapter mReviewsAdapter;
    private MovieTrailersAdapter mTrailersAdapter;

    private MovieDbHelper mDbHelper;

    private Boolean mIsFavoriteMovie = null;

    private ActivityMovieDetailBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_movie_detail);

        mDbHelper = new MovieDbHelper(this);

        if (!getIntent().hasExtra(ARG_MOVIE_DATA)) {
            return;
        }

        final MovieModel movie = getIntent().getParcelableExtra(ARG_MOVIE_DATA);

        setData(movie);

        mBinding.ivFavoriteToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsFavoriteMovie == null) {
                    return;
                }

                // fire a broadcast which tell it's subscribers about this event
                Intent i = new Intent(ACTION_FAVORITE_STATUS_CHANGED);
                LocalBroadcastManager.getInstance(MovieDetailActivity.this).sendBroadcast(i);


                setFavoriteMovieImage(!mIsFavoriteMovie);
                new ToggleFavoriteStatusTask(movie).execute(mIsFavoriteMovie);
            }
        });

        // activity is destroyed and our data is saved then show reviews and trailers from there
        // otherwise load it from internet using loaders
        if (savedInstanceState != null && savedInstanceState.containsKey(ARG_REVIEWS)
                && savedInstanceState.containsKey(ARG_TRAILERS)) {
            mReviews = (ReviewModel[]) savedInstanceState.getParcelableArray(ARG_REVIEWS);
            mTrailers = (TrailerModel[]) savedInstanceState.getParcelableArray(ARG_TRAILERS);

            mReviewsAdapter.setReviews(mReviews);
            mTrailersAdapter.setTrailers(mTrailers);
        } else {
            String movieId = String.valueOf(movie.getId());
            initLoaders(movieId);
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(ARG_FAVORITE_MOVIE)) {
            mIsFavoriteMovie = savedInstanceState.getBoolean(ARG_FAVORITE_MOVIE);
            setFavoriteMovieImage(mIsFavoriteMovie);
        } else {
            new CheckFavoriteTask().execute(movie.getId());
        }
    }

    private void setData(MovieModel movieModel) {
        String movieTitle = movieModel.getMovieTitle();
        String movieReleaseDate = movieModel.getReleaseRate();
        Double movieRatings = movieModel.getUserRatings();
        String movieSynopsis = movieModel.getMovieSynopsis();
        String moviePosterUrl = movieModel.getMoviePosterUrl();


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // set action bar title as movie title
            //actionBar.setTitle(movieTitle);

            // show back button on actionbar
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mBinding.tvMovieTitle.setText(movieTitle);
        mBinding.tvMovieReleaseDate.setText(movieReleaseDate);
        mBinding.tvMovieRatings.setText(String.valueOf(movieRatings));
        mBinding.tvMovieSynopsis.setText(movieSynopsis);

        if (moviePosterUrl != null) {
            Picasso.get().load(moviePosterUrl).into(mBinding.ivMoviePoster);
        }

        mTrailersAdapter = new MovieTrailersAdapter();
        mReviewsAdapter = new MovieReviewsAdapter();

        mBinding.rvReviews.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration reviewsListDivider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        reviewsListDivider.setDrawable(getResources().getDrawable(R.drawable.reviews_list_divider));
        mBinding.rvReviews.addItemDecoration(reviewsListDivider);
        mBinding.rvReviews.setAdapter(mReviewsAdapter);

        mBinding.rvTrailers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mBinding.rvTrailers.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));
        mBinding.rvTrailers.setAdapter(mTrailersAdapter);
    }

    private void setFavoriteMovieImage(boolean isFavorite) {
        if (isFavorite) {
            mBinding.ivFavoriteToggle.setImageResource(R.drawable.ic_favorite_blue_filled_24dp);
        } else {
            mBinding.ivFavoriteToggle.setImageResource(R.drawable.ic_favorite_blue_empty_24px);
        }

        mIsFavoriteMovie = isFavorite;
    }

    private void initLoaders(final String movieId) {
        mReviewsLoaderCallbacks = new LoaderManager.LoaderCallbacks<ReviewModel[]>() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public Loader<ReviewModel[]> onCreateLoader(int id, Bundle args) {
                return new AsyncTaskLoader<ReviewModel[]>(MovieDetailActivity.this) {
                    ReviewModel[] cachedReviews;

                    @Override
                    protected void onStartLoading() {
                        if (cachedReviews != null) {
                            deliverResult(cachedReviews);
                        } else {
                            forceLoad();
                        }
                    }

                    @Override
                    public void deliverResult(ReviewModel[] data) {
                        cachedReviews = data;
                        super.deliverResult(data);
                    }

                    @Override
                    public ReviewModel[] loadInBackground() {
                        ReviewModel[] reviews = null;
                        URL url = NetworkUtils.buildMovieReviewsUrl(movieId);
                        try {
                            String response = NetworkUtils.getResponseFromHttpUrl(url);
                            if (response != null) {
                                reviews = TmdbJsonUtils.getReviewsListFromJson(response);
                            }
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                        return reviews;
                    }
                };
            }

            @Override
            public void onLoadFinished(Loader<ReviewModel[]> loader, ReviewModel[] data) {
                setReviews(data);
            }

            @Override
            public void onLoaderReset(Loader<ReviewModel[]> loader) {

            }
        };

        mTrailerLoaderCallbacks = new LoaderManager.LoaderCallbacks<TrailerModel[]>() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public Loader<TrailerModel[]> onCreateLoader(int id, Bundle args) {
                return new AsyncTaskLoader<TrailerModel[]>(MovieDetailActivity.this) {
                    TrailerModel[] cachedTrailers;

                    @Override
                    protected void onStartLoading() {
                        if (cachedTrailers != null) {
                            deliverResult(cachedTrailers);
                        } else {
                            forceLoad();
                        }
                    }

                    @Override
                    public void deliverResult(TrailerModel[] data) {
                        cachedTrailers = data;
                        super.deliverResult(data);
                    }

                    @Override
                    public TrailerModel[] loadInBackground() {
                        TrailerModel[] trailers = null;

                        URL trailerUrl = NetworkUtils.buildMovieTrailersUrl(movieId);
                        try {
                            String response = NetworkUtils.getResponseFromHttpUrl(trailerUrl);
                            trailers = TmdbJsonUtils.getTrailersListFromJson(response);
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }

                        return trailers;
                    }
                };
            }

            @Override
            public void onLoadFinished(Loader<TrailerModel[]> loader, TrailerModel[] data) {
                setTrailers(data);
            }

            @Override
            public void onLoaderReset(Loader<TrailerModel[]> loader) {

            }
        };

        getSupportLoaderManager().initLoader(REVIEW_LOADER_ID, null, mReviewsLoaderCallbacks);
        getSupportLoaderManager().initLoader(TRAILER_LOADER_ID, null, mTrailerLoaderCallbacks);
    }

    private void setReviews(ReviewModel[] reviews) {
        mReviewsAdapter.setReviews(reviews);
    }

    private void setTrailers(TrailerModel[] trailers) {
        mTrailersAdapter.setTrailers(trailers);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mReviews != null) {
            outState.putParcelableArray(ARG_REVIEWS, mReviews);
        }

        if (mTrailers != null) {
            outState.putParcelableArray(ARG_TRAILERS, mTrailers);
        }

        if (mIsFavoriteMovie != null) {
            outState.putBoolean(ARG_FAVORITE_MOVIE, mIsFavoriteMovie);
        }
    }

    private class CheckFavoriteTask extends AsyncTask<Integer, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Integer... params) {
            if (params.length == 1) {
                Integer movieId = params[0];
                return mDbHelper.isFavoriteMovie(movieId);
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            setFavoriteMovieImage(result);
        }
    }

    private class ToggleFavoriteStatusTask extends AsyncTask<Boolean, Void, Boolean> {
        private MovieModel mMovie;

        public ToggleFavoriteStatusTask(MovieModel movie) {
            this.mMovie = movie;
        }

        @Override
        protected Boolean doInBackground(Boolean... params) {
            if (params.length == 1) {
                Boolean favStatusToSet = params[0];
                if (favStatusToSet == null) {
                    return null;
                }

                if (favStatusToSet) {
                    mDbHelper.addFavoriteMovieToDb(mMovie);
                    return true;
                } else {
                    mDbHelper.removeFavoriteMovieFromDb(mMovie.getId());
                    return false;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean favStatus) {
            if (favStatus == null) {
                return;
            }

            String message = getString(R.string.movie_add_favorite_message);

            if (!favStatus) {
                message = getString(R.string.movie_remove_favorite_message);
            }

            Toast.makeText(MovieDetailActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    }
}
