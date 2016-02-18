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

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

import me.albinmathew.celluloid.R;
import me.albinmathew.celluloid.api.ApiManager;
import me.albinmathew.celluloid.api.base.BaseBean;
import me.albinmathew.celluloid.api.base.BaseReviewBean;
import me.albinmathew.celluloid.api.base.BaseVideoBean;
import me.albinmathew.celluloid.api.response.MoviesResponseBean;
import me.albinmathew.celluloid.api.response.ReviewResponseBean;
import me.albinmathew.celluloid.api.response.VideoResponseBean;
import me.albinmathew.celluloid.app.CAConstants;
import me.albinmathew.celluloid.ui.activities.MovieDetailsActivity;
import me.albinmathew.celluloid.ui.activities.MoviesActivity;
import me.albinmathew.celluloid.utilities.CommonUtil;

/**
 * A fragment representing a single Movie detail screen.
 * This fragment is either contained in a {@link MoviesActivity}
 * in two-pane mode (on tablets) or a {@link MovieDetailsActivity}
 * on handsets.
 */
public class MovieDetailFragment extends Fragment implements View.OnClickListener {


    private int mutedColor;
    private CollapsingToolbarLayout collapsingToolbar;
    private MoviesResponseBean moviesResponseBean;
    private ImageView posterImage;
    private TextView mTitleView;
    private TextView mReleaseDate;
    private TextView mRating;
    private TextView mDescription;
    private TextView mVotes;
    private TextView mGenre;
    private TextView mTrailerLabel;
    private HorizontalScrollView mTrailersScrollView;
    private LinearLayout mTrailersView;
    private TextView mReviewsLabel;
    private LinearLayout mReviewsView;
    private FloatingActionButton mFavorite;
    private Context mContext;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(CAConstants.INTENT_EXTRA)) {
            moviesResponseBean = getArguments().getParcelable(CAConstants.INTENT_EXTRA);
            ;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_details, container, false);
        initViews(rootView);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initValues();
        setToolBar(view);
        loadBackdrop(view);
        fetchVideoList();
    }

    private void fetchReviewsList() {
        ApiManager.getInstance().fetchReviewsList(new ApiManager.ProgressListener<BaseBean>() {
            @Override
            public void inProgress() {

            }

            @Override
            public void failed(String message) {

            }

            @Override
            public void completed(BaseBean response) {
                BaseReviewBean reviewBean = (BaseReviewBean) response;
                ArrayList<ReviewResponseBean> reviewArrayList = (ArrayList<ReviewResponseBean>) reviewBean.getResults();
                showReviews(reviewArrayList);
            }
        }, moviesResponseBean.getId(), 1);
    }

    private void fetchVideoList() {
        ApiManager.getInstance().fetchVideosList(new ApiManager.ProgressListener<BaseBean>() {
            @Override
            public void inProgress() {

            }

            @Override
            public void failed(String message) {

            }

            @Override
            public void completed(BaseBean response) {
                BaseVideoBean videoBean = (BaseVideoBean) response;
                ArrayList<VideoResponseBean> videoArrayList = (ArrayList<VideoResponseBean>) videoBean.getResults();
                showTrailers(videoArrayList);
            }
        }, moviesResponseBean.getId());
    }

    private void initValues() {
        mTitleView.setText(moviesResponseBean.getOriginalTitle());
        mReleaseDate.setText(CommonUtil.getDisplayReleaseDate(moviesResponseBean.getReleaseDate()));
        mRating.setText(getString(R.string.movie_details_rating, moviesResponseBean.getVoteAverage()));
        mDescription.setText(moviesResponseBean.getOverview());
        mVotes.setText(getString(R.string.movie_details_votes, moviesResponseBean.getVoteCount()));
        mGenre.setText(getString(R.string.movie_details_genre, CommonUtil.getGenreList(moviesResponseBean)));
        Picasso.with(mContext).load(CAConstants.POSTER_BASE_URL + moviesResponseBean.getPosterPath()).into(posterImage);
    }


    private void initViews(View rootView) {
        posterImage = (ImageView) rootView.findViewById(R.id.poster_image);
        mTitleView = (TextView) rootView.findViewById(R.id.movie_name);
        mReleaseDate = (TextView) rootView.findViewById(R.id.release_date);
        mRating = (TextView) rootView.findViewById(R.id.rating);
        mDescription = (TextView) rootView.findViewById(R.id.description);
        mVotes = (TextView) rootView.findViewById(R.id.votes);
        mGenre = (TextView) rootView.findViewById(R.id.genres);
        mTrailerLabel = (TextView) rootView.findViewById(R.id.trailers_label);
        mTrailersScrollView = (HorizontalScrollView) rootView.findViewById(R.id.trailers_container);
        mTrailersView = (LinearLayout) rootView.findViewById(R.id.trailers);
        mReviewsLabel = (TextView) rootView.findViewById(R.id.reviews_label);
        mReviewsView = (LinearLayout) rootView.findViewById(R.id.reviews);
        mFavorite = (FloatingActionButton) rootView.findViewById(R.id.fab);
        mFavorite.setOnClickListener(this);
    }

    private void setToolBar(View view) {
        collapsingToolbar = (CollapsingToolbarLayout) view.findViewById(R.id.toolbar_layout);
        collapsingToolbar.setTitle(moviesResponseBean.getTitle());
    }

    /**
     * Loads the backdrop image
     *
     * @param view
     */
    private void loadBackdrop(View view) {
        ImageView backdropImage = (ImageView) view.findViewById(R.id.backdrop);
        Picasso.with(mContext).load(CAConstants.BACKDROP_BASE_URL + moviesResponseBean.getBackdropPath()).into(backdropImage);
        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... params) {
                try {
                    return Picasso.with(mContext).load(CAConstants.BACKDROP_BASE_URL + moviesResponseBean.getBackdropPath()).get();
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
                            if (mContext != null && isAdded()) {
                                mutedColor = palette.getDarkMutedColor(getResources().getColor(R.color.colorPrimaryDark));
                                collapsingToolbar.setContentScrimColor(mutedColor);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    Window window = getActivity().getWindow();
                                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                                    window.setStatusBarColor(mutedColor - 10);
                                }
                            }
                        }
                    });
                }
            }
        }.execute(null, null, null);
    }

    @Override
    public void onClick(View view) {
        {
            switch (view.getId()) {
                case R.id.video_thumb:
                    String videoUrl = (String) view.getTag();
                    Intent playVideoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
                    startActivity(playVideoIntent);
                    break;

                case R.id.review_content:
                    TextView review = (TextView) view;
                    if (review.getMaxLines() == 5) {
                        review.setMaxLines(500);
                    } else {
                        review.setMaxLines(5);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public void showTrailers(ArrayList<VideoResponseBean> trailers) {
        if (trailers.isEmpty()) {
            mTrailerLabel.setVisibility(View.GONE);
            mTrailersView.setVisibility(View.GONE);
            mTrailersScrollView.setVisibility(View.GONE);

        } else {
            mTrailerLabel.setVisibility(View.VISIBLE);
            mTrailersView.setVisibility(View.VISIBLE);
            mTrailersScrollView.setVisibility(View.VISIBLE);
            mTrailersView.removeAllViews();
            LayoutInflater inflater = getActivity().getLayoutInflater();
            Picasso picasso = Picasso.with(getContext());
            for (VideoResponseBean trailer : trailers) {
                ViewGroup thumbContainer = (ViewGroup) inflater.inflate(R.layout.video, mTrailersView, false);
                ImageView thumbView = (ImageView) thumbContainer.findViewById(R.id.video_thumb);
                thumbView.setTag(CommonUtil.getUrl(trailer));
                thumbView.requestLayout();
                thumbView.setOnClickListener(this);
                picasso
                        .load(CommonUtil.getThumbnailUrl(trailer))
                        .resizeDimen(R.dimen.video_width, R.dimen.video_height)
                        .centerCrop()
                        .placeholder(R.color.cardview_shadow_start_color)
                        .into(thumbView);
                mTrailersView.addView(thumbContainer);
            }
        }
        fetchReviewsList();
    }

    public void showReviews(ArrayList<ReviewResponseBean> reviews) {
        if (reviews.isEmpty()) {
            mReviewsLabel.setVisibility(View.GONE);
            mReviewsView.setVisibility(View.GONE);
        } else {
            mReviewsLabel.setVisibility(View.VISIBLE);
            mReviewsView.setVisibility(View.VISIBLE);

            mReviewsView.removeAllViews();
            LayoutInflater inflater = getActivity().getLayoutInflater();
            for (ReviewResponseBean review : reviews) {
                ViewGroup reviewContainer = (ViewGroup) inflater.inflate(R.layout.review, mReviewsView,
                        false);
                TextView reviewAuthor = (TextView) reviewContainer.findViewById(R.id.review_author);
                TextView reviewContent = (TextView) reviewContainer.findViewById(R.id.review_content);
                reviewAuthor.setText(review.getAuthor());
                reviewContent.setText(review.getContent());
                reviewAuthor.setPadding(10, 10, 10, 10);
                reviewContent.setPadding(10, 10, 10, 10);
                reviewContent.setOnClickListener(this);
                mReviewsView.addView(reviewContainer);
            }
        }
    }

}
