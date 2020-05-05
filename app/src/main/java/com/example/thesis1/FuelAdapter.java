package com.example.thesis1;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FuelAdapter extends RecyclerView.Adapter<FuelAdapter.CustomViewHolder> {

    private List<Fuel_stations> dataList;
    private Context context;
    private double lat;
    private double lon;

    //researched custom adapter here https://medium.com/@prakash_pun/retrofit-a-simple-android-tutorial-48437e4e5a23
    //all click listener related code based upon https://stackoverflow.com/questions/24471109/recyclerview-onclick
    private static FuelAdapter.ClickListener clickListener;

    FuelAdapter(Context context, List<Fuel_stations> dataList, double lat, double lon){
        this.context = context;
        this.dataList = dataList;
        this.lat = lat;
        this.lon = lon;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        final View mView;
        private TextView stationNameTitle;
        private TextView streetAddressTitle;
        private Button directionsBtn;

        CustomViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            //sets the click listener to this instance of CustomAdapter which implements the OnClickListener
            //itemView.setOnClickListener(this);

            stationNameTitle = mView.findViewById(R.id.stationName);
            streetAddressTitle = mView.findViewById(R.id.streetAddress);
            directionsBtn = mView.findViewById(R.id.directionsBtn);


        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.fuel_card_view_friend:
                    // run the cardview on click listener
                    clickListener.onItemClick(getAdapterPosition(), v);
            }
        }
    }

    @Override
    public FuelAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.fuel_row, parent, false);
        return new FuelAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final FuelAdapter.CustomViewHolder holder, int position) {
        String station = dataList.get(position).getStation_name();
        String street = dataList.get(position).getStreet_address();
        String intersection = dataList.get(position).getIntersection_directions();
        holder.stationNameTitle.setText(station);
        holder.streetAddressTitle.setText(street);

        holder.directionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //need to get the index of the task that has been clicked
                int index = holder.getAdapterPosition();
                //eventually fix this so it uses actual phone location
                String latitude = String.valueOf(lat);
                String longitude = String.valueOf(lon);
                String url = "http://maps.google.com/maps?daddr=" + dataList.get(index).getStreet_address() +"&saddr=" + latitude + "," + longitude;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                context.startActivity(intent);

            }
        });
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

    void setOnItemClickListener(FuelAdapter.ClickListener clickListener) {
        FuelAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }
}
