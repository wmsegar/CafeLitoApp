package com.example.cmawms0.cafelitoapp;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cmawms0.cafelitoapp.data.CoffeeShop;
import com.example.cmawms0.cafelitoapp.service.CoffeeOrderService;
import com.example.cmawms0.cafelitoapp.service.CoffeeOrderServiceCallback;
import com.example.cmawms0.cafelitoapp.service.CoffeeShopService;
import com.example.cmawms0.cafelitoapp.service.CoffeeShopServiceCallback;


public class MainActivity extends AppCompatActivity implements GoogleLocationService.LocationCallback, CoffeeShopServiceCallback, CoffeeOrderServiceCallback{

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
    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;


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

        //Set ListView for Side Navigation
        mDrawerList = (ListView) findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();

        //Helper method to populate menu
        addDrawerItems();
        setupDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


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
            if (TextUtils.isEmpty(nameTextField.getText().toString())){
                Toast.makeText(this,"Please enter your name", Toast.LENGTH_LONG).show();
                return;
            }
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
        //This method links to the Crash Me button to simulate an app crash for testing
    }

    private void addDrawerItems(){
        //This method is used to add the items to the Navigation Menu
        String [] menuArray = {"Home", "Order", "Locations"};
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,menuArray);
        mDrawerList.setAdapter(mAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "It worked!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupDrawer(){
        //This method gets called in OnCreate to setup the navigation drawer to toggle
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_closed){
            /**Called when the drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView){
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Navigation!");
                invalidateOptionsMenu(); //create call to onPrepareOptionsMenu()
            }

            /**Called when the drawer has settled in a completely closed state. */
            public void onDrawerClosed (View view){
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); //creates call to onPrepareOptionsMenu

            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
