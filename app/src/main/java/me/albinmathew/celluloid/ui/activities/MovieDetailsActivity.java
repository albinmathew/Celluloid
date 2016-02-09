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

package me.albinmathew.celluloid.ui.activities;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;

import me.albinmathew.celluloid.R;
import me.albinmathew.celluloid.api.response.MoviesResponseBean;
import me.albinmathew.celluloid.app.CAConstants;
import me.albinmathew.celluloid.utilities.CommonUtil;

/**
 * The Movie details activity which displays detailed descriptions on selected movie .
 */
public class MovieDetailsActivity extends AppCompatActivity {

    private int mutedColor;
    private CollapsingToolbarLayout collapsingToolbar;
    private MoviesResponseBean moviesResponseBean;
    private ImageView backdropImage;
    private ImageView posterImage;
    private TextView mTitleView;
    private TextView mReleaseDate;
    private TextView mRating;
    private TextView mDescription;
    private TextView mVotes;
    private TextView mGenre;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        moviesResponseBean = getIntent().getParcelableExtra(CAConstants.INTENT_EXTRA);
        initViews();
        setToolBar();
        initValues();
        loadBackdrop();
    }

    private void initValues() {
        mTitleView.setText(moviesResponseBean.getOriginalTitle());
        mReleaseDate.setText(CommonUtil.getDisplayReleaseDate(moviesResponseBean.getReleaseDate()));
        mRating.setText(getString(R.string.movie_details_rating, moviesResponseBean.getVoteAverage()));
        mDescription.setText(moviesResponseBean.getOverview());
        mVotes.setText(getString(R.string.movie_details_votes, moviesResponseBean.getVoteCount()));
        mGenre.setText(getString(R.string.movie_details_genre,CommonUtil.getGenreList(moviesResponseBean)));
        Picasso.with(MovieDetailsActivity.this).
                load(CAConstants.POSTER_BASE_URL+moviesResponseBean.getPosterPath()).into(posterImage);
    }

    private void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        collapsingToolbar.setTitle(moviesResponseBean.getTitle());
    }

    private void initViews() {
        backdropImage = (ImageView) findViewById(R.id.backdrop);
        posterImage = (ImageView) findViewById(R.id.poster_image);
        mTitleView = (TextView) findViewById(R.id.movie_name);
        mReleaseDate = (TextView) findViewById(R.id.release_date);
        mRating = (TextView) findViewById(R.id.rating);
        mDescription = (TextView) findViewById(R.id.description);
        mVotes = (TextView) findViewById(R.id.votes);
        mGenre = (TextView) findViewById(R.id.genres);
    }

    /**
     * Loads the backdrop image
     */
    private void loadBackdrop() {

        Picasso.with(this).load(CAConstants.BACKDROP_BASE_URL+moviesResponseBean.getBackdropPath()).into(backdropImage);
        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... params) {
                try {
                    return Picasso.with(MovieDetailsActivity.this).
                            load(CAConstants.BACKDROP_BASE_URL+moviesResponseBean.getBackdropPath()).get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                if(bitmap!=null){
                    Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                        @Override
                        public void onGenerated(Palette palette) {
                            mutedColor = palette.getDarkMutedColor(getResources().getColor(R.color.colorPrimaryDark));
                            collapsingToolbar.setContentScrimColor(mutedColor);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                Window window = MovieDetailsActivity.this.getWindow();
                                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                                window.setStatusBarColor(mutedColor-10);
                            }
                        }
                    });
                }
            }
        }.execute(null,null,null);
    }
}
