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

package me.albinmathew.celluloid.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.List;

/**
 * The type Genre list.
 *
 * @author albin
 * @date 8 /2/16
 */
public class GenreList implements Parcelable {
    /**
     * The constant CREATOR.
     */
    public static final Parcelable.Creator<GenreList> CREATOR = new Parcelable.Creator<GenreList>() {
        @NonNull
        public GenreList createFromParcel(@NonNull Parcel source) {
            return new GenreList(source);
        }

        @NonNull
        public GenreList[] newArray(int size) {
            return new GenreList[size];
        }
    };
    private List<Genre> genres;

    /**
     * Instantiates a new Genre list.
     */
    public GenreList() {
    }

    /**
     * Instantiates a new Genre list.
     *
     * @param in the in
     */
    protected GenreList(@NonNull Parcel in) {
        this.genres = in.createTypedArrayList(Genre.CREATOR);
    }

    /**
     * Gets genres.
     *
     * @return the genres
     */
    public List<Genre> getGenres() {
        return genres;
    }

    /**
     * Sets genres.
     *
     * @param genres the genres
     */
    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeTypedList(genres);
    }
}
