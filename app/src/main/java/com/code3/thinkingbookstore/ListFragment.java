package com.code3.thinkingbookstore;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

public class ListFragment extends Fragment {
    private RecyclerView recyclerViewBook;
    private RecyclerListAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        DataInit(view);
        return view;
    }
    private void DataInit(View view){
        recyclerViewBook = (RecyclerView) view.findViewById(R.id.recyclerView_book_list);

        recyclerViewBook.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(recyclerViewBook.getContext(),2);
        recyclerViewBook.setLayoutManager(layoutManager);
        adapter = new RecyclerListAdapter();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("book_list");
        getData(databaseReference);
        recyclerViewBook.setAdapter(adapter);
        recyclerViewBook.setItemAnimator(new DefaultItemAnimator());
    }
    private void getData(DatabaseReference databaseReference){
        adapter.listData.clear();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int cnt = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    RecyclerListData listData = new RecyclerListData(); // 반복문으로 데이터 List를 추출해냄
                    listData.setBookIdx(Integer.parseInt((String)snapshot.getKey()));
                    for(DataSnapshot ds : snapshot.getChildren()){
                        if(cnt==0){
                            listData.setImageView((String)ds.getValue());
                        }
                        cnt++;
                    }
                    cnt=0;
                    adapter.listData.add(listData); // 담은 데이터들을 배열리스트에 넣고 리사이클러뷰로 보낼 준비
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