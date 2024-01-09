package com.example.tfdam;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.tfdam.model.Image;
import com.example.tfdam.util.NetUtil;
import com.example.tfdam.util.NotificationHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ImageInfoActivity extends AppCompatActivity {
    public static final String TAG = "ImageInfoActivity";
    private ProgressBar progressBar;
    private ImageView ivImg;
    Image imageData;
    TextView tvAuthor, tvResolution;
    Button btnDownload, btnSendPng, btnShareUrl;
    String apiUrl = "https://picsum.photos/id/";
    RequestQueue volleyQueue;
    NotificationHandler notificationHandler;
    private final int HIGH_PRIORITY = 2;
    private final int LOW_PRIORITY = 1;
    int notificationCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_info);

        notificationHandler = new NotificationHandler(this);

        progressBar = findViewById(R.id.imageInfo_pb_imgProgress);
        ivImg = findViewById(R.id.imgInfo_iv_image);
        tvAuthor = findViewById(R.id.imageInfo_tv_author);
        tvResolution = findViewById(R.id.imageInfo_tv_resolution);
        btnDownload = findViewById(R.id.imgInfo_btn_download);
        btnSendPng = findViewById(R.id.imgInfo_btn_send_png);
        btnShareUrl = findViewById(R.id.imgInfo_btn_share_url);

        String imgId = getIntent().getStringExtra("imgId");
        String url = apiUrl+imgId+"/info";

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("dd/MM/yyyy hh:mm a");
        Gson gson = gsonBuilder.create();

        volleyQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    String imageUrl;
                    imageData = gson.fromJson(response.toString(), Image.class);
                    try {
                        //imageUrl = response.getString("download_url");
                        //Glide.with(this).load(imageUrl).skipMemoryCache(true).into(ivImg);
                        new Thread(new DownloadImage(this, imageData.getDownload_url())).start();
                        tvAuthor.setText(response.getString("author"));
                        tvResolution.setText(String.format("%s x %s", response.getString("height"), response.getString("width")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(this, "Cannot fetch dog image", Toast.LENGTH_LONG).show();
                    Log.e("ImageInfoActivity", "loadImage error: ${error.localizedMessage}");
                }
        );
        volleyQueue.add(jsonObjectRequest);
        btnDownload.setOnClickListener(view -> {
            Uri imageUri = downloadImage(imageData.getBitmap());
            makeNotification(HIGH_PRIORITY, imageUri);
        });
        btnSendPng.setOnClickListener(view -> {
            shareImage(imageData.getBitmap());
        });
        btnShareUrl.setOnClickListener(view -> {
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Check this image at " + imageData.getUrl());
            sendIntent.setType("text/plain");
            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
        });
    }

    private void shareImage(Bitmap imageBitmap) {
        /*// Save the image to a temporary file
        File imagePath = new File(getCacheDir(), "images");
        imagePath.mkdirs();
        File imageFile = new File(imagePath, "shared_image.png");

        try (FileOutputStream out = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        // Create an Intent with ACTION_SEND
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/jpeg");
        // Set the image URI
        //Uri uriToImage = Uri.fromFile(imageFile);
        Uri uriToImage = insertImageToMediaStore(imageBitmap, "Shared Image", "Check out this image!");

        shareIntent.putExtra(Intent.EXTRA_STREAM, uriToImage);

        // Optionally, add a subject and text
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Shared Image");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Check this image out!");

        // Start the chooser activity
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    private Uri insertImageToMediaStore(Bitmap bitmap, String title, String description) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, title);
        values.put(MediaStore.Images.Media.DESCRIPTION, description);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        Uri imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        try (OutputStream out = getContentResolver().openOutputStream(imageUri)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return imageUri;
    }
    public Uri downloadImage(Bitmap bitmap){
        ContentValues values = new ContentValues();
        //values.put(MediaStore.Images.Media.TITLE, title);
        //values.put(MediaStore.Images.Media.DESCRIPTION, description);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        Uri imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        try (OutputStream out = getContentResolver().openOutputStream(imageUri)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imageUri;
    }
    public void makeNotification(int notificationPriority, Uri imageUri){
        Intent openImageIntent = new Intent(Intent.ACTION_VIEW);
        openImageIntent.setDataAndType(imageUri, "image/jpeg");
        openImageIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);

        PendingIntent contentIntent = PendingIntent.getActivity(
                this,
                0,
                openImageIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        String titleString = "Download completed";
        String msgString = "Tap to open your image";
        boolean isPriority = notificationPriority == HIGH_PRIORITY;
        Notification.Builder nBuilder =  notificationHandler.createNotification(titleString, msgString, isPriority, imageData.getBitmap(), contentIntent);
        notificationHandler.getManager().notify(notificationCount++, nBuilder.build());
        notificationHandler.publishGroup(isPriority);
    }

    public void loadBitmapToImageview(Bitmap bitmap) {
        imageData.setBitmap(bitmap);
        ivImg.setImageBitmap(imageData.getBitmap());
    }
    public void prepareUIForDownload() {
        progressBar.setVisibility(View.VISIBLE);
    }
    public void prepareUIAfterDownload() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    class DownloadImage implements Runnable {
        Context ctx;
        String imageUrl;
        DownloadImage(Context ctx, String imageUrl) {
            this.ctx = ctx;
            this.imageUrl = imageUrl;
        }

        public void run() {
            ((ImageInfoActivity)ctx).runOnUiThread(() -> ((ImageInfoActivity)ctx).prepareUIForDownload());
            Bitmap imageBitmap = NetUtil.readImageHTTPGet(imageUrl);
            ((ImageInfoActivity)ctx).runOnUiThread(() -> {
                ((ImageInfoActivity)ctx).loadBitmapToImageview(imageBitmap);
                ((ImageInfoActivity)ctx).prepareUIAfterDownload();
            });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (volleyQueue != null) {
            volleyQueue.cancelAll(TAG);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}