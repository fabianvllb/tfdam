package com.example.tfdam;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.example.tfdam.model.Image;
import com.example.tfdam.model.Planet;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class HomeActivity extends AppCompatActivity {
    public static final String TAG = "HomeActivity";
    private ProgressBar progressBar;
    private BottomNavigationView bottomNavigationView;
    private int page = 0;
    private final int listLenght = 10;
    private final int numberOfColumns = 2;
    private final String baseUrl = "https://picsum.photos/v2/list?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        progressBar = findViewById(R.id.progressBar);
        Random random = new Random();
        int min = 2; // inclusive
        int max = 20; // exclusive
        page = random.nextInt(max - min) + min;
        Toast.makeText((HomeActivity)this, "Page "+page,Toast.LENGTH_SHORT).show();
        String url = String.format("%spage=%s&limit=%s", baseUrl, page, listLenght);
        int recyclerViewSpanCount = 2;

        NavigationBarView.OnItemSelectedListener {item ->
                when(item.itemId) {
            R.id.item_1 -> {
                // Respond to navigation item 1 click
                true
            }
            R.id.item_2 -> {
                // Respond to navigation item 2 click
                true
            }
        else -> false
        }
        }

        //new Thread(new ImageDownloaderThread(HomeActivity.this, listLenght)).start();
        prepareUIForDownload();
        RequestQueue volleyQueue = Volley.newRequestQueue(HomeActivity.this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    new Thread(new ImageDownloaderThread(HomeActivity.this, response)).start();
                    /*for(Image imgData : list) {
                        stringBuilder.append(String.format("%s, ",imgData.getDownload_url()));
                        FutureTarget<Bitmap> futureTarget =
                                Glide.with(this)
                                        .asBitmap()
                                        .load(imgData.getDownload_url()) //img.getDownload_url()
                                        .submit(800,800);
                        try {
                            imgData.setBitmap(futureTarget.get());
                        } catch (ExecutionException | InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        Glide.with(this).clear(futureTarget);
                    }*/

                    /*for (int i = 0; i < response.length(); i++) {

                        try {
                            JSONObject responseObj = response.getJSONObject(i);
                            List<Image> list = Arrays.asList(gson.fromJson(responseObj.toString(), Image.class));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }*/
                },
                error -> {
                    Toast.makeText(HomeActivity.this, "Failed to get the data..", Toast.LENGTH_SHORT).show();
                    Log.e("HomeActivity", "loadImage error: " + error.toString());
                }
        );
        volleyQueue.add(jsonArrayRequest);
        prepareUIAfterDownload();
    }

    public void prepareUIForDownload() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void prepareUIAfterDownload() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    public void showDownloadResults(List<Image> results) {
        RecyclerView recyclerView = findViewById(R.id.home_rv_images);
        recyclerView.setAdapter(new ImageAdapter(this, results));
        recyclerView.addItemDecoration(new GridItemDecoration());
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}