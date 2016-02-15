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

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.IOException;

import me.albinmathew.celluloid.R;
import me.albinmathew.celluloid.api.response.MoviesResponseBean;
import me.albinmathew.celluloid.app.CAConstants;
import me.albinmathew.celluloid.ui.fragments.MovieDetailFragment;

/**
 * An activity representing a single Movie detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link MoviesActivity}.
 */
public class MovieDetailsActivity extends AppCompatActivity {

    private int mutedColor;
    private CollapsingToolbarLayout collapsingToolbar;
    private MoviesResponseBean moviesResponseBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        moviesResponseBean = getIntent().getParcelableExtra(CAConstants.INTENT_EXTRA);
        setToolBar();
        loadDetailsFragment(savedInstanceState);
        loadBackdrop();
    }

    private void loadDetailsFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(CAConstants.INTENT_EXTRA, getIntent().getParcelableExtra(CAConstants.INTENT_EXTRA));
            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            navigateUpTo(new Intent(this, MoviesActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        collapsingToolbar.setTitle(moviesResponseBean.getTitle());
    }

    /**
     * Loads the backdrop image
     */
    private void loadBackdrop() {
        ImageView backdropImage = (ImageView) findViewById(R.id.backdrop);
        Picasso.with(this).load(CAConstants.BACKDROP_BASE_URL + moviesResponseBean.getBackdropPath()).into(backdropImage);
        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... params) {
                try {
                    return Picasso.with(MovieDetailsActivity.this).load(CAConstants.BACKDROP_BASE_URL + moviesResponseBean.getBackdropPath()).get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                if (bitmap != null) {
                    Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                        @Override
                        public void onGenerated(Palette palette) {
                            mutedColor = palette.getDarkMutedColor(getResources().getColor(R.color.colorPrimaryDark));
                            collapsingToolbar.setContentScrimColor(mutedColor);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                Window window = MovieDetailsActivity.this.getWindow();
                                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                                window.setStatusBarColor(mutedColor - 10);
                            }
                        }
                    });
                }
            }
        }.execute(null, null, null);
    }
}
