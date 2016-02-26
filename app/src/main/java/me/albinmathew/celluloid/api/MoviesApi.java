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

import android.support.annotation.Nullable;

import me.albinmathew.celluloid.api.base.BaseApi;
import me.albinmathew.celluloid.api.base.BaseMovieBean;
import me.albinmathew.celluloid.app.CAConstants;
import me.albinmathew.celluloid.app.CelluloidApp;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * The type Movies api.
 *
 * @author albin
 * @date 2 /2/16
 */
public class MoviesApi extends BaseApi implements Callback<BaseMovieBean> {

    @Nullable
    private BaseAPIListener otBaseApiListener = null;

    /**
     * Instantiates a new Movies api.
     */
    public MoviesApi() {
    }

    /**
     * Fetch movies list.
     *
     * @param apiListener the api listener
     * @param sortOrder   the sort order
     * @param pageCount   the page count
     */
    public void fetchMoviesList(BaseAPIListener apiListener, String sortOrder, int pageCount) {
        otBaseApiListener = apiListener;
        CelluloidApp.getRestClient().getMovieDbService().getMoviesList(sortOrder, CAConstants.API_KEY, pageCount, false, this);
    }

    /**
     * Clear listener.
     */
    public void clearListener() {
        this.otBaseApiListener = null;
    }

    @Nullable
    @Override
    public BaseAPIListener getApiListener() {
        return otBaseApiListener;
    }

    @Override
    public void success(BaseMovieBean responseBean, Response response) {
        successResponse(responseBean, response);
    }

    @Override
    public void failure(RetrofitError error) {
        failureResponse(error);
    }
}
