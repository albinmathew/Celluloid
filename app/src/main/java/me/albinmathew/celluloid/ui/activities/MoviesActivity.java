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

import android.app.ProgressDialog;
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
import me.albinmathew.celluloid.listener.RecyclerViewScrollListener;
import me.albinmathew.celluloid.listener.SortSelectListener;
import me.albinmathew.celluloid.ui.fragments.SortDialogFragment;
import me.albinmathew.celluloid.utilities.NetworkUtil;

/**
 * @author albin
 * @date 2/2/16
 */

public class MoviesActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener,
        ControlLayerListener,SortSelectListener{

    private MoviesAdapter mMoviesAdapter;
    private RecyclerView mRecyclerView;
    private GridLayoutManager mGridLayoutManager;
    private  int mPageCount = 0;
    private SwipeRefreshLayout swipeLayout;
    private ProgressDialog progressDialog;
    private RelativeLayout mFilterPanelLinearLayout;
    private ControlLayerListener mControlLayerListener = this;
    private String mCurrentSortSelection = CAConstants.POPULARITY;
    private boolean sorted = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mFilterPanelLinearLayout = (RelativeLayout) findViewById(R.id.sort_layout);

        mGridLayoutManager =  new GridLayoutManager(this,2);
        mLoadMoreEndlessScrolling.setGridLayoutManager(mGridLayoutManager);

        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.addOnScrollListener(mLoadMoreEndlessScrolling);

        mMoviesAdapter = new MoviesAdapter(this,new ArrayList<MoviesResponseBean>());
        mRecyclerView.setAdapter(mMoviesAdapter);

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeResources(android.R.color.holo_red_light,
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_dark);

        if(NetworkUtil.hasInternetAccess(this)){
            retrieveMovieList(mCurrentSortSelection, getPageCount()+1);
        }else{
           showSnackbar();
        }
    }

    private void retrieveMovieList(final String sortOrder, final int pageCount) {
        ApiManager.getInstance().fetchMoviesList(new ApiManager.ProgressListener<BaseResponseBean>() {
            @Override
            public void inProgress() {
                progressDialog =   new ProgressDialog(MoviesActivity.this);
                progressDialog.setMessage("Please Wait..");
                progressDialog.show();
            }

            @Override
            public void failed(String message) {
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                showSnackbar();
            }

            @Override
            public void completed(BaseResponseBean responseBean) {
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                ArrayList<MoviesResponseBean> moviesArrayList = (ArrayList<MoviesResponseBean>) responseBean.getResults();
                mPageCount = responseBean.getPage();
                if(sorted){
                    mMoviesAdapter.getMoviesList().clear();
                }
                sorted = false;
                mMoviesAdapter.getMoviesList().addAll(moviesArrayList);
                mMoviesAdapter.notifyDataSetChanged();
                mRecyclerView.swapAdapter(mMoviesAdapter,false);
            }
        },sortOrder,pageCount);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private RecyclerViewScrollListener mLoadMoreEndlessScrolling = new RecyclerViewScrollListener(mGridLayoutManager) {
        @Override
        public void onLoadMore() {
            if(NetworkUtil.hasInternetAccess(MoviesActivity.this)){
                retrieveMovieList(mCurrentSortSelection, getPageCount()+1);
            }else{
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
    public void showControls() {
        mFilterPanelLinearLayout.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
    }

    @Override
    public void hideControls() {
        mFilterPanelLinearLayout.animate().translationY(mFilterPanelLinearLayout.getHeight()).
                setInterpolator(new AccelerateInterpolator(2));
    }

    private void showSnackbar(){
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
        if (id == R.id.action_settings) {
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
                if(NetworkUtil.hasInternetAccess(MoviesActivity.this)){
                    retrieveMovieList(mCurrentSortSelection,1);
                }else{
                    showSnackbar();
                }
            }
        },1000);

    }

    public void onSortClick(View view) {
        SortDialogFragment sortDialogFragment = SortDialogFragment.newInstance();
        sortDialogFragment.setSortSelectListener(this);
        sortDialogFragment.setCurrentSortOrder(mCurrentSortSelection);
        sortDialogFragment.show(getFragmentManager(), null);
    }

    public void onFilterClick(View view) {
    }

    @Override
    public void onSortCategorySelected(String selection) {
        mCurrentSortSelection = selection;
        if(NetworkUtil.hasInternetAccess(MoviesActivity.this)){
            sorted = true;
            retrieveMovieList(mCurrentSortSelection, 1);
        }else{
            showSnackbar();
        }
    }
    public int getPageCount() {
        return mPageCount;
    }

}
