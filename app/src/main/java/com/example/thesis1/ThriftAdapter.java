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

import java.util.List;

public class ThriftAdapter extends RecyclerView.Adapter<ThriftAdapter.CustomViewHolder> {

    private List<Businesses> dataList;
    private Context context;
    private double lat;
    private double lon;

    //researched custom adapter here https://medium.com/@prakash_pun/retrofit-a-simple-android-tutorial-48437e4e5a23
    //all click listener related code based upon https://stackoverflow.com/questions/24471109/recyclerview-onclick
    private static ThriftAdapter.ClickListener clickListener;

    ThriftAdapter(Context context, List<Businesses> dataList, double lat, double lon){
        this.context = context;
        this.dataList = dataList;
        this.lat = lat;
        this.lon = lon;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        final View mView;
        private TextView thriftNameTitle;
        private TextView streetAddressTitle;
        private Button directionsBtn;

        CustomViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            //sets the click listener to this instance of CustomAdapter which implements the OnClickListener
            //itemView.setOnClickListener(this);

            thriftNameTitle = mView.findViewById(R.id.thriftShopName);
            streetAddressTitle = mView.findViewById(R.id.thriftStreetAddress);
            directionsBtn = mView.findViewById(R.id.thriftDirectionsBtn);


        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.thrift_card_view_friend:
                    // run the cardview on click listener
                    clickListener.onItemClick(getAdapterPosition(), v);
            }
        }
    }

    @Override
    public ThriftAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.thrift_row, parent, false);
        return new ThriftAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ThriftAdapter.CustomViewHolder holder, int position) {
        String thriftShop = dataList.get(position).getName();
        String street = dataList.get(position).getLocation().getAddress1();
        holder.thriftNameTitle.setText(thriftShop);
        holder.streetAddressTitle.setText(street);

        holder.directionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //need to get the index of the task that has been clicked
                int index = holder.getAdapterPosition();
                //eventually fix this so it uses actual phone location
                String latitude = String.valueOf(lat);
                String longitude = String.valueOf(lon);
                String url = "http://maps.google.com/maps?daddr=" + dataList.get(index).getLocation().getAddress1() +"&saddr=" + latitude + "," + longitude;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                context.startActivity(intent);

            }
        });
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

    void setOnItemClickListener(ThriftAdapter.ClickListener clickListener) {
        ThriftAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }
}
