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

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.albinmathew.celluloid.R;
import me.albinmathew.celluloid.api.ApiManager;
import me.albinmathew.celluloid.api.base.BaseReviewBean;
import me.albinmathew.celluloid.api.base.BaseVideoBean;
import me.albinmathew.celluloid.api.response.MoviesResponseBean;
import me.albinmathew.celluloid.api.response.ReviewResponseBean;
import me.albinmathew.celluloid.api.response.VideoResponseBean;
import me.albinmathew.celluloid.app.CAConstants;
import me.albinmathew.celluloid.data.MovieContract;
import me.albinmathew.celluloid.ui.activities.MovieDetailsActivity;
import me.albinmathew.celluloid.ui.activities.MoviesActivity;
import me.albinmathew.celluloid.utilities.CommonUtil;

/**
 * A fragment representing a single Movie detail screen. This fragment is either contained in a
 * {@link MoviesActivity} in two-pane mode (on tablets) or a {@link MovieDetailsActivity} on
 * handsets.
 */
public class MovieDetailFragment extends Fragment implements View.OnClickListener {

    @Bind(R.id.poster_image)
    public ImageView posterImage;
    @Bind(R.id.movie_name)
    public TextView mTitleView;
    @Bind(R.id.release_date)
    public TextView mReleaseDate;
    @Bind(R.id.rating)
    public TextView mRating;
    @Bind(R.id.description)
    public TextView mDescription;
    @Bind(R.id.votes)
    public TextView mVotes;
    @Bind(R.id.genres)
    public TextView mGenre;
    @Bind(R.id.trailers_label)
    public TextView mTrailerLabel;
    @Bind(R.id.trailers_container)
    public HorizontalScrollView mTrailersScrollView;
    @Bind(R.id.trailers)
    public LinearLayout mTrailersView;
    @Bind(R.id.reviews_label)
    public TextView mReviewsLabel;
    @Bind(R.id.reviews)
    public LinearLayout mReviewsView;
    @Bind(R.id.fab)
    public FloatingActionButton mFavorite;

