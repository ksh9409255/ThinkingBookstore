package com.code3.thinkingbookstore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LikedSentenceActivity extends AppCompatActivity {
    private ImageButton back;
    ArrayList<Sentence> sentenceList;
    static boolean calledAlready = false;
    StListAdapter mAdapter;
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liked_sentence);

        if(!calledAlready) {
            FirebaseDatabase.getInstance();
            calledAlready = true;
        }

        sentenceList = new ArrayList<Sentence>();
        mRecyclerView = (RecyclerView)findViewById(R.id.list_sentence);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mAdapter = new StListAdapter(sentenceList);
        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                mLinearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        if(getIntent().getExtras().getString("isGetting") != null) {
            mAdapter.setOnItemClickListener(new StListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View v, int pos) {
                    Intent intent = getIntent();
                    intent.putExtra("sentence", mAdapter.sentence.get(pos).st);
                    Log.e("string", mAdapter.sentence.get(pos).st);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
        } else {

        }


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dataRef = database.getReference("sentence_like").child(getIntent().getExtras().getString("bookIdx"));
        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Log.i("i", dataSnapshot.getKey()+"<<<<<<<<<<"+dataSnapshot.getValue());
                    sentenceList.add(new Sentence(dataSnapshot.getValue().toString(), dataSnapshot.getKey()));
                }
                mAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        back = (ImageButton)findViewById(R.id.backbtn_liked);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private static final int REQUEST_CODE_SENTENCE = 1;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("requestcode <<<<<<<<<<", requestCode+"");
        if(requestCode == REQUEST_CODE_SENTENCE) {
            mAdapter.setOnItemClickListener(new StListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View v, int pos) {
                    Intent intent = getIntent();
                    intent.putExtra("sentence", mAdapter.sentence.get(pos).st);
                    Log.e("string", mAdapter.sentence.get(pos).st);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
        }
    }

    public class Sentence {
        private String user;
        private String st;

        public String getUser() {
            return user;
        }

        public String getSt() {
            return st;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public void setSt(String st) {
            this.st = st;
        }

        public Sentence(String user, String st) {
            this.user = user;
            this.st = st;
        }
    }
}