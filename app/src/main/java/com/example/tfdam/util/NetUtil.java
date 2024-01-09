package com.example.tfdam.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;

public class NetUtil {
    public static String readHTTPGet(String url){
        String responseS = "";
        try{
            URL website = new URL(url);
            URLConnection connection = website.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuilder response = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null)
                response.append(inputLine);

            in.close();

            responseS = response.toString();
        }catch (Exception e){
            Log.e("NetUtil", Objects.requireNonNull(e.getMessage()));
        }
        return responseS;
    }

    public static Bitmap readImageHTTPGet(String url){
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(new URL(url).openConnection().getInputStream());
        } catch (Exception e) {
            Log.e("NetworkUtil", e.toString());
        }
        return bitmap;
    }
}
