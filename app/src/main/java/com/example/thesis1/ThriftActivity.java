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
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ThriftActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_ACCESS_FINE_LOCATION = 1;
    String username;
    String password;
    int score;
    int numTasks;
    LocationManager locationManager;
    private AppSettings appSettings = AppSettings.getInstance("StartingCity", 52.344578, -24.409285, "StartingState");
    ProgressDialog progressDialog;
    private ThriftAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thrift);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //extract the intent data
        Intent intent = getIntent();
        username = (String) intent.getSerializableExtra("Logged in username");
        password = (String) intent.getSerializableExtra("Logged in password");
        score = (int) intent.getSerializableExtra("User score");

        recyclerView = findViewById(R.id.thriftRecyclerView);

        getLocationOnStart();

    }

    private void getLocationOnStart(){
        //location code from https://developer.android.com/guide/topics/location/strategies
        // Acquire a reference to the system Location Manager
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
        makeUseOfNewLocation(myLocation);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), AlternativesActivity.class);
        myIntent.putExtra("Logged in username", username);
        myIntent.putExtra("Logged in password", password);
        myIntent.putExtra("User score", score);

        startActivity(myIntent);
        return true;
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
                MapQuestData data = response.body();
                appSettings.setCity(data.getResults().get(0).getLocations().get(0).getCityName());
                appSettings.setState(data.getResults().get(0).getLocations().get(0).getstateName());
                TextView thriftTxt = findViewById(R.id.thriftText);
                String location = appSettings.getCity() +", "+ appSettings.getState();
                thriftTxt.setText(location);
                getThriftData();
            }

            @Override
            public void onFailure(Call<MapQuestData> call, Throwable t) {
                t.printStackTrace();
                progressDialog.dismiss();
                Toast.makeText(ThriftActivity.this, "Something went wrong with initial user location!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getThriftData(){
        GetThriftService service = YelpClientInstance.getRetrofitInstance().create(GetThriftService.class);
        Call<YelpData> call = service.getTestDataBySearch("Bearer eRUvr3DT0Kr90LidFGi7xH1MyxflWXkIc4ffVbr5fzrzM-Aqhw5N47qY2nGgDwawoqc5MVJNdiVf0PGHMpWJaCg9Tk6YgD5xsXka67eqvAqYJ6oPq86ztGoicVx_XnYx",
                "thrift", String.valueOf(appSettings.getLatitude()), String.valueOf(appSettings.getLongitude()));
        call.enqueue(new Callback<YelpData>() {
            @Override
            public void onResponse(Call<YelpData> call, Response<YelpData> response) {
                if(response.body().getBusinesses() == null){
                    Toast.makeText(ThriftActivity.this, "No thrift stores found at this location :( try again!", Toast.LENGTH_LONG).show();
                }
                else{
                    List<Businesses> businesses = response.body().getBusinesses();
                    buildThriftAdapter(businesses);
                }
            }

            @Override
            public void onFailure(Call<YelpData> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(ThriftActivity.this, "Something went wrong with data!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void buildThriftAdapter(List<Businesses> businesses){
        adapter = new ThriftAdapter(this, businesses, appSettings.getLatitude(), appSettings.getLongitude());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ThriftActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
}


