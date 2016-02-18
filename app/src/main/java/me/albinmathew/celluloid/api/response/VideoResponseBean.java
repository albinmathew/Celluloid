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

import com.google.gson.annotations.SerializedName;

import me.albinmathew.celluloid.api.base.BaseVideoBean;

/**
 * @author albin
 * @date 16/2/16
 */
public class VideoResponseBean extends BaseVideoBean{

    private String id;
    private String name;
    private String site;
    @SerializedName("key")
    private String videoId;
    private int size;
    private String type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.site);
        dest.writeString(this.videoId);
        dest.writeInt(this.size);
        dest.writeString(this.type);
    }

    public VideoResponseBean() {
    }

    protected VideoResponseBean(Parcel in) {
        super(in);
        this.id = in.readString();
        this.name = in.readString();
        this.site = in.readString();
        this.videoId = in.readString();
        this.size = in.readInt();
        this.type = in.readString();
    }

    public static final Creator<VideoResponseBean> CREATOR = new Creator<VideoResponseBean>() {
        public VideoResponseBean createFromParcel(Parcel source) {
            return new VideoResponseBean(source);
        }

        public VideoResponseBean[] newArray(int size) {
            return new VideoResponseBean[size];
        }
    };
}
