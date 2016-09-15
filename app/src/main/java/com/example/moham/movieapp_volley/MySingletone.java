package com.example.moham.movieapp_volley;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by moham on 6/12/2016.
 */
public class MySingletone {
    private static MySingletone mInstance;
    private RequestQueue requestQueue;
    private static Context mctx;

    private MySingletone(Context context){
        mctx=context;
        getRequestQueue();

    }

    private RequestQueue getRequestQueue(){

        if(requestQueue == null){

            requestQueue= Volley.newRequestQueue(mctx.getApplicationContext());
        }
        return requestQueue;
    }

    public static  MySingletone getmInstance(Context context) {
        if(mInstance==null){
            mInstance=new MySingletone(context);
        }
        return mInstance;
    }
    public void addToRequestQueue(Request request){
        requestQueue.add(request);
    }
}
