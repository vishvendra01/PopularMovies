package com.popmovies;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.popmovies.model.TrailerModel;


public class MovieTrailersAdapter extends RecyclerView.Adapter<MovieTrailersAdapter.MovieTrailerHolder> {
    private TrailerModel[] mTrailers;

    public void setTrailers(TrailerModel[] trailers) {
        this.mTrailers = trailers;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MovieTrailerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_movie_trailer, parent, false);

        return new MovieTrailerHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieTrailerHolder holder, int position) {
        final TrailerModel trailer = mTrailers[position];

        holder.videoTitle.setText(trailer.getName());
    }

    @Override
    public int getItemCount() {
        if (mTrailers == null) {
            return 0;
        } else {
            return mTrailers.length;
        }
    }

    public final static class MovieTrailerHolder extends RecyclerView.ViewHolder {
        public TextView videoTitle;

        public MovieTrailerHolder(View itemView) {
            super(itemView);
            videoTitle = (TextView) itemView.findViewById(R.id.tv_video_title);
        }
    }
}
