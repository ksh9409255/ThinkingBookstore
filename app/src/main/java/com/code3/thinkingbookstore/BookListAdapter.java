package com.code3.thinkingbookstore;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class BookListAdapter extends RecyclerView.Adapter<BookListAdapter.CustomViewHolder> {
    ArrayList<GetSentence1Activity.Books> books;

    public interface OnItemClickListener {
        void onItemClick(View v, int pos);
    }
    private OnItemClickListener mListener = null;
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ImageView bookcover;
        protected TextView name;

        public CustomViewHolder(View view) {
            super(view);
            this.bookcover = (ImageView)view.findViewById(R.id.bookcover_list1);
            this.name = (TextView)view.findViewById(R.id.bookname_list1);

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

    public BookListAdapter(ArrayList<GetSentence1Activity.Books> data) {
        this.books = data;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_listitem, parent, false);

        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        StorageReference storageRef;
        storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(books.get(position).getBookcover());
        storageRef.getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                holder.bookcover.setImageBitmap(bitmap);
            }
        });

        holder.name.setText(books.get(position).getName());
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return (null != books ? books.size() : 0);
    }

    public BookListAdapter() {
    }
}
