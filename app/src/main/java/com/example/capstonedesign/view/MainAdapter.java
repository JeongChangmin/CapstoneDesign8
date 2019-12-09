package com.example.capstonedesign.view;

import android.app.Activity;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.capstonedesign.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.GalleryViewHolder> {
    private ArrayList<Postinfo> mDataset = null;
    private Activity activity;


    public interface OnItemClickListener{
        void onItemClick(View v, int pos);
    }

    private OnItemClickListener mListener = null;

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }

    public class GalleryViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;

        GalleryViewHolder(View itemView) {
            super(itemView);

           itemView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   int pos = getAdapterPosition();
                   if (pos != RecyclerView.NO_POSITION) {
                       if (mListener != null) {
                           mListener.onItemClick((CardView) v, pos);
                       }
                   }
               }
           });

            cardView = itemView.findViewById(R.id.courseCardView);
        }
    }

    public MainAdapter(Activity activity, ArrayList<Postinfo> myDataset) {
        mDataset = myDataset;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MainAdapter.GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_post, parent, false);
        final GalleryViewHolder galleryViewHolder = new GalleryViewHolder(cardView);

        return galleryViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final GalleryViewHolder holder, int position) {

        CardView cardView = holder.cardView;
        TextView titleTextView = cardView.findViewById(R.id.titleTextView);
        titleTextView.setText(mDataset.get(position).getTitle());  // 타이틀을 뽑아오기

        TextView createAtTextView = cardView.findViewById(R.id.createAtTextView);
        createAtTextView.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(mDataset.get(position).getCreateAt()));

        LinearLayout contentsLayout = cardView.findViewById(R.id.contentsLayout);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        ArrayList<String> contentsList = mDataset.get(position).getContents();
        for (int i = 0; i < contentsList.size(); i++){
            String contents = contentsList.get(i);
            if(Patterns.WEB_URL.matcher(contents).matches()){
                ImageView imageView =new ImageView(activity);
                imageView.setLayoutParams(layoutParams);
                contentsLayout.addView(imageView);
                Glide.with(activity).load(contents).centerCrop().override(500).into(imageView);

            }else {
                TextView textView =new TextView(activity);
                textView.setLayoutParams(layoutParams);
                textView.setText(contents);
                contentsLayout.addView(textView);
            }
        }
        /* Glide.with(activity).load(mDataset.get(position)).centerCrop().override(500).into(imageView);

         */
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}


