package com.hs.todoapp.Model;

public class ToDoModel {
    private int id,status;
    private String task;
    private byte[] task_img;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public byte[] getTask_img() {
        return task_img;
    }

    public void setTask_img(byte[] task_img) {
        this.task_img = task_img;
    }
}
