package com.code3.thinkingbookstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

public class BoradItemActivity extends AppCompatActivity {
    Button btnDown;
    Button btnBack;
    ImageView contentBorad;
    TextView userName;
    TextView writerName;
    TextView bookName;
    TextView bookIntro;

    FirebaseStorage storage;
    private String postIdx;
    private String imagePath;
    RecyclerHomeData homeData;

    boolean fileReadPermission;
    boolean fileWritePermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borad_item);
        btnBack = (Button) findViewById(R.id.btn_back_borad);
        btnDown = (Button) findViewById(R.id.btn_down_borad);
        contentBorad = (ImageView)findViewById(R.id.content_borad);
        userName = (TextView)findViewById(R.id.text_borad_user);
        writerName = (TextView)findViewById(R.id.text_borad_bookwriter);
        bookName = (TextView)findViewById(R.id.text_borad_bookname);
        bookIntro = (TextView)findViewById(R.id.text_borad_source);
        postIdx = getIntent().getStringExtra("postIdx");
        imagePath = getIntent().getStringExtra("imagePath");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("post_list").child(String.valueOf(postIdx));
        storage = FirebaseStorage.getInstance();
        homeData=new RecyclerHomeData();
        getData(this,databaseReference);
        btnBack.setOnClickListener(l -> {
            finish();
        });
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
            fileReadPermission=true;
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
            fileWritePermission=true;
        }
        if(!fileReadPermission||!fileWritePermission){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},200);
        }
        btnDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                StorageReference pathReference = storage.getReference().child("post/uploads/" + imagePath);
                File localFile = null;
                localFile = new File("/storage/self/primary/Pictures/" + imagePath);
                pathReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Log.e("다운!","다운!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==200&&grantResults.length>0){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                fileReadPermission=true;
            }
            if(grantResults[1]==PackageManager.PERMISSION_GRANTED){
                fileReadPermission=true;
            }
        }
    }
    private void getData(BoradItemActivity view,DatabaseReference databaseReference){
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int cnt=0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(cnt==0){
                        homeData.setBookName((String)snapshot.getValue());
                        bookName.setText(homeData.getBookName());
                    }
                    else if(cnt==1){
                        homeData.setBookSource((String)snapshot.getValue());
                        bookIntro.setText(homeData.getBookSource());
                    }
                    else if(cnt==3){
                        homeData.setImageView((String)snapshot.getValue());
                        Glide.with(view).load(homeData.getImageView()).into(contentBorad);
                    }
                    else if(cnt==4){
                        homeData.setUserName((String)snapshot.getValue());
                        userName.setText(homeData.getUserName());
                    }
                    else if(cnt==5){
                        homeData.setWriter((String)snapshot.getValue());
                        writerName.setText(homeData.getWriter());
                    }
                    cnt++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 디비를 가져오던중 에러 발생 시
                Log.e("Fraglike", String.valueOf(databaseError.toException())); // 에러문 출력
            }
        });
    }
}