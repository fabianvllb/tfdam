package com.example.tfdam;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.FutureTarget;
import com.example.tfdam.model.Image;
import com.example.tfdam.model.Planet;
import com.example.tfdam.util.ImageListRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.example.tfdam.util.NetUtil;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ImageDownloaderThread implements Runnable {
    Context ctx;
    JSONArray response;
    //String apiUrl = "https://picsum.photos/v2/list?page=1&limit=";
    public ImageDownloaderThread(Context ctx, JSONArray response){
        this.ctx = ctx;
        this.response = response;
    }
    @Override
    public void run() {
        ((HomeActivity)ctx).runOnUiThread(() -> ((HomeActivity)ctx).prepareUIForDownload());

        Log.i("ImageDownloaderThread",response.toString());
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("dd/MM/yyyy hh:mm a");
        Gson gson = gsonBuilder.create();
        List<Image> list = Arrays.asList(gson.fromJson(response.toString(), Image[].class));
        Log.i("ImageDownloaderThread","DownloadUrl: " + list.get(0).getDownload_url());

        for(Image imgData : list) {
            FutureTarget<Bitmap> futureTarget =
                    Glide.with((HomeActivity)ctx)
                            .asBitmap()
                            .load(imgData.getDownload_url()) //img.getDownload_url()
                            .submit(800,800);
            try {
                imgData.setBitmap(futureTarget.get());
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            //Glide.with((HomeActivity)ctx).clear(futureTarget);
        }

        //String imageList = NetUtil.readHTTPGet(apiUrl+numberOfImages);

        //String url = apiUrl+numberOfImages;
        //RequestQueue volleyQueue = Volley.newRequestQueue((HomeActivity)ctx);
        /*ImageListRequest imageListRequest = new ImageListRequest(
                url,
                (Response.Listener<List<Image>>) response -> {
                    List<Image> list = response;
                    for(Image imgData : list){
                        FutureTarget<Bitmap> futureTarget =
                                Glide.with(ctx)
                                        .asBitmap()
                                        .load(imgData.getDownload_url()) //img.getDownload_url()
                                        .submit(800,800);
                        try {
                            imgData.setBitmap(futureTarget.get());
                        } catch (ExecutionException | InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        Glide.with(ctx).clear(futureTarget);
                    }
                },
                (Response.ErrorListener) error -> {
                    Toast.makeText(ctx, "Cannot fetch image", Toast.LENGTH_SHORT).show();
                    Log.e("ImageInfoActivity", "loadImage error: " + error.toString());
                });*/
        /*JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                (Response.Listener<JSONArray>) response -> {

                    //Toast.makeText(ctx, , Toast.LENGTH_SHORT).show();
                    //imageList = response.getString("download_url");
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    gsonBuilder.setDateFormat("dd/MM/yyyy hh:mm a");
                    Gson gson = gsonBuilder.create();
                    List<Image> list = Arrays.asList(gson.fromJson(response.toString(), Image[].class));

                    for(Image imgData : list){
                        FutureTarget<Bitmap> futureTarget = Glide.with(ctx)
                                .asBitmap()
                                .load(imgData.getDownload_url())
                                .submit(800,800);
                    }*/

                    /*for(Image imgData : list){

                        FutureTarget<Bitmap> futureTarget =
                                Glide.with(ctx)
                                        .asBitmap()
                                        .load(imgData.getDownload_url()) //img.getDownload_url()
                                        .submit(800,800);
                        try {
                            imgData.setBitmap(futureTarget.get());
                        } catch (ExecutionException | InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        Glide.with(ctx).clear(futureTarget);
                    }*/

                    /*for (Image img : list) {
                        Bitmap b = NetUtil.readImageHTTPGet(img.getDownload_url());
                        img.setBitmap(b);
                    }

                    ((Activity)ctx).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((HomeActivity)ctx).showDownloadResults(list);
                            ((HomeActivity)ctx).prepareUIAfterDownload();
                        }
                    });*/
                /*},
                (Response.ErrorListener) error -> {
                    Toast.makeText(ctx, "Cannot fetch image", Toast.LENGTH_SHORT).show();
                    Log.e("ImageInfoActivity", "loadImage error: " + error.toString());
                }
        );*/
        //volleyQueue.add(imageListRequest);
        /*Bitmap b = NetUtil.readImageHTTPGet(imgData.getDownload_url());
        imgData.setBitmap(b);*/

        ((HomeActivity)ctx).runOnUiThread(() -> {
            ((HomeActivity)ctx).showDownloadResults(list);
            ((HomeActivity)ctx).prepareUIAfterDownload();
        });
    }
}
