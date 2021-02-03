package com.popmovies;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.popmovies.model.TrailerModel;


public class MovieTrailersAdapter extends RecyclerView.Adapter<MovieTrailersAdapter.MovieTrailerHolder> {
    private TrailerModel[] mTrailers;
    private ClickListener mClickListener;

    public MovieTrailersAdapter(ClickListener clickListener) {
        this.mClickListener = clickListener;
    }

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
    public void onBindViewHolder(@NonNull final MovieTrailerHolder holder, int position) {
        final TrailerModel trailer = mTrailers[position];

        holder.videoTitle.setText(trailer.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null) {
                    mClickListener.onTrailerClick(mTrailers[holder.getAdapterPosition()]);
                }
            }
        });
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

    public interface ClickListener {
        void onTrailerClick(TrailerModel trailer);
    }
}
