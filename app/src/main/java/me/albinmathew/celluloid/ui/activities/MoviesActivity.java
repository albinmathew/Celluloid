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
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.albinmathew.celluloid.R;
import me.albinmathew.celluloid.adapter.MoviesAdapter;
import me.albinmathew.celluloid.api.ApiManager;
import me.albinmathew.celluloid.api.base.BaseMovieBean;
import me.albinmathew.celluloid.api.response.MoviesResponseBean;
import me.albinmathew.celluloid.app.CAConstants;
import me.albinmathew.celluloid.data.MovieContract;
import me.albinmathew.celluloid.listener.ControlLayerListener;
import me.albinmathew.celluloid.listener.OnScrollListener;
import me.albinmathew.celluloid.listener.SortSelectListener;
import me.albinmathew.celluloid.ui.fragments.SortDialogFragment;
import me.albinmathew.celluloid.utilities.CommonUtil;

/**
 * The Movies activity which displays movies in grid layout.
 *
 * @author albin
 * @date 2 /2/16
 */
public class MoviesActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener,
        ControlLayerListener, SortSelectListener {

    private static final String STATE_MOVIES = "state_movies";
    private static final String STATE_SELECTED_POSITION = "state_selected_position";
    private static final String STATE_CURRENT_SORT_SELECTION = "state_movies_current_sort";
    private static final String STATE_PAGE_COUNT = "state_movie_page_count";
    private final ControlLayerListener mControlLayerListener = this;

    @Bind(R.id.recycler_view)
    public RecyclerView mRecyclerView;
    @Bind(R.id.swipe_refresh_layout)
    public SwipeRefreshLayout swipeLayout;
    @Bind(R.id.sort_layout)
    public RelativeLayout mFilterPanelLinearLayout;

    private int mPageCount = 0;
    private int mSelectedPosition = -1;
    @Nullable
    private String mCurrentSortSelection = CAConstants.POPULARITY;
    @Nullable
    private MoviesAdapter mMoviesAdapter;
    private GridLayoutManager mGridLayoutManager;
    @Nullable
    private final OnScrollListener mLoadMoreEndlessScrolling = new OnScrollListener(mGridLayoutManager) {
        @Override
        public void onLoadMore() {
            if (!mCurrentSortSelection.equals(CAConstants.FAVOURITES)) {
                if (CommonUtil.hasInternetAccess(MoviesActivity.this)) {
                    retrieveMovieList(mCurrentSortSelection, getPageCount() + 1);
                } else {
                    showSnackBar();
                }
            }
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                if (mControlLayerListener != null) {
                    mControlLayerListener.showControls();
                }
            } else {
                if (mControlLayerListener != null) {
                    mControlLayerListener.hideControls();
                }
            }
        }
    };
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initViews();
        if (savedInstanceState != null) {
            mSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION, -1);
            mPageCount = savedInstanceState.getInt(STATE_PAGE_COUNT, 1);
            mCurrentSortSelection = savedInstanceState.getString(STATE_CURRENT_SORT_SELECTION);
        }
        ArrayList<MoviesResponseBean> restoredMovies = savedInstanceState != null
                ? savedInstanceState.<MoviesResponseBean>getParcelableArrayList(STATE_MOVIES) : new ArrayList<MoviesResponseBean>();
        mMoviesAdapter = new MoviesAdapter(this, restoredMovies);
        mRecyclerView.setAdapter(mMoviesAdapter);
        if (savedInstanceState == null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    swipeLayout.setRefreshing(true);
                    if (CommonUtil.hasInternetAccess(MoviesActivity.this)) {
                        clearAdapter();
                        retrieveMovieList(mCurrentSortSelection, 1);
                    } else {
                        swipeLayout.setRefreshing(false);
                        showSnackBar();
                        showFavouriteMovies();
                    }
                }
            }, 500);
        }
    }

    /**
     * Initialise views
     */
    private void initViews() {
        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;
        }
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeResources(android.R.color.holo_red_light);
        mGridLayoutManager = new GridLayoutManager(this, CommonUtil.isTablet(this) ? 3 : 2);
        mLoadMoreEndlessScrolling.setGridLayoutManager(mGridLayoutManager);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.addOnScrollListener(mLoadMoreEndlessScrolling);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return swipeLayout.isRefreshing();
            }
        });
    }

    /**
     * Clears the adapter
     */
    private void clearAdapter() {
        mPageCount = 0;
        mMoviesAdapter.getMoviesList().clear();
        if (mLoadMoreEndlessScrolling != null) {
            mLoadMoreEndlessScrolling.clearItemCountVariables();
        }
    }

    /**
     * Retrieve list of movies from server
     *
     * @param sortOrder sorting category
     * @param pageCount page number
     */
    private void retrieveMovieList(final String sortOrder, final int pageCount) {
        ApiManager.getInstance().fetchMoviesList(new ApiManager.ProgressListener<BaseMovieBean>() {
            @Override
            public void inProgress() {
                if (!swipeLayout.isRefreshing()) {
                    swipeLayout.setRefreshing(true);
                }
            }

            @Override
            public void failed(String message) {
                if (swipeLayout.isRefreshing()) {
                    swipeLayout.setRefreshing(false);
                }
                showSnackBar();
            }

            @Override
            public void completed(@NonNull BaseMovieBean responseBean) {
                ArrayList<MoviesResponseBean> moviesArrayList = (ArrayList<MoviesResponseBean>) responseBean.getResults();
                mPageCount = responseBean.getPage();
                mMoviesAdapter.getMoviesList().addAll(moviesArrayList);
                mMoviesAdapter.notifyDataSetChanged();
                if (swipeLayout.isRefreshing()) {
                    swipeLayout.setRefreshing(false);
                }
            }
        }, sortOrder, pageCount);
    }

    private void showSnackBar() {
        Snackbar.make(findViewById(android.R.id.content), "Check internet connectivity", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    /**
     * show Favourite movies from database
     */
    private void showFavouriteMovies() {
        mCurrentSortSelection = CAConstants.FAVOURITES;
        Cursor cursor = getContentResolver().query(MovieContract.Movie.CONTENT_URI, null, null, null, MovieContract.Movie._ID+" DESC");
        ArrayList<MoviesResponseBean> mPosterList = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                MoviesResponseBean resultModel = new MoviesResponseBean();
                resultModel.setTitle(cursor.getString(cursor.getColumnIndex(MovieContract.Movie.COLUMN_TITLE)));
                resultModel.setPosterPath(cursor.getString(cursor.getColumnIndex(MovieContract.Movie.COLUMN_POSTER_URL)));
                resultModel.setBackdropPath(cursor.getString(cursor.getColumnIndex(MovieContract.Movie.COLUMN_BACK_DROP_URL)));
                resultModel.setOriginalTitle(cursor.getString(cursor.getColumnIndex(MovieContract.Movie.COLUMN_ORIGINAL_TITLE)));
                resultModel.setOverview(cursor.getString(cursor.getColumnIndex(MovieContract.Movie.COLUMN_PLOT)));
                resultModel.setVoteAverage(cursor.getDouble(cursor.getColumnIndex(MovieContract.Movie.COLUMN_RATING)));
                resultModel.setReleaseDate(cursor.getString(cursor.getColumnIndex(MovieContract.Movie.COLUMN_RELEASE_DATE)));
                resultModel.setId(cursor.getLong(cursor.getColumnIndex(MovieContract.Movie.COLUMN_MOVIE_ID)));
                resultModel.setGenreId(CommonUtil.convertStringToArray(cursor.getString(cursor.getColumnIndex(MovieContract.Movie.COLUMN_GENRE_ID))));
                mPosterList.add(resultModel);
            }
            clearAdapter();
            mMoviesAdapter.getMoviesList().addAll(mPosterList);
            mMoviesAdapter.notifyDataSetChanged();
            cursor.close();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(STATE_MOVIES, new ArrayList<>(mMoviesAdapter.getMoviesList()));
        outState.putInt(STATE_SELECTED_POSITION, mSelectedPosition);
        outState.putInt(STATE_PAGE_COUNT, mPageCount);
        outState.putString(STATE_CURRENT_SORT_SELECTION, mCurrentSortSelection);
    }

    @Override
    public void showControls() {
        mFilterPanelLinearLayout.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
    }

    @Override
    public void hideControls() {
        mFilterPanelLinearLayout.animate().translationY(mFilterPanelLinearLayout.getHeight()).
                setInterpolator(new AccelerateInterpolator(2));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_movies, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeLayout.setRefreshing(false);
                if (!mCurrentSortSelection.equals(CAConstants.FAVOURITES)) {
                    if (CommonUtil.hasInternetAccess(MoviesActivity.this)) {
                        clearAdapter();
                        retrieveMovieList(mCurrentSortSelection, 1);
                    } else {
                        showSnackBar();
                    }
                } else {
                    showFavouriteMovies();
                }
            }
        }, 500);

    }

    /**
     * On sort click.
     *
     * @param view the view
     */
    public void onSortClick(View view) {
        SortDialogFragment sortDialogFragment = SortDialogFragment.newInstance();
        sortDialogFragment.setSortSelectListener(this);
        sortDialogFragment.setCurrentSortOrder(mCurrentSortSelection);
        sortDialogFragment.show(getFragmentManager(), null);
    }

    @Override
    public void onSortCategorySelected(String selection) {
        mCurrentSortSelection = selection;
        switch (mCurrentSortSelection) {
            case CAConstants.POPULARITY:
            case CAConstants.VOTE_AVERAGE:
                fetchSortedList();
                break;
            case CAConstants.FAVOURITES:
                showFavouriteMovies();
                break;
            default:
                showFavouriteMovies();
                break;
        }
    }

    /**
     * Fetch sorted list
     */
    private void fetchSortedList() {
        if (CommonUtil.hasInternetAccess(MoviesActivity.this)) {
            clearAdapter();
            retrieveMovieList(mCurrentSortSelection, 1);
        } else {
            showSnackBar();
        }
    }

    /**
     * Gets page count.
     *
     * @return the page count
     */
    private int getPageCount() {
        return mPageCount;
    }

    /**
     * Is two pane boolean.
     *
     * @return the boolean
     */
    public boolean isTwoPane() {
        return mTwoPane;
    }
}
