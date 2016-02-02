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

package me.albinmathew.celluloid.api.service;

import me.albinmathew.celluloid.api.base.BaseResponseBean;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * @author albin
 * @date 1/2/16
 */
public interface MovieDbService {

    @GET("/3/discover/movie")
    void getMoviesList(@Query("sort_by") String sort_type,
                      @Query("api_key") String api_key, @Query("page") int page, Callback<BaseResponseBean> callback);
}
