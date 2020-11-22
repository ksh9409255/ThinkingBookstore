package com.code3.thinkingbookstore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.hamed.htepubreadr.component.EpubReaderComponent;
import io.hamed.htepubreadr.entity.BookEntity;
import io.hamed.htepubreadr.entity.FontEntity;
import io.hamed.htepubreadr.ui.view.EpubView;
import io.hamed.htepubreadr.util.EpubUtil;

import static android.view.View.GONE;

public class ViewerActivity extends AppCompatActivity {
    private ImageButton backbtn, bookmark, list;
    private Button textprop, page;
    private FloatingActionButton copy, darkmode, comment;
    private SeekBar seekbar;
    private Spinner spinner, spinner2;
    private boolean isBarOpen = true;
    private boolean isDarkmodeOn = false;
    private Animation fromup, toup, fromdown, todown;
    private LinearLayout upbar, downbar, textproperties;
    private FloatingActionMenu menu_viewer;

    private FrameLayout epubFrame;
    private CardView epubCard;
    private EpubView epubView;
    private EpubReaderComponent epubReader;
    private BookEntity bookEntity;
    private BookAdapter adapter;
    private String content;
    private List<FontEntity> listFont = new ArrayList<>();
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewer);

        backbtn = (ImageButton)findViewById(R.id.backbtn_viewer);
        bookmark = (ImageButton)findViewById(R.id.bookmark_viewer);
        list = (ImageButton)findViewById(R.id.list_viewer);
        textprop = (Button)findViewById(R.id.textprop_viewer);
        page = (Button)findViewById(R.id.page_viewer);
        copy = (FloatingActionButton)findViewById(R.id.copy_viewer);
        darkmode = (FloatingActionButton)findViewById(R.id.darkmode_viewer);
        comment = (FloatingActionButton)findViewById(R.id.comment_viewer);
        seekbar = (SeekBar)findViewById(R.id.seekbar_viewer);
        spinner = (Spinner)findViewById(R.id.spinner_viewer);
        spinner2 = (Spinner)findViewById(R.id.spinner2_viewer);
        epubCard = (CardView)findViewById(R.id.epubCard_viewer);
        epubFrame = (FrameLayout)findViewById(R.id.epubFrame_viewer);

        menu_viewer = (FloatingActionMenu)findViewById(R.id.menu_viewer);
        copy = (FloatingActionButton)findViewById(R.id.copy_viewer);
        darkmode = (FloatingActionButton)findViewById(R.id.darkmode_viewer);
        comment = (FloatingActionButton)findViewById(R.id.comment_viewer);

        listFont.add(new FontEntity("https://hamedtaherpour.github.io/sample-assets/font/Acme.css", "Acme"));
        listFont.add(new FontEntity("https://hamedtaherpour.github.io/sample-assets/font/IndieFlower.css", "IndieFlower"));
        listFont.add(new FontEntity("https://hamedtaherpour.github.io/sample-assets/font/SansitaSwashed.css", "SansitaSwashed"));

        /* 책 불러오기 */
        try {
            epubReader = new EpubReaderComponent("/data/data/com.code3.thinkingbookstore/files/gaskell-cranford.epub");
            bookEntity = epubReader.make(this);
        } catch(Exception e) {

        }
        List<String> allPage = bookEntity.getPagePathList();
        adapter = new BookAdapter(allPage, epubReader.getAbsolutePath());
        epubView = (EpubView)findViewById(R.id.epub_view);
        epubView.setVerticalScrollBarEnabled(false);
        epubView.setOnTouchListener(new View.OnTouchListener() {
            public final static int FINGER_RELEASED = 0;
            public final static int FINGER_TOUCHED = 1;
            public final static int FINGER_DRAGGING = 2;
            public final static int FINGER_UNDEFINED = 3;

            private int fingerState = FINGER_RELEASED;

            private float m_downY;  // 스크롤 방지

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (fingerState == FINGER_RELEASED) fingerState = FINGER_TOUCHED;
                        else fingerState = FINGER_UNDEFINED;
                        m_downY = motionEvent.getY(); // 스크롤 방지
                        break;

                    case MotionEvent.ACTION_UP:
                        motionEvent.setLocation(motionEvent.getX(), m_downY);   // 스크롤 방지
                        if(fingerState != FINGER_DRAGGING) {
                            fingerState = FINGER_RELEASED;

                            // Your onClick codes
                            if(isBarOpen){
                                upbar.startAnimation(toup);
                                downbar.startAnimation(todown);
                                menu_viewer.startAnimation(todown);
                            } else {
                                upbar.startAnimation(fromup);
                                downbar.startAnimation(fromdown);
                                menu_viewer.startAnimation(fromdown);
                            }

                            if(textproperties.getVisibility() == View.VISIBLE) {
                                textproperties.setVisibility(View.GONE);
                            }
                        }
                        else if (fingerState == FINGER_DRAGGING) fingerState = FINGER_RELEASED;
                        else fingerState = FINGER_UNDEFINED;
                        break;

                    case MotionEvent.ACTION_MOVE:
                        motionEvent.setLocation(motionEvent.getX(), m_downY);   // 스크롤 방지
                        if (fingerState == FINGER_TOUCHED || fingerState == FINGER_DRAGGING) fingerState = FINGER_DRAGGING;
                        else fingerState = FINGER_UNDEFINED;
                        break;

                    default:
                        fingerState = FINGER_UNDEFINED;

                }
                return false;
            }
        });
        epubFrame.setOnTouchListener(new View.OnTouchListener() {
            private boolean isRight;
            private float startX;
            private float endX;
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN :
                        startX = motionEvent.getX();
                        if(startX > 1029) {
                            isRight = true;
                        } else if(startX < 52) {
                            isRight = false;
                        }
                        Log.i("i", ""+startX);
                        break;
                    case MotionEvent.ACTION_UP :
                        endX = motionEvent.getX();
                        if(isRight && startX - endX > 20) {
                        }
                }
                return false;
            }
        });

        epubView.setBaseUrl(epubReader.getAbsolutePath());
        try {
            content = EpubUtil.getHtmlContent(allPage.get(2));
        } catch (Exception e) {
            e.printStackTrace();
        }
        epubView.setUp(content);


        /* 애니매이션 */
        upbar = (LinearLayout)findViewById(R.id.upbar);
        downbar = (LinearLayout)findViewById(R.id.downbar);
        textproperties = (LinearLayout)findViewById(R.id.textprop_menu);

        fromup = AnimationUtils.loadAnimation(this, R.anim.translate_fromup);
        toup = AnimationUtils.loadAnimation(this, R.anim.translate_toup);
        fromdown = AnimationUtils.loadAnimation(this, R.anim.translate_fromdown);
        todown = AnimationUtils.loadAnimation(this, R.anim.translate_todown);

        fromup.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                upbar.setVisibility(View.VISIBLE);
                menu_viewer.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isBarOpen = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        toup.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                upbar.setVisibility(View.INVISIBLE);
                menu_viewer.setVisibility(View.INVISIBLE);
                isBarOpen = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        fromdown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                downbar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        todown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                downbar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        /* 뒤로가기 버튼 */
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        /* 글자 설정 */
        textprop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(textproperties.getVisibility() == View.VISIBLE) {
                    textproperties.setVisibility(View.GONE);
                } else {
                    textproperties.setVisibility(View.VISIBLE);
                }
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {    // 글꼴
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                adapter.setFontEntity(listFont.get(i));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {   // 글자 색
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch(i) {
                    case 0 : epubView.loadUrl("javascript:document.body.style.setProperty(\"color\", \"black\");"); break;
                    case 1 : epubView.loadUrl("javascript:document.body.style.setProperty(\"color\", \"white\");"); break;
                    case 2 : epubView.loadUrl("javascript:document.body.style.setProperty(\"color\", \"green\");"); break;
                    case 3 : epubView.loadUrl("javascript:document.body.style.setProperty(\"color\", \"blue\");"); break;
                    case 4 : epubView.loadUrl("javascript:document.body.style.setProperty(\"color\", \"red\");"); break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {  // 글자크기
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                epubView.setFontSize(i);
                //Log.i("i", "i" + i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        /* 북마크 */
        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        /* 책갈피 이동 */
        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        /* 현재 페이지 표시(누르면 페이지 이동) */
        page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        /* 플로팅 메뉴 */
        comment.setOnClickListener(new View.OnClickListener() { // 댓글창 이동
            @Override
            public void onClick(View view) {

            }
        });

        darkmode.setOnClickListener(new View.OnClickListener() {    // 다크모드
            @Override
            public void onClick(View view) {
                if(isDarkmodeOn) {
                    epubView.setBackgroundColor(Color.WHITE);
                    epubCard.setBackgroundColor(Color.WHITE);
                    epubView.loadUrl("javascript:document.body.style.setProperty(\"color\", \"black\");");
                    spinner2.setSelection(0);
                    isDarkmodeOn = false;
                } else {
                    epubView.setBackgroundColor(Color.BLACK);
                    epubCard.setBackgroundColor(Color.BLACK);
                    epubView.getSettings().setJavaScriptEnabled(true);
                    epubView.loadUrl("javascript:document.body.style.setProperty(\"color\", \"white\");");
                    spinner2.setSelection(1);
                    isDarkmodeOn = true;
                }
            }
        });

        copy.setOnClickListener(new View.OnClickListener() { // 링크 복사
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    public void viewerOnClick(View v) {
        if(isBarOpen){
            upbar.startAnimation(toup);
            downbar.startAnimation(todown);
            menu_viewer.startAnimation(todown);
        } else {
            upbar.startAnimation(fromup);
            downbar.startAnimation(fromdown);
            menu_viewer.startAnimation(fromdown);
        }

        if(textproperties.getVisibility() == View.VISIBLE) {
            textproperties.setVisibility(View.GONE);
        }
    }
}