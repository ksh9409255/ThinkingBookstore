package com.code3.thinkingbookstore;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;
import java.util.List;

public class ListFragment extends Fragment {
    private RecyclerView recyclerViewBook;
    private RecyclerView recyclerViewDown;
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
        recyclerViewDown = (RecyclerView) view.findViewById(R.id.recyclerView_book_down);
        recyclerViewDown.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(recyclerViewDown.getContext(),LinearLayoutManager.HORIZONTAL,false);
        recyclerViewDown.setLayoutManager(layoutManager);
        adapter = new RecyclerListAdapter();
        getData();
        recyclerViewDown.setAdapter(adapter);
        recyclerViewDown.setItemAnimator(new DefaultItemAnimator());

        recyclerViewBook.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(recyclerViewBook.getContext(),LinearLayoutManager.HORIZONTAL,false);
        recyclerViewBook.setLayoutManager(layoutManager);
        adapter = new RecyclerListAdapter();
        getData();
        recyclerViewBook.setAdapter(adapter);
        recyclerViewBook.setItemAnimator(new DefaultItemAnimator());
    }
    private void getData(){
        adapter.listData.clear();
        List<Integer> listContentImageView = Arrays.asList(
                R.drawable.sample_book,
                R.drawable.sample_book,
                R.drawable.sample_book,
                R.drawable.sample_book,
                R.drawable.sample_book,
                R.drawable.sample_book,
                R.drawable.sample_book);
        for (int i = 0; i < listContentImageView.size(); i++) {
            RecyclerListData data = new RecyclerListData();
            data.setImageView(listContentImageView.get(i));
            adapter.addItem(data);
        }
        adapter.notifyDataSetChanged();
    }
}