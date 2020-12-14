package com.code3.thinkingbookstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ReviewActivity extends AppCompatActivity {
    private RecyclerView recyclerViewReview;
    private RecyclerReviewAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private TextView textReviewDetail;
    private TextView userNameView;
    private EditText reviewEdit;
    private Button btnAdapt;
    private Button btnBack;
    private Button btnGoodSent;

    private String bookIdx;
    private String bookCover;
    private String review;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        textReviewDetail = (TextView)findViewById(R.id.text_review_detail);
        userNameView = (TextView)findViewById(R.id.text_review_user);
        reviewEdit = (EditText)findViewById(R.id.editText);
        btnAdapt = (Button)findViewById(R.id.btn_review_reg);
        btnBack = (Button)findViewById(R.id.btn_review_back);
        btnGoodSent = (Button)findViewById(R.id.btn_good_sent);

        bookIdx = getIntent().getStringExtra("bookIdx");
        bookCover = getIntent().getStringExtra("bookCover");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("review_list").child(String.valueOf(bookIdx));
        user = FirebaseAuth.getInstance().getCurrentUser();
        DataInit(this,databaseReference);
        userNameView.setText(user.getDisplayName()+" 님!");
        btnBack.setOnClickListener(l->{
            finish();
        });
        btnAdapt.setOnClickListener(l->{
            SimpleDateFormat format1 = new SimpleDateFormat ( "yyyy-MM-dd");
            Date time = new Date();
            String time1 = format1.format(time);
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference rootRef = firebaseDatabase.getReference();
            user = FirebaseAuth.getInstance().getCurrentUser();
            review = reviewEdit.getText().toString().trim();
            RecyclerReviewData reviewData = new RecyclerReviewData(user.getDisplayName(), time1, review);
            RecyclerMypageData mypageData = new RecyclerMypageData(bookCover,Integer.parseInt(String.valueOf(bookIdx)),review,time1);
            rootRef.child("review_list").child(bookIdx).push().setValue(reviewData);
            rootRef.child(user.getDisplayName()).push().setValue(mypageData);
            Toast.makeText(this,"리뷰 등록이 완료되었습니다!",Toast.LENGTH_SHORT).show();
            reviewEdit.getText().clear();
        });
        btnGoodSent.setOnClickListener(l->{
            Intent intent = new Intent(this,LikedSentenceActivity.class);
            intent.putExtra("bookIdx",bookIdx);
            startActivity(intent);
        });
    }
    private void DataInit(ReviewActivity view,DatabaseReference databaseReference){
        recyclerViewReview = (RecyclerView) view.findViewById(R.id.recyclerView_review_list);
        recyclerViewReview.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(recyclerViewReview.getContext());
        recyclerViewReview.setLayoutManager(layoutManager);
        adapter = new RecyclerReviewAdapter();
        getData(databaseReference);
        recyclerViewReview.setAdapter(adapter);
        recyclerViewReview.setItemAnimator(new DefaultItemAnimator());
    }
    private void getData(DatabaseReference databaseReference){
        adapter.listData.clear();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int cnt = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    RecyclerReviewData recyclerReviewData = new RecyclerReviewData();
                    for(DataSnapshot ds : snapshot.getChildren()){
                        if(cnt==0){
                            recyclerReviewData.setContent((String)ds.getValue());
                        }
                        else if(cnt==1){
                            recyclerReviewData.setDate((String)ds.getValue());
                        }
                        else if(cnt==2){
                            recyclerReviewData.setUserName((String)ds.getValue());
                        }
                        cnt++;
                    }
                    cnt=0;
                    adapter.listData.add(recyclerReviewData);
                }
                adapter.notifyDataSetChanged(); // 리스트 저장 및 새로고침해야 반영이 됨
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 디비를 가져오던중 에러 발생 시
                Log.e("Fraglike", String.valueOf(databaseError.toException())); // 에러문 출력
            }
        });
    }
}