package com.example.tisegallery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.bumptech.glide.Glide;
import com.example.tisegallery.adapter.ImagesAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView image_recycler;
    ImagesAdapter adapter;
    ArrayList<ImageDataModel> images = new ArrayList<>();
    final String IMAGES_BASE_URL = "https://api.unsplash.com/photos/?client_id=";
    final String CLIENT_ID = "E5DKFsaegfgGLLviUcpzJj9ykudtVf2oRn9QwgD7wgQ";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        image_recycler = findViewById(R.id.image_recycler);


        ImageGetter imageGetter = ImageGetter.getInstance(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, IMAGES_BASE_URL+CLIENT_ID, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i=0; i<response.length(); i++){
                    try {
                        JSONObject object = response.getJSONObject(i);
                        String imageUrl = object.getJSONObject("urls").getString("regular");
                        String imageTitle = object.getString("alt_description");
                        images.add(new ImageDataModel(imageTitle, imageUrl));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                loadImages();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("stuff", "Error response");
                Log.v("stuff", String.valueOf(error));
            }
        });

        imageGetter.addToQueue(jsonArrayRequest);

    }

    public void loadImages(){
        adapter = new ImagesAdapter(images, MainActivity.this);
        LinearLayoutManager llm = new LinearLayoutManager(this.getApplicationContext());
        llm.setOrientation(RecyclerView.VERTICAL);
        image_recycler.setLayoutManager(llm);
        image_recycler.setAdapter(adapter);
    }

}
