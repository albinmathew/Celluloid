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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import me.albinmathew.celluloid.R;
import me.albinmathew.celluloid.api.response.MoviesResponseBean;
import me.albinmathew.celluloid.app.CAConstants;
import me.albinmathew.celluloid.ui.activities.MovieDetailsActivity;
import me.albinmathew.celluloid.ui.widget.CAImageView;
import me.albinmathew.celluloid.utilities.CommonUtil;

/**
 * The Movies adapter to display list of movies.
 *
 * @author albin
 * @date 28 /1/16
 */
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieGridViewHolder> {

    private List<MoviesResponseBean> mMoviesList;
    private Context mContext;

    /**
     * Instantiates a new Movies adapter.
     *
     * @param context    the context
     * @param moviesList the movies list
     */
    public MoviesAdapter(Context context, List<MoviesResponseBean> moviesList) {
        this.mMoviesList = moviesList;
        this.mContext = context;
    }

    @Override
    public MovieGridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_movie, parent, false);
        return new MovieGridViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return mMoviesList.size();
    }

    @Override
    public void onBindViewHolder(MovieGridViewHolder holder, int position) {
        final MoviesResponseBean movies = mMoviesList.get(position);
        holder.mMovieId.setText(CommonUtil.getGenreList(movies));
        holder.mMovieName.setText(movies.getTitle());
        Picasso.with(mContext).load(CAConstants.POSTER_BASE_URL + movies.getPosterPath()).into(holder.mImageView);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, MovieDetailsActivity.class);
                intent.putExtra(CAConstants.INTENT_EXTRA, movies);
                context.startActivity(intent);
            }
        });
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
        private TextView mMovieId;
        private TextView mMovieName;
        private CAImageView mImageView;
        private View mView;

        /**
         * Instantiates a new Movie grid view holder.
         *
         * @param itemView the item view
         */
        public MovieGridViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mMovieId = (TextView) itemView.findViewById(R.id.movie_item_genres);
            mMovieName = (TextView) itemView.findViewById(R.id.movie_item_title);
            mImageView = (CAImageView) itemView.findViewById(R.id.movie_item_image);
        }
    }
}
