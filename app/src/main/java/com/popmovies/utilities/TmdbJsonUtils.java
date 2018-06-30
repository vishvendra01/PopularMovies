package com.popmovies.utilities;

import android.support.annotation.Nullable;

import com.popmovies.model.MovieModel;

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
    private static final String MOVIE_TITLE_KEY = "original_title";
    private static final String MOVIE_POSTER_KEY = "poster_path";
    private static final String MOVIE_SYNOPSIS_KEY = "overview";
    private static final String MOVIE_RATING_KEY = "vote_average";
    private static final String MOVIE_RELEASE_DATE_KEY = "release_date";

    private static final String MOVIE_POSTER_BASE_URL = "https://image.tmdb.org/t/p/w500/";

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

                String movieTitle = movieJsonObject.getString(MOVIE_TITLE_KEY);
                String moviePosterPath = movieJsonObject.getString(MOVIE_POSTER_KEY);
                String movieSynopsis = movieJsonObject.getString(MOVIE_SYNOPSIS_KEY);
                Double movieRatings = movieJsonObject.getDouble(MOVIE_RATING_KEY);
                String movieReleaseDate = movieJsonObject.getString(MOVIE_RELEASE_DATE_KEY);

                String moviePosterUrl = null;
                if (moviePosterPath != null) { // poster path might be null as per TMDB api.
                    moviePosterUrl = MOVIE_POSTER_BASE_URL + moviePosterPath;
                }

                MovieModel movieModel = new MovieModel(movieTitle, moviePosterUrl, movieSynopsis,
                        movieRatings, movieReleaseDate);

                moviesArray[i] = movieModel;
            }
        }

        return moviesArray;
    }
}
