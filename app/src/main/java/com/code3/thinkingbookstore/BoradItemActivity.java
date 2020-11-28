package com.code3.thinkingbookstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BoradItemActivity extends AppCompatActivity {
    Button btnDown;
    Button btnBack;
    ImageView contentBorad;
    TextView userName;
    TextView writerName;
    TextView bookName;
    TextView bookIntro;

    private int postIdx;
    RecyclerHomeData homeData;
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
        postIdx = getIntent().getIntExtra("postIdx",-1);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("post_list").child(String.valueOf(postIdx));
        homeData=new RecyclerHomeData();
        getData(this,databaseReference);
        btnBack.setOnClickListener(l -> {
            finish();
        });

        btnDown.setOnClickListener(l -> {
            finish();
        });

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
                    else if(cnt==2){
                        homeData.setImageView((String)snapshot.getValue());
                        Glide.with(view).load(homeData.getImageView()).into(contentBorad);
                    }
                    else if(cnt==4){
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