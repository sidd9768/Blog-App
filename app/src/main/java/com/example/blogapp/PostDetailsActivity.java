package com.example.blogapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class PostDetailsActivity extends AppCompatActivity {

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        Intent intent = getIntent();

        String title = intent.getStringExtra("blogTitle");
        String description = intent.getStringExtra("blogDescription");
        String image_url = intent.getStringExtra("blogImageUrl");
        String thumb_url = intent.getStringExtra("blogThumbUrl");
        String user_id = intent.getStringExtra("user_id");

        setBlogPostDetails(title, description, image_url, thumb_url);

//        Toast.makeText(PostDetailsActivity.this, title, Toast.LENGTH_SHORT).show();

    }

    public void setBlogPostDetails(String titleOfPost, String descriptionOfPost, String image_Url, String thumb_Url){

        TextView titleText = (TextView) findViewById(R.id.title_blog_detail);
        TextView descriptionText = (TextView) findViewById(R.id.descriptionOfPost);
        ImageView blogImage = (ImageView) findViewById(R.id.image_blog_detail);

        titleText.setText(titleOfPost);
        descriptionText.setText(descriptionOfPost);

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.image_placeholder);

        Glide.with(PostDetailsActivity.this)
                .setDefaultRequestOptions(requestOptions)
                .load(image_Url)
                .thumbnail(Glide.with(PostDetailsActivity.this).load(thumb_Url))
                .into(blogImage);

    }
}
