package com.code3.thinkingbookstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.io.BufferedReader;

public class PlaceDetailActivity extends AppCompatActivity {
    private TextView likenum, hatenum, bookname, author;
    private ImageView bookcover;
    private ImageButton likebtn, hatebtn;
    ExpandableTextView expTv1, expTv2;

    FirebaseUser user;
    FirebaseStorage storage;
    StorageReference storageRef;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference rootRef;
    BookDescrip newpage;
    String name_book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);

        name_book = "Cranford";
        newpage = new BookDescrip();
        bindView();
        setFirebase();

        likebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference upvotesRef = rootRef.child("book_descrip/Allan_and_the_Ice_Gods");
                upvotesRef.runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        Like currentValue = mutableData.getValue(Like.class);
                        if (currentValue == null) {
                            return Transaction.success(mutableData);
                        } else {
                            Log.i("i", "sdfsfdfsdfsdfsdfsdf");
                            mutableData.setValue(currentValue);

                        }
                        //likenum.setText(currentValue);

                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(
                            DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                        System.out.println("Transaction completed");
                    }
                });
            }
        });
    }

    @IgnoreExtraProperties
    static public class Like {
        public String user;
        public int num;

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public void setNum(int num) {
            this.num = num;
        }

        public int getNum() {
            return num;
        }

        public Like() {
        }

        public Like(String user, int num) {
            this.user = user;
            this.num = num;
        }
    }

    public void gotoReview(View v) {

    }

    public void setBookcover(String Url) {
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl(Url);
        storageRef.getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                bookcover.setImageBitmap(bitmap);
            }
        });
    }

    public void setFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        rootRef = firebaseDatabase.getReference();
        loadBookDescrip();

    }

    public void loadBookDescrip() {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot postSnapshot: snapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    BookDescrip mypage = postSnapshot.getValue(BookDescrip.class);
                    if(key.equals(name_book)) {
                        setBookcover(mypage.getBookcover());
                        bookname.setText(key);
                        author.setText(mypage.getAuthor());
                        expTv1.setText(mypage.getDescription());
                        expTv2.setText(mypage.getAuthor_descrip());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        rootRef.child("book_descrip").addValueEventListener(valueEventListener);
    }

    @IgnoreExtraProperties
    static public class BookDescrip {
        public String author;
        public String author_descrip;
        public String bookcover;
        public String description;

        public void setAuthor(String author) {
            this.author = author;
        }

        public void setAuthor_descrip(String author_descrip) {
            this.author_descrip = author_descrip;
        }

        public void setBookcover(String bookcover) {
            this.bookcover = bookcover;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getAuthor() {
            return author;
        }

        public String getAuthor_descrip() {
            return author_descrip;
        }

        public String getBookcover() {
            return bookcover;
        }

        public String getDescription() {
            return description;
        }

        public BookDescrip() {
        }

        public BookDescrip(String author, String author_descrip, String bookcover, String description) {
            this.author = author;
            this.author_descrip = author_descrip;
            this.bookcover = bookcover;
            this.description = description;
        }
    }


    public void bindView() {
        likenum = (TextView)findViewById(R.id.likenum);
        hatenum = (TextView)findViewById(R.id.hatenum);
        bookname = (TextView)findViewById(R.id.bookname_descrip);
        author = (TextView)findViewById(R.id.author_descrip);
        bookcover = (ImageView)findViewById(R.id.bookcover_descrip);
        likebtn = (ImageButton)findViewById(R.id.like_descrip);
        hatebtn = (ImageButton)findViewById(R.id.hate_descrip);

        ((TextView)findViewById(R.id.sample1).findViewById(R.id.title)).setText("책 소개");
        ((TextView)findViewById(R.id.sample2).findViewById(R.id.title)).setText("저자 소개");
        expTv1 = (ExpandableTextView) findViewById(R.id.sample1)
                .findViewById(R.id.expand_text_view);
        expTv2 = (ExpandableTextView) findViewById(R.id.sample2)
                .findViewById(R.id.expand_text_view);
    }
}