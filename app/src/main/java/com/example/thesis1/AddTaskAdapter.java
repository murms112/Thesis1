package com.example.thesis1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class AddTaskAdapter extends RecyclerView.Adapter<AddTaskAdapter.CustomViewHolder>{
    private ArrayList<SustainableTask> dataList;
    private Context context;

    //researched custom adapter here https://medium.com/@prakash_pun/retrofit-a-simple-android-tutorial-48437e4e5a23
    //all click listener related code based upon https://stackoverflow.com/questions/24471109/recyclerview-onclick
    private static ClickListener clickListener;

    AddTaskAdapter(Context context, ArrayList<SustainableTask> dataList){
        this.context = context;
        this.dataList = dataList;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        final View mView;
        private TextView taskTitle;
        private Button addTaskBtn;

        CustomViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            //sets the click listener to this instance of CustomAdapter which implements the OnClickListener
            itemView.setOnClickListener(this);

            taskTitle = mView.findViewById(R.id.allTaskTitle);
            addTaskBtn = mView.findViewById(R.id.addTaskBtn);
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.allTask_card_view_friend:
                    // run the cardview on click listener
                    clickListener.onItemClick(getAdapterPosition(), v);
            }
        }
    }
    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.all_tasks_task_row, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder holder, int position) {
        holder.taskTitle.setText(dataList.get(position).getTitle());
        holder.addTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //need to get the index of the task that has been clicked
                int index = holder.getAdapterPosition();
                String clickedTaskIndex = String.valueOf(index);
                LogTaskActivity.addTaskToDatabase(index, clickedTaskIndex);

            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    void setOnItemClickListener(ClickListener clickListener) {
        AddTaskAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }
}
