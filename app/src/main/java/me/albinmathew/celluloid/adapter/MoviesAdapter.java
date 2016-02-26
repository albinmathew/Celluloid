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

package me.albinmathew.celluloid.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.albinmathew.celluloid.R;
import me.albinmathew.celluloid.api.response.MoviesResponseBean;
import me.albinmathew.celluloid.app.CAConstants;
import me.albinmathew.celluloid.ui.activities.MovieDetailsActivity;
import me.albinmathew.celluloid.ui.activities.MoviesActivity;
import me.albinmathew.celluloid.ui.fragments.MovieDetailFragment;
import me.albinmathew.celluloid.ui.widget.CAImageView;
import me.albinmathew.celluloid.utilities.CommonUtil;

/**
 * The Movies adapter to display list of movies.
 *
 * @author albin
 * @date 28 /1/16
 */
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieGridViewHolder> {

    private final MoviesActivity mContext;
    private List<MoviesResponseBean> mMoviesList;

    /**
     * Instantiates a new Movies adapter.
     *
     * @param context    the context
     * @param moviesList the movies list
     */
    public MoviesAdapter(MoviesActivity context, List<MoviesResponseBean> moviesList) {
        this.mMoviesList = moviesList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public MovieGridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_movie, parent, false);
        return new MovieGridViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MovieGridViewHolder holder, int position) {
        final MoviesResponseBean movies = mMoviesList.get(position);
        holder.mMovieId.setText(CommonUtil.getGenreList(movies));
        holder.mMovieName.setText(movies.getTitle());
        Glide.with(mContext)
                .load(CAConstants.POSTER_BASE_URL + movies.getPosterPath())
                .error(R.drawable.placeholder)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.mImageView);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                if (mContext.isTwoPane()) {
                    Bundle arguments = new Bundle();
                    arguments.putParcelable(CAConstants.INTENT_EXTRA, movies);
                    MovieDetailFragment fragment = new MovieDetailFragment();
                    fragment.setArguments(arguments);
                    mContext.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.movie_detail_container, fragment)
                            .commit();
                } else {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, MovieDetailsActivity.class);
                    intent.putExtra(CAConstants.INTENT_EXTRA, movies);
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMoviesList.size();
    }

    /**
     * Gets movies list.
     *
     * @return the movies list
     */
    public List<MoviesResponseBean> getMoviesList() {
        return mMoviesList;
    }

    /**
     * Sets movies list.
     *
     * @param mMoviesList the m movies list
     */
    public void setMoviesList(List<MoviesResponseBean> mMoviesList) {
        this.mMoviesList = mMoviesList;
    }

    /**
     * The type Movie grid view holder.
     */
    public class MovieGridViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.movie_item_genres)
        TextView mMovieId;
        @Bind(R.id.movie_item_title)
        TextView mMovieName;
        @Bind(R.id.movie_item_image)
        CAImageView mImageView;
        @NonNull
        private final View mView;

        /**
         * Instantiates a new Movie grid view holder.
         *
         * @param itemView the item view
         */
        public MovieGridViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            ButterKnife.bind(this, itemView);
        }
    }
}
