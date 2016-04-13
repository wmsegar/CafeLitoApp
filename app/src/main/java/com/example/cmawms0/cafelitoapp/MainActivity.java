package com.example.cmawms0.cafelitoapp;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.cmawms0.cafelitoapp.data.CoffeeShop;
import com.example.cmawms0.cafelitoapp.service.CoffeeShopService;
import com.example.cmawms0.cafelitoapp.service.CoffeeShopServiceCallback;


public class MainActivity extends Activity implements GoogleLocationService.LocationCallback, CoffeeShopServiceCallback{

    public static final String TAG = MainActivity.class.getSimpleName();
    private GoogleLocationService mGoogleLocationService;
    private TextView txtLatLong;
    private TextView txtNearestCoffeeshop;
    private Spinner drinkSpinner;
    private Spinner sizeSpinner;
    private Button btnOrderCoffee;
    private TextView txtName;
    private EditText nameTextField;
    private CoffeeShopService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtLatLong = (TextView) findViewById(R.id.txtLatLong);
        txtNearestCoffeeshop = (TextView) findViewById(R.id.txtNearestCoffeeshop);
        txtName = (TextView) findViewById(R.id.txtName);
        nameTextField = (EditText) findViewById(R.id.nameTextField);

        //Bind Drink values to Spinner
        drinkSpinner = (Spinner) findViewById(R.id.drinkSpinner);
        ArrayAdapter<CharSequence> drinkAdapter = ArrayAdapter.createFromResource(this,
                R.array.drink_array, android.R.layout.simple_spinner_dropdown_item);
        drinkAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        drinkSpinner.setAdapter(drinkAdapter);

        //Bind Size values to Spinner
       sizeSpinner = (Spinner) findViewById(R.id.sizeSpinner);
        ArrayAdapter<CharSequence> sizeAdapter = ArrayAdapter.createFromResource(this,
               R.array.size_array, android.R.layout.simple_spinner_dropdown_item);
        sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sizeSpinner.setAdapter(sizeAdapter);

        //Create new instance of GoogleLocationService
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

        txtNearestCoffeeshop.setText("Your coffee will be ready for pick up at: " + coffeeShop.getName());
    }

    @Override
    public void serviceFailure(Exception exception) {
        txtNearestCoffeeshop.setText("Couldn't get CoffeeShop");
    }

    public void submitCoffee (View view){

    }
}
