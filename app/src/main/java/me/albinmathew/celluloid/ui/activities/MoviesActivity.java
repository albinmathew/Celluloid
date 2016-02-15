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
import android.os.Bundle;
import android.os.Handler;
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

import me.albinmathew.celluloid.R;
import me.albinmathew.celluloid.adapter.MoviesAdapter;
import me.albinmathew.celluloid.api.ApiManager;
import me.albinmathew.celluloid.api.base.BaseResponseBean;
import me.albinmathew.celluloid.api.response.MoviesResponseBean;
import me.albinmathew.celluloid.app.CAConstants;
import me.albinmathew.celluloid.listener.ControlLayerListener;
import me.albinmathew.celluloid.listener.HidingScrollListener;
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
    private MoviesAdapter mMoviesAdapter;
    private RecyclerView mRecyclerView;
    private GridLayoutManager mGridLayoutManager;
    private int mPageCount = 0;
    private SwipeRefreshLayout swipeLayout;
    private RelativeLayout mFilterPanelLinearLayout;
    private String mCurrentSortSelection = CAConstants.POPULARITY;
    private final OnScrollListener mLoadMoreEndlessScrolling = new OnScrollListener(mGridLayoutManager) {
        @Override
        public void onLoadMore() {
            if (CommonUtil.hasInternetAccess(MoviesActivity.this)) {
                retrieveMovieList(mCurrentSortSelection, getPageCount() + 1);
            } else {
                showSnackbar();
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
    private int mSelectedPosition = -1;
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private HidingScrollListener mFilterPanelScrollListener = new HidingScrollListener() {
        @Override
        public void onHide() {
            if (mControlLayerListener != null) {
                mControlLayerListener.hideControls();
            }
        }

        @Override
        public void onShow() {
            if (mControlLayerListener != null) {
                mControlLayerListener.showControls();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);
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
                        showSnackbar();
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
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeResources(android.R.color.holo_red_light);
        mFilterPanelLinearLayout = (RelativeLayout) findViewById(R.id.sort_layout);
        mGridLayoutManager = new GridLayoutManager(this, CommonUtil.isTablet(this) ? 3 : 2);
        mLoadMoreEndlessScrolling.setGridLayoutManager(mGridLayoutManager);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
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

    private void retrieveMovieList(final String sortOrder, final int pageCount) {
        ApiManager.getInstance().fetchMoviesList(new ApiManager.ProgressListener<BaseResponseBean>() {
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
                showSnackbar();
            }

            @Override
            public void completed(BaseResponseBean responseBean) {
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
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

    private void showSnackbar() {
        Snackbar.make(findViewById(android.R.id.content), "Check internet connectivity", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_movies, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
                if (CommonUtil.hasInternetAccess(MoviesActivity.this)) {
                    clearAdapter();
                    retrieveMovieList(mCurrentSortSelection, 1);
                } else {
                    showSnackbar();
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

    /**
     * On filter click.
     *
     * @param view the view
     */
    public void onFilterClick(View view) {
    }

    @Override
    public void onSortCategorySelected(String selection) {
        mCurrentSortSelection = selection;
        if (CommonUtil.hasInternetAccess(MoviesActivity.this)) {
            clearAdapter();
            retrieveMovieList(mCurrentSortSelection, 1);
        } else {
            showSnackbar();
        }
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
     * Gets page count.
     *
     * @return the page count
     */
    private int getPageCount() {
        return mPageCount;
    }

    public boolean isTwoPane() {
        return mTwoPane;
    }
}
