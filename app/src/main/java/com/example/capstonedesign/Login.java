package com.example.capstonedesign;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.buttonSignin).setOnClickListener(onClickListener);
        findViewById(R.id.buttonSignup).setOnClickListener(onClickListener);

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

    }

    /*
        // 메인화면에서 뒤로가기 눌렀을때 앱종료
        @Override
        public void onBackPressed(){
            super.onBackPressed();
            moveTaskToBack(true);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    */
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.buttonSignin:
                    signUp();

                    break;
                case R.id.buttonSignup:
                    signUp();
                    gotoSignUp();
                    break;

            }
        }
    };

    //==========================================로그인=============================================
    private void signUp() {
        String email = ((EditText) findViewById(R.id.emailtext)).getText().toString();
        String password = ((EditText) findViewById(R.id.passwordtext)).getText().toString();

        if (email.length() > 0 && password.length() > 0) {
            final RelativeLayout loaderLayout = findViewById(R.id.loaderlayout);
            loaderLayout.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                startToast("Success!");
                                gotoWrite();
                            } else {

                                if (task.getException() != null) {
                                    startToast(task.getException().toString());
                                    //실패했을때 출력
                                }
                            }
                        }
                    });

        } else {
            startToast("Wrong Email or Password.");
        }
        //==============================================================================================


    }

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

    }

    //함수 메인화면
    private void gotoWrite() {
        Intent intent = new Intent(this, Write.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  //스택초기화
        startActivity(intent);
    }

    //함수 회원가입
    private void gotoSignUp() {
        Intent intent = new Intent(this, Signup.class);


        startActivity(intent);
    }

}
