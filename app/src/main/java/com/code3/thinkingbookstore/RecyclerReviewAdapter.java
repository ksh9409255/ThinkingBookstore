package com.code3.thinkingbookstore;

import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class RecyclerReviewAdapter extends RecyclerView.Adapter<RecyclerReviewAdapter.ItemViewHolder> {
    public ArrayList<RecyclerReviewData> listData = new ArrayList<>();

    @NonNull
    @Override
    public RecyclerReviewAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_review_item, parent, false);
        return new RecyclerReviewAdapter.ItemViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull RecyclerReviewAdapter.ItemViewHolder holder, int position) {
        holder.onBind(listData.get(position));
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    void addItem(RecyclerReviewData RecyclerReviewData) {
        listData.add(RecyclerReviewData);
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView userName;
        private TextView date;
        private TextView content;

        ItemViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.text_review_user);
            date = itemView.findViewById(R.id.text_review_date);
            content = itemView.findViewById(R.id.text_review_content);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        void onBind(RecyclerReviewData listData) {
            userName.setText(listData.getUserName()+"님");
            date.setText(listData.getDate());
            content.setText(Html.fromHtml(listData.getcontent()));
        }
    }
}
