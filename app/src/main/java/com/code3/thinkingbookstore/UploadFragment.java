package com.code3.thinkingbookstore;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import ja.burhanrashid52.photoeditor.OnPhotoEditorListener;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.ViewType;

import static android.app.Activity.RESULT_OK;

public class UploadFragment extends Fragment {
    private ImageView uploadPic;
    private Button btnEdit;
    private Button btnUpload;
    private EditText uploadDetail;
    private EditText uploadWriter;
    private EditText uploadBookName;

    FirebaseUser user;

    String imageView = "https://firebasestorage.googleapis.com/v0/b/thinkingbookshop.appspot.com/o/post%2Fempty.png?alt=media&token=3b7e50a9-188c-488d-9cd8-0a6b0a837320";

    private RecyclerHomeData data;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload, container, false);

        uploadPic = (ImageView)view.findViewById(R.id.upload_content_image_view);
        btnEdit = (Button)view.findViewById(R.id.upload_pic_edit);
        btnUpload = (Button)view.findViewById(R.id.btn_upload);
        uploadDetail = (EditText)view.findViewById(R.id.upload_edit_detail);
        uploadWriter = (EditText)view.findViewById(R.id.upload_writer_edit);
        uploadBookName = (EditText)view.findViewById(R.id.upload_book_name_edit);

        btnEdit.setOnClickListener(l->{
            Intent intent = new Intent(getContext(),PhotoEditActivity.class);
            startActivityForResult(intent,2000);
        });

        btnUpload.setOnClickListener(l->{
            String detail = uploadDetail.getText().toString().trim();
            String writer = uploadWriter.getText().toString().trim();
            String bookName = uploadBookName.getText().toString().trim();
            user = FirebaseAuth.getInstance().getCurrentUser();

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            data = new RecyclerHomeData(imageView,user.getDisplayName(),detail,writer,bookName);
            DatabaseReference databaseReference = database.getReference("post_list");
            databaseReference.push().setValue(data);
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==2000){
            if(resultCode==RESULT_OK){
                SimpleDateFormat sdf= new SimpleDateFormat("yyyyMMddhhmmss");
                String filename= sdf.format(new Date())+ ".jpg";
                imageView="/data/data/com.code3.thinkingbookstore/files/temp.jpg";
                Uri file = Uri.fromFile(new File(imageView));
                FirebaseStorage firebaseStorage= FirebaseStorage.getInstance();
                StorageReference imgRef= firebaseStorage.getReference("post/uploads/"+filename);
                imgRef.putFile(file);
                Glide.with(getView()).load(imageView).diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(uploadPic);
                imageView=String.valueOf(imgRef);
            }
        }
    }
}