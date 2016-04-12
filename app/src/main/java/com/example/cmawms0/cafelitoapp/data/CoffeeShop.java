package com.example.cmawms0.cafelitoapp.data;

import org.json.JSONObject;

/**
 * Created by Wayne.Segar on 4/12/2016.
 */
public class CoffeeShop implements JSONPopulator {
    private String name;
    private String openStreetMapId;

    public String getName(){
        return name;
    }

    public String getOpenStreetMapId(){
        return openStreetMapId;
    }

    @Override
    public void populate(JSONObject data) {
        name = data.optString("name");
        openStreetMapId = data.optString("openStreetMapId");
    }
}
