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

package me.albinmathew.celluloid.api.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import me.albinmathew.celluloid.api.service.MovieDbService;
import me.albinmathew.celluloid.app.CAConstants;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * The Rest client.
 *
 * @author albin
 * @date 2 /2/16
 */
public class RestClient {

    private final MovieDbService movieDbService;


    /**
     * Instantiates a new Rest client.
     */
    public RestClient() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(new ItemTypeAdapterFactory())
                .create();

        RestAdapter parseRestAdapter = new RestAdapter.Builder()
                .setEndpoint(CAConstants.API_BASE_URL)
                .setConverter(new GsonConverter(gson))
                .build();

        movieDbService = parseRestAdapter.create(MovieDbService.class);
    }

    /**
     * Gets movie db service.
     *
     * @return the movie db service
     */
    public MovieDbService getMovieDbService() {
        return movieDbService;
    }
}
