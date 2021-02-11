package com.popmovies;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

    public MovieAdapter() {
    }

    public MovieAdapter(OnItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    public void setMovies(MovieModel[] moviesData) {
        this.mMoviesData = moviesData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MovieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie, parent, false);

        MovieHolder movieHolder = new MovieHolder(inflatedView);
        return movieHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MovieHolder holder, int position) {
        // as we know our item layout's parent is a imageview so we can typecast
        // itemview to imageview for our use.
        ImageView movieImageView = holder.posterImageView;

        holder.titleTextView.setText(mMoviesData[position].getMovieTitle());
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
        private ImageView posterImageView;
        private TextView titleTextView;

        public MovieHolder(View itemView) {
            super(itemView);
            posterImageView = itemView.findViewById(R.id.image_movie_poster);
            titleTextView = itemView.findViewById(R.id.text_title);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(MovieModel clickedMovie);
    }
}
