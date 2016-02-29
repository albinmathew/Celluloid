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

package me.albinmathew.celluloid.app;

import android.app.Application;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import me.albinmathew.celluloid.api.http.RestClient;
import me.albinmathew.celluloid.models.Genre;
import me.albinmathew.celluloid.models.GenreList;

/**
 * The type Celluloid app.
 *
 * @author albin
 * @date 1 /2/16
 */
public class CelluloidApp extends Application {
    private static final Map<Integer, String> genreMap = new HashMap<>();
    private static CelluloidApp sCelluloidApp;
    private static RestClient   sRestClient;

    /**
     * Gets rest client.
     *
     * @return the rest client
     */
    public static RestClient getRestClient() {
        return sRestClient;
    }

    /**
     * Gets genre map.
     *
     * @return the genre map
     */
    @NonNull
    public static Map<Integer, String> getGenreMap() {
        return genreMap;
    }

    /**
     * Gets celluloid app.
     *
     * @return the celluloid app
     */
    public synchronized static CelluloidApp getInstance() {
        return sCelluloidApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sRestClient = new RestClient();
        sCelluloidApp = this;
        Gson gson = new Gson();
        GenreList genreResponse = gson.fromJson(loadJSONFromAsset(), GenreList.class);
        ArrayList<Genre> genreArrayList = (ArrayList<Genre>) genreResponse.getGenres();
        for (Genre genre : genreArrayList) {
            genreMap.put(genre.getId(), genre.getName());
        }
    }

    /**
     * Load json from asset string.
     *
     * @return the string
     */
    private String loadJSONFromAsset() {
        String json;
        try {
            InputStream is = this.getAssets().open("genre.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            //noinspection ResultOfMethodCallIgnored
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            return null;
        }
        return json;
    }
}