    private int mutedColor;
    private CollapsingToolbarLayout collapsingToolbar;
    @Nullable
    private MoviesResponseBean moviesResponseBean;
    private Context mContext;
    @Nullable
    private Toolbar mToolbar = null;
    private ContentValues values;
    private boolean isFavourite = false;
    private List<VideoResponseBean> videoArrayList;
    private ShareActionProvider shareActionProvider;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon
     * screen orientation changes).
     */
    public MovieDetailFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(CAConstants.INTENT_EXTRA)) {
            moviesResponseBean = getArguments().getParcelable(CAConstants.INTENT_EXTRA);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_details, container, false);
        ButterKnife.bind(this, rootView);
        initViews(rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initValues();
        setToolBar(view);
        loadBackdrop(view);
        createContentValues();
        fetchVideoList();
        fetchReviewsList();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_movie_details, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share_trailer);
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        MenuItemCompat.setActionProvider(menuItem, shareActionProvider);
    }

    private void initValues() {
        mTitleView.setText(moviesResponseBean.getOriginalTitle());
        mReleaseDate.setText(CommonUtil.getDisplayReleaseDate(moviesResponseBean.getReleaseDate()));
        mRating.setText(getString(R.string.movie_details_rating, moviesResponseBean.getVoteAverage()));
        mDescription.setText(moviesResponseBean.getOverview());
        mVotes.setText(getString(R.string.movie_details_votes, moviesResponseBean.getVoteCount()));
        mGenre.setText(getString(R.string.movie_details_genre, CommonUtil.getGenreList(moviesResponseBean)));
        Glide.with(getContext())
                .load(CAConstants.POSTER_BASE_URL + moviesResponseBean.getPosterPath())
                .error(R.drawable.placeholder)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(posterImage);
    }

    private void setToolBar(@NonNull View view) {
        collapsingToolbar = (CollapsingToolbarLayout) view.findViewById(R.id.toolbar_layout);
        collapsingToolbar.setTitle(moviesResponseBean.getTitle());
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
    }

    /**
     * Loads the backdrop image
     *
     * @param view backdrop
     */
    private void loadBackdrop(@NonNull View view) {
        ImageView backdropImage = (ImageView) view.findViewById(R.id.backdrop);
        Glide.with(getContext())
                .load(CAConstants.BACKDROP_BASE_URL +
                        moviesResponseBean.getBackdropPath())
                .error(R.drawable.placeholder)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(backdropImage);
        new AsyncTask<Void, Void, Bitmap>() {
            @Nullable
            @Override
            protected Bitmap doInBackground(Void... params) {
                try {
                    return Picasso.with(mContext).load(CAConstants.BACKDROP_BASE_URL +
                            moviesResponseBean.getBackdropPath()).error(R.drawable.placeholder).get();
                } catch (IOException ignored) {
                }
                return null;
            }

            @Override
            protected void onPostExecute(@Nullable Bitmap bitmap) {
                super.onPostExecute(bitmap);
                if (bitmap != null) {
                    Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                        @Override
                        public void onGenerated(@NonNull Palette palette) {
                            if (mContext != null && isAdded()) {
                                mutedColor = palette.getDarkMutedColor(getResources().
                                        getColor(R.color.colorPrimaryDark));
                                collapsingToolbar.setContentScrimColor(mutedColor);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    Window window = getActivity().getWindow();
                                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                                    window.setStatusBarColor(mutedColor - 6);
                                }
                            }
                        }
                    });
                }
            }
        }.execute(null, null, null);
    }

    private void createContentValues() {
        values = new ContentValues();
        if (moviesResponseBean.getPosterPath() == null) {
            moviesResponseBean.setPosterPath("No url found");
        }
        if (moviesResponseBean.getBackdropPath() == null) {
            moviesResponseBean.setBackdropPath("No url found");
        }

        values.put(MovieContract.Movie.COLUMN_TITLE, moviesResponseBean.getTitle());
        values.put(MovieContract.Movie.COLUMN_POSTER_URL, moviesResponseBean.getPosterPath());
        values.put(MovieContract.Movie.COLUMN_BACK_DROP_URL, moviesResponseBean.getBackdropPath());
        values.put(MovieContract.Movie.COLUMN_ORIGINAL_TITLE, moviesResponseBean.getOriginalTitle());
        values.put(MovieContract.Movie.COLUMN_PLOT, moviesResponseBean.getOverview());
        values.put(MovieContract.Movie.COLUMN_RATING, moviesResponseBean.getVoteAverage());
        values.put(MovieContract.Movie.COLUMN_RELEASE_DATE, moviesResponseBean.getReleaseDate());
        values.put(MovieContract.Movie.COLUMN_MOVIE_ID, moviesResponseBean.getId());
        values.put(MovieContract.Movie.COLUMN_GENRE_ID, CommonUtil.convertArrayToString(moviesResponseBean.getGenreId()));

        Cursor c = getContext().getContentResolver().
                query(MovieContract.Movie.CONTENT_URI,
                        new String[]{MovieContract.Movie.COLUMN_MOVIE_ID},
                        MovieContract.Movie.COLUMN_MOVIE_ID + "= ? ",
                        new String[]{String.valueOf(moviesResponseBean.getId())},
                        null);

        if (c != null) {
            if (c.getCount() > 0) {
                showFavourites();
            } else {
                showUnFavourites();
            }
            c.close();
        }
    }

    private void fetchVideoList() {
        ApiManager.getInstance().fetchVideosList(new ApiManager.ProgressListener<BaseVideoBean>() {
            @Override
            public void inProgress() {

            }

            @Override
            public void failed(String message) {

            }

            @Override
            public void completed(@NonNull BaseVideoBean response) {
                videoArrayList = response.getResults();
                showTrailers(videoArrayList);
            }
        }, moviesResponseBean.getId());
    }

    private void fetchReviewsList() {
        ApiManager.getInstance().fetchReviewsList(new ApiManager.ProgressListener<BaseReviewBean>() {
            @Override
            public void inProgress() {

            }

            @Override
            public void failed(String message) {

            }

            @Override
            public void completed(@NonNull BaseReviewBean response) {
                ArrayList<ReviewResponseBean> reviewArrayList = (ArrayList<ReviewResponseBean>) response.getResults();
                showReviews(reviewArrayList);
            }
        }, moviesResponseBean.getId(), 1);
    }

    public void showFavourites() {
        isFavourite = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mFavorite.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_favorite_full, mContext.getTheme()));
        } else {
            mFavorite.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_favorite_full));
        }
    }

    public void showUnFavourites() {
        isFavourite = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mFavorite.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_favorite_border, mContext.getTheme()));
        } else {
            mFavorite.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_favorite_border));
        }
    }

    public void showTrailers(@NonNull List<VideoResponseBean> trailers) {
        if (trailers.isEmpty()) {
            mTrailerLabel.setVisibility(View.GONE);
            mTrailersView.setVisibility(View.GONE);
            mTrailersScrollView.setVisibility(View.GONE);

        } else {
            mTrailerLabel.setVisibility(View.VISIBLE);
            mTrailersView.setVisibility(View.VISIBLE);
            mTrailersScrollView.setVisibility(View.VISIBLE);
            mTrailersView.removeAllViews();
            if (mContext != null && isAdded()) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                Picasso picasso = Picasso.with(getContext());
                shareVideos();
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
        }
    }

    public void showReviews(@NonNull List<ReviewResponseBean> reviews) {
        if (reviews.isEmpty()) {
            mReviewsLabel.setVisibility(View.GONE);
            mReviewsView.setVisibility(View.GONE);
        } else {
            mReviewsLabel.setVisibility(View.VISIBLE);
            mReviewsView.setVisibility(View.VISIBLE);
            mReviewsView.removeAllViews();
            if (mContext != null && isAdded()) {
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

    private void shareVideos() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String trailersVideo = "No Videos to Share";
        shareIntent.putExtra(Intent.EXTRA_TEXT, trailersVideo);
        try {
            if (videoArrayList != null) {
                trailersVideo = CommonUtil.getUrl(videoArrayList.get(0));
            }
        } catch (IndexOutOfBoundsException ignored) {
        }

        shareIntent.putExtra(Intent.EXTRA_TEXT, trailersVideo);
        if (shareActionProvider != null) {
            shareActionProvider.setShareIntent(shareIntent);
        }
    }

    private void initViews(@NonNull View rootView) {
        if (getActivity() instanceof MoviesActivity) {
            mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        } else {
            mToolbar = (Toolbar) rootView.findViewById(R.id.detail_toolbar);
        }
        mFavorite.setOnClickListener(this);
    }

    @Override
    public void onClick(@NonNull View view) {
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
                case R.id.fab:
                    setFavourites();
                default:
                    break;
            }
        }
    }

    private void setFavourites() {
        if (isFavourite) {
            showUnFavourites();
            int rowDeleted = getContext().getContentResolver().delete(MovieContract.Movie.CONTENT_URI, MovieContract.Movie.COLUMN_MOVIE_ID + "= ?", new String[]{String.valueOf(moviesResponseBean.getId())});
            if (rowDeleted > 0) {
                showSnackBar("Removed " + moviesResponseBean.getTitle() + " from favourites");
            }
        } else {
            showFavourites();
            Uri rowUri = getContext().getContentResolver().insert(MovieContract.Movie.CONTENT_URI, values);
            long rowId = ContentUris.parseId(rowUri);
            if (rowId > 0) {
                showSnackBar("Added " + moviesResponseBean.getTitle() + " to favourites");
            }
        }
    }

    private void showSnackBar(@NonNull String message) {
        Snackbar.make(getActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }
}
