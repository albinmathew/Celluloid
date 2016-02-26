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

package me.albinmathew.celluloid.api;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import me.albinmathew.celluloid.api.base.BaseApi;
import me.albinmathew.celluloid.api.base.BaseBean;
import me.albinmathew.celluloid.api.base.BaseMovieBean;
import me.albinmathew.celluloid.api.base.BaseReviewBean;
import me.albinmathew.celluloid.api.base.BaseVideoBean;
import retrofit.RetrofitError;

/**
 * The Api manager for all APIs.
 *
 * @author albin
 * @date 2 /2/16
 */
public class ApiManager {

    private static ApiManager sApiManager;

    @Nullable
    private MoviesApi mMoviesApi = null;
    @Nullable
    private ReviewsApi mReviewsApi = null;
    @Nullable
    private VideosApi mVideosApi = null;

    @Nullable
    private ProgressListener<BaseMovieBean> moviesFetchListener;
    @Nullable
    private ProgressListener<BaseReviewBean> reviewFetchListener;
    @Nullable
    private ProgressListener<BaseVideoBean> videoFetchListener;

    private boolean isMoviesAPILoading = false;
    private boolean isReviewsAPILoading = false;
    private boolean isVideosAPILoading = false;

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static ApiManager getInstance() {
        if (sApiManager == null) {
            sApiManager = new ApiManager();
        }
        return sApiManager;
    }

    @NonNull
    private MoviesApi getMoviesApi() {
        if (mMoviesApi != null) {
            mMoviesApi.clearListener();
        }
        mMoviesApi = new MoviesApi();
        return mMoviesApi;
    }

    @NonNull
    private ReviewsApi getReviewsApi() {
        if (mReviewsApi != null) {
            mReviewsApi.clearListener();
        }
        mReviewsApi = new ReviewsApi();
        return mReviewsApi;
    }

    @NonNull
    private VideosApi getVideosApi() {
        if (mVideosApi != null) {
            mVideosApi.clearListener();
        }
        mVideosApi = new VideosApi();
        return mVideosApi;
    }

    /**
     * Fetch movies list.
     *
     * @param listener  the listener
     * @param sortOrder the sort order
     * @param pageCount the page count
     */
    public void fetchMoviesList(ProgressListener<BaseMovieBean> listener, String sortOrder, int pageCount) {

        moviesFetchListener = listener;
        if (moviesFetchListener != null) {
            moviesFetchListener.inProgress();
        }
        if (isMoviesAPILoading) {
            return;
        }
        isMoviesAPILoading = true;

        getMoviesApi().fetchMoviesList(new BaseApi.BaseAPIListener() {
            @Override
            public void requestCompleted(BaseBean response) {
                isMoviesAPILoading = false;
                // return data
                if (moviesFetchListener != null) {
                    BaseMovieBean responseBean = (BaseMovieBean) response;
                    moviesFetchListener.completed(responseBean);
                    moviesFetchListener = null;
                }
            }

            @Override
            public void requestFailed(@NonNull RetrofitError error) {
                // return error
                if (moviesFetchListener != null) {
                    moviesFetchListener.failed(error.getMessage());
                    moviesFetchListener = null;
                }
            }

        }, sortOrder, pageCount);
    }

    /**
     * Fetch reviews list.
     *
     * @param listener  the listener
     * @param movieId   the movie id
     * @param pageCount the page count
     */
    public void fetchReviewsList(ProgressListener<BaseReviewBean> listener, long movieId, int pageCount) {

        reviewFetchListener = listener;
        if (reviewFetchListener != null) {
            reviewFetchListener.inProgress();
        }
        if (isReviewsAPILoading) {
            return;
        }
        isReviewsAPILoading = true;

        getReviewsApi().fetchReviewsList(new BaseApi.BaseAPIListener() {
            @Override
            public void requestCompleted(BaseBean response) {
                isReviewsAPILoading = false;
                // return data
                if (reviewFetchListener != null) {
                    BaseReviewBean responseBean = (BaseReviewBean) response;
                    reviewFetchListener.completed(responseBean);
                    reviewFetchListener = null;
                }
            }

            @Override
            public void requestFailed(@NonNull RetrofitError error) {
                // return error
                if (reviewFetchListener != null) {
                    reviewFetchListener.failed(error.getMessage());
                    reviewFetchListener = null;
                }
            }

        }, movieId, pageCount);
    }

    /**
     * Fetch videos list.
     *
     * @param listener the listener
     * @param movieId  the movie id
     */
    public void fetchVideosList(ProgressListener<BaseVideoBean> listener, long movieId) {

        videoFetchListener = listener;
        if (videoFetchListener != null) {
            videoFetchListener.inProgress();
        }
        if (isVideosAPILoading) {
            return;
        }
        isVideosAPILoading = true;

        getVideosApi().fetchVideosList(new BaseApi.BaseAPIListener() {
            @Override
            public void requestCompleted(BaseBean response) {
                isVideosAPILoading = false;
                // return data
                if (videoFetchListener != null) {
                    BaseVideoBean responseBean = (BaseVideoBean)response;
                    videoFetchListener.completed(responseBean);
                    videoFetchListener = null;
                }
            }

            @Override
            public void requestFailed(@NonNull RetrofitError error) {
                // return error
                if (videoFetchListener != null) {
                    videoFetchListener.failed(error.getMessage());
                    videoFetchListener = null;
                }
            }

        }, movieId);
    }

    /**
     * The interface Progress listener.
     *
     * @param <T> the type parameter
     */
    public interface ProgressListener<T> {

        /**
         * In progress.
         */
        void inProgress();

        /**
         * Failed.
         *
         * @param message the message
         */
        void failed(String message);

        /**
         * Completed.
         *
         * @param object the object
         */
        void completed(T object);

    }
}
