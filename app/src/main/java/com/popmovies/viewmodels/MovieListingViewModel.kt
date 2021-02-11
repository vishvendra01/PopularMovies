package com.popmovies.viewmodels

import android.app.Application
import android.graphics.Movie
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.popmovies.data.MovieRepository
import com.popmovies.model.MovieModel
import com.popmovies.utilities.NetworkUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MovieListingViewModel(app: Application) : AndroidViewModel(app) {
    val movieListingViewStateLiveData = MutableLiveData<MovieListingViewState>()
    private val repository = MovieRepository()
    private val disposeBag = CompositeDisposable()

    init {
        movieListingViewStateLiveData.value = MovieListingViewState()
    }

    private fun getMovieListingViewState(): MovieListingViewState {
        return movieListingViewStateLiveData.value!!
    }

    override fun onCleared() {
        super.onCleared()
        disposeBag.clear()
    }

    fun fetchMovies() {
        movieListingViewStateLiveData.value = getMovieListingViewState().copy(progress = true)

        repository.getNowPlayMovies()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    movieListingViewStateLiveData.value =
                            getMovieListingViewState().copy(progress = false, success = true, movies = it)
                }
                .run { disposeBag.add(this) }

    }
}

data class MovieListingViewState(
        val progress: Boolean = false,
        val success: Boolean = false,
        val error: Boolean = false,
        val movies: List<MovieModel> = emptyList()
)