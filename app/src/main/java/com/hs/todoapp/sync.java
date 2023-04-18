package com.hs.todoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hs.todoapp.Model.ToDoModel;
import com.hs.todoapp.Model.UploadModel;
import com.hs.todoapp.Utils.DatabaseHandler;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import Util.UserApi;

public class sync extends AppCompatActivity {

    private GoogleSignInClient gsc;
    private GoogleSignInOptions gso;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = firestore.collection("user");

    private DatabaseHandler db;
    private SQLiteDatabase sqLiteDatabase;
    private List<ToDoModel> toUpSyntasklist;
    private List<ToDoModel> toDnSyntasklist;

    private List<UploadModel> toUpdatetasklist;

    private Button signOut;
    private ImageView iv;
    private TextView tv;
    private Button up1;
    private Button download;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync);

        signOut = findViewById(R.id.signout);
        up1 = findViewById(R.id.up);
        tv = findViewById(R.id.tv);
        download = findViewById(R.id.down);
        //tv.setText(UserApi.getInstance().getUserEmail());

        gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        gsc= GoogleSignIn.getClient(this,gso);

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });

        up1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upload();
            }
        });

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadandupdatedb();
            }
        });
    }

    private void signOut(){
        gsc.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent i = new Intent(sync.this,MainActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    private void upload(){
        /*
        collectionReference.whereEqualTo("useremail",UserApi.getInstance().getUserEmail())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // all field where same email
                        if(!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                                Log.d("namiii", queryDocumentSnapshot.getId());
                                collectionReference.document().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        // Deleted task
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // error
                                    }
                                });
                            }
                        }
                        else{
                            Toast.makeText(sync.this,"no data with email",Toast.LENGTH_LONG).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(sync.this,"Error downloading list",Toast.LENGTH_LONG).show();
                    }
                });
        */


        db = new DatabaseHandler(this);
        db.openDatabase();
        toUpSyntasklist = new ArrayList<>();
        toUpSyntasklist = db.getAllTasks();
        Collections.reverse(toUpSyntasklist);
        for(ToDoModel toDoModel :toUpSyntasklist){
            UploadModel uploadModel = new UploadModel();
            uploadModel.setUseremail(UserApi.getInstance().getUserEmail());
            uploadModel.setId(toDoModel.getId());
            uploadModel.setStatus(toDoModel.getStatus());
            uploadModel.setTask(toDoModel.getTask());
            uploadModel.setStrimg(uploadModel.byteToString(toDoModel.getTask_img()));
            collectionReference.add(uploadModel);
        }
        Intent i = new Intent(sync.this,todoactivity.class);
        startActivity(i);
        finish();
    }

    private void downloadandupdatedb(){
        db = new DatabaseHandler(this);
        db.openDatabase();
        // Downloading
        collectionReference.whereEqualTo("useremail",UserApi.getInstance().getUserEmail())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(!queryDocumentSnapshots.isEmpty()) {

                            // we got our list<uploadmode>

                            toUpdatetasklist = new ArrayList<>();
                            for(QueryDocumentSnapshot q : queryDocumentSnapshots){
                                UploadModel task= q.toObject(UploadModel.class);
                                toUpdatetasklist.add(task);
                            }

                            // we have all tasks downloaded in toUpdatetasklist
                            // we copy data from toUpdatetasklist to db by using todomodel object
                            // we delete the existing table and convert string to byte[] and insert

                            db.deleteAll();

                            for(UploadModel uploadModel : toUpdatetasklist){
                                ToDoModel toDoModel = new ToDoModel();
                                toDoModel.setTask(uploadModel.getTask());
                                String str=uploadModel.getStrimg();
                                toDoModel.setTask_img(Stringtobyte(uploadModel.getStrimg()));
                                toDoModel.setStatus(uploadModel.getStatus());
                                toDoModel.setId(uploadModel.getId());
                                db.insertTask(toDoModel);
                            }
                            db.close();
                            Intent i = new Intent(sync.this,todoactivity.class);
                            startActivity(i);
                            finish();
                        }
                        else{
                            // the querylist is empty
                            db.close();
                            Toast.makeText(sync.this,"Nothing to download",Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // some error
                        db.close();
                        Toast.makeText(sync.this,"something went wrong",Toast.LENGTH_LONG).show();
                    }
                });
    }
    public  byte[] Stringtobyte(String str){
        byte [] encodeByte= new byte[0];
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            encodeByte = Base64.getDecoder().decode(str);
        }
        return encodeByte;
    }

}