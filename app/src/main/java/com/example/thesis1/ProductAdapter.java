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

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.CustomViewHolder>{
    private ArrayList<Product> dataList;
    private Context context;

    //researched custom adapter here https://medium.com/@prakash_pun/retrofit-a-simple-android-tutorial-48437e4e5a23
    //all click listener related code based upon https://stackoverflow.com/questions/24471109/recyclerview-onclick
    private static ClickListener clickListener;

    ProductAdapter(Context context, ArrayList<Product> dataList){
        this.context = context;
        this.dataList = dataList;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        final View mView;
        private TextView productTitle;
        private Button goToLinkBtn;

        CustomViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            //sets the click listener to this instance of CustomAdapter which implements the OnClickListener
            itemView.setOnClickListener(this);

            productTitle = mView.findViewById(R.id.productName);
            goToLinkBtn = mView.findViewById(R.id.productSiteLink);
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.product_card_view_friend:
                    // run the cardview on click listener
                    clickListener.onItemClick(getAdapterPosition(), v);
            }
        }
    }
    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.product_row, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder holder, int position) {
        holder.productTitle.setText(dataList.get(position).getName());
        holder.goToLinkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //need to get the index of the product that has been clicked
                int index = holder.getAdapterPosition();
                String url = dataList.get(index).getLink();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    void setOnItemClickListener(ClickListener clickListener) {
        ProductAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }
}
