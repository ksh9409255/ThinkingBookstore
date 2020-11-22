package com.code3.thinkingbookstore;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import io.hamed.htepubreadr.component.EpubReaderComponent;
import io.hamed.htepubreadr.entity.BookEntity;

import static android.view.View.GONE;

public class ViewerActivity extends AppCompatActivity {
    private ImageButton backbtn, bookmark, list;
    private Button textprop, page;
    private FloatingActionButton copy, darkmode, comment;
    private SeekBar seekbar;
    private Spinner spinner, spinner2;
    private boolean isBarOpen = true;
    private Animation fromup, toup, fromdown, todown;
    private LinearLayout upbar, downbar, textproperties;
    private FloatingActionMenu menu_viewer;

    private EpubReaderComponent epubReader;
    private BookEntity bookEntity;

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

        menu_viewer = (FloatingActionMenu)findViewById(R.id.menu_viewer);
        copy = (FloatingActionButton)findViewById(R.id.copy_viewer);
        darkmode = (FloatingActionButton)findViewById(R.id.darkmode_viewer);
        comment = (FloatingActionButton)findViewById(R.id.comment_viewer);

        try {
            epubReader = new EpubReaderComponent("/data/data/com.code3.thinkingbookstore/files/gaskell-cranford.epub");
        } catch(Exception e) {

        }

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

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {   // 글자 색
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {  // 글자크기
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

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
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        darkmode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        comment.setOnClickListener(new View.OnClickListener() {
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