package com.code3.thinkingbookstore;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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
        DataInit(view);
        return view;
    }
    private void DataInit(View view){
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerHomeAdapter();
        getData();
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }
    private void getData(){
        List<Integer> listContentImageView = Arrays.asList(
                R.drawable.sample_image,
                R.drawable.sample_image,
                R.drawable.sample_image,
                R.drawable.sample_image,
                R.drawable.sample_image,
                R.drawable.sample_image,
                R.drawable.sample_image);
        for (int i = 0; i < listContentImageView.size(); i++) {
            RecyclerHomeData data = new RecyclerHomeData();
            data.setImageView(listContentImageView.get(i));
            adapter.addItem(data);
        }
        adapter.notifyDataSetChanged();
    }
}