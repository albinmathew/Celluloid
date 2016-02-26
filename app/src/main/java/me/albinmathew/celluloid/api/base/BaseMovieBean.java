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

package me.albinmathew.celluloid.api.base;

import android.os.Parcel;
import android.support.annotation.NonNull;

import java.util.List;

import me.albinmathew.celluloid.api.response.MoviesResponseBean;

/**
 * @author albin
 * @date 2/2/16
 */
public class BaseMovieBean extends BaseBean {

    private List<MoviesResponseBean> results;

    public List<MoviesResponseBean> getResults() {
        return results;
    }

    public void setResults(List<MoviesResponseBean> results) {
        this.results = results;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(results);
    }

    public BaseMovieBean() {
    }

    protected BaseMovieBean(@NonNull Parcel in) {
        super(in);
        this.results = in.createTypedArrayList(MoviesResponseBean.CREATOR);
    }

    public static final Creator<BaseMovieBean> CREATOR = new Creator<BaseMovieBean>() {
        @NonNull
        public BaseMovieBean createFromParcel(@NonNull Parcel source) {
            return new BaseMovieBean(source);
        }

        @NonNull
        public BaseMovieBean[] newArray(int size) {
            return new BaseMovieBean[size];
        }
    };
}
