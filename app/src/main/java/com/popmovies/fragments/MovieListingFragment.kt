package com.popmovies.fragments

import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.popmovies.MovieAdapter
import com.popmovies.R
import com.popmovies.viewmodels.MovieListingViewModel
import com.popmovies.viewmodels.MovieListingViewState

class MovieListingFragment : Fragment() {
    private lateinit var errorMessageTextView: TextView
    private lateinit var progressLoading: ProgressBar
    private lateinit var retryButton: Button

    private val movieListingAdapter = MovieAdapter()
    private val viewModel: MovieListingViewModel by lazy {
        ViewModelProviders.of(this)[MovieListingViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragmen_movie_listing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupMovieListingRecyclerView()
        bindLiveData()
        viewModel.fetchMovies()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main, menu)
    }

    private fun setupViews() {
        errorMessageTextView = requireView().findViewById(R.id.tv_error_message)
        progressLoading = requireView().findViewById(R.id.pb_loading)
        retryButton = requireView().findViewById(R.id.btn_retry)
    }

    private fun setupMovieListingRecyclerView() {
        val recyclerView = requireView().findViewById<RecyclerView>(R.id.rv_movies)
        recyclerView.adapter = movieListingAdapter
        movieListingAdapter.setItemClickListener { }
    }

    private fun bindLiveData() {
        viewModel.movieListingViewStateLiveData.observe(this, { movieListingState ->
            renderMovieListingViewState(movieListingState)
        })
    }

    private fun renderMovieListingViewState(movieListingViewState: MovieListingViewState) {
        progressLoading.visibility = if (movieListingViewState.progress) View.VISIBLE else View.GONE
        errorMessageTextView.visibility = if (movieListingViewState.error) View.VISIBLE else View.GONE
        retryButton.visibility = if (movieListingViewState.error) View.VISIBLE else View.GONE
        if(movieListingViewState.success){
            movieListingAdapter.setMovies(movieListingViewState.movies.toTypedArray())
        }
    }
}