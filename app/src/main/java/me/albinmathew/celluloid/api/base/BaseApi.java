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

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 *  Base api.
 *
 * @author albin
 * @date 1 /2/16
 */
public abstract class BaseApi implements Callback<BaseResponseBean> {
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

    @Override
    public void success(BaseResponseBean responseBean, Response response) {
        if (getApiListener() != null) {
            getApiListener().requestCompleted(responseBean);
        }
    }

    @Override
    public void failure(RetrofitError error) {
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
        void requestCompleted(BaseResponseBean response);

        /**
         * Request failed.
         *
         * @param error the error
         */
        void requestFailed(RetrofitError error);
    }
}
