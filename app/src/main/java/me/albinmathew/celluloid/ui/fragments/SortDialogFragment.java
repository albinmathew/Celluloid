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

package me.albinmathew.celluloid.ui.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.albinmathew.celluloid.R;
import me.albinmathew.celluloid.app.CAConstants;
import me.albinmathew.celluloid.listener.SortSelectListener;

/**
 * The Sort dialog fragment for sorting popular and most rated movies.
 *
 * @author albin
 * @date 5 /2/16
 */
public class SortDialogFragment extends DialogFragment {

    /**
     * The constant STATE_SORT_LISTENER.
     */
    public static final String STATE_SORT_LISTENER = "state_sort_listener";
    private static final String STATE_CURRENT_SORT_SELECTION = "state_movies_current_sort";
    private SortSelectListener mSortSelectListener;
    @Nullable
    private String mCurrentSortOrder;
    private Dialog mDialog;

    @Bind(R.id.button_sort_popularity)
    public RadioButton mButtonPopularity;
    @Bind(R.id.button_sort_mostrated)
    public RadioButton mButtonRating;
    @Bind(R.id.button_favourites)
    public RadioButton mButtonFavourite;

    /**
     * Instantiates a new Sort dialog fragment.
     */
    public SortDialogFragment() {
    }

    /**
     * New instance sort dialog fragment.
     *
     * @return the sort dialog fragment
     */
    @NonNull
    public static SortDialogFragment newInstance() {
        return new SortDialogFragment();
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        mDialog = new Dialog(getActivity());
        mDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mDialog.setContentView(R.layout.dialog_sort);
        ButterKnife.bind(this, mDialog);

        if (savedInstanceState != null) {
            mCurrentSortOrder = savedInstanceState.getString(STATE_CURRENT_SORT_SELECTION);
        }
        toggle();
        onClickListeners();
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setCancelable(false);
        setRetainInstance(true);
        mDialog.show();
        return mDialog;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_CURRENT_SORT_SELECTION, mCurrentSortOrder);
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setDismissMessage(null);
        }
        super.onDestroyView();
    }

    /**
     * Click listeners
     */
    private void onClickListeners() {
        mButtonPopularity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSortSelectListener != null) {
                    mCurrentSortOrder = CAConstants.POPULARITY;
                    toggle();
                    mSortSelectListener.onSortCategorySelected(mCurrentSortOrder);
                    mDialog.dismiss();
                }
            }
        });
        mButtonRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSortSelectListener != null) {
                    mCurrentSortOrder = CAConstants.VOTE_AVERAGE;
                    toggle();
                    mSortSelectListener.onSortCategorySelected(mCurrentSortOrder);
                    mDialog.dismiss();
                }
            }
        });
        mButtonFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSortSelectListener != null) {
                    mCurrentSortOrder = CAConstants.FAVOURITES;
                    toggle();
                    mSortSelectListener.onSortCategorySelected(mCurrentSortOrder);
                    mDialog.dismiss();
                }
            }
        });
    }

    private void toggle() {
        switch (getCurrentSortOrder()) {
            case CAConstants.POPULARITY:
                mButtonPopularity.toggle();
                break;
            case CAConstants.VOTE_AVERAGE:
                mButtonRating.toggle();
                break;
            default:
                mButtonFavourite.toggle();
                break;
        }
    }

    /**
     * Sets sort select listener.
     *
     * @param mSortSelectListener the m sort select listener
     */
    public void setSortSelectListener(SortSelectListener mSortSelectListener) {
        this.mSortSelectListener = mSortSelectListener;
    }

    /**
     * Gets current sort order.
     *
     * @return the current sort order
     */
    @Nullable
    private String getCurrentSortOrder() {
        return mCurrentSortOrder;
    }

    /**
     * Sets current sort order.
     *
     * @param mCurrentSortOrder the m current sort order
     */
    public void setCurrentSortOrder(String mCurrentSortOrder) {
        this.mCurrentSortOrder = mCurrentSortOrder;
    }
}
