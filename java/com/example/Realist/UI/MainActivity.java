package com.example.dttapp.UI;import androidx.annotation.NonNull;import androidx.appcompat.app.AppCompatActivity;import androidx.core.app.ActivityCompat;import androidx.lifecycle.Observer;import androidx.lifecycle.ViewModelProvider;import androidx.recyclerview.widget.LinearLayoutManager;import androidx.recyclerview.widget.RecyclerView;import android.Manifest;import android.annotation.TargetApi;import android.app.AlertDialog;import android.app.Dialog;import android.app.SearchManager;import android.content.Context;import android.content.DialogInterface;import android.content.Intent;import android.content.pm.PackageManager;import android.graphics.Color;import android.location.Location;import android.location.LocationListener;import android.location.LocationManager;import android.os.Build;import android.os.Bundle;import android.util.Log;import android.view.MenuItem;import android.view.View;import android.view.inputmethod.EditorInfo;import android.widget.Button;import android.widget.FrameLayout;import android.widget.SearchView;import android.widget.Toast;import com.example.dttapp.Adapter;import com.example.dttapp.Model.House;import com.example.dttapp.ViewModel.HouseViewModel;import com.example.dttapp.R;import com.google.android.gms.common.ConnectionResult;import com.google.android.gms.common.GoogleApiAvailability;import com.google.android.gms.common.api.GoogleApiClient;import com.google.android.gms.location.FusedLocationProviderClient;import com.google.android.gms.location.LocationRequest;import com.google.android.material.bottomnavigation.BottomNavigationView;import java.util.ArrayList;import java.util.List;public class MainActivity extends AppCompatActivity implements Adapter.OnItemListener {    private static final String TAG = "MainActivity";    private static final int ERROR_DIALOG_REQUEST = 000;    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;    private Boolean mLocationPermissionsGranted = false;    /**     * Request code for location permission request.     * @see #onRequestPermissionsResult(int, String[], int[])     */    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 0;    /**     * Flag indicating whether a requested permission has been denied after returning in     * {@link #onRequestPermissionsResult(int, String[], int[])}.     */    private Location myLocation;    private LocationRequest mLocationRequest;    private LocationManager locationManager;    private RecyclerView recyclerView;    private Adapter adapter;    private HouseViewModel viewModel;    private MainActivity context;    private Adapter.OnItemListener listener;    private Button location, sort;    private BottomNavigationView btmNav;    private SearchView searchView;    private GoogleApiClient mGoogleApiClient;    private double[] myCoordinates = new double[2];    private FusedLocationProviderClient fusedLocationClient;    private LocationListener locationListenerGPS;    private FrameLayout emptyState;    @Override    protected void onCreate(Bundle savedInstanceState) {        setTheme(R.style.AppTheme);        super.onCreate(savedInstanceState);        setContentView(R.layout.activity_main);        context = this;        listener = this;        //set up recycler view        recyclerView = findViewById(R.id.recyclerView);        viewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(HouseViewModel.class);        viewModel.getUserMutableLiveData().observe(context, houseListUpdateObserver);        //pop up rationale for location permission        askPermission();        //set up location permission        setUpLocationPermission();        //set up the sort button        setUpSort();        //set up bottom navigation        setUpBottomNav();        //set up search bar        setUpSearch();    }    Observer<ArrayList<House>> houseListUpdateObserver = new Observer<ArrayList<House>>() {        @Override        public void onChanged(ArrayList<House> houses) {            adapter = new Adapter(listener, context, houses);            recyclerView.setLayoutManager(new LinearLayoutManager(context));            recyclerView.setAdapter(adapter);            //if the search does not have a match -> set the empty state image to be visible            if(adapter.getItemCount() == 0) {                emptyState.setVisibility(View.VISIBLE);            } else {                emptyState.setVisibility(View.INVISIBLE);            }        }    };    public void setUpLocationPermission() {        location = (Button) findViewById(R.id.button_location);        location.setOnClickListener(new View.OnClickListener() {            @Override            public void onClick(View view) {                if (isServicesOK()) {                    ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,                            Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);                }}});    }    public void setUpSort() {        sort = (Button) findViewById(R.id.button_sort);        sort.setOnClickListener(new View.OnClickListener() {            @Override            public void onClick(View view) {                //sort the data                viewModel.sortHouses();                adapter.notifyDataSetChanged();            }        });    }    public void setUpSearch() {        emptyState = (FrameLayout) findViewById(R.id.search_empty);        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);        searchView = (SearchView) findViewById(R.id.searchView);        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);        searchView.setQueryHint("Search for a home");        searchView.setSearchableInfo(searchManager                .getSearchableInfo(getComponentName()));        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {            @Override            public boolean onQueryTextSubmit(String query) {                return false;            }            @Override            public boolean onQueryTextChange(String newText) {                Log.d(TAG, "Search View ");                Log.d(TAG, newText);                viewModel.getFilter().filter(newText);                adapter.notifyDataSetChanged();                return false;            }        });    }    public void setUpBottomNav() {        btmNav = (BottomNavigationView) findViewById(R.id.bottom_nav);        btmNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {            @Override            public boolean onNavigationItemSelected(@NonNull MenuItem item) {                Intent intent = null;                switch (item.getItemId()) {                    case R.id.home:                        // Respond to navigation item 1 click                        intent = new Intent(MainActivity.this, MainActivity.class);                        MainActivity.this.startActivity(intent);                        break;                    case R.id.info:                        // Respond to navigation item 2 click                        intent = new Intent(MainActivity.this, Info.class);                        MainActivity.this.startActivity(intent);                        break;                }                return MainActivity.super.onOptionsItemSelected(item);            }        });    }    //open detail activity onClick    @Override    public void onItemClick(House house) {        Intent intent = new Intent(this, HouseDetail.class);        intent.putExtra("House", house);        startActivity(intent);    }    public boolean isServicesOK() {        Log.d(TAG, "isServicesOK: checking google services version");        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);        if (available == ConnectionResult.SUCCESS) {            //everything is fine and the user can make map requests            Log.d(TAG, "isServicesOK: Google Play Services is working");            return true;        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {            //an error occured but we can resolve it            Log.d(TAG, "isServicesOK: an error occured but we can fix it");            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);            dialog.show();        } else {            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();        }        return false;    }    //pop up to show permission rationale and  ask for permission ActivityCompat.requestPermissions    public void askPermission() {            Log.d(TAG, "getLocationPermission: getting location permissions");            final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);            alertBuilder.setCancelable(true);            alertBuilder.setTitle("Permission suggested");            alertBuilder.setMessage("This app requires user's location to show the distance and navigation from user's device to the houses. ");            alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)                public void onClick(DialogInterface dialog, int which) {                }});            final AlertDialog alert = alertBuilder.create();            //change the color of the "ok" option            alert.setOnShowListener( new DialogInterface.OnShowListener() {                @Override                public void onShow(DialogInterface arg0) {                    alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);                }            });            alert.show();    }    //invoked after user interacts with the request permission dialog    @Override    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {        Log.d(TAG, "onRequestPermissionsResult: called.");        mLocationPermissionsGranted = false;        switch (requestCode) {            case LOCATION_PERMISSION_REQUEST_CODE: {                if (grantResults.length > 0) {                    for (int i = 0; i < grantResults.length; i++) {                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {                            mLocationPermissionsGranted = true;                            //after the location is granted, get rid of the location button                            //location.setVisibility(GONE);                            //get the user's location updates                            locationListenerGPS = new LocationListener() {                                @Override                                public void onLocationChanged(@NonNull Location location) {                                    double latitude = location.getLatitude();                                    double longitude = location.getLongitude();//                                    String msg = " Latitude: " + latitude + " Longitude: " + longitude;//                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();                                }};                            getDeviceLocation();                            Log.d(TAG, "onRequestPermissionsResult: permission granted");                        } else {                            //when permission DENIED -> show a rationale                            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);                            alertBuilder.setCancelable(true);                            alertBuilder.setTitle("Permission suggested");                            alertBuilder.setMessage("This app requires user's location to show the " +                                    "distance and navigation from user's device to the houses." +                                    "Allow permission in Settings.");                            //if the user agrees to provide location -> redirect to settings                            alertBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {                                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)                                public void onClick(DialogInterface dialog, int which) {                                    startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);                                }});                            //if the user doesn't want to provide location -> move on                            alertBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {                                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)                                public void onClick(DialogInterface dialog, int which) {                                    mLocationPermissionsGranted = false;                                }});                            final AlertDialog alert;                            alert = alertBuilder.create();                            //change the color of the options                            alert.setOnShowListener(new DialogInterface.OnShowListener() {                                @Override                                public void onShow(DialogInterface arg0) {                                    //if the user allow location -> redirect to settings                                    alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);                                    alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GRAY);                                }});                            alert.show();                            Log.d(TAG, "onRequestPermissionsResult: permission failed");                        }                    }                }            }        }    }    private void getDeviceLocation(){        /*         * Get the best and most recent location of the device, which may be null in rare         * cases when a location is not available.         */        Log.d(TAG, "get device location: ");        if (mLocationPermissionsGranted) {            String location_context = context.LOCATION_SERVICE;            locationManager = (LocationManager) context.getSystemService(location_context);            List<String> providers = locationManager.getProviders(true);            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {                for (String provider : providers) {                    try {                        Location location = locationManager.getLastKnownLocation(provider);                        Log.d(TAG, "task successful, getting coordinates");                        if (location != null) {                            viewModel.updateDistance(location);                            adapter.notifyDataSetChanged();                        }                         locationManager.requestLocationUpdates(provider, 1000, 0, locationListenerGPS);                    } catch (SecurityException e) {                        e.printStackTrace();                    }                }            }        }    }}