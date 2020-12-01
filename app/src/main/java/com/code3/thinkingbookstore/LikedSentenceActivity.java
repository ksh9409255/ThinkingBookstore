package com.code3.thinkingbookstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
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
    private ListView listView;
    private ImageButton back;
    ArrayList<Sentence> sentenceList;
    static boolean calledAlready = false;
    StListAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liked_sentence);

        if(!calledAlready) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            calledAlready = true;
        }

        sentenceList = new ArrayList<Sentence>();
        listView = (ListView)findViewById(R.id.list_sentence);
        myAdapter = new StListAdapter(this, sentenceList);
        listView.setAdapter(myAdapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dataRef = database.getReference("sentence_like").child(getIntent().getExtras().getString("bookIdx"));
        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    //Log.i("i", dataSnapshot.getKey()+"<<<<<<<<<<"+dataSnapshot.getValue());
                    sentenceList.add(new Sentence(dataSnapshot.getValue().toString(), dataSnapshot.getKey()));
                }
                myAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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