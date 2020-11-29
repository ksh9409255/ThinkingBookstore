package com.code3.thinkingbookstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{
    HomeFragment homeFragment = new HomeFragment();
    ListFragment listFragment = new ListFragment();
    UploadFragment likeFragment = new UploadFragment();
    MypageFragment myPageFragment = new MypageFragment();

    FirebaseUser user;

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container, homeFragment).commit();
                return true;
            case R.id.menu_list:
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container, listFragment).commit();
                return true;
            case R.id.menu_like:
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container, likeFragment).commit();
                return true;
            case R.id.menu_myPage:
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container, myPageFragment).commit();
                return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null) {  // 로그인 안되어있으면
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        } else {    // 로그인 되어있으면

        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bn_bottom_navi);
        bottomNavigationView.setItemIconTintList(null);

        getSupportFragmentManager().beginTransaction().replace(R.id.main_container, homeFragment).commit(); //특정 Fragment를 첫화면으로 설정하고 싶을 경우 homeFragment->해당Fragment로 변경경

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }
}