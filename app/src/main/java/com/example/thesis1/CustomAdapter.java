package com.example.thesis1;

import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder>{
    private ArrayList <SustainableTask> dataList;
    private Context context;
    String title;
    String score;

    //researched custom adapter here https://medium.com/@prakash_pun/retrofit-a-simple-android-tutorial-48437e4e5a23
    //all click listener code based upon https://stackoverflow.com/questions/24471109/recyclerview-onclick
    private static ClickListener clickListener;

    CustomAdapter(Context context, ArrayList<SustainableTask> dataList){
        this.context = context;
        this.dataList = dataList;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        final View mView;
        private TextView taskTitle;
        private TextView scoreTitle;
        private ImageView deleteBtn;

        CustomViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            //sets the click listener to this instance of CustomAdapter which implements the OnClickListener
            itemView.setOnClickListener(this);

            taskTitle = mView.findViewById(R.id.taskTitle);
            scoreTitle = mView.findViewById(R.id.taskScore);
            deleteBtn = mView.findViewById(R.id.deleteBtn);
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.card_view_friend:
                    // run the cardview on click listener
                    clickListener.onItemClick(getAdapterPosition(), v);
            }
        }
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.task_row, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder holder, final int position) {
        final HomeScreenActivity home = new HomeScreenActivity();
        holder.taskTitle.setText(dataList.get(position).getTitle());
        holder.scoreTitle.setText(String.valueOf(dataList.get(position).getScoreValue()));
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //need to get the index of the task that has been clicked
                int index = holder.getAdapterPosition();
                String clickedTaskIndex = String.valueOf(index);
                //method that gets the unique index of that task and removes it from firebase
                //researched calling activity method from adapter here: https://stackoverflow.com/questions/31059390/android-null-pointer-exception-when-calling-new-intent
                if(context instanceof HomeScreenActivity){
                    ((HomeScreenActivity)context).deleteTaskFromDatabase(index, dataList.get(position).getScoreValue());
                }


            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    void setOnItemClickListener(ClickListener clickListener) {
        CustomAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }
}
