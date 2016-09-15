package com.example.moham.movieapp_volley;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by moham on 8/12/2016.
 */
public class DetailFragment extends Fragment {
    DBController dbController;
    ListView listView_trailers;
    ProgressDialog dialog;
    ArrayList<Trailer_Model> keys;
    ArrayList<Review_Model> reviews_list;
    private ListView listView_reviews;
    ArrayAdapter arrayAdapter_trailers, arrayAdapter_reviews;
    Movie_model movieIntent;

    public DetailFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dialog = new ProgressDialog(getContext());

        dbController = new DBController(getContext());
        final Intent intent = getActivity().getIntent();
        if (getArguments() != null || intent.getSerializableExtra("movie") != null) {
            dialog.setMessage("LOADING TRAILERS ..");
            dialog.show();
            if (Utitlity.mTwoPane) {
                movieIntent = (Movie_model) getArguments().getSerializable("movie");
            } else {
                movieIntent = (Movie_model) intent.getSerializableExtra("movie");
            }
            View view = inflater.inflate(R.layout.detail_fragment, container, false);
            // Load Title text
            getActivity().setTitle(movieIntent.getTitle());
            // Load image in Image View
            ImageView detailImage = (ImageView) view.findViewById(R.id.detail_image);
            Picasso.with(getActivity()).load("https://image.tmdb.org/t/p/w185" + movieIntent.getPoster_path())
                    .placeholder(R.drawable.poster_place_holder)
                    .into(detailImage);

            // Load Release Date
            TextView releaseDateText = (TextView) view.findViewById(R.id.detail_release_date);
            releaseDateText.setText(movieIntent.getRelease_date());

            // Load Popularity
            TextView popularityText = (TextView) view.findViewById(R.id.detail_popularity);
            popularityText.setText("Popularity: " + movieIntent.getPopularity());

            //Load Overview
            TextView overviewText = (TextView) view.findViewById(R.id.detail_overview);
            overviewText.setText(movieIntent.getOverview());

            // Load Average
            TextView voteAverageText = (TextView) view.findViewById(R.id.detail_vote_average);
            voteAverageText.setText("Voter Average: " + movieIntent.getVote_average());

            // Load Vote Count
            TextView voteCountText = (TextView) view.findViewById(R.id.detail_vote_count);
            voteCountText.setText("Voter Count: " + movieIntent.getVote_count());

            final Button add_to_fav_btn = (Button) view.findViewById(R.id.add_fav_button_id);

            if (dbController.is_In_MyFav(movieIntent.getId())) {
                add_to_fav_btn.setEnabled(false);
                add_to_fav_btn.setTextColor(Color.GRAY);
            } else {
                add_to_fav_btn.setEnabled(true);
                add_to_fav_btn.setTextColor(Color.RED);
            }

            add_to_fav_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DBController dbController = new DBController(getActivity());
                    long l = dbController.insert_movie(movieIntent);
                    if (l > 0) {
                        Toast.makeText(getActivity(), movieIntent.getOriginal_title() + " has been added to your favourits", Toast.LENGTH_SHORT).show();
                        add_to_fav_btn.setEnabled(false);
                        add_to_fav_btn.setTextColor(Color.GRAY);
                    }
                }
            });

            listView_trailers = (ListView) view.findViewById(R.id.trailers_list_id);
            watchYoutubeVideo(movieIntent.getId());
            listView_trailers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent_youtube = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + keys.get(position).getKey()));
                    startActivity(intent_youtube);
                }
            });

            listView_reviews = (ListView) view.findViewById(R.id.reviews_list_id);
            getReviewFromWeb(movieIntent.getId());
            listView_reviews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String content = reviews_list.get(position).getContent();
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage(content);
                    builder.setTitle("REVIEW CONTENT");
                    builder.show();
                }
            });
            return view;
        } else {
            return null;
        }

    }

    private void watchYoutubeVideo(int id) {
        String uri = "http://api.themoviedb.org/3/movie/" + id + "/videos?api_key=faa38d8564f66b5c1339d257bf6a6da9";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, uri, (String) null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                keys = new ArrayList<>();
                String[] strings=null;
                try {
                    JSONArray results_arr = response.getJSONArray("results");
                    strings = new String[results_arr.length()];
                    for (int i = 0; i < results_arr.length(); i++) {
                        String key = results_arr.getJSONObject(i).getString("key");
                        Trailer_Model model = new Trailer_Model();
                        model.setKey(key);
                        model.setName("Trailer " + (i + 1));
                        strings[i] = model.getName();
                        keys.add(model);
                    }
                    arrayAdapter_trailers = new ArrayAdapter(getContext(), R.layout.custome_trailers_reviews_lstview_layout, strings);
                    listView_trailers.setAdapter(arrayAdapter_trailers);
                    dialog.dismiss();
                } catch (JSONException ex) {
                    Toast.makeText(getContext(), ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    dialog.dismiss();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(request);

    }

    private void getReviewFromWeb(int id) {
        String uri = "http://api.themoviedb.org/3/movie/" + id + "/reviews?api_key=faa38d8564f66b5c1339d257bf6a6da9";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, uri, (String) null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                reviews_list = new ArrayList<>();
                String[] strings=null;
                try {
                    JSONArray results_arr = response.getJSONArray("results");
                    strings = new String[results_arr.length()];
                    for (int i = 0; i < results_arr.length(); i++) {
                        String author = results_arr.getJSONObject(i).getString("author");
                        String content = results_arr.getJSONObject(i).getString("content");
                        Review_Model model = new Review_Model();
                        model.setAuthor(author);
                        model.setContent(content);
                        strings[i] = "Author :" + model.getAuthor();
                        reviews_list.add(model);
                    }
                    arrayAdapter_reviews = new ArrayAdapter(getContext(), R.layout.custome_trailers_reviews_lstview_layout, strings);
                    listView_reviews.setAdapter(arrayAdapter_reviews);
                } catch (JSONException ex) {
                    Toast.makeText(getContext(), ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error.getMessage()+"", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(request);

    }

}

class Trailer_Model {
    private String name;
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

class Review_Model {
    private String author;
    private String content;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}