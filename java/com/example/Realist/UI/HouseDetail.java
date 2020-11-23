package com.example.dttapp.UI;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.dttapp.AppController;
import com.example.dttapp.Model.House;
import com.example.dttapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class HouseDetail extends AppCompatActivity implements OnMapReadyCallback  {
    private static final String TAG = "House Detail";
    private House house;
    private ImageView image, bed, bath, layer, location;
    private TextView price, numBed, numBath, size, distance, description;
    private GoogleMap map;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private int latitude, longitude;
    private ArrayList<LatLng> listStep;
    private PolylineOptions polyline;
    private Bundle mapViewBundle = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);
        house = (House)getIntent().getSerializableExtra("House");
        listStep = new ArrayList<LatLng>();
        polyline = new PolylineOptions();

        Toolbar toolbar = findViewById(R.id.detail_toolbar);
        //Change the icon of the up button
        toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        //add the return/up button and remove the title from actionbar
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowTitleEnabled(false);


        image = (ImageView) findViewById(R.id.imageView);
        bed = (ImageView) findViewById(R.id.bed);
        bath = (ImageView) findViewById(R.id.bath);
        layer = (ImageView) findViewById(R.id.layer);
        location = (ImageView) findViewById(R.id.location);
        image = (ImageView) findViewById(R.id.image);
        price = (TextView) findViewById(R.id.price);
        numBath = (TextView) findViewById(R.id.numBath);
        numBed = (TextView) findViewById(R.id.numBed);
        size = (TextView) findViewById(R.id.numLayer);
        distance = (TextView) findViewById(R.id.distance);
        description = (TextView) findViewById(R.id.description_text);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        setContent();
    }

    private void setContent() {
        Picasso.get()
                .load(house.getImage())
                .into(image);
        price.setText('$' + house.getPrice());
        numBed.setText(house.getNumBed());
        numBath.setText(house.getNumBath());
        size.setText(house.getSize());
        description.setText(house.getDescription());
        if(house.getDistance() != null) {
            distance.setText(house.getDistance());
        }
    }


    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
        this.latitude = Integer.parseInt(house.getLatitude());
        this.longitude = Integer.parseInt(house.getLongitude());

        //add marker at the coordinates of the house
        //marker 1: the position of the house
        map.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 6));
        //marker 2: the position of the user
        MarkerOptions myLoc = new MarkerOptions();
        myLoc.position(new LatLng(house.getMyLat(), house.getMyLong()));
        myLoc.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        myLoc.title("This is you");
        map.addMarker(myLoc).showInfoWindow();
//        CircleOptions addCircle = new CircleOptions().center(new LatLng(house.getMyLat(), house.getMyLong())).
//                radius(10).fillColor(Color.BLUE).strokeColor(Color.BLUE).strokeWidth(10);
//        map.addCircle(addCircle);

        //add a button to show navigation in Google maps
        Button fab = (Button) findViewById(R.id.button_nav);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(HouseDetail.this);
                builder.setMessage("Open Google Maps?")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                String latitude = String.valueOf(house.getLatitude());
                                String longitude = String.valueOf(house.getLongitude());
                                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
                                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                mapIntent.setPackage("com.google.android.apps.maps");

                                try{
                                    if (mapIntent.resolveActivity(HouseDetail.this.getPackageManager()) != null) {
                                        startActivity(mapIntent);
                                    }
                                }catch (NullPointerException e){
                                    Log.e(TAG, "onClick: NullPointerException: Couldn't open map." + e.getMessage() );
                                    Toast.makeText(HouseDetail.this, "Couldn't open map", Toast.LENGTH_SHORT).show();
                                }

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                dialog.cancel();
                            }
                        });
                final AlertDialog alert = builder.create();
                alert.show();
            }});
        makeURL(house.getLatitude(), house.getLongitude(), String.valueOf(house.getMyLat()), String.valueOf(house.getMyLong()));
    }
    public String makeURL (String sourcelat, String sourcelng, String destlat, String destlng ){
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(sourcelat);
        urlString.append(",");
        urlString.append(sourcelng);
        urlString.append("&destination=");// to
        urlString.append(destlat);
        urlString.append(",");
        urlString.append(destlng);
        urlString.append("&key="+"AIzaSyCU6Xc4mOB6Y36MgC5RVBzlYHn4d4jmONY");
        Log.d(TAG, urlString.toString());
        //request for data from Directions API
        request(urlString.toString());
        return urlString.toString();
    }

    public void request(String url) {

         JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response == null) {
                            Toast.makeText(AppController.getInstance(), "No route drawn", Toast.LENGTH_LONG).show();
                        } else {
                            JSONArray routeObj = null;
                            try {
                                Log.d(TAG, response.toString());
                                routeObj = response.getJSONArray("routes");
                                JSONObject routes = routeObj.getJSONObject(0);
                                JSONObject overviewPolylines = routes
                                        .getJSONObject("overview_polyline");
                                String points = overviewPolylines.getString("points");
                                decodePoly(points);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
            }
        });

        // Add the request to the RequestQueue.
        AppController.getInstance().getRequestQueue().add(request);

    }

    //The overview_polyline is encoded -> decode
    private ArrayList<LatLng> decodePoly(String encoded) {

        Log.i("Location", "String received: "+encoded);
        ArrayList<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),(((double) lng / 1E5)));
            poly.add(p);
        }

        for(int i=0;i<poly.size();i++){
            Log.i("Location", "Point sent: Latitude: "+poly.get(i).latitude+" Longitude: "+poly.get(i).longitude);
        }

        drawRoute(poly);
        return poly;
    }

    //add the polylines to create a route from user to the house
    public void drawRoute(ArrayList<LatLng> poly) {
//        Log.d(TAG, String.valueOf(poly.size()));
        polyline = new PolylineOptions();
        for(LatLng i: poly) {
            polyline = polyline.add(i);
        }
        polyline = polyline.color(Color.BLUE);
        polyline = polyline.width(5);
        Log.d(TAG, "add polyline");
        map.addPolyline(polyline);
    }

}


