package com.code3.thinkingbookstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

public class PlaceDetailActivity extends AppCompatActivity {
    private TextView likenum, hatenum, bookname, author, coverimage;
    private ImageView bookcover;
    private ImageButton likebtn, hatebtn, backbtn;
    private Button read;
    ExpandableTextView expTv1, expTv2;

    FirebaseUser user;
    FirebaseStorage storage;
    StorageReference storageRef;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference rootRef;
    BookDescrip newpage;
    String name_book;
    String bookIdx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);

        Intent intent = getIntent();
        bookIdx = intent.getExtras().getString("bookIdx");

        newpage = new BookDescrip();
        bindView();
        setFirebase();
        displayNumberOfLikes(bookIdx, user.getUid());
        displayNumberOfHates(bookIdx, user.getUid());

        likebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLikeClicked(likebtn, bookIdx, user.getUid());
                displayNumberOfLikes(bookIdx, user.getUid());
            }
        });

        hatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onHateClicked(hatebtn, bookIdx, user.getUid());
                displayNumberOfHates(bookIdx, user.getUid());
            }
        });

        read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StorageReference pathReference = storage.getReference().child("epubfiles/"+bookname.getText()+".epub");
                File localFile = null;
                //localFile = File.createTempFile(bookname.getText().toString(), ".epub");
                localFile = new File("/data/data/com.code3.thinkingbookstore/files", bookname.getText().toString()+".epub");
                //localFile.deleteOnExit();

                pathReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        // Local temp file has been created
                        Intent intent = new Intent(PlaceDetailActivity.this, ViewerActivity.class);
                        intent.putExtra("bookname", bookname.getText());
                        intent.putExtra("bookCover", coverimage.getText());
                        intent.putExtra("bookIdx", bookIdx);
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });

            }
        });

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void displayNumberOfHates(String bookId, String currentUserId){
        DatabaseReference hatesRef = FirebaseDatabase.getInstance().getReference().child("book_hate").child(bookId+"_hates");
        hatesRef.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    long numOfHates = 0;
                    if(dataSnapshot.hasChild("hates")){
                        numOfHates = dataSnapshot.child("hates").getValue(Long.class);
                    }

                    //Populate numOfHates on post i.e. textView.setText(""+numOfHates)
                    //This is to check if the user has liked the post or not
                    hatenum.setText(""+numOfHates);
                    hatebtn.setSelected(dataSnapshot.hasChild(currentUserId));
                    likebtn.setEnabled(!dataSnapshot.hasChild(currentUserId));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void onHateClicked(View v, String bookId, String userId){
        DatabaseReference hatesRef = FirebaseDatabase.getInstance().getReference().child("book_hate").child(bookId+"_hates").child("hates");
        DatabaseReference uidRef = FirebaseDatabase.getInstance().getReference().child("book_hate").child(bookId+"_hates").child(userId);
        hatesRef.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long numHates = 0;
                if(dataSnapshot.exists()){
                    numHates = dataSnapshot.getValue(Long.class);
                }
                if(hatebtn.isSelected()){
                    hatesRef.setValue(numHates-1);
                    uidRef.removeValue();
                    likebtn.setEnabled(true);

                } else {
                    hatesRef.setValue(numHates+1);
                    uidRef.setValue(userId);
                    likebtn.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void displayNumberOfLikes(String bookId, String currentUserId){
        DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference().child("book_like").child(bookId+"_likes");
        likesRef.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    long numOfLikes = 0;
                    if(dataSnapshot.hasChild("likes")){
                        numOfLikes = dataSnapshot.child("likes").getValue(Long.class);
                    }

                    //Populate numOfLikes on post i.e. textView.setText(""+numOfLikes)
                    //This is to check if the user has liked the post or not
                    likenum.setText(""+numOfLikes);
                    likebtn.setSelected(dataSnapshot.hasChild(currentUserId));
                    hatebtn.setEnabled(!dataSnapshot.hasChild(currentUserId));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void onLikeClicked(View v, String bookId, String userId){
        DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference().child("book_like").child(bookId+"_likes").child("likes");
        DatabaseReference uidRef = FirebaseDatabase.getInstance().getReference().child("book_like").child(bookId+"_likes").child(userId);
        likesRef.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long numLikes = 0;
                if(dataSnapshot.exists()){
                    numLikes = dataSnapshot.getValue(Long.class);
                }
                if(likebtn.isSelected()){
                    //If already liked then user wants to unlike the post
                    likesRef.setValue(numLikes-1);
                    uidRef.removeValue();
                    hatebtn.setEnabled(true);

                } else {
                    //If not liked already then user wants to like the post
                    likesRef.setValue(numLikes+1);
                    uidRef.setValue(userId);
                    hatebtn.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void gotoReview(View v) {
        Intent intent = new Intent(PlaceDetailActivity.this, ReviewActivity.class);
        intent.putExtra("bookname", bookname.getText());
        intent.putExtra("bookCover", coverimage.getText());
        intent.putExtra("bookIdx", bookIdx);
        startActivity(intent);
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
        user = FirebaseAuth.getInstance().getCurrentUser();
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
                    if(key.equals(bookIdx)) {
                        setBookcover(mypage.getBookcover());
                        bookname.setText(mypage.getName());
                        author.setText(mypage.getAuthor());
                        expTv1.setText(mypage.getDescription());
                        expTv2.setText(mypage.getAuthor_descrip());
                        coverimage.setText(mypage.getBookcover());
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
        public String name;

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

        public void setName(String name) {
            this.name = name;
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

        public String getName() {
            return name;
        }

        public BookDescrip() {
        }

        public BookDescrip(String author, String author_descrip, String bookcover, String description, String name) {
            this.author = author;
            this.author_descrip = author_descrip;
            this.bookcover = bookcover;
            this.description = description;
            this.name = name;
        }
    }

    public void bindView() {
        likenum = (TextView)findViewById(R.id.likenum);
        hatenum = (TextView)findViewById(R.id.hatenum);
        bookname = (TextView)findViewById(R.id.bookname_descrip);
        author = (TextView)findViewById(R.id.author_descrip);
        coverimage = (TextView)findViewById(R.id.bookcover_save);
        bookcover = (ImageView)findViewById(R.id.bookcover_descrip);
        likebtn = (ImageButton)findViewById(R.id.like_descrip);
        hatebtn = (ImageButton)findViewById(R.id.hate_descrip);
        backbtn = (ImageButton)findViewById(R.id.backbtn_descrip);
        read = (Button)findViewById(R.id.read_descrip);

        ((TextView)findViewById(R.id.sample1).findViewById(R.id.title)).setText("책 소개");
        ((TextView)findViewById(R.id.sample2).findViewById(R.id.title)).setText("저자 소개");
        expTv1 = (ExpandableTextView) findViewById(R.id.sample1)
                .findViewById(R.id.expand_text_view);
        expTv2 = (ExpandableTextView) findViewById(R.id.sample2)
                .findViewById(R.id.expand_text_view);
    }
}