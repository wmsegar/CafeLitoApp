package com.example.cmawms0.cafelitoapp.service;
import android.location.Location;
import android.os.AsyncTask;

import com.example.cmawms0.cafelitoapp.data.CoffeeShop;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
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
    private String drink;
    private String size;
    private String userName;
    private String openStreetMapId;

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

    public void submitNewOrder(String drink, final String size, final String name, final String openStreetMapId){
        this.drink = drink;
        this.size = size;
        this.userName = name;
        this.openStreetMapId = openStreetMapId;
        final JSONObject order = new JSONObject();
        final JSONObject coffeeType = new JSONObject();

        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {

                String endpoint = "http://64.15.191.169/service/coffeeshop/order";
                String json = "";
                //Build JSON Object
                try {
                    coffeeType.put("name", name);
                    order.put("sizes", size);
                    order.put("drinker", name);
                    order.put("coffeeShopId", openStreetMapId);
                    order.put("type", coffeeType);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Convert JSON object to String
                json = order.toString();
                String response = "";
                try {
                    URL url = new URL(endpoint);
                    HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoInput(true);
                    connection.setDoOutput(true);

                    OutputStream outputStream = connection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
                    writer.write(json);

                    writer.flush();
                    writer.close();
                    outputStream.close();
                    int responseCode = connection.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK){
                        String line;
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        while ((line = bufferedReader.readLine()) != null){
                            response+= line;
                        }
                    }else {
                        response = connection.getResponseMessage();
                    }

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

            }
        };
    }
}
