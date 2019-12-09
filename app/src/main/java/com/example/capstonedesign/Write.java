package com.example.capstonedesign;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.capstonedesign.ui.MapActivity;
import com.example.capstonedesign.ui.MapData;
import com.example.capstonedesign.view.Postinfo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Write extends AppCompatActivity {
    private static final String TAG = "Write";
    private FirebaseUser user;
    private ArrayList<String> pathList = new ArrayList<>();
    private LinearLayout parent;
    private List<Double> latitude = new ArrayList<>();
    private List<Double> longitude = new ArrayList<>();

    private int pathCount, successCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        parent = findViewById(R.id.contentsLayout);

        findViewById(R.id.buttonAddCourse).setOnClickListener(onClickListener);
        findViewById(R.id.buttonSave).setOnClickListener(onClickListener);
        findViewById(R.id.buttonPic).setOnClickListener(onClickListener);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0: {
                if (resultCode == Activity.RESULT_OK) {
                    String profilePath = data.getStringExtra("profilePath");
                    pathList.add(profilePath);
                    LinearLayout parent = findViewById(R.id.contentsLayout);
                    ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                    ImageView imageView = new ImageView(Write.this);
                    imageView.setLayoutParams(layoutParams);

                    Glide.with(this).load(profilePath).centerCrop().override(500).into(imageView);
                    parent.addView(imageView);

                    EditText editText = new EditText(Write.this);
                    editText.setLayoutParams(layoutParams);
                    editText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_CLASS_TEXT);
                    parent.addView(editText);

                }
                break;
            }
            case 2: {
                if (resultCode == RESULT_OK) {
                    ArrayList<Double> tLat = new ArrayList<>();
                    ArrayList<Double> tLong = new ArrayList<>();
                    MapData item = new MapData(tLat, tLong);
                    item = data.getParcelableExtra("KEY");

                    latitude = item.getLatitude();
                    longitude = item.getLongitude();
                }
            }
        }
    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.buttonAddCourse:
                    courseAdd();// 코스 추가 화면 이동
                    break;
                case R.id.buttonSave:
                    testSave();
                    startToast("Saved");
                    break;
                case R.id.buttonPic:  //갤러리 액티비티
                    if (ContextCompat.checkSelfPermission(Write.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(Write.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                1);

                        if (ActivityCompat.shouldShowRequestPermissionRationale(Write.this,
                                Manifest.permission.READ_EXTERNAL_STORAGE)) {

                        } else {
                            startToast("권한 허용이 필요합니다.");
                        }
                    } else {
                        addPic(Gallery.class, "image");
                    }

                    break;
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    addPic(Gallery.class, "media");
                } else {
                    startToast("권한 허용이 필요합니다.");
                }
            }
        }
    }


    private void testSave() {
        final String Title = ((EditText) findViewById(R.id.editTitleText)).getText().toString();


        if (Title.length() > 0) {

            final ArrayList<String> contentsList = new ArrayList<>();

            user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();

            for (int i = 0; i < parent.getChildCount(); i++) {
                View view = parent.getChildAt(i);
                if (view instanceof EditText) {
                    String text = ((EditText) view).getText().toString();
                    if (text.length() > 0) {
                        contentsList.add(text);
                    } else {
                        contentsList.add(pathList.get(pathCount));
                        String[] pathArray = pathList.get(pathCount).split("||.");
                        final StorageReference mountainImagesRef = storageRef.child("posts/" + user.getUid() + "/" + pathCount + "." + pathArray[pathArray.length - 1]);
                        try {
                            InputStream stream = new FileInputStream(new File(pathList.get(pathCount)));
                            StorageMetadata metadata = new StorageMetadata.Builder().setCustomMetadata("index", "" + (contentsList.size() - 1)).build();
                            UploadTask uploadTask = mountainImagesRef.putStream(stream, metadata);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle unsuccessful uploads
                                    Log.e("실패", "실패");
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Log.e("성공", "성공");
                                    final int index = Integer.parseInt(taskSnapshot.getMetadata().getCustomMetadata("index"));
                                    mountainImagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            contentsList.set(index, uri.toString());
                                            successCount++;
                                            if (pathList.size() == successCount) {

                                                Postinfo postinfo = new Postinfo(Title, contentsList, user.getUid(), new Date(), latitude, longitude);
                                                uploader(postinfo);
                                                for (int a = 0; a < contentsList.size(); a++) {
                                                    Log.e("로그", "콘텐츠" + contentsList.get(a));
                                                }
                                                //완료
                                            }
                                        }
                                    });

                                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                                    // ...
                                }
                            });


                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                        pathCount++;
                    }
                }
            }
        } else {
            startToast("내용을 입력해주세요");
        }
    }

    private void uploader(Postinfo postinfo) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("posts").add(postinfo).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                finish();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

    }

    private void courseAdd() {
        Intent intent = new Intent(this, MapActivity.class);

        startActivityForResult(intent, 2);  //코스 추가
    }

    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    private void addPic(Class c, String media) {
        Intent intent = new Intent(this, c);
        intent.putExtra("media", media);
        startActivityForResult(intent, 0);
    }


}