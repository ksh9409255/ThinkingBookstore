package com.code3.thinkingbookstore;

import android.os.Build;
import android.text.Html;
import android.util.Log;
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

public class RecyclerMypageAdapter extends RecyclerView.Adapter<RecyclerMypageAdapter.ItemViewHolder> {

    public ArrayList<RecyclerMypageData> listData = new ArrayList<>();

    @NonNull
    @Override
    public RecyclerMypageAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_mypage_onereview_item, parent, false);
        return new RecyclerMypageAdapter.ItemViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull RecyclerMypageAdapter.ItemViewHolder holder, int position) {
        holder.onBind(listData.get(position));
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    void addItem(RecyclerMypageData RecyclerMypageData) {
        listData.add(RecyclerMypageData);
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView contentImageView;
        private TextView date;
        private TextView review;

        ItemViewHolder(View itemView) {
            super(itemView);
            contentImageView = itemView.findViewById(R.id.content_book_onewreview);
            date = itemView.findViewById(R.id.text_date);
            review = itemView.findViewById(R.id.text_onereview);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        void onBind(RecyclerMypageData listData) {
            Glide.with(itemView).load(listData.getImageView()).into(contentImageView);
            review.setText(listData.getOneReview());
            date.setText(listData.getDate());
            String temp = listData.getOneReview();
            try {
                String piece0 = temp.substring(0, temp.indexOf("@"));
                String piece1 = temp.substring(temp.indexOf("@"), temp.indexOf("%")+1);
                String piece2 = temp.substring(temp.indexOf("%") + 1);

                temp =  piece0 + "<font color = '#6200EE'>" + piece1 + "</font>" + piece2;
            } catch(RuntimeException e) {
                Log.e("error", "runtime err");
            }
            review.setText(Html.fromHtml(temp));
            contentImageView.setClipToOutline(true);
        }
    }
}
