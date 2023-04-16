package com.hs.todoapp.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.hs.todoapp.AddNewTask;
import com.hs.todoapp.MainActivity;
import com.hs.todoapp.Model.ToDoModel;
import com.hs.todoapp.R;
import com.hs.todoapp.Utils.DatabaseHandler;
import com.hs.todoapp.todoactivity;

import java.util.List;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder>{
    private List<ToDoModel> todoList;
    private todoactivity activity;
    private DatabaseHandler db;

    public ToDoAdapter(DatabaseHandler db,todoactivity activity){
        this.db=db;
        this.activity = activity;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_layout,parent,false);
        return new ViewHolder(itemView);
    }

    public void onBindViewHolder(ViewHolder holder,int position){
        db.openDatabase();
        ToDoModel item = todoList.get(position);
        holder.task.setText(item.getTask());
        holder.task.setChecked(toBoolean(item.getStatus()));    //Accepts bool
        holder.iv.setImageBitmap(getImage(item.getTask_img())); // byte[] to imageview
        holder.task.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    db.updateStatus(item.getId(),1);
                }else{
                    db.updateStatus(item.getId(),0);
                }
            }
        });
    }
    private  boolean toBoolean(int n){
        return n!=0;
        // we convert n to boolean here
    }

    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
        // byte[] to bitmap
    }

    public void setTasks(List<ToDoModel> todoList){
        this.todoList=todoList;
        notifyDataSetChanged();
    }



    public int getItemCount(){
        return todoList.size();
    }

    public void deleteItem(int position){
        ToDoModel item = todoList.get(position);
        db.deleteTask(item.getId());
        todoList.remove(position);
        notifyItemRemoved(position);
    }


    public void editItem(int position){
        ToDoModel item = todoList.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt("id",item.getId());
        bundle.putString("task",item.getTask());
        bundle.putByteArray("task_image",item.getTask_img());
        AddNewTask fragment = new AddNewTask();
        fragment.setArguments(bundle);
        fragment.show(activity.getSupportFragmentManager(),AddNewTask.TAG);

    }

    public Context getContext() {
        return activity;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        CheckBox task;
        ImageView iv;

        ViewHolder(View view){
            super(view);
            task = view.findViewById(R.id.todoCheckBox);
            iv= view.findViewById(R.id.todoImageView);
        }
    }
}
