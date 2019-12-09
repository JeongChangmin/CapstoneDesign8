package com.example.capstonedesign;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstonedesign.ui.MapActivity2;
import com.example.capstonedesign.ui.MapData;
import com.example.capstonedesign.view.MainAdapter;
import com.example.capstonedesign.view.Postinfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class courseView extends AppCompatActivity {
    private static final String TAG = "courseView";
    RecyclerView recyclerView;
    MainAdapter mAdapter;
    ArrayList<Postinfo> postList = new ArrayList<>();

    ArrayList<Double> mCoursePointX;
    ArrayList<Double> mCoursePointY;

    MapData mCourse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_view);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        mAdapter = new MainAdapter(courseView.this, postList);

        init();

        db.collection("posts")
                .orderBy("createAt", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Postinfo temppost = document.toObject(Postinfo.class);
                                postList.add(temppost);
                            }


                            mAdapter.notifyDataSetChanged();

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void init() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(courseView.this));
        recyclerView.setAdapter(mAdapter);


        mAdapter.setOnItemClickListener(new MainAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                Postinfo mSelectedPost = postList.get(pos);

                mCoursePointX = new ArrayList<Double>(mSelectedPost.getLatitude());
                mCoursePointY = new ArrayList<Double>(mSelectedPost.getLongitude());

                mCourse = new MapData(mCoursePointX, mCoursePointY);

                Intent intent = new Intent(courseView.this, MapActivity2.class);

                intent.putExtra("course", mCourse);
                startActivity(intent);

            }
        });
    }


}
