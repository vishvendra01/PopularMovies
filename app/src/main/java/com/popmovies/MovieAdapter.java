package com.popmovies;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.popmovies.model.MovieModel;
import com.squareup.picasso.Picasso;

/**
 * Author : Vishvendra
 * Version: 1.0
 * 6/30/2018
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieHolder> {
    private MovieModel[] mMoviesData;
    private OnItemClickListener mItemClickListener;

    public MovieAdapter(OnItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    public void setMovies(MovieModel[] moviesData) {
        this.mMoviesData = moviesData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MovieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_movie_listing, parent, false);

        MovieHolder movieHolder = new MovieHolder(inflatedView);
        return movieHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MovieHolder holder, int position) {
        // as we know our item layout's parent is a imageview so we can typecast
        // itemview to imageview for our use.
        ImageView movieImageView = (ImageView) holder.itemView;

        String moviePosterUrl = mMoviesData[position].getMoviePosterUrl();
        if (moviePosterUrl != null) {
            Picasso.get().load(moviePosterUrl).into(movieImageView);
        }

        // set on click listener for list item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int clickedPosition = holder.getAdapterPosition();
                MovieModel clickedMovie = mMoviesData[clickedPosition];

                mItemClickListener.onItemClick(clickedMovie);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mMoviesData == null) {
            return 0;
        } else {
            return mMoviesData.length;
        }
    }

    class MovieHolder extends RecyclerView.ViewHolder {

        public MovieHolder(View itemView) {
            super(itemView);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(MovieModel clickedMovie);
    }
}
