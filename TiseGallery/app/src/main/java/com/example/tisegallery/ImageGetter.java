package com.example.tisegallery;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

public class ImageGetter {

    private static ImageGetter instance;
    private static RequestQueue requestQueue;
    private JsonArrayRequest jsonArrayRequest;
    JsonObjectRequest jsonObjectRequest;

    public static synchronized ImageGetter getInstance(Context context){
        if (instance == null){
            instance = new ImageGetter();
        }

        setRequestQueue(context);
        return instance;
    }

    private static void setRequestQueue(Context context){
        if (requestQueue==null){
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
    }

    public static void addToQueue(JsonObjectRequest jsonObjectRequest){
        requestQueue.add(jsonObjectRequest);
    }
    public static void addToQueue(JsonArrayRequest jsonArrayRequest){
        requestQueue.add(jsonArrayRequest);
    }



}
