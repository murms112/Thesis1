package com.example.thesis1;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FuelActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_ACCESS_FINE_LOCATION = 1;
    String username;
    String password;
    int score;
    int numTasks;
    private RecyclerView recyclerView;
    private FuelAdapter adapter;
    LocationManager locationManager;
    private AppSettings appSettings = AppSettings.getInstance("StartingCity", 52.344578, -24.409285, "StartingState");
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fuel);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //extract the intent data
        Intent intent = getIntent();
        username = (String) intent.getSerializableExtra("Logged in username");
        password = (String) intent.getSerializableExtra("Logged in password");
        score = (int) intent.getSerializableExtra("User score");
        //numTasks = (int) intent.getSerializableExtra("Number of logged tasks");

        recyclerView = findViewById(R.id.fuelRecyclerView);

        getLocationOnStart();

    }

    private void getLocationOnStart(){
        //location code from https://developer.android.com/guide/topics/location/strategies
        // Acquire a reference to the system Location Manager
        //locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener;

        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                makeUseOfNewLocation(location);
            }
            //these are empty but it needs them to have a new LocationListener
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        //permissions code based upon https://developer.android.com/training/permissions/requesting
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_ACCESS_FINE_LOCATION);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        Location myLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        //Location newLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        makeUseOfNewLocation(myLocation);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocationOnStart();
                }
                return;
            }
        }
    }

    private void makeUseOfNewLocation(Location location){
        if(location == null){
            Log.e("location error", "location error");
            return;
        }
        appSettings.setLatitude(location.getLatitude());
        appSettings.setLongitude(location.getLongitude());
        String coordinateString = String.valueOf(appSettings.getLatitude()) + ',' + String.valueOf(appSettings.getLongitude());

        //fetches the city name of users current location from MapQuest API
        GetCityFromCoordsService coordsService = MapQuestClientInstance.getRetrofitInstance().create(GetCityFromCoordsService.class);
        Call<MapQuestData> call = coordsService.getCityFromCoordinates("kxs6BATjKpjmIGN2QjYlWA0GDG1JykEZ", coordinateString, "json");
        call.enqueue(new Callback<MapQuestData>() {
            @Override
            public void onResponse(Call<MapQuestData> call, Response<MapQuestData> response) {
                //progressDialog.dismiss();
                MapQuestData data = response.body();
                appSettings.setCity(data.getResults().get(0).getLocations().get(0).getCityName());
                appSettings.setState(data.getResults().get(0).getLocations().get(0).getstateName());
                TextView fuelTxt = findViewById(R.id.fuelText);
                String location = appSettings.getCity() +", "+ appSettings.getState();
                fuelTxt.setText(location);
                getNRELData();
            }

            @Override
            public void onFailure(Call<MapQuestData> call, Throwable t) {
                t.printStackTrace();
                progressDialog.dismiss();
                Toast.makeText(FuelActivity.this, "Something went wrong with initial user location!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getNRELData(){
        GetNRELDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetNRELDataService.class);
        Call<NRELData> call = service.getTestDataBySearch("Zi1fjKapaju8ji3dpqjnPGOayUMTTyzcs9djNaDx", appSettings.getCity()+"+"+appSettings.getState(), "ELEC", 100);
        call.enqueue(new Callback<NRELData>() {
            @Override
            public void onResponse(Call<NRELData> call, Response<NRELData> response) {
                if(response.body().getFuel_stations() == null){
                    Toast.makeText(FuelActivity.this, "No fuel stations found at this location :( try again!", Toast.LENGTH_LONG).show();
                }
                else{
                    List<Fuel_stations> fuelStations = response.body().getFuel_stations();
                    buildFuelAdapter(fuelStations);
                }
            }

            @Override
            public void onFailure(Call<NRELData> call, Throwable t) {
                t.printStackTrace();
                //progressDialog.dismiss();
                Toast.makeText(FuelActivity.this, "Something went wrong with data!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void buildFuelAdapter(List<Fuel_stations> fuelStations){
        //these lines will eventually go into a method called after the data is received from the API
        adapter = new FuelAdapter(this, fuelStations, appSettings.getLatitude(), appSettings.getLongitude());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(FuelActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), AlternativesActivity.class);
        myIntent.putExtra("Logged in username", username);
        myIntent.putExtra("Logged in password", password);
        myIntent.putExtra("User score", score);
        //myIntent.putExtra("Number of logged tasks", numTasks);

        startActivity(myIntent);
        return true;
    }
}
