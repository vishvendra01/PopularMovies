package com.popmovies.data

import com.popmovies.Config
import com.popmovies.model.MovieModel
import com.popmovies.rest.MoviesApi
import com.popmovies.rest.MoviesApiClient
import io.reactivex.Observable

class MovieRepository {
    private val moviesApi = MoviesApiClient().getMoviesApi()

    fun getNowPlayMovies(): Observable<List<MovieModel>> {
        return moviesApi.getNowPlayingMovies(Config.TMDB_API_KEY)
                .map {
                    it.results.also { it?.forEach { it.moviePosterUrl = MoviesApi.POSTER_BASE_URL + it.moviePosterUrl } }
                }
                .flatMap {
                    Observable.just(it)
                }
    }

    fun getPopularMovies(): Observable<List<MovieModel>> {
        return moviesApi.getPopularMovies(Config.TMDB_API_KEY)
                .flatMap { Observable.just(it.results) }
    }

    fun getTopRatedMovies(): Observable<List<MovieModel>> {
        return moviesApi.getPopularMovies(Config.TMDB_API_KEY)
                .flatMap { Observable.just(it.results) }
    }
}