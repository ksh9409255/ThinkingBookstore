package com.code3.thinkingbookstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class GetSentence1Activity extends AppCompatActivity {
    private ImageButton back;
    private ListView listView;
    ArrayList<Books> bookList;
    static boolean calledAlready = false;
    BookListAdapter myAdapter;
    TextView text1, text2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_sentence1);

        back = (ImageButton)findViewById(R.id.backbtn_list1);
        listView = (ListView)findViewById(R.id.list_list1);
        text1 = new TextView(this);
        text2 = new TextView(this);

        if(!calledAlready) {
            FirebaseDatabase.getInstance();
            calledAlready = true;
        }

        bookList = new ArrayList<Books>();
        myAdapter = new BookListAdapter(this, bookList);
        listView.setAdapter(myAdapter);

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot postSnapshot: snapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    PlaceDetailActivity.BookDescrip mypage = postSnapshot.getValue(PlaceDetailActivity.BookDescrip.class);
                    bookList.add(new Books(mypage.getBookcover(), mypage.getName()));
                    myAdapter.notifyDataSetChanged();
                    Log.e("name, imagepath", key+"??"+mypage.getBookcover()+"//"+mypage.getName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        rootRef.child("book_descrip").addValueEventListener(valueEventListener);

        back = (ImageButton)findViewById(R.id.backbtn_list1);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public class Books {
        private String bookcover;
        private String name;

        public Books() {
        }

        public Books(String bookcover, String name) {
            this.bookcover = bookcover;
            this.name = name;
        }

        public void setBookcover(String bookcover) {
            this.bookcover = bookcover;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getBookcover() {
            return bookcover;
        }

        public String getName() {
            return name;
        }
    }
}