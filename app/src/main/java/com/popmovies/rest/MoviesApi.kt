package com.popmovies.rest

import com.popmovies.responses.MovieListingResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface MoviesApi {
    companion object {
        const val BASE_URL = "https://api.themoviedb.org/3/"
        const val POSTER_BASE_URL = "https://image.tmdb.org/t/p/w500/"
        const val API_KEY = "api_key"
        const val TOP_RATED_MOVIES_ENDPOINT = "movie/top_rated"
        const val POPULAR_MOVIES_ENDPOINT = "movie/popular"
        const val NOW_PLAYING_MOVIES_ENDPOINT = "movie/now_playing"
    }

    @GET(NOW_PLAYING_MOVIES_ENDPOINT)
    fun getNowPlayingMovies(@Query(API_KEY) apiKey: String): Observable<MovieListingResponse>

    @GET(TOP_RATED_MOVIES_ENDPOINT)
    fun getTopRatedMovies(@Query(API_KEY) apiKey: String): Observable<MovieListingResponse>

    @GET(POPULAR_MOVIES_ENDPOINT)
    fun getPopularMovies(@Query(API_KEY) apiKey: String): Observable<MovieListingResponse>
}