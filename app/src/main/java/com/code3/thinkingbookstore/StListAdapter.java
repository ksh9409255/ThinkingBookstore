package com.code3.thinkingbookstore;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class StListAdapter extends RecyclerView.Adapter<StListAdapter.CustomViewHolder> {
    ArrayList<LikedSentenceActivity.Sentence> sentence;

    public interface OnItemClickListener {
        void onItemClick(View v, int pos);
    }
    private StListAdapter.OnItemClickListener mListener = null;
    public void setOnItemClickListener(StListAdapter.OnItemClickListener listener) {
        this.mListener = listener;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView likeduser;
        protected TextView likedst;

        public CustomViewHolder(View view) {
            super(view);
            this.likeduser = (TextView)view.findViewById(R.id.liked_user);
            this.likedst = (TextView)view.findViewById(R.id.liked_sentence);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    Log.e("pos", pos+"");
                    if(pos!= RecyclerView.NO_POSITION && mListener != null) {
                        mListener.onItemClick(v, pos);
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sentence_listitem, parent, false);

        StListAdapter.CustomViewHolder viewHolder = new StListAdapter.CustomViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        Log.e("sentence", sentence.get(position).getSt());
        holder.likedst.setText(sentence.get(position).getSt());
        holder.likeduser.setText(sentence.get(position).getUser());
    }

    @Override
    public int getItemCount() {
        return (null != sentence ? sentence.size() : 0);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public StListAdapter(ArrayList<LikedSentenceActivity.Sentence> data) {
        this.sentence = data;
    }
}
