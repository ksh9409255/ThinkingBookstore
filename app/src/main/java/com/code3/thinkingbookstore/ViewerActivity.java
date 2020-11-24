package com.code3.thinkingbookstore;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
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
    private Toast mToast;

    private FrameLayout epubFrame;
    private CardView epubCard;
    private EpubView epubView;
    private EpubReaderComponent epubReader;
    private BookEntity bookEntity;
    private List<String> allPage;
    private BookAdapter adapter;
    private String content;
    private List<FontEntity> listFont = new ArrayList<>();
    private Context context = this;
    private int pageNum = 0;
    private int scrollLen;
    private int totalPage;
    private int chapter = 2;
    float contentHeight;
    float total;
    float percent = 0;
    private Point size;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewer);

        String bookName = getIntent().getStringExtra("bookname");

        bindView();

        size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);

        listFont.add(new FontEntity("https://hamedtaherpour.github.io/sample-assets/font/Acme.css", "Acme"));
        listFont.add(new FontEntity("https://hamedtaherpour.github.io/sample-assets/font/IndieFlower.css", "IndieFlower"));
        listFont.add(new FontEntity("https://hamedtaherpour.github.io/sample-assets/font/SansitaSwashed.css", "SansitaSwashed"));

        /* 책 불러오기 */
        loadBook("gaskell-cranford.epub");

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
            private float startX;
            private float endX;
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN :
                        startX = motionEvent.getX();
                        return true;
                    case MotionEvent.ACTION_UP :
                        endX = motionEvent.getX();
                        if(startX > 1029 && startX - endX > 100) {  // slide <<
                            if(percent < 1.0) { // 다음 페이지들 남았으면
                                pageNum++;
                                page.setText((pageNum+1) + " Page");
                                ObjectAnimator anim = ObjectAnimator.ofInt(epubView, "scrollY",
                                        epubView.getScrollY(), epubView.getHeight() * pageNum);
                                anim.setDuration(0);
                                anim.start();
                                Log.i("o", (size.y)+"<<<<<<<<<<");
                                Log.i("o", (total/size.y)+"<<<<<<<<<<");
                                Log.i("i", total+"%%%%%%%%%%");
                            } else if(percent == 1.0) { // 남은 페이지 없으면 다음 챕터로
                                Log.i("o", percent+"<<<<<<<<<<");
                                chapter ++;
                                if(chapter == allPage.size()) { // 책의 맨 마지막 페이지
                                    chapter--;
                                    if(mToast != null) mToast.cancel();
                                    mToast = Toast.makeText(getApplicationContext(), "마지막 페이지", Toast.LENGTH_SHORT);
                                    mToast.show();
                                    return false;
                                }

                                pageNum = 0;
                                page.setText((pageNum+1) + " Page");
                                try {
                                    content = EpubUtil.getHtmlContent(allPage.get(chapter));
                                } catch (Exception e) {
                                    if(mToast != null) mToast.cancel();
                                    mToast = Toast.makeText(getApplicationContext(), "마지막 페이지", Toast.LENGTH_SHORT);
                                    mToast.show();
                                }
                                epubView.setUp(content);
                                if(mToast != null) mToast.cancel();
                                mToast = Toast.makeText(getApplicationContext(), "챕터 "+(chapter - 2), Toast.LENGTH_SHORT);
                                mToast.show();
                            }
                            //Log.i("i", "slide<<<<<<<<<<<<<<<<<<<");
                        }
                        else if(startX < 52 && endX - startX > 100) {   // slide >>
                            if(percent > 0.0) { // 앞 페이지들 남았으면
                                pageNum--;
                                page.setText((pageNum+1) + " Page");
                                ObjectAnimator anim = ObjectAnimator.ofInt(epubView, "scrollY",
                                        epubView.getScrollY(), epubView.getHeight() * pageNum);
                                anim.setDuration(0);
                                anim.start();
                            } else if(percent == 0.0) { // 앞에 남은 페이지 없으면 전 챕터로
                                chapter --;
                                if(chapter == 1) { // 책의 맨 첫 페이지
                                    chapter++;
                                    if(mToast != null) mToast.cancel();
                                    mToast = Toast.makeText(getApplicationContext(), "첫 페이지", Toast.LENGTH_SHORT);
                                    mToast.show();
                                    return false;
                                }

                                try {
                                    content = EpubUtil.getHtmlContent(allPage.get(chapter));
                                } catch (Exception e) {
                                    if(mToast != null) mToast.cancel();
                                    mToast = Toast.makeText(getApplicationContext(), "첫 페이지", Toast.LENGTH_SHORT);
                                    mToast.show();
                                }
                                epubView.setUp(content);
                                epubView.setWebViewClient(new WebViewClient() {
                                    @Override
                                    public void onPageFinished(WebView view, String url) {
                                        final WebView newView = epubView;

                                        newView.postDelayed(new Runnable() {
                                            public void run() {
                                                newView.postDelayed(new Runnable() {
                                                    public void run() {
                                                        pageNum = (int)(epubView.getContentHeight() * getResources().getDisplayMetrics().density)/epubView.getHeight();
                                                        page.setText(pageNum + " Page");
                                                        ObjectAnimator anim = ObjectAnimator.ofInt(newView, "scrollY",
                                                                newView.getScrollY(), newView.getHeight() * pageNum);
                                                        anim.setDuration(0);
                                                        anim.start();
                                                        page.setText((pageNum+1) + " Page");
                                                    }
                                                }, 50);
                                            }
                                        }, 10);
                                    }
                                });
                            }
                            //Log.i("i", "slide>>>>>>>>>>>>>>>>>>>");
                        }
                }
                return false;
            }
        });

        epubView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int scrollX, int scrollY, int i2, int i3) {
                scrollLen = scrollY;
                contentHeight = epubView.getContentHeight() * epubView.getScaleY();
                total = contentHeight * getResources().getDisplayMetrics().density - view.getHeight();
                // on some devices just 1dp was missing to the bottom when scroll stopped, so we subtract it to reach 1
                percent = Math.min(scrollY / (total - getResources().getDisplayMetrics().density), 1);
                //Log.d("SCROLL", "Percentage: " + percent*100);
                if (scrollY >= total - 1) {
                    //Log.d("SCROLL", "Reached bottom");
                }
            }
        });

        /* 애니매이션 */
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
                chapter = allPage.size() - 1;
                try {
                    content = EpubUtil.getHtmlContent(allPage.get(chapter));
                } catch (Exception e) {
                    if(mToast != null) mToast.cancel();
                    mToast = Toast.makeText(getApplicationContext(), "마지막 페이지", Toast.LENGTH_SHORT);
                    mToast.show();
                }
                epubView.setUp(content);
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
                Log.i("i", epubView.getScale()+"scale<<<<<<<<<<");
                Log.i("i", epubView.getContentHeight()+"ContentHeight<<<<<<<<<<<<<<");
                Log.i("i", epubView.getScrollY()+"scrollY<<<<<<<<<<<<");
                Log.i("i", epubView.getHeight()+"height<<<<<<<<<<<<<<<<");
                Log.i("i", epubView.getContentHeight() * epubView.getScale()+"height<<<<<<<<<<<<<");
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

    private void bindView() {
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

        upbar = (LinearLayout)findViewById(R.id.upbar);
        downbar = (LinearLayout)findViewById(R.id.downbar);
        textproperties = (LinearLayout)findViewById(R.id.textprop_menu);

        fromup = AnimationUtils.loadAnimation(this, R.anim.translate_fromup);
        toup = AnimationUtils.loadAnimation(this, R.anim.translate_toup);
        fromdown = AnimationUtils.loadAnimation(this, R.anim.translate_fromdown);
        todown = AnimationUtils.loadAnimation(this, R.anim.translate_todown);
    }

    private void loadBook(String book_file_name) {
        try {
            epubReader = new EpubReaderComponent("/data/data/com.code3.thinkingbookstore/files/verne-an-antarctic-mystery.epub");
            bookEntity = epubReader.make(this);
        } catch(Exception e) {
            Log.i("o", "err<<<<<<<<<<<<<<<<");

        }
        allPage = bookEntity.getPagePathList();
        adapter = new BookAdapter(allPage, epubReader.getAbsolutePath());
        epubView = (EpubView)findViewById(R.id.epub_view);
        epubView.setVerticalScrollBarEnabled(false);
        epubView.setBaseUrl(epubReader.getAbsolutePath());
        try {
            content = EpubUtil.getHtmlContent(allPage.get(chapter));
        } catch (Exception e) {
            e.printStackTrace();
        }
        epubView.setUp(content);
        page.setText((pageNum+1) + " Page");
    }
}