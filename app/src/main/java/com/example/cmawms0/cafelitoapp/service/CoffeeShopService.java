package com.example.cmawms0.cafelitoapp.service;
import android.location.Location;
import android.os.AsyncTask;

import com.example.cmawms0.cafelitoapp.data.CoffeeShop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Wayne.Segar on 4/12/2016.
 */
public class CoffeeShopService {
    private CoffeeShopServiceCallback callback;
    private String latitude;
    private String longitude;
    private String name;
    private Exception error;

    public CoffeeShopService(CoffeeShopServiceCallback callback){
        this.callback = callback;
    }

    public String getName(){
        return name;
    }

    public void findNearestCoffeeShop(String lat, String lon){
        this.latitude = lat;
        this.longitude = lon;

            new AsyncTask<String, Void, String>(){

                @Override
                protected String doInBackground(String... params) {

                    String endpoint = "http://64.15.191.169/service/coffeeshop/nearest/" + latitude + "/" + longitude;

                    try{
                        URL url = new URL(endpoint);

                        URLConnection connection = url.openConnection();
                        InputStream inputStream = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                        StringBuilder result = new StringBuilder();
                        String line;
                        while((line = reader.readLine()) != null){
                            result.append(line);
                        }

                        return result.toString();

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(String s) {
                   if (s == null && error != null){
                        callback.serviceFailure(error);
                       return;
                   }

                    try {
                        JSONObject data = new JSONObject(s);

                        CoffeeShop coffeeShop = new CoffeeShop();

                        coffeeShop.populate(data);

                        callback.serviceSuccess(coffeeShop);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        callback.serviceFailure(e);
                    }
                }
            }.execute(latitude, longitude);
    }
}
