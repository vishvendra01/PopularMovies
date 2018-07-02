package com.popmovies;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Author : Vishvendra
 * Version: 1.0
 * 6/29/2018
 */

public class MovieDetailActivity extends AppCompatActivity {
    public static final String ARG_MOVIE_TITLE = "movie_title";
    public static final String ARG_MOVIE_POSTER_URL = "movie_poster_url";
    public static final String ARG_MOVIE_SYNOPSIS = "movie_synopsis";
    public static final String ARG_MOVIE_RELEASE_DATE = "movie_release_date";
    public static final String ARG_MOVIE_RATING = "movie_rating";

    private TextView mMovieTitleTextView;
    private TextView mReleaseDateTextView;
    private TextView mRatingTextView;
    private TextView mSynopsisTextView;
    private ImageView mPosterImageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        init();
    }

    private void init() {
        mMovieTitleTextView = (TextView) findViewById(R.id.tv_movie_title);
        mReleaseDateTextView = (TextView) findViewById(R.id.tv_movie_release_date);
        mRatingTextView = (TextView) findViewById(R.id.tv_movie_ratings);
        mSynopsisTextView = (TextView) findViewById(R.id.tv_movie_synopsis);
        mPosterImageView = (ImageView) findViewById(R.id.iv_movie_poster);

        String movieTitle = getIntent().getStringExtra(ARG_MOVIE_TITLE);
        String movieReleaseDate = getIntent().getStringExtra(ARG_MOVIE_RELEASE_DATE);
        Double movieRatings = getIntent().getDoubleExtra(ARG_MOVIE_RATING, 0.0);
        String movieSynopsis = getIntent().getStringExtra(ARG_MOVIE_SYNOPSIS);
        String moviePosterUrl = getIntent().getStringExtra(ARG_MOVIE_POSTER_URL);


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // set action bar title as movie title
            //actionBar.setTitle(movieTitle);

            // show back button on actionbar
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mMovieTitleTextView.setText(movieTitle);
        mReleaseDateTextView.setText(movieReleaseDate);
        mRatingTextView.setText(String.valueOf(movieRatings));
        mSynopsisTextView.setText(movieSynopsis);

        if (moviePosterUrl != null) {
            Picasso.get().load(moviePosterUrl).into(mPosterImageView);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
