package com.code3.thinkingbookstore;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class BookListAdapter extends BaseAdapter {
    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<GetSentence1Activity.Books> books;

    public BookListAdapter(Context context, ArrayList<GetSentence1Activity.Books> data) {
        mContext = context;
        books = data;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return books.size();
    }

    @Override
    public Object getItem(int i) {
        return books.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View listView = mLayoutInflater.inflate(R.layout.book_listitem, null);

        ImageView bookcover = (ImageView)listView.findViewById(R.id.bookcover_list1);
        TextView bookname = (TextView)listView.findViewById(R.id.bookname_list1);

        StorageReference storageRef;
        storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(books.get(i).getBookcover());
        storageRef.getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                bookcover.setImageBitmap(bitmap);
            }
        });

        bookname.setText(books.get(i).getName());
        return listView;
    }
}
