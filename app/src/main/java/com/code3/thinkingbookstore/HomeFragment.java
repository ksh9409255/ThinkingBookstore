package com.code3.thinkingbookstore;

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
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;


public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerHomeAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("post_list");
        DataInit(view,databaseReference);
        return view;
    }
    private void DataInit(View view,DatabaseReference databaseReference){
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerHomeAdapter();
        getData(databaseReference);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }
    private void getData(DatabaseReference databaseReference){
        adapter.listData.clear();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int cnt = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    RecyclerHomeData homeData = new RecyclerHomeData(); // 반복문으로 데이터 List를 추출해냄
                    homeData.setPostIdx(Integer.parseInt((String)snapshot.getKey()));
                    for(DataSnapshot ds : snapshot.getChildren()){
                        if(cnt==0){
                            homeData.setBookName((String)ds.getValue());
                        }
                        else if(cnt==1){
                            homeData.setBookSource((String)ds.getValue());
                        }
                        else if(cnt==2){
                            homeData.setImageView((String)ds.getValue());
                        }
                        else if(cnt==4){
                            homeData.setWriter((String)ds.getValue());
                        }
                        cnt++;
                    }
                    cnt=0;
                    adapter.listData.add(homeData); // 담은 데이터들을 배열리스트에 넣고 리사이클러뷰로 보낼 준비
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