package com.example.cmawms0.cafelitoapp;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.Toast;

import com.example.cmawms0.cafelitoapp.data.CoffeeShop;
import com.example.cmawms0.cafelitoapp.service.CoffeeOrderService;
import com.example.cmawms0.cafelitoapp.service.CoffeeOrderServiceCallback;
import com.example.cmawms0.cafelitoapp.service.CoffeeShopService;
import com.example.cmawms0.cafelitoapp.service.CoffeeShopServiceCallback;


public class MainActivity extends Activity implements GoogleLocationService.LocationCallback, CoffeeShopServiceCallback, CoffeeOrderServiceCallback{

    public static final String TAG = MainActivity.class.getSimpleName();
    private GoogleLocationService mGoogleLocationService;
    private TextView txtLatLong;
    private TextView txtNearestCoffeeshop;
    private Spinner drinkSpinner;
    private Spinner sizeSpinner;
    private EditText nameTextField;
    private CoffeeShopService service;
    private CoffeeOrderService orderService;
    private String openStreetMapId;
    private String coffeeShopLat;
    private String coffeeShopLong;
    private String coffeeShopName;
    Double currentLat;
    Double currentLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtLatLong = (TextView) findViewById(R.id.txtLatLong);
        txtNearestCoffeeshop = (TextView) findViewById(R.id.txtNearestCoffeeshop);
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
        //service.findNearestCoffeeShop("37.422006", "-122.084095");

        currentLat = location.getLatitude();
        currentLong = location.getLongitude();

    }

    @Override
    public void serviceSuccess(CoffeeShop coffeeShop) {

        txtNearestCoffeeshop.setText("Your coffee will be ready for pick up at: " + coffeeShop.getName());
        openStreetMapId = coffeeShop.getOpenStreetMapId();
        coffeeShopLat = coffeeShop.getLatitude();
        coffeeShopLong = coffeeShop.getLongitude();
        coffeeShopName = coffeeShop.getName();
    }

    @Override
    public void serviceFailure(Exception exception) {
        txtNearestCoffeeshop.setText("Couldn't get CoffeeShop");
    }

    public void submitCoffee (View view){
        //Toast.makeText(this, openStreetMapId, Toast.LENGTH_LONG).show();

        orderService = new CoffeeOrderService(this);
        if(openStreetMapId != null){
            orderService.submitNewOrder(drinkSpinner.getSelectedItem().toString(),sizeSpinner.getSelectedItem().toString(),nameTextField.getText().toString(),openStreetMapId);
        } else{
            Toast.makeText(this,"There are no coffee shops near you", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void orderSuccess() {
        Toast.makeText(this, "Order has been sent", Toast.LENGTH_LONG).show();
    }

    @Override
    public void orderFailure(Exception exception) {

    }

    public void showCoffeeShop(View view){
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("coffeeShopLat", coffeeShopLat);
        intent.putExtra("coffeeShopLong", coffeeShopLong);
        intent.putExtra("coffeeShopName", coffeeShopName);
        intent.putExtra("currentLat", currentLat);
        intent.putExtra("currentLong", currentLong);

        startActivity(intent);
    }

    public void crashMe(){

    }
}
