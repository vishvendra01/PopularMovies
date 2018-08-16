package com.popmovies.utilities;

import android.support.annotation.Nullable;

import com.popmovies.model.MovieModel;
import com.popmovies.model.ReviewModel;
import com.popmovies.model.TrailerModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*
 * Author : Vishvendra
 * Version: 1.0
 * 6/29/2018
 */

/**
 * Utility functions to handle TMDB JSON data.
 */
public class TmdbJsonUtils {

    private static final String MOVIES_RESULTS_KEY = "results";
    private static final String MOVIES_ID_KEY = "id";
    private static final String MOVIE_TITLE_KEY = "original_title";
    private static final String MOVIE_POSTER_KEY = "poster_path";
    private static final String MOVIE_SYNOPSIS_KEY = "overview";
    private static final String MOVIE_RATING_KEY = "vote_average";
    private static final String MOVIE_RELEASE_DATE_KEY = "release_date";

    private static final String MOVIE_POSTER_BASE_URL = "https://image.tmdb.org/t/p/w500/";

    private static final String REVIEW_RESULTS_KEY = "results";
    private static final String REVIEW_ID_KEY = "id";
    private static final String REVIEW_URL_KEY = "url";
    private static final String REVIEW_AUTHOR_KEY = "author";
    private static final String REVIEW_CONTENT_KEY = "content";

    private static final String TRAILERS_RESULTS_KEY = "results";
    private static final String TRAILERS_ID_KEY = "id";
    private static final String TRAILERS_KEY_KEY = "key";
    private static final String TRAILERS_NAME_KEY = "name";
    private static final String TRAILERS_SITE_KEY = "site";
    private static final String TRAILERS_TYPE_KEY = "type";


    /**
     * This method parses JSON from a web response and returns a list of {@link MovieModel}
     *
     * @param moviesJsonStr JSON response from server
     * @return Array of {@link MovieModel} describing movies data. It might be null.
     * @throws JSONException If JSON data cannot be properly parsed
     */
    @Nullable
    public static MovieModel[] getMoviesListFromJson(String moviesJsonStr) throws JSONException {
        MovieModel[] moviesArray = null;

        JSONObject moviesResponseJsonObject = new JSONObject(moviesJsonStr);
        if (moviesResponseJsonObject.has(MOVIES_RESULTS_KEY)) {
            JSONArray moviesResultJsonArray = moviesResponseJsonObject.getJSONArray(MOVIES_RESULTS_KEY);

            moviesArray = new MovieModel[moviesResultJsonArray.length()];

            for (int i = 0; i < moviesResultJsonArray.length(); i++) {
                JSONObject movieJsonObject = moviesResultJsonArray.getJSONObject(i);

                int id = movieJsonObject.getInt(MOVIES_ID_KEY);
                String movieTitle = movieJsonObject.getString(MOVIE_TITLE_KEY);
                String moviePosterPath = movieJsonObject.getString(MOVIE_POSTER_KEY);
                String movieSynopsis = movieJsonObject.getString(MOVIE_SYNOPSIS_KEY);
                Double movieRatings = movieJsonObject.getDouble(MOVIE_RATING_KEY);
                String movieReleaseDate = movieJsonObject.getString(MOVIE_RELEASE_DATE_KEY);

                String moviePosterUrl = null;
                if (moviePosterPath != null) { // poster path might be null as per TMDB api.
                    moviePosterUrl = MOVIE_POSTER_BASE_URL + moviePosterPath;
                }

                MovieModel movieModel = new MovieModel(id, movieTitle, moviePosterUrl, movieSynopsis,
                        movieRatings, movieReleaseDate);

                moviesArray[i] = movieModel;
            }
        }

        return moviesArray;
    }

    /**
     * This method parses json returned from reviews web services into array of {@link ReviewModel}
     *
     * @param reviewsJson json returned by review web service
     * @return Array of {@link ReviewModel} it might be null
     * @throws JSONException if there something wrong with our json
     */
    @Nullable
    public static ReviewModel[] getReviewsListFromJson(String reviewsJson) throws JSONException {
        ReviewModel[] reviews = null;

        JSONObject reviewsJsonObject = new JSONObject(reviewsJson);
        if (reviewsJsonObject.has(REVIEW_RESULTS_KEY)) {
            JSONArray reviewsJsonArray = reviewsJsonObject.getJSONArray(REVIEW_RESULTS_KEY);
            reviews = new ReviewModel[reviewsJsonArray.length()];

            for (int i = 0; i < reviewsJsonArray.length(); i++) {
                JSONObject reviewJsonObject = reviewsJsonArray.getJSONObject(i);

                String id = reviewJsonObject.getString(REVIEW_ID_KEY);
                String url = reviewJsonObject.getString(REVIEW_URL_KEY);
                String author = reviewJsonObject.getString(REVIEW_AUTHOR_KEY);
                String content = reviewJsonObject.getString(REVIEW_CONTENT_KEY);

                ReviewModel reviewModel = new ReviewModel(id, author, content, url);

                reviews[i] = reviewModel;
            }
        }

        return reviews;
    }

    /**
     * This method parses json returned from trailer api into array of {@link TrailerModel}
     *
     * @param trailersJson json string returned by trailer api
     * @return Array of {@link TrailerModel}
     * @throws JSONException if something is wrong with our json
     */
    @Nullable
    public static TrailerModel[] getTrailersListFromJson(String trailersJson) throws JSONException {
        TrailerModel[] trailers = null;

        JSONObject trailersJsonObject = new JSONObject(trailersJson);
        if (trailersJsonObject.has(TRAILERS_RESULTS_KEY)) {
            JSONArray trailersJsonArray = trailersJsonObject.getJSONArray(TRAILERS_RESULTS_KEY);
            trailers = new TrailerModel[trailersJsonArray.length()];

            for (int i = 0; i < trailersJsonArray.length(); i++) {
                JSONObject trailerJsonObject = trailersJsonArray.getJSONObject(i);

                String id = trailerJsonObject.getString(TRAILERS_ID_KEY);
                String key = trailerJsonObject.getString(TRAILERS_KEY_KEY);
                String name = trailerJsonObject.getString(TRAILERS_NAME_KEY);
                String site = trailerJsonObject.getString(TRAILERS_SITE_KEY);
                String type = trailerJsonObject.getString(TRAILERS_TYPE_KEY);

                TrailerModel trailerModel = new TrailerModel(id, key, name, site, type);

                trailers[i] = trailerModel;
            }
        }

        return trailers;
    }

}
