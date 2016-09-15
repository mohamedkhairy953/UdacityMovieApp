package com.example.moham.movieapp_volley;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by moham on 8/12/2016.
 */
public class MoviesFragment extends Fragment {
    private static final String API_KEY = "faa38d8564f66b5c1339d257bf6a6da9";
    private static final String PREF_KEY_INT = "sort_by";
    private static final int FAVOURITE_LIST_NAME = 0;
    private static final int POPULAR_LIST_NAME = 1;
    private static final int TOP_VOTED_LIST_NAME = 2;
    GridView gridView;
    GridViewAdapter gridViewAdapter;
    private ArrayList<Movie_model> topVoted_arrylist = new ArrayList<>();
    private ArrayList<Movie_model> Popular_arrylist = new ArrayList<>();

    String WebAddress = "http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key="
            + API_KEY;
    String WebAddressVote = "http://api.themoviedb.org/3/discover/movie?sort_by=vote_average.desc&api_key="
            + API_KEY;

    public MoviesFragment() {

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root_view = inflater.inflate(R.layout.main_fragment, container, false);
        gridView = (GridView) root_view.findViewById(R.id.topMovieGrid);
        if (savedInstanceState != null) {
            switch (Utitlity.listName) {
                case FAVOURITE_LIST_NAME:
                    loadAdapter(gridView, new DBController(getActivity()).get_all_fav_movies(), "My Favourits Movies", FAVOURITE_LIST_NAME);
                    break;
                case POPULAR_LIST_NAME:
                    volleyRequest(WebAddress, POPULAR_LIST_NAME);
                    break;
                case TOP_VOTED_LIST_NAME:
                    volleyRequest(WebAddressVote, TOP_VOTED_LIST_NAME);
                    break;
            }

        } else {
            switch (Utitlity.getSharedPref(getActivity()).getInt(PREF_KEY_INT, 3)) {
                case FAVOURITE_LIST_NAME:
                    loadAdapter(gridView, new DBController(getActivity()).get_all_fav_movies(), "My Favourits Movies", FAVOURITE_LIST_NAME);
                    break;
                case POPULAR_LIST_NAME:
                    volleyRequest(WebAddress, POPULAR_LIST_NAME);

                    break;
                case TOP_VOTED_LIST_NAME:
                    volleyRequest(WebAddressVote, TOP_VOTED_LIST_NAME);
                    break;
                default:
                    volleyRequest(WebAddress, POPULAR_LIST_NAME);

            }
        }
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie_model movie = (Movie_model) parent.getAdapter().getItem(position);
                if (Utitlity.mTwoPane) {
                    Bundle arg = new Bundle();
                    arg.putSerializable("movie", movie);
                    DetailFragment detailFragment = new DetailFragment();
                    detailFragment.setArguments(arg);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container2, detailFragment).commit();
                } else {
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    intent.putExtra("movie", movie);
                    startActivity(intent);
                }
            }
        });
        return root_view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void loadAdapter(GridView view, ArrayList<Movie_model> list, String activityTitle, int listName) {
        gridViewAdapter = new GridViewAdapter(getActivity(), list);
        view.setAdapter(gridViewAdapter);
        getActivity().setTitle(activityTitle);
        SharedPreferences.Editor editor = Utitlity.getSharedPref(getActivity()).edit();
        editor.putInt(PREF_KEY_INT, listName);
        editor.apply();
        Utitlity.listName = listName;
    }

    private void volleyRequest(String my_url, final int listname) {
        final ProgressDialog dialo = new ProgressDialog(getContext());
        dialo.setMessage("LOADING ...");
        dialo.show();
        final ArrayList<Movie_model> list = new ArrayList<>();
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, my_url, (String) null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray resultsArray = response.getJSONArray("results");
                    for (int i = 0; i < resultsArray.length(); i++) {
                        JSONObject indexObject = resultsArray.getJSONObject(i);
                        Movie_model indexMovie = new Movie_model();
                        indexMovie.setBackdrop_path(indexObject.getString("backdrop_path"));
                        indexMovie.setId(indexObject.getInt("id"));
                        indexMovie.setOriginal_title(indexObject.getString("original_title"));
                        indexMovie.setOverview(indexObject.getString("overview"));
                        indexMovie.setRelease_date(indexObject.getString("release_date"));
                        indexMovie.setPoster_path(indexObject.getString("poster_path"));
                        indexMovie.setPopularity(indexObject.getDouble("popularity"));
                        indexMovie.setTitle(indexObject.getString("title"));
                        indexMovie.setVote_average(indexObject.getInt("vote_average"));
                        indexMovie.setVote_count(indexObject.getInt("vote_count"));
                        list.add(indexMovie); // Add each item to the list
                    }
                    dialo.dismiss();
                    switch (listname) {
                        case FAVOURITE_LIST_NAME:
                            loadAdapter(gridView, new DBController(getActivity()).get_all_fav_movies(), "My Favourits Movies", FAVOURITE_LIST_NAME);
                            break;
                        case POPULAR_LIST_NAME:
                            loadAdapter(gridView, list, "Popular Movies", POPULAR_LIST_NAME);
                            break;
                        case TOP_VOTED_LIST_NAME:
                            loadAdapter(gridView, list, "Top Voted Movies", TOP_VOTED_LIST_NAME);
                            break;
                    }
                } catch (Exception e) {
                    dialo.dismiss();
                    Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialo.dismiss();
                Toast.makeText(getContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        );
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_detail, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.sort_pop_id) {
            volleyRequest(WebAddress, POPULAR_LIST_NAME);
        } else if (id == R.id.sort_vote_id) {

            volleyRequest(WebAddressVote, TOP_VOTED_LIST_NAME);
        } else {
            loadAdapter(gridView, new DBController(getActivity()).get_all_fav_movies(), "My Favourits Movies", FAVOURITE_LIST_NAME);
        }

        return true;
    }
}
