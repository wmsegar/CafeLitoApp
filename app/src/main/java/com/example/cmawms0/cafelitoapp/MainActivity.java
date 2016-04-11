package com.example.cmawms0.cafelitoapp;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends Activity implements GoogleLocationService.LocationCallback{

    public static final String TAG = MainActivity.class.getSimpleName();
    private GoogleLocationService mGoogleLocationService;
    private TextView txtLatLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        txtLatLong = (TextView) findViewById(R.id.txtLatLong);

        txtLatLong.setText(Double.toString(location.getLatitude()) + " " + Double.toString(location.getLongitude()));
    }
}
