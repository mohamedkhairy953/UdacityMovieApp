package com.example.moham.movieapp_volley;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by moham on 8/12/2016.
 */
public class GridViewAdapter extends BaseAdapter {
    Context context;
    ArrayList<Movie_model> movieList;

    public GridViewAdapter(Context context, ArrayList<Movie_model> movieDbList) {
        this.context = context;
        this.movieList = movieDbList;
    }
    @Override
    public int getCount() {
        return movieList.size();
    }

    @Override
    public Movie_model getItem(int position) {
        return movieList.get(position);
    }


    @Override
    public long getItemId(int position) {
        return  123456000 + position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.custom_item_row, parent, false);
        }
        Movie_model movieDb = getItem(position);


        ImageView imageViewcustom = (ImageView) convertView.findViewById(R.id.customImageView);
        Picasso.with(context).load("https://image.tmdb.org/t/p/w185" + movieDb.getPoster_path())
                .placeholder(R.drawable.poster_place_holder)
                .into(imageViewcustom);

        return convertView;
    }
}

