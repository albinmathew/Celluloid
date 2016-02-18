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

import android.util.Log;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 *  Base api.
 *
 * @author albin
 * @date 1 /2/16
 */
public abstract class BaseApi  {
    /**
     * Instantiates a new Base api.
     */
    protected BaseApi() {
    }

    /**
     * Gets api listener.
     *
     * @return the api listener
     */
    protected abstract BaseAPIListener getApiListener();


    protected void successResponse(BaseBean responseBean, Response response) {
        Log.d("SUCCESS_URL",response.getUrl());
        if (getApiListener() != null) {
            getApiListener().requestCompleted(responseBean);
        }
    }

    protected void failureResponse(RetrofitError error) {
        Log.d("ERROR_URL",error.getUrl() + "\n ERROR_REASON : "+error.getResponse().getStatus());
        if (getApiListener() != null) {
            getApiListener().requestFailed(error);
        }
    }

    /**
     * The interface Base api listener.
     */
    public interface BaseAPIListener {
        /**
         * Request completed.
         *
         * @param response the response
         */
        void requestCompleted(BaseBean response);

        /**
         * Request failed.
         *
         * @param error the error
         */
        void requestFailed(RetrofitError error);
    }
}
