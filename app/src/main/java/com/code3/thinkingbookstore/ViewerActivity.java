package com.code3.thinkingbookstore;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.github.clans.fab.FloatingActionMenu;

public class ViewerActivity extends AppCompatActivity {
    boolean isBarOpen = true;
    Animation fromup, toup, fromdown, todown;
    LinearLayout upbar, downbar;
    FloatingActionMenu menu_viewer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewer);

        menu_viewer = (FloatingActionMenu)findViewById(R.id.menu_viewer);

        upbar = (LinearLayout)findViewById(R.id.upbar);
        downbar = (LinearLayout)findViewById(R.id.downbar);

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
        } else { //페이지 닫혀있으면
            upbar.startAnimation(fromup);
            downbar.startAnimation(fromdown);
            menu_viewer.startAnimation(fromdown);
        }
    }
}