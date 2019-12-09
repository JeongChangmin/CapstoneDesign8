package com.example.capstonedesign;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//---------------------------------------단순 시간경과후 화면 전환------------------------------------------
        Handler timer = new Handler();

        timer.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, MainMenu.class);
                startActivity(intent);
                finish();
            }
        }, 3000);
// ---------------------------------------단순 시간경과후 화면 전환------------------------------------------


//-----------------------------------------로그인 후 화면 전환-----------------------------------------------
  /*   if(FirebaseAuth.getInstance().getCurrentUser() == null){  // 현재 서버에 계정이 로그인이 되어 있는지 판단
         Handler handler = new Handler(){
             public void handleMessage (Message msg){  //로그인 안되어 있을시 로그인창으로 이동
                 super .handleMessage(msg);
                 startActivity(new Intent(MainActivity.this, Login.class));
                 finish();
             }
         };

         handler.sendEmptyMessageDelayed(0,3000);
     }else{
         Handler handler = new Handler(){
             public void handleMessage (Message msg){  // 로그인이 되어 있을시 메인메뉴로 이동
                 super .handleMessage(msg);
                 startActivity(new Intent(MainActivity.this, MainMenu.class));
                 finish();
             }
         };

         handler.sendEmptyMessageDelayed(0,3000);

     }
*/
        //-----------------------------------------로그인 후 화면 전환-----------------------------------------------
    }
}