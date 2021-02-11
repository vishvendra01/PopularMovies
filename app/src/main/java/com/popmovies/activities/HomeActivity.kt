package com.popmovies.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.popmovies.R
import com.popmovies.fragments.MovieListingFragment

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setupToolbar()
        displayMovieListingFragment()
    }

    private fun setupToolbar(){
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
    }

    private fun displayMovieListingFragment() {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.homeContainerFrameLayout, MovieListingFragment())
                .commit()
    }
}