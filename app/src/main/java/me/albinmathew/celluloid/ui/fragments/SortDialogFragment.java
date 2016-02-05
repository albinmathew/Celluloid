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
 * @author albin
 * @date 5/2/16
 */
public class SortDialogFragment extends DialogFragment {

    private Dialog mDialog;
    private RadioButton mButtonPopularity;
    private RadioButton mButtonRating;
    private SortSelectListener mSortSelectListener;

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
        setUpViews(mDialog);
        onClickListeners();
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        mDialog.setCancelable(false);
        mDialog.show();
        return mDialog;
    }

    private void onClickListeners() {
        mButtonPopularity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSortSelectListener!=null){
                    mSortSelectListener.onSortCategorySelected(CAConstants.POPULARITY);
                    mDialog.dismiss();
                }
            }
        });
        mButtonRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSortSelectListener!=null){
                    mSortSelectListener.onSortCategorySelected(CAConstants.VOTE_AVERAGE);
                    mDialog.dismiss();
                }
            }
        });
    }

    private void setUpViews(Dialog dialog) {
        mButtonRating = (RadioButton) dialog.findViewById(R.id.button_sort_mostrated);
        mButtonPopularity = (RadioButton) dialog.findViewById(R.id.button_sort_popularity);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void setSortSelectListener(SortSelectListener mSortSelectListener) {
        this.mSortSelectListener = mSortSelectListener;
    }
}
