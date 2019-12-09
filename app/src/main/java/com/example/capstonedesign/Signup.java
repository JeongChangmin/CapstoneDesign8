package com.example.capstonedesign;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Signup extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.buttonCreate).setOnClickListener(onClickListener);
        findViewById(R.id.gotologinbutton).setOnClickListener(onClickListener);

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.buttonCreate:
                    signUp();
                    break;
                case R.id.gotologinbutton:
                    startloginactivity();
                    break;

            }
        }
    };

    //=========================================회원가입=============================================
    private void signUp() {
        String email = ((EditText) findViewById(R.id.emailtext)).getText().toString();
        String password = ((EditText) findViewById(R.id.passwordtext)).getText().toString();
        String passwordcheck = ((EditText) findViewById(R.id.passwordchecktext)).getText().toString();


        if (email.length() > 0 && password.length() > 0 && passwordcheck.length() > 0) {
            if (password.equals(passwordcheck)) {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    startToast("Success!");
                                    //성공했을때 출력
                                } else {
                                    if (task.getException() != null) {
                                        startToast(task.getException().toString());
                                        //실패했을때 출력
                                    }
                                }

                            }
                        });
            } else {
                startToast("Wrong Password.");
            }
        } else {
            startToast("Wrong Email or Password.");
        }
        //==============================================================================================


    }

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

    }

    private void startloginactivity() {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }
}
