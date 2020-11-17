package com.code3.thinkingbookstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    EditText idIn, pwIn;
    InputMethodManager imm;
    Button loginBtn, regiBtn;
    FirebaseAuth mAuth;
    Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        idIn = (EditText)findViewById(R.id.idIn);
        pwIn = (EditText)findViewById(R.id.pwIn);
        imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);

        loginBtn = (Button)findViewById(R.id.loginBtn);
        regiBtn = (Button)findViewById(R.id.regiBtn);
        mAuth = FirebaseAuth.getInstance();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = idIn.getText().toString().trim();
                String pwd = pwIn.getText().toString().trim();

                mAuth.signInWithEmailAndPassword(email, pwd)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()) {   // 로그인 성공
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                } else {    // 로그인 실패
                                    if(mToast != null) mToast.cancel();
                                    mToast = Toast.makeText(getApplicationContext(), "로그인 실패", Toast.LENGTH_SHORT);
                                    mToast.show();
                                }
                            }
                        });
            }
        });

        regiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    public void linearOnClick(View v) {
        imm.hideSoftInputFromWindow(idIn.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(pwIn.getWindowToken(), 0);
    }
}