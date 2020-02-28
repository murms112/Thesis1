package com.example.thesis1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class LeaderboardMainAdapter extends RecyclerView.Adapter<LeaderboardMainAdapter.CustomViewHolder>{
    private ArrayList<User> dataList;
    private Context context;

    //researched custom adapter here https://medium.com/@prakash_pun/retrofit-a-simple-android-tutorial-48437e4e5a23
    //all click listener related code based upon https://stackoverflow.com/questions/24471109/recyclerview-onclick
    private static ClickListener clickListener;

    LeaderboardMainAdapter(Context context, ArrayList<User> dataList){
        this.context = context;
        this.dataList = dataList;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        final View mView;
        private TextView usernameTitle;
        private TextView scoreTitle;

        CustomViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            //sets the click listener to this instance of CustomAdapter which implements the OnClickListener
            itemView.setOnClickListener(this);

            usernameTitle = mView.findViewById(R.id.leaderboardUsername);
            scoreTitle = mView.findViewById(R.id.leaderboardScore);
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.leaderboard_card_view_friend:
                    // run the cardview on click listener
                    clickListener.onItemClick(getAdapterPosition(), v);
            }
        }
    }
    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.leaderboard_main_row, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder holder, int position) {
        holder.usernameTitle.setText(dataList.get(position).getUsername());
        String scoreTxt = "Score: " + dataList.get(position).getScore();
        holder.scoreTitle.setText(scoreTxt);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    void setOnItemClickListener(ClickListener clickListener) {
        LeaderboardMainAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }
}
