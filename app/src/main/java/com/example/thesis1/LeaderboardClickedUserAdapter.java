package com.example.thesis1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class LeaderboardClickedUserAdapter extends RecyclerView.Adapter<LeaderboardClickedUserAdapter.CustomViewHolder> {
    private ArrayList<SustainableTask> dataList;
    private Context context;

    //researched custom adapter here https://medium.com/@prakash_pun/retrofit-a-simple-android-tutorial-48437e4e5a23
    //all click listener related code based upon https://stackoverflow.com/questions/24471109/recyclerview-onclick
    private static LeaderboardClickedUserAdapter.ClickListener clickListener;

    LeaderboardClickedUserAdapter(Context context, ArrayList<SustainableTask> dataList){
        this.context = context;
        this.dataList = dataList;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        final View mView;
        private TextView taskTitle;
        private TextView scoreTitle;

        CustomViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            //sets the click listener to this instance of CustomAdapter which implements the OnClickListener
            itemView.setOnClickListener(this);

            taskTitle = mView.findViewById(R.id.clickedUserTaskName);
            scoreTitle = mView.findViewById(R.id.clickedUserTaskScore);
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.leaderboard_clicked_user_card_view_friend:
                    // run the cardview on click listener
                    clickListener.onItemClick(getAdapterPosition(), v);
            }
        }
    }

    @Override
    public LeaderboardClickedUserAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.leaderboard_clicked_user_row, parent, false);
        return new LeaderboardClickedUserAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final LeaderboardClickedUserAdapter.CustomViewHolder holder, int position) {
        holder.taskTitle.setText(dataList.get(position).getTitle());
        String scoreTxt;
        scoreTxt = String.valueOf(dataList.get(position).getScoreValue());
        holder.scoreTitle.setText(scoreTxt);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    void setOnItemClickListener(LeaderboardClickedUserAdapter.ClickListener clickListener) {
        LeaderboardClickedUserAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }
}
