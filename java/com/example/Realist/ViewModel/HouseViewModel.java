package com.example.dttapp.ViewModel;

import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.widget.Filter;
import android.widget.Filterable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.dttapp.AppController;
import com.example.dttapp.Model.House;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class HouseViewModel extends AndroidViewModel implements Filterable {
    private static final String TAG = "ViewModel";
    private String url = "https://intern.docker-dev.d-tt.nl/api/house";
    private MutableLiveData<ArrayList<House>> houseLiveData;
    private ArrayList<House> houses;
    private ArrayList<House> housesSorted;
    private ArrayList<House> housesFiltered;
    private Context context = AppController.getInstance();

    public HouseViewModel(Application application) {
        super(application);
        houseLiveData = new MutableLiveData<>();
        //create the first list
        populateList();
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                //if the query is empty -> show the original recycler view
                if (charString.isEmpty()) {
                    //if there is no search -> show the whole list
                    housesFiltered = houses;
                } else { //if user searches -> create a temporary arrayList -> compare to the address of each house -> filter the matching houses
                    ArrayList<House> tempHousesFiltered= new ArrayList<>();
                    for (House h : houses) {
                        //match the query with the address
                        if (h.getAddress().toLowerCase().contains(charString.toLowerCase())) {
                            tempHousesFiltered.add(h);
                        }
                    }
                    housesFiltered = tempHousesFiltered;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = housesFiltered;
                return filterResults;
            }
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                houseLiveData.postValue((ArrayList<House>) filterResults.values);
            }
        };
    }

    public MutableLiveData<ArrayList<House>> getUserMutableLiveData() {
        return houseLiveData;
    }

    //update the user's location as permission is allowed
    public void updateDistance(Location myLocation) {
        for(House house: houses) {
            house.setDistance(myLocation);
        }
    }

    //sort house by price from low to high
    public void sortHouses() {
        housesSorted = houses;
        Collections.sort(housesSorted, new Comparator<House>() {
            @Override
            public int compare(House lhs, House rhs) {
                //low to high
                return Integer.parseInt(lhs.getPrice()) - Integer.parseInt(rhs.getPrice());
            }
        });
        houseLiveData.setValue(housesSorted);
    }

    public void populateList(){

        houses = new ArrayList<>();
        JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        //System.out.println(jsonObject.toString());
                        House house = new House();
                        //Parse thru the Json and update each house's info
                        house.setImage(jsonObject.getString("image"));
                        //System.out.println(jsonObject.getString("image"));
                        house.setPrice(jsonObject.getString("price"));
                        //System.out.println(jsonObject.getString("price"));
                        house.setZip(jsonObject.getString("zip"));
                        house.setCity(jsonObject.getString("city"));
                        house.setNumBed(jsonObject.getString("bedrooms"));
                        // System.out.println(jsonObject.getString("bedrooms"));
                        house.setNumBath(jsonObject.getString("bathrooms"));
                        house.setSize(jsonObject.getString("size"));
                        //System.out.println(house.getSize());
                        house.setDescription(jsonObject.getString("description"));
                        house.setLatitude(jsonObject.getString("latitude"));
                        house.setLongitude(jsonObject.getString("longitude"));
                        house.setCreatedDate(jsonObject.getString("createdDate"));
                        //house.setDistance(myLocation);
                        //Add the house in the ArrayList
                        houses.add(house);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                //set the data in Live Data
                houseLiveData.setValue(houses);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("Volley", error.toString());

            }
        }) {    // add Access Key
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Access-Key", "98bww4ezuzfePCYFxJEWyszbUXc7dxRx");

                return params;
            }
        };
        //Add to request queue
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonRequest);
    }
}
