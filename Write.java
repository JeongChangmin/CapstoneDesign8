package com.example.capstonedesign;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.capstonedesign.ui.MapActivity;
import com.google.firebase.auth.FirebaseAuth;

public class Write extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        findViewById(R.id.buttonAddCourse).setOnClickListener(onClickListener);



    }
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.buttonAddCourse:
                    courseAdd();// 메인로그인화면으로 이동
            }
        }
    };


    private void  courseAdd(){
        Intent intent = new Intent(this, MapActivity.class);

        startActivity(intent);
    }
}
