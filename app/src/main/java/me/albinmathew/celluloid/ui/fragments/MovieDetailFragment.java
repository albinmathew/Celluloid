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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import me.albinmathew.celluloid.R;
import me.albinmathew.celluloid.api.response.MoviesResponseBean;
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
public class MovieDetailFragment extends Fragment {


    private MoviesResponseBean moviesResponseBean;
    private ImageView posterImage;
    private TextView mTitleView;
    private TextView mReleaseDate;
    private TextView mRating;
    private TextView mDescription;
    private TextView mVotes;
    private TextView mGenre;

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
            moviesResponseBean = getArguments().getParcelable(CAConstants.INTENT_EXTRA);;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.content_movie_details, container, false);
        initViews(rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initValues();
    }

    private void initValues() {
        mTitleView.setText(moviesResponseBean.getOriginalTitle());
        mReleaseDate.setText(CommonUtil.getDisplayReleaseDate(moviesResponseBean.getReleaseDate()));
        mRating.setText(getString(R.string.movie_details_rating, moviesResponseBean.getVoteAverage()));
        mDescription.setText(moviesResponseBean.getOverview());
        mVotes.setText(getString(R.string.movie_details_votes, moviesResponseBean.getVoteCount()));
        mGenre.setText(getString(R.string.movie_details_genre, CommonUtil.getGenreList(moviesResponseBean)));
        Picasso.with(getActivity()).load(CAConstants.POSTER_BASE_URL + moviesResponseBean.getPosterPath()).into(posterImage);
    }


    private void initViews(View rootView) {
        posterImage = (ImageView) rootView.findViewById(R.id.poster_image);
        mTitleView = (TextView) rootView.findViewById(R.id.movie_name);
        mReleaseDate = (TextView) rootView.findViewById(R.id.release_date);
        mRating = (TextView) rootView.findViewById(R.id.rating);
        mDescription = (TextView) rootView.findViewById(R.id.description);
        mVotes = (TextView) rootView.findViewById(R.id.votes);
        mGenre = (TextView) rootView.findViewById(R.id.genres);
    }
}
