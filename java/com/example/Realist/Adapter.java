package com.example.dttapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dttapp.Model.House;
import com.squareup.picasso.Picasso;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;



public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>  {
    private ArrayList<House> houses;
    private Activity context;
    private Adapter.OnItemListener onItemListener;
    private static final String TAG = "Adapter";


    //VH is use to display the items
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView image, bed, bath, layer, location;
        private TextView price, address, numBed, numBath, numLayer, distance;
        LinearLayout parentLayout;
        OnItemListener onItemListener;
        public ViewHolder(View v, OnItemListener onItemListener) {
            super(v);
            parentLayout = v.findViewById(R.id.parentLayout);
            image = (ImageView) v.findViewById(R.id.imageView);
            bed = (ImageView) v.findViewById(R.id.bed);
            bath = (ImageView) v.findViewById(R.id.bath);
            layer = (ImageView) v.findViewById(R.id.layer);
            location = (ImageView) v.findViewById(R.id.location);

            price = (TextView) v.findViewById(R.id.price);
            address = (TextView) v.findViewById(R.id.address);
            numBed = (TextView) v.findViewById(R.id.numBed);
            numBath = (TextView) v.findViewById(R.id.numBath);
            numLayer = (TextView) v.findViewById(R.id.numLayer);
            distance = (TextView) v.findViewById(R.id.distance);

            this.onItemListener = onItemListener;
            itemView.setOnClickListener(this);
        }
        //each viewHolder is an item -> onClick = click on 1 item
        @Override
        public void onClick(View view) {
            onItemListener.onItemClick(houses.get(getAdapterPosition())); //get the position of the house clicked
        }
    }
    //to listen to click on item and open the detail activity
    public interface OnItemListener {
        void onItemClick(House house);
    }
    //constructor for the adapter
    public Adapter(OnItemListener onItemListener, Activity context, ArrayList<House> houses) {
        this.onItemListener = onItemListener;
        this.context = context;
        this.houses = houses;
    }

    @NonNull
    @Override
    //create new view (instantiated by the layout manager)
    public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        //create new view
        View houseView = inflater.inflate(R.layout.recycler_items, parent, false);
        return new ViewHolder(houseView, onItemListener);
    }

    @Override
    //pass data to ViewHolder
    //replace the views' content
    public void onBindViewHolder(@NonNull Adapter.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

            final House house = houses.get(position);

            //create the address string
            String address = house.getZip() + " " + house.getCity();
            Picasso.get()
                    .load(house.getImage())
                    .into(holder.image);

            holder.price.setText('$' + house.getPrice());
            holder.address.setText(address);
            holder.numBed.setText(house.getNumBed());
            holder.numBath.setText(house.getNumBath());
            holder.numLayer.setText(house.getSize());
            holder.distance.setText(house.getDistance());

    }

    @Override
    //count the no. of items to be displayed
    public int getItemCount() {
        return houses.size();
    }
}
