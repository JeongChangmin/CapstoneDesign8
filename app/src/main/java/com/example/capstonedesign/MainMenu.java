package com.example.capstonedesign;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);


        findViewById(R.id.wrtitebutton).setOnClickListener(onClickListener);
        findViewById(R.id.logout).setOnClickListener(onClickListener);
        findViewById(R.id.buttonTheme).setOnClickListener(onClickListener);
        findViewById(R.id.buttonCourseView).setOnClickListener(onClickListener);


    }

    // 메인화면에서 뒤로가기 눌렀을때 앱종료
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.logout:
                    FirebaseAuth.getInstance().signOut();  //서버로부터 로그아웃
                    startloginactivity();
                    break;// 메인로그인화면으로 이동
                case R.id.wrtitebutton:
                    if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                        startloginactivity();
                        break;

                    } else {
                        write();
                        break;


                    }
                case R.id.buttonTheme:
                    gotoTheme();
                    break;

                case R.id.buttonCourseView:
                    gotoCourseView();
                    break;


            }
        }
    };

    //함수 로그인
    private void startloginactivity() {
        Intent intent = new Intent(this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //스택초기화
        startActivity(intent);
    }

    private void write() {
        Intent intent = new Intent(this, Write.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //스택초기화
        startActivity(intent);
    }

    private void gotoTheme() {
        Intent intent = new Intent(this, Theme.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //스택초기화
        startActivity(intent);
    }

    private void gotoCourseView() {
        Intent intent = new Intent(this, courseView.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //스택초기화
        startActivity(intent);
    }


}
