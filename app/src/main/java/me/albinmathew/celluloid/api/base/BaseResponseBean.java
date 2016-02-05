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
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import me.albinmathew.celluloid.api.response.MoviesResponseBean;

/**
 * @author albin
 * @date 2/2/16
 */
public class BaseResponseBean implements Parcelable {

    private int page;
    @SerializedName("total_results")
    private int totalResults;
    @SerializedName("total_pages")
    private int totalPages;
    private List<MoviesResponseBean> results;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

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
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.page);
        dest.writeInt(this.totalResults);
        dest.writeInt(this.totalPages);
        dest.writeTypedList(results);
    }

    public BaseResponseBean() {
    }

    protected BaseResponseBean(Parcel in) {
        this.page = in.readInt();
        this.totalResults = in.readInt();
        this.totalPages = in.readInt();
        this.results = in.createTypedArrayList(MoviesResponseBean.CREATOR);
    }

    public static final Parcelable.Creator<BaseResponseBean> CREATOR = new Parcelable.Creator<BaseResponseBean>() {
        public BaseResponseBean createFromParcel(Parcel source) {
            return new BaseResponseBean(source);
        }

        public BaseResponseBean[] newArray(int size) {
            return new BaseResponseBean[size];
        }
    };
}
