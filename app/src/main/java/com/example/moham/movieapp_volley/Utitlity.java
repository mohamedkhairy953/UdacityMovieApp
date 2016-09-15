package com.example.moham.movieapp_volley;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by moham on 9/8/2016.
 */
public class Utitlity{


    public static boolean mTwoPane;
    public static int listName;

    public static SharedPreferences getSharedPref(Context c){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(c);
          return preferences;
    }
}
