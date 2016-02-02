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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import me.albinmathew.celluloid.R;
import me.albinmathew.celluloid.models.Movies;

/**
 * @author albin
 * @date 28/1/16
 */
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieGridViewHolder> {

    private List<Movies> mMoviesList;

    private Context mContext;

    public MoviesAdapter(Context context, List<Movies> moviesList) {
        this.mMoviesList = moviesList;
        this.mContext = context;
    }

    @Override
    public MovieGridViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.test_layout, parent, false);
        return new MovieGridViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return mMoviesList.size();
    }

    @Override
    public void onBindViewHolder(MovieGridViewHolder holder, int position) {
        Movies movies = mMoviesList.get(position);
        holder.mMovieId.setText(String.valueOf(movies.getId()));
        holder.mMovieName.setText(movies.getName());
    }

    public class MovieGridViewHolder extends RecyclerView.ViewHolder {
        private TextView mMovieId;
        private TextView mMovieName;

        public MovieGridViewHolder(View itemView) {
            super(itemView);
            mMovieId = (TextView) itemView.findViewById(R.id.list_movie_id);
            mMovieName = (TextView) itemView.findViewById(R.id.list_movie_name);
        }
    }
}
