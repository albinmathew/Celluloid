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

import java.util.List;

import me.albinmathew.celluloid.api.response.ReviewResponseBean;

/**
 * @author albin
 * @date 16/2/16
 */
public class BaseReviewBean extends BaseBean {

    private List<ReviewResponseBean> results;

    public List<ReviewResponseBean> getResults() {
        return results;
    }

    public void setResults(List<ReviewResponseBean> results) {
        this.results = results;
    }

    public BaseReviewBean() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(results);
    }

    protected BaseReviewBean(Parcel in) {
        super(in);
        this.results = in.createTypedArrayList(ReviewResponseBean.CREATOR);
    }

    public static final Creator<BaseReviewBean> CREATOR = new Creator<BaseReviewBean>() {
        public BaseReviewBean createFromParcel(Parcel source) {
            return new BaseReviewBean(source);
        }

        public BaseReviewBean[] newArray(int size) {
            return new BaseReviewBean[size];
        }
    };
}
