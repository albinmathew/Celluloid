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

import me.albinmathew.celluloid.api.base.BaseApi;
import me.albinmathew.celluloid.api.base.BaseVideoBean;
import me.albinmathew.celluloid.app.CAConstants;
import me.albinmathew.celluloid.app.CelluloidApp;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * The type Videos api.
 *
 * @author albin
 * @date 16/2/16
 */
public class VideosApi extends BaseApi implements Callback<BaseVideoBean> {

    private BaseAPIListener otBaseApiListener = null;

    /**
     * Instantiates a new Movies api.
     */
    public VideosApi() {
    }

    /**
     * Fetch movies list.
     *
     * @param apiListener the api listener
     * @param movieId     the movie id
     */
    public void fetchVideosList(BaseAPIListener apiListener, long movieId) {
        otBaseApiListener = apiListener;
        CelluloidApp.getRestClient().getMovieDbService().getVideosList(movieId, CAConstants.API_KEY, this);
    }

    /**
     * Clear listener.
     */
    public void clearListener() {
        this.otBaseApiListener = null;
    }

    @Override
    public BaseAPIListener getApiListener() {
        return otBaseApiListener;
    }

    @Override
    public void success(BaseVideoBean responseBean, Response response) {
        successResponse(responseBean,response);
    }

    @Override
    public void failure(RetrofitError error) {
        failureResponse(error);
    }
}
