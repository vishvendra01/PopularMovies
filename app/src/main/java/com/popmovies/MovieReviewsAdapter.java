package com.popmovies;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.popmovies.model.ReviewModel;

public class MovieReviewsAdapter extends RecyclerView.Adapter<MovieReviewsAdapter.MovieReviewHolder> {
    private ReviewModel[] mReviews;

    public void setReviews(ReviewModel[] reviews) {
        this.mReviews = reviews;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MovieReviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_movie_review, parent, false);

        return new MovieReviewHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieReviewHolder holder, int position) {
        ReviewModel review = mReviews[position];
        holder.tvReviewAuthor.setText(review.getAuthor());
        holder.tvReviewContent.setText(review.getContent());
    }

    @Override
    public int getItemCount() {
        if (mReviews == null) {
            return 0;
        } else {
            return mReviews.length;
        }
    }

    public static final class MovieReviewHolder extends RecyclerView.ViewHolder {
        public TextView tvReviewContent;
        private TextView tvReviewAuthor;

        public MovieReviewHolder(View itemView) {
            super(itemView);
            tvReviewContent = (TextView) itemView.findViewById(R.id.tv_review_content);
            tvReviewAuthor = (TextView) itemView.findViewById(R.id.tv_review_user_name);
        }
    }
}
