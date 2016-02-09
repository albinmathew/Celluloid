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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;

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

    private Dialog mDialog;
    private RadioButton mButtonPopularity;
    private RadioButton mButtonRating;
    private SortSelectListener mSortSelectListener;
    private String mCurrentSortOrder;
    private static final String STATE_CURRENT_SORT_SELECTION = "state_movies_current_sort";
    public static final String STATE_SORT_LISTENER  =   "state_sort_listener";

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
    public static SortDialogFragment newInstance() {
        return new SortDialogFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mDialog = new Dialog(getActivity());
        mDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mDialog.setContentView(R.layout.dialog_sort);
        setUpViews(mDialog, savedInstanceState);
        onClickListeners();
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setCancelable(false);
        setRetainInstance(true);
        mDialog.show();
        return mDialog;
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance())
            getDialog().setDismissMessage(null);
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
                    if(!getCurrentSortOrder().equals(CAConstants.POPULARITY)){
                        mButtonPopularity.toggle();
                    }
                    mSortSelectListener.onSortCategorySelected(CAConstants.POPULARITY);
                    mDialog.dismiss();
                }
            }
        });
        mButtonRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSortSelectListener != null) {
                    if(!getCurrentSortOrder().equals(CAConstants.VOTE_AVERAGE)){
                        mButtonRating.toggle();
                    }
                    mSortSelectListener.onSortCategorySelected(CAConstants.VOTE_AVERAGE);
                    mDialog.dismiss();
                }
            }
        });
    }

    private void setUpViews(Dialog dialog, Bundle savedInstanceState) {
        mButtonRating = (RadioButton) dialog.findViewById(R.id.button_sort_mostrated);
        mButtonPopularity = (RadioButton) dialog.findViewById(R.id.button_sort_popularity);
        if(savedInstanceState!=null){
            mCurrentSortOrder = savedInstanceState.getString(STATE_CURRENT_SORT_SELECTION);
        }
        if(getCurrentSortOrder().equals(CAConstants.POPULARITY)){
           mButtonPopularity.toggle();
        }else{
            mButtonRating.toggle();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_CURRENT_SORT_SELECTION, mCurrentSortOrder);
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
    public String getCurrentSortOrder() {
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
