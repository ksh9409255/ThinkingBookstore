package com.code3.thinkingbookstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends AppCompatActivity {
    private EditText newid, newpw, newnick;
    private InputMethodManager imm;
    private Button morphBtn;
    private FirebaseAuth mAuth;
    Toast nToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        newid = (EditText)findViewById(R.id.newid);
        newpw = (EditText)findViewById(R.id.newpw);
        imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        morphBtn = (Button)findViewById(R.id.morphBtn);
        mAuth = FirebaseAuth.getInstance();

        morphBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newID = newid.getText().toString().trim();
                String newPW = newpw.getText().toString().trim();
                Log.i("i", newID);

                mAuth.createUserWithEmailAndPassword(newID, newPW)
                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()) {
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName("Jane Q. User")
                                            .build();

                                    user.updateProfile(profileUpdates)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d("d", "User profile updated.");
                                                    }
                                                }
                                            });
                                    if(nToast != null) nToast.cancel();
                                    nToast = Toast.makeText(getApplicationContext(), "회원가입 성공", Toast.LENGTH_SHORT);
                                    nToast.show();
                                    finish();
                                    overridePendingTransition(0, 0);
                                } else {
                                    if(nToast != null) nToast.cancel();
                                    nToast = Toast.makeText(getApplicationContext(), "회원가입 실패", Toast.LENGTH_SHORT);
                                    nToast.show();
                                }
                            }
                        });
            }
        });
    }

    public void registerOnClick(View v) {
        imm.hideSoftInputFromWindow(newid.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(newpw.getWindowToken(), 0);
    }
}