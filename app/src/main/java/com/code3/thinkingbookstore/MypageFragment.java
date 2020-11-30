package com.code3.thinkingbookstore;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;
import java.util.List;


public class MypageFragment extends Fragment {

    private RecyclerView recyclerViewOnereview;
    private RecyclerMypageAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private TextView userName;
    private TextView userEmail;
    private Button logOut;

    FirebaseUser user;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mypage, container, false);
        userName = (TextView)view.findViewById(R.id.text_username);
        userEmail = (TextView)view.findViewById(R.id.text_userid);
        logOut = (Button)view.findViewById(R.id.btn_logout);
        user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference(user.getDisplayName());
        logOut.setOnClickListener(l->{
            FirebaseAuth mAuth ;
            mAuth = FirebaseAuth.getInstance();
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getContext(), LoginActivity.class));
        });
        DataInit(view,databaseReference);
        userName.setText(user.getDisplayName()+"님 반갑습니다!");
        userEmail.setText(user.getEmail());
        return view;
    }
    private void DataInit(View view,DatabaseReference databaseReference){
        recyclerViewOnereview = (RecyclerView) view.findViewById(R.id.recyclerView_onereview_list);
        recyclerViewOnereview.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(recyclerViewOnereview.getContext(),LinearLayoutManager.HORIZONTAL,false);
        recyclerViewOnereview.setLayoutManager(layoutManager);
        adapter = new RecyclerMypageAdapter();
        getData(databaseReference);
        recyclerViewOnereview.setAdapter(adapter);
        recyclerViewOnereview.setItemAnimator(new DefaultItemAnimator());
    }

    private void getData(DatabaseReference databaseReference){
        adapter.listData.clear();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int cnt = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    RecyclerMypageData mypageData = new RecyclerMypageData(); // 반복문으로 데이터 List를 추출해냄
                    for(DataSnapshot ds : snapshot.getChildren()){
                        if(cnt==0){
                            mypageData.setImageView((String)ds.getValue());
                        }
                        else if(cnt==1){
                            mypageData.setBookIdx(Integer.parseInt(String.valueOf(ds.getValue())));
                        }
                        else if(cnt==2){
                            mypageData.setOneReview((String)ds.getValue());
                        }
                        cnt++;
                    }
                    cnt=0;
                    adapter.listData.add(mypageData); // 담은 데이터들을 배열리스트에 넣고 리사이클러뷰로 보낼 준비
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

