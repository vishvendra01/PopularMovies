package com.popmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Author : Vishvendra
 * Version: 1.0
 * 6/29/2018
 */

public class MovieModel implements Parcelable {
    private int id;
    @SerializedName("original_title")
    private String movieTitle;
    @SerializedName("poster_path")
    private String moviePosterUrl;
    @SerializedName("overview")
    private String movieSynopsis;
    @SerializedName("vote_average")
    private double userRatings;
    @SerializedName("release_date")
    private String releaseRate;

    public MovieModel(int id, String movieTitle, String moviePosterUrl, String movieSynopsis,
                      double userRatings, String releaseRate) {
        this.id = id;
        this.movieTitle = movieTitle;
        this.moviePosterUrl = moviePosterUrl;
        this.movieSynopsis = movieSynopsis;
        this.userRatings = userRatings;
        this.releaseRate = releaseRate;
    }

    public MovieModel(Parcel in) {
        id = in.readInt();
        movieTitle = in.readString();
        moviePosterUrl = in.readString();
        movieSynopsis = in.readString();
        userRatings = in.readDouble();
        releaseRate = in.readString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getMoviePosterUrl() {
        return moviePosterUrl;
    }

    public void setMoviePosterUrl(String moviePosterUrl) {
        this.moviePosterUrl = moviePosterUrl;
    }

    public String getMovieSynopsis() {
        return movieSynopsis;
    }

    public void setMovieSynopsis(String movieSynopsis) {
        this.movieSynopsis = movieSynopsis;
    }

    public double getUserRatings() {
        return userRatings;
    }

    public void setUserRatings(double userRatings) {
        this.userRatings = userRatings;
    }

    public String getReleaseRate() {
        return releaseRate;
    }

    public void setReleaseRate(String releaseRate) {
        this.releaseRate = releaseRate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(movieTitle);
        dest.writeString(moviePosterUrl);
        dest.writeString(movieSynopsis);
        dest.writeDouble(userRatings);
        dest.writeString(releaseRate);
    }

    public static final Creator<MovieModel> CREATOR = new Creator<MovieModel>() {
        @Override
        public MovieModel createFromParcel(Parcel source) {
            return new MovieModel(source);
        }

        @Override
        public MovieModel[] newArray(int size) {
            return new MovieModel[size];
        }
    };
}
