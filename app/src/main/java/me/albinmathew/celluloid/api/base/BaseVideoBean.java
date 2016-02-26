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

import me.albinmathew.celluloid.api.response.VideoResponseBean;

/**
 * @author albin
 * @date 16/2/16
 */
public class BaseVideoBean extends BaseBean {

    private List<VideoResponseBean> results;

    public List<VideoResponseBean> getResults() {
        return results;
    }

    public void setResults(List<VideoResponseBean> results) {
        this.results = results;
    }

    public BaseVideoBean() {
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

    protected BaseVideoBean(@NonNull Parcel in) {
        super(in);
        this.results = in.createTypedArrayList(VideoResponseBean.CREATOR);
    }

    public static final Creator<BaseVideoBean> CREATOR = new Creator<BaseVideoBean>() {
        @NonNull
        public BaseVideoBean createFromParcel(@NonNull Parcel source) {
            return new BaseVideoBean(source);
        }

        @NonNull
        public BaseVideoBean[] newArray(int size) {
            return new BaseVideoBean[size];
        }
    };
}
