package com.example.tfdam.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.example.tfdam.model.Image;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class ImageListRequest extends JsonRequest<List<Image>> {
    private final Gson gson = new Gson();
    private final TypeToken<List<Image>> typeToken = new TypeToken<List<Image>>() {
    };
    private final Response.Listener<List<Image>> listener;

    /**
     * Make a GET request and return a parsed list of Image objects from the response.
     *
     * @param url URL of the request to make
     * @param listener Listener for handling successful responses
     * @param errorListener Listener for handling error responses
     */
    public ImageListRequest(String url, Response.Listener<List<Image>> listener, Response.ErrorListener errorListener) {
        super(String.valueOf(Method.GET), url, null, errorListener);
        this.listener = listener;
    }

    @Override
    protected void deliverResponse(List<Image> response) {
        listener.onResponse(response);
    }

    @Override
    protected Response<List<Image>> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(
                    response.data,
                    HttpHeaderParser.parseCharset(response.headers));

            // Deserialize the JSON array into a List<Image>
            List<Image> imageList = gson.fromJson(json, typeToken.getType());

            // Set the Bitmaps for each Image in the list
            /*for (Image image : imageList) {
                byte[] imageData = response.data; // You may need to adjust this based on your API response structure
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                image.setBitmap(bitmap);
            }*/

            return Response.success(
                    imageList,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException | JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }
}