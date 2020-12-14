package com.code3.thinkingbookstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;

import ja.burhanrashid52.photoeditor.OnPhotoEditorListener;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.ViewType;

import static android.view.View.VISIBLE;

public class PhotoEditActivity extends AppCompatActivity {
    Button complete;
    Button[] Buttons = new Button[5];
    Integer[] ButtonIDs = {R.id.editcolor1, R.id.editcolor2, R.id.editcolor3, R.id.editcolor4, R.id.editcolor5};
    int[] colors = {Color.BLACK, Color.RED, Color.BLUE, Color.YELLOW, Color.GREEN};
    ImageButton getPho, getSen, close, getEdit;
    TextView iseditable;
    private Toast mToast;
    private LinearLayout mLayout;
    private EditText edit;

    private static final int REQUEST_CODE = 0;
    private static final int REQUEST_CODE_SENTENCE = 1;
    PhotoEditor mPhotoEditor;
    PhotoEditorView mPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_edit);

        bindView();

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_CODE);

        getPho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        getSen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mPhotoEditor.addText("sldf", Color.BLUE);
                Intent intent = new Intent(PhotoEditActivity.this, GetSentence1Activity.class);
                startActivityForResult(intent, REQUEST_CODE_SENTENCE);
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeToast("편집 취소");
                finish();
            }
        });
        getEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLayout.setVisibility(VISIBLE);
                edit.post(new Runnable() {
                    @Override
                    public void run() {
                        edit.setFocusableInTouchMode(true);
                        edit.requestFocus();

                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

                        imm.showSoftInput(edit,0);
                    }
                });
            }
        });
    }

    public void editOnClick(View v) {
        try {
            mPhotoEditor.addText(edit.getText().toString(), Color.BLACK);
        } catch(Exception e) {

        }
        mLayout.setVisibility(View.GONE);
        edit.setText("");
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);
    }

    private void bindView() {
        for(int i=0; i<Buttons.length; i++) {
            Buttons[i] = (Button)findViewById(ButtonIDs[i]);
        }
        getPho = (ImageButton)findViewById(R.id.getphoto);
        getSen = (ImageButton)findViewById(R.id.getsentence);
        close = (ImageButton)findViewById(R.id.backbtn_edit);
        iseditable = (TextView)findViewById(R.id.iseditable);
        mLayout = (LinearLayout)findViewById(R.id.edit_back);
        edit = (EditText)findViewById(R.id.edit_edit);
        getEdit = (ImageButton)findViewById(R.id.getedittext);
        complete = (Button)findViewById(R.id.complete_edit);

        mPhoto = findViewById(R.id.photoeditor);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    Bitmap img = BitmapFactory.decodeStream(in);
                    in.close();

                    mPhoto.getSource().setImageBitmap(img);
                    mPhotoEditor = new PhotoEditor.Builder(this, mPhoto)
                            .setPinchTextScalable(true)
                            .build();
                    mPhotoEditor.setOnPhotoEditorListener(new OnPhotoEditorListener() {
                        @Override
                        public void onEditTextChangeListener(View rootView, String text, int colorCode) {
                            //mPhotoEditor.editText(rootView, text, Color.GREEN);
                            if(text.length() >= 10) {
                                makeToast("\""+text.substring(1, 10)+"...\" 선택");
                            } else {
                                makeToast(text+" 선택");
                            }

                            iseditable.setVisibility(View.GONE);
                            for(int i=0; i<Buttons.length; i++) {
                                final int index;
                                index = i;

                                Buttons[index].setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        mPhotoEditor.editText(rootView, text, colors[index]);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onAddViewListener(ViewType viewType, int numberOfAddedViews) {

                        }

                        @Override
                        public void onRemoveViewListener(ViewType viewType, int numberOfAddedViews) {

                        }

                        @Override
                        public void onStartViewChangeListener(ViewType viewType) {

                        }

                        @Override
                        public void onStopViewChangeListener(ViewType viewType) {

                        }
                    });

                    complete.setOnClickListener(new View.OnClickListener() {
                        @SuppressLint("MissingPermission")
                        @Override
                        public void onClick(View view) {
                            Log.e("button", "pressed");
                            mPhotoEditor.saveAsFile("/data/data/com.code3.thinkingbookstore/files/temp.jpg", new PhotoEditor.OnSaveListener() {
                                @Override
                                public void onSuccess(@NonNull String imagePath) {
                                    Log.e("PhotoEditor", "Image Saved Successfully");
                                    Intent intent = getIntent();
                                    setResult(RESULT_OK, intent);
                                    makeToast("편집 완료");
                                    finish();
                                }

                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Log.e("PhotoEditor", "Failed to save Image");
                                    makeToast("편집 실패");
                                }
                            });
                        }
                    });
                } catch (Exception e) {

                }
            } else if (resultCode == RESULT_CANCELED) {
                makeToast("사진 선택 취소");
            }
        }

        if(requestCode == REQUEST_CODE_SENTENCE) {
            if(resultCode == RESULT_OK) {
                mPhotoEditor.addText(data.getStringExtra("sentence"), Color.BLACK);
            } else if (resultCode == RESULT_CANCELED) {
                makeToast("문장 선택 취소");
            }
        }
    }

    private void makeToast(String msg) {
        if(mToast != null) mToast.cancel();
        mToast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
        mToast.show();
    }
}