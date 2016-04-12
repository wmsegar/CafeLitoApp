package com.example.cmawms0.cafelitoapp;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.cmawms0.cafelitoapp.data.CoffeeShop;
import com.example.cmawms0.cafelitoapp.service.CoffeeShopService;
import com.example.cmawms0.cafelitoapp.service.CoffeeShopServiceCallback;

public class MainActivity extends Activity implements GoogleLocationService.LocationCallback, CoffeeShopServiceCallback{

    public static final String TAG = MainActivity.class.getSimpleName();
    private GoogleLocationService mGoogleLocationService;
    private TextView txtLatLong;
    private TextView txtNearestCoffeeshop;
    private CoffeeShopService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtLatLong = (TextView) findViewById(R.id.txtLatLong);
        txtNearestCoffeeshop = (TextView) findViewById(R.id.txtNearestCoffeeshop);
        mGoogleLocationService = new GoogleLocationService(this, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleLocationService.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGoogleLocationService.disconnect();
    }

    @Override
    public void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());
        txtLatLong.setText(Double.toString(location.getLatitude()) + " " + Double.toString(location.getLongitude()));
        service = new CoffeeShopService(this);
        service.findNearestCoffeeShop(Double.toString(location.getLatitude()), Double.toString(location.getLongitude()));

    }

    @Override
    public void serviceSuccess(CoffeeShop coffeeShop) {
        txtNearestCoffeeshop.setText(coffeeShop.getName());
    }

    @Override
    public void serviceFailure(Exception exception) {
        txtNearestCoffeeshop.setText("Couldn't get CoffeeShop");
    }
}
