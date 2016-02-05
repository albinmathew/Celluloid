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

package me.albinmathew.celluloid.listener;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * @author albin
 * @date 3/2/16
 */
public abstract class RecyclerViewScrollListener extends RecyclerView.OnScrollListener {
    private int mFirstVisibleItem, mVisibleItemCount, mTotalItemCount;
    private int mPreviousTotal = 0; // The total number of items in the dataset after the last load
    private boolean mIsLoading = true; // True if we are still waiting for the last set of data to load.
    private int mVisibleThreshold = 4; // The minimum amount of items to have below your current scroll position before loading more.

    private GridLayoutManager mGridLayoutManager;

    public RecyclerViewScrollListener(GridLayoutManager gridLayoutManager) {
        this.mGridLayoutManager = gridLayoutManager;
    }

    public void setGridLayoutManager(GridLayoutManager gridLayoutManager) {
        this.mGridLayoutManager = gridLayoutManager;

    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        mVisibleItemCount = recyclerView.getChildCount();
        mTotalItemCount = mGridLayoutManager.getItemCount();
        mFirstVisibleItem = mGridLayoutManager.findFirstVisibleItemPosition();

        if (mIsLoading) {
            if (mTotalItemCount > mPreviousTotal) {
                mIsLoading = false;
                mPreviousTotal = mTotalItemCount;
            }
        }
        if (!mIsLoading && (mTotalItemCount - mVisibleItemCount)
                <= (mFirstVisibleItem + mVisibleThreshold)) {

            onLoadMore();

            mIsLoading = true;
        }
    }

    public abstract void onLoadMore();

    public void clearItemCountVariables() {
        mVisibleItemCount = 0;
        mTotalItemCount = 0;
        mFirstVisibleItem = 0;
        mPreviousTotal = 0;
    }

}
