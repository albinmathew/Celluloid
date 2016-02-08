/*
 * Copyright 2015.  Emin Yahyayev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.albinmathew.celluloid.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import me.albinmathew.celluloid.R;

/**
 * The  Aspect Ratio adjusted image view.
 */
public class CAImageView extends ImageView {

    private float aspectRatio = 0;
    private AspectRatioSource aspectRatioSource = null;

    /**
     * Instantiates a new CAimage view.
     *
     * @param context the context
     */
    public CAImageView(Context context) {
        super(context);
    }

    /**
     * Instantiates a new Ca image view.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public CAImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CAImageView);
        aspectRatio = a.getFloat(R.styleable.CAImageView_imageAspectRatio, 0);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        float localRatio = aspectRatio;

        if (localRatio == 0.0 && aspectRatioSource != null
                && aspectRatioSource.getHeight() > 0) {
            localRatio =
                    (float) aspectRatioSource.getWidth()
                            / (float) aspectRatioSource.getHeight();
        }

        if (localRatio == 0.0) {
            super.onMeasure(widthSpec, heightSpec);
        } else {
            int lockedWidth = MeasureSpec.getSize(widthSpec);
            int lockedHeight = MeasureSpec.getSize(heightSpec);

            if (lockedWidth == 0 && lockedHeight == 0) {
                throw new IllegalArgumentException(
                        "Both width and height cannot be zero -- watch out for scrollable containers");
            }

            // Get the padding of the border background.
            int hPadding = getPaddingLeft() + getPaddingRight();
            int vPadding = getPaddingTop() + getPaddingBottom();

            // Resize the preview frame with correct aspect ratio.
            lockedWidth -= hPadding;
            lockedHeight -= vPadding;

            if (lockedHeight > 0 && (lockedWidth > lockedHeight * localRatio)) {
                lockedWidth = (int) (lockedHeight * localRatio + .5);
            } else {
                lockedHeight = (int) (lockedWidth / localRatio + .5);
            }

            // Add the padding of the border.
            lockedWidth += hPadding;
            lockedHeight += vPadding;

            // Ask children to follow the new preview dimension.
            super.onMeasure(MeasureSpec.makeMeasureSpec(lockedWidth, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(lockedHeight, MeasureSpec.EXACTLY));
        }
    }

    /**
     * Sets aspect ratio source.
     *
     * @param v the v
     */
    public void setAspectRatioSource(View v) {
        this.aspectRatioSource = new ViewAspectRatioSource(v);
    }

    /**
     * Sets aspect ratio source.
     *
     * @param aspectRatioSource the aspect ratio source
     */
    public void setAspectRatioSource(AspectRatioSource aspectRatioSource) {
        this.aspectRatioSource = aspectRatioSource;
    }

    // from com.android.camera.PreviewFrameLayout, with slight
    // modifications

    /**
     * Sets aspect ratio.
     *
     * @param aspectRatio the aspect ratio
     */
    public void setAspectRatio(float aspectRatio) {
        if (aspectRatio <= 0.0) {
            throw new IllegalArgumentException(
                    "aspect ratio must be positive");
        }

        if (this.aspectRatio != aspectRatio) {
            this.aspectRatio = aspectRatio;
            requestLayout();
        }
    }

    /**
     * The interface Aspect ratio source.
     */
    public interface AspectRatioSource {
        /**
         * Gets width.
         *
         * @return the width
         */
        int getWidth();

        /**
         * Gets height.
         *
         * @return the height
         */
        int getHeight();
    }

    private static class ViewAspectRatioSource implements
            AspectRatioSource {
        private View v = null;

        /**
         * Instantiates a new View aspect ratio source.
         *
         * @param v the v
         */
        ViewAspectRatioSource(View v) {
            this.v = v;
        }

        @Override
        public int getWidth() {
            return (v.getWidth());
        }

        @Override
        public int getHeight() {
            return (v.getHeight());
        }
    }

}
