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

package me.albinmathew.celluloid.utilities;

import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import me.albinmathew.celluloid.api.response.MoviesResponseBean;
import me.albinmathew.celluloid.api.response.VideoResponseBean;
import me.albinmathew.celluloid.app.CAConstants;
import me.albinmathew.celluloid.app.CelluloidApp;

/**
 * The Common util class for Celluloid app.
 *
 * @author albin
 * @date 3 /2/16
 */
public class CommonUtil {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

    /**
     * Checks for internet access
     *
     * @param context context
     * @return hasInternet boolean
     */
    public static boolean hasInternetAccess(Context context) {
        try {
            boolean hasInternet = false;

            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            NetworkInfo wifi = cm
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo mobile = cm
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if ((wifi.isAvailable() || mobile.isAvailable())
                    && networkInfo != null
                    && networkInfo.isConnectedOrConnecting()) {

                hasInternet = true;
            }

            return hasInternet;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check for wifi internet connectivity
     *
     * @param context context
     * @return hasInternet boolean
     */
    public static boolean hasWifiInternetAccess(Context context) {
        try {
            boolean hasWifiInternet = false;
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            NetworkInfo wifi = cm
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (wifi != null && wifi.isAvailable() && networkInfo != null
                    && networkInfo.isConnectedOrConnecting()) {

                hasWifiInternet = true;
            }

            return hasWifiInternet;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check for 3g internet connectivity
     *
     * @param context context
     * @return hasInternet boolean
     */
    public static boolean has3gInternetAccess(Context context) {
        try {
            boolean has3gInternet = false;
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            NetworkInfo mobile = cm
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (mobile != null && mobile.isAvailable() && // !ic_mobile.isRoaming() &&
                    networkInfo != null && networkInfo.isConnectedOrConnecting()) {

                has3gInternet = true;
            }

            return has3gInternet;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns the month and release date of the movie
     *
     * @param releaseDate release date in yyyy-MM-dd format
     * @return month and year
     */
    public static String getDisplayReleaseDate(String releaseDate) {
        if (TextUtils.isEmpty(releaseDate)) return "";
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(DATE_FORMAT.parse(releaseDate));
            return calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH) + " " + calendar.get(Calendar.YEAR);
        } catch (ParseException e) {
            return "";
        }
    }

    /**
     * Get genre list string.
     *
     * @param movies the movies
     * @return the genre
     */
    public static String getGenreList(MoviesResponseBean movies) {
        String genre = "";
        for (int id : movies.getGenreId()) {
            genre += CelluloidApp.getGenreMap().get(id).concat(", ");
        }
        genre = genre.replaceAll(" $", "").replaceAll(",$", "");
        return genre;
    }

    /**
     * Is tablet boolean.
     *
     * @param context given context
     * @return true if its a tablet
     */
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /**
     * Gets url.
     *
     * @param video the video
     * @return the url
     */
    public static String getUrl(VideoResponseBean video) {
        if (CAConstants.SITE_YOUTUBE.equalsIgnoreCase(video.getSite())) {
            return String.format("http://www.youtube.com/watch?v=%1$s", video.getVideoId());
        } else {
            return CAConstants.EMPTY;
        }
    }

    /**
     * Gets thumbnail url.
     *
     * @param video the video
     * @return the thumbnail url
     */
    public static String getThumbnailUrl(VideoResponseBean video) {
        if (CAConstants.SITE_YOUTUBE.equalsIgnoreCase(video.getSite())) {
            return String.format("http://img.youtube.com/vi/%1$s/0.jpg", video.getVideoId());
        } else {
            return CAConstants.EMPTY;
        }
    }

}
