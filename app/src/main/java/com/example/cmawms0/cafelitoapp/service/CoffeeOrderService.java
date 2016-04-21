package com.example.cmawms0.cafelitoapp.service;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Wayne.Segar on 4/21/2016.
 */
public class CoffeeOrderService {
    private CoffeeOrderServiceCallback callback;
    private Exception error;
    private String drink;
    private String size;
    private String userName;
    private String openStreetMapId;

    public CoffeeOrderService(CoffeeOrderServiceCallback callback){
        this.callback = callback;
    }

    public void submitNewOrder(final String drink, final String size, final String name, final String openStreetMapId){
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
                    coffeeType.put("name", drink);
                    order.put("type", coffeeType);
                    order.put("sizes", size);
                    order.put("drinker", name);
                    order.put("coffeeShopId", openStreetMapId);

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
                    connection.setRequestProperty("Content-Type", "application/json");

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
                    callback.orderFailure(error);
                    return;
                }

            }
        }.execute();
    }

}
