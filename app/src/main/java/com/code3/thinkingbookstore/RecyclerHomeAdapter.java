package com.code3.thinkingbookstore;

import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

public class RecyclerHomeAdapter extends RecyclerView.Adapter<RecyclerHomeAdapter.ItemViewHolder>{

    public ArrayList<RecyclerHomeData> listData = new ArrayList<>();
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_home_item, parent, false);
        return new ItemViewHolder(view);
    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull RecyclerHomeAdapter.ItemViewHolder holder, int position) {
        holder.onBind(listData.get(position));
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    void addItem(RecyclerHomeData recyclerHomeData) {
        listData.add(recyclerHomeData);
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView contentImageView;

        ItemViewHolder(View itemView) {
            super(itemView);

            contentImageView = itemView.findViewById(R.id.content_image_view);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        void onBind(RecyclerHomeData listData) {
            Glide.with(itemView).load(listData.getImageView()).into(contentImageView);
            contentImageView.setClipToOutline(true);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(itemView.getContext(), BoradItemActivity.class);
                    intent.putExtra("postIdx", listData.getPostIdx());
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }
}
