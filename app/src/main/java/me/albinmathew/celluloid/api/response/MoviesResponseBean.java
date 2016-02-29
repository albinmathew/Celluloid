/*
 * Copyright (c) 2016. Albin Mathew
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.albinmathew.celluloid.api.response;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import me.albinmathew.celluloid.api.base.BaseMovieBean;

/**
 * @author albin
 * @date 2/2/16
 */
public class MoviesResponseBean extends BaseMovieBean implements Parcelable {

    public static final Creator<MoviesResponseBean> CREATOR = new Creator<MoviesResponseBean>() {
        @NonNull
        public MoviesResponseBean createFromParcel(@NonNull Parcel source) {
            return new MoviesResponseBean(source);
        }

        @NonNull
        public MoviesResponseBean[] newArray(int size) {
            return new MoviesResponseBean[size];
        }
    };
    @SerializedName("poster_path")
    private String  posterPath;
    private boolean adult;
    private String  overview;
    @SerializedName("release_date")
    private String  releaseDate;
    private long    id;
    @SerializedName("original_title")
    private String  originalTitle;
    @SerializedName("original_language")
    private String  originalLanguage;
    private String  title;
    @SerializedName("backdrop_path")
    private String  backdropPath;
    private double  popularity;
    @SerializedName("vote_count")
    private long    voteCount;
    private boolean video;
    @SerializedName("vote_average")
    private double  voteAverage;
    @SerializedName("genre_ids")
    private int[]   genreId;

    public MoviesResponseBean() {
    }

    private MoviesResponseBean(@NonNull Parcel in) {
        super(in);
        this.posterPath = in.readString();
        this.adult = in.readByte() != 0;
        this.overview = in.readString();
        this.releaseDate = in.readString();
        this.id = in.readLong();
        this.originalTitle = in.readString();
        this.originalLanguage = in.readString();
        this.title = in.readString();
        this.backdropPath = in.readString();
        this.popularity = in.readDouble();
        this.voteCount = in.readLong();
        this.video = in.readByte() != 0;
        this.voteAverage = in.readDouble();
        this.genreId = in.createIntArray();
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    public long getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public int[] getGenreId() {
        return genreId;
    }

    public void setGenreId(int[] genreId) {
        this.genreId = genreId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.posterPath);
        dest.writeByte(adult ? (byte) 1 : (byte) 0);
        dest.writeString(this.overview);
        dest.writeString(this.releaseDate);
        dest.writeLong(this.id);
        dest.writeString(this.originalTitle);
        dest.writeString(this.originalLanguage);
        dest.writeString(this.title);
        dest.writeString(this.backdropPath);
        dest.writeDouble(this.popularity);
        dest.writeLong(this.voteCount);
        dest.writeByte(video ? (byte) 1 : (byte) 0);
        dest.writeDouble(this.voteAverage);
        dest.writeIntArray(this.genreId);
    }
}
