package com.code3.thinkingbookstore;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import io.hamed.htepubreadr.component.EpubReaderComponent;
import io.hamed.htepubreadr.entity.BookEntity;
import io.hamed.htepubreadr.entity.FontEntity;
import io.hamed.htepubreadr.entity.HtmlBuilderEntity;
import io.hamed.htepubreadr.module.HtmlBuilderModule;
import io.hamed.htepubreadr.ui.view.EpubView;
import io.hamed.htepubreadr.util.EpubUtil;

public class ViewerActivity extends AppCompatActivity {
    private ImageButton backbtn, bookmark, list;
    private Button textprop, page;
    private TextView chnow;
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
    private int fontNow = 0;
    private int colorNow = 0;
    private int pageNum = 0;
    private int scrollLen;
    private int totalPage;
    private int chapter = 2;
    float contentHeight;
    float total;
    float percent = 0;

    private LinearLayout chbar;
    private ImageButton chbackbtn;
    private ListView chapterList;
    private ArrayList<String> chapters = new ArrayList<String>();
    private ArrayAdapter<String> madapter;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewer);

        String bookName = getIntent().getStringExtra("bookname");

        bindView();

        /* 책 불러오기 */
        loadFont();
        loadBook(bookName);

        madapter = new ArrayAdapter<String>(this, R.layout.chapter_listitem, chapters);
        chapterList.setAdapter(madapter);
        chapterList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                pageNum = 0;
                page.setText((pageNum+1) + " Page");
                chapter = i + 2;
                try {
                    content = EpubUtil.getHtmlContent(allPage.get(chapter));
                } catch (Exception e) {
                    makeToast("마지막 페이지");
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
                                        changeTextColor(colorNow);
                                    }
                                }, 10);
                            }
                        }, 10);
                    }
                });
                changeTextColor(colorNow);
                chnow.setText("/Ch"+(chapter-1));
                makeToast("챕터 "+(chapter - 1));
                copy.setLabelText("0%");
            }
        });

        /* epubView 화면 터치 설정 */
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

                            if(chbar.getVisibility() == View.VISIBLE) {
                                chbar.setVisibility(View.GONE);
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
                                copy.setLabelText(String.format("%.0f", percent * 100)+"%");
                            } else if(percent == 1.0) { // 남은 페이지 없으면 다음 챕터로
                                chapter ++;
                                if(chapter == allPage.size()) { // 책의 맨 마지막 페이지
                                    chapter--;
                                    makeToast("마지막 페이지");
                                    return false;
                                }

                                pageNum = 0;
                                page.setText((pageNum+1) + " Page");
                                try {
                                    content = EpubUtil.getHtmlContent(allPage.get(chapter));
                                } catch (Exception e) {
                                    makeToast("마지막 페이지");
                                }
                                changeFont(fontNow);
                                epubView.setUp(content);
                                epubView.setWebViewClient(new WebViewClient() {
                                    @Override
                                    public void onPageFinished(WebView view, String url) {
                                        final WebView newView = epubView;

                                        newView.postDelayed(new Runnable() {
                                            public void run() {
                                                newView.postDelayed(new Runnable() {
                                                    public void run() {
                                                        changeTextColor(colorNow);
                                                    }
                                                }, 10);
                                            }
                                        }, 10);
                                    }
                                });
                                chnow.setText("/Ch"+(chapter-1));
                                makeToast("챕터 "+(chapter - 1));
                                copy.setLabelText("0%");
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
                                copy.setLabelText(String.format("%.0f", percent * 100)+"%");
                            } else if(percent == 0.0) { // 앞에 남은 페이지 없으면 전 챕터로
                                chapter --;
                                if(chapter == 1) { // 책의 맨 첫 페이지
                                    chapter++;
                                    makeToast("첫 페이지");
                                    return false;
                                }

                                try {
                                    content = EpubUtil.getHtmlContent(allPage.get(chapter));
                                } catch (Exception e) {
                                    makeToast("첫 페이지");
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
                                                        changeTextColor(colorNow);
                                                    }
                                                }, 100);
                                            }
                                        }, 100);
                                    }
                                });
                                chnow.setText("/Ch"+(chapter-1));
                                makeToast("챕터 "+(chapter - 1));
                                copy.setLabelText("100%");
                            }
                            //Log.i("i", "slide>>>>>>>>>>>>>>>>>>>");
                        }
                        changeTextColor(colorNow);
                }
                return false;
            }
        });

        /* epubView 실시간 스크롤 위치 구하기 */
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

        /* 글자 설정 창 Visibility */
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
                changeFont(i);
                fontNow = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {   // 글자 색
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                colorNow = i;
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

        /* 동영상 검색 */
        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewerActivity.this, VideoActivity.class);
                intent.putExtra("bookname", getIntent().getStringExtra("bookname"));
                startActivity(intent);
            }
        });

        /* 목차 */
        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(chbar.getVisibility() == View.VISIBLE) {
                    chbar.setVisibility(View.GONE);
                } else {
                    chbar.setVisibility(View.VISIBLE);
                }
            }
        });
        chbackbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chbar.setVisibility(View.GONE);
            }
        });

        /* 플로팅 메뉴 */
        comment.setOnClickListener(new View.OnClickListener() { // 댓글창 이동
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewerActivity.this, ReviewActivity.class);
                intent.putExtra("bookname", getIntent().getStringExtra("bookname"));
                intent.putExtra("bookCover", getIntent().getStringExtra("coverimage"));
                intent.putExtra("bookIdx", getIntent().getStringExtra("bookIdx"));
                startActivity(intent);
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
                    colorNow = 0;
                    isDarkmodeOn = false;
                } else {
                    epubView.setBackgroundColor(Color.BLACK);
                    epubCard.setBackgroundColor(Color.BLACK);
                    epubView.getSettings().setJavaScriptEnabled(true);
                    epubView.loadUrl("javascript:document.body.style.setProperty(\"color\", \"white\");");
                    spinner2.setSelection(1);
                    colorNow = 1;
                    isDarkmodeOn = true;
                }
            }
        });

        copy.setOnClickListener(new View.OnClickListener() { // 링크 복사
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                //String pasteData = ("<font color = '#6200EE'>"+"@Ch"+chapter+"_"+String.format("%.0f", percent * 100)+"%</font>");
                String pasteData = ("@Ch"+chapter+"_"+String.format("%.0f", percent * 100)+"%");
                clipboard.setPrimaryClip(ClipData.newPlainText("label", pasteData));
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActionModeStarted(final android.view.ActionMode mode) {
        Menu menu = mode.getMenu();
        menu.add("Like!")
                .setEnabled(true)
                .setVisible(true)
                .setOnMenuItemClickListener(item -> {
                    //해당 메뉴를 눌렸을 때 수행할 작업
                    epubView.evaluateJavascript("(function(){return window.getSelection().toString()})()",
                            new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String value) {
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    try {
                                        onLikeClicked(value, getIntent().getStringExtra("bookIdx"), user.getDisplayName());
                                        makeToast("좋아요 완료");
                                    } catch(DatabaseException e) {
                                        makeToast(". # $ [ ] 문자가 들어가면 안됩니다");
                                    }
                                    //Log.i("i", value+"<<<<<<<<<<<<<<<<<<<");
                                }
                            });
                    return true;
                });
        super.onActionModeStarted(mode);
    }

    public void onLikeClicked(String sentence, String bookId, String userId){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference rootRef = firebaseDatabase.getReference();

        DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference().child("sentence_like").child(bookId).child(sentence);
        likesRef.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    //If not liked already then user wants to like the post
                    likesRef.setValue(userId);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
        chnow = (TextView)findViewById(R.id.chnow_viewer);
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

        chbar = (LinearLayout)findViewById(R.id.drawer_viewer);
        chbackbtn = (ImageButton)findViewById(R.id.backbtn_chlist);
        chapterList = (ListView)findViewById(R.id.chapter_list);
    }

    private void loadFont() {
        listFont.add(new FontEntity("https://hamedtaherpour.github.io/sample-assets/font/Acme.css", "Acme"));
        listFont.add(new FontEntity("https://hamedtaherpour.github.io/sample-assets/font/IndieFlower.css", "IndieFlower"));
        listFont.add(new FontEntity("https://hamedtaherpour.github.io/sample-assets/font/SansitaSwashed.css", "SansitaSwashed"));
    }

    private void loadBook(String book_file_name) {
        try {
            epubReader = new EpubReaderComponent("/data/data/com.code3.thinkingbookstore/files/"+book_file_name+".epub");
            bookEntity = epubReader.make(this);
            allPage = bookEntity.getPagePathList();
        } catch(Exception e) {
            makeToast("책 불러오기 실패");
        }
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
        chnow.setText("/Ch"+(chapter-1));
        changeFont(0);

        for(int i=2; i<allPage.size(); i++) {
            Log.i("i", i+"<<<<<<<<<<<<<<<<<<");
            chapters.add(new String("Chapter "+(i-1)));
        }
        //madapter.notifyDataSetChanged();
    }

    private void changeFont(int i) {
        try {
            content = content.replaceAll("src=\"../", "src=\"" + epubView.getBaseUrl() + "");
            content = content.replaceAll("href=\"../", "href=\"" + epubView.getBaseUrl() + "");
        } catch (Exception e) {
            e.printStackTrace();
            content = "404";
        }
        HtmlBuilderModule htmlBuilderModule = new HtmlBuilderModule();
        HtmlBuilderEntity entity = new HtmlBuilderEntity(
                "img{display: inline; height: auto; max-width: 100%;}",
                listFont.get(i).getUrl(),
                content
        );
        epubView.loadDataWithBaseURL(epubView.getBaseUrl(), htmlBuilderModule.getBaseContent(entity), "text/html", "UTF-8", null);
        epubView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                final WebView newView = epubView;

                newView.postDelayed(new Runnable() {
                    public void run() {
                        newView.postDelayed(new Runnable() {
                            public void run() {
                                pageNum = 0;
                                page.setText((pageNum+1) + " Page");
                            }
                        }, 50);
                    }
                }, 10);
            }
        });
    }

    private void changeTextColor(int i) {
        Log.e("sdlk", i+"");
        switch(i) {
            case 0 : epubView.loadUrl("javascript:document.body.style.setProperty(\"color\", \"black\");"); break;
            case 1 : epubView.loadUrl("javascript:document.body.style.setProperty(\"color\", \"white\");"); break;
            case 2 : epubView.loadUrl("javascript:document.body.style.setProperty(\"color\", \"green\");"); break;
            case 3 : epubView.loadUrl("javascript:document.body.style.setProperty(\"color\", \"blue\");"); break;
            case 4 : epubView.loadUrl("javascript:document.body.style.setProperty(\"color\", \"red\");"); break;
        }
    }

    private void makeToast(String msg) {
        if(mToast != null) mToast.cancel();
        mToast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
        mToast.show();
    }
}