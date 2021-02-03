/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.popmovies.utilities;

import android.net.Uri;

import androidx.annotation.Nullable;

import com.popmovies.Config;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * These utilities will be used to communicate with the network.
 */
public class NetworkUtils {

    private final static String TMDB_BASE_URL =
            "https://api.themoviedb.org/";

    private final static String PARAM_API_KEY = "api_key";

    private final static String MOVIE_ENDPOINT = "movie";
    private final static String SORT_BY_TOP_RATED = "top_rated";
    private final static String SORT_BY_POPULAR = "popular";
    private final static String REVIEW_ENDPOINT = "reviews";
    private final static String VIDEOS_ENDPOINT = "videos";


    private final static String apiVersion = "3";

    /**
     * Builds the URL used to query top rated movies on TMDB.
     *
     * @return The URL to use to query the TMDB.
     */
    public static URL buildTopRatedMoviesUrl() {
        Uri builtUri = Uri.parse(TMDB_BASE_URL).buildUpon()
                .appendPath(apiVersion)
                .appendPath(MOVIE_ENDPOINT)
                .appendPath(SORT_BY_TOP_RATED)
                .appendQueryParameter(PARAM_API_KEY, Config.TMDB_API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * Builds the URL used to get popular movies on TMDB
     *
     * @return The URL to use to retrieve popular movies on TMDB
     */
    public static URL buildPopularMoviesUrl() {
        Uri builtUri = Uri.parse(TMDB_BASE_URL).buildUpon()
                .appendPath(apiVersion)
                .appendPath(MOVIE_ENDPOINT)
                .appendPath(SORT_BY_POPULAR)
                .appendQueryParameter(PARAM_API_KEY, Config.TMDB_API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }


    /**
     * Build url to fetch movies reviews for chosen movie
     *
     * @param movieId movie id of given movie as per tmdb api
     * @return movies reviews url
     */
    public static URL buildMovieReviewsUrl(String movieId) {
        Uri builtUri = Uri.parse(TMDB_BASE_URL).buildUpon()
                .appendPath(apiVersion)
                .appendPath(MOVIE_ENDPOINT)
                .appendPath(movieId)
                .appendPath(REVIEW_ENDPOINT)
                .appendQueryParameter(PARAM_API_KEY, Config.TMDB_API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * Build url to fetch movie trailers for chosen movie
     *
     * @param movieId movie id for movie as per tmdb api
     * @return movie trailers url
     */
    public static URL buildMovieTrailersUrl(String movieId) {
        Uri builtUri = Uri.parse(TMDB_BASE_URL).buildUpon()
                .appendPath(apiVersion)
                .appendPath(MOVIE_ENDPOINT)
                .appendPath(movieId)
                .appendPath(VIDEOS_ENDPOINT)
                .appendQueryParameter(PARAM_API_KEY, Config.TMDB_API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response which might be null
     * @throws IOException Related to network and stream reading
     */
    @Nullable
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}