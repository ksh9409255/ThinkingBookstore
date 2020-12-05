package com.code3.thinkingbookstore;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class StListAdapter extends BaseAdapter {
    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<LikedSentenceActivity.Sentence> sentence;

    public StListAdapter(Context context, ArrayList<LikedSentenceActivity.Sentence> data) {
        mContext = context;
        sentence = data;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return sentence.size();
    }

    @Override
    public Object getItem(int i) {
        return sentence.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View listView = mLayoutInflater.inflate(R.layout.sentence_listitem, null);

        TextView likeduser = (TextView)listView.findViewById(R.id.liked_user);
        TextView likedst = (TextView)listView.findViewById(R.id.liked_sentence);

        likeduser.setText(sentence.get(i).getUser());
        likedst.setText(sentence.get(i).getSt());
        return listView;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }
}
