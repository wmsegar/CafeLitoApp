package com.example.cmawms0.cafelitoapp.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Wayne.Segar on 4/12/2016.
 */
public class CoffeeShop implements JSONPopulator {
    private String name;
    private String openStreetMapId;
    private String latitude;
    private String longitude;

    public String getName(){
        return name;
    }

    public String getOpenStreetMapId(){
        return openStreetMapId;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    @Override
    public void populate(JSONObject data) {
        name = data.optString("name");
        openStreetMapId = data.optString("openStreetMapId");
        try {
            longitude = data.getJSONObject("location").getJSONArray("coordinates").get(0).toString();
            latitude = data.getJSONObject("location").getJSONArray("coordinates").get(1).toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
