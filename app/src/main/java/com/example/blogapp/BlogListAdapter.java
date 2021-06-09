package com.example.blogapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import androidx.annotation.NonNull;

public class BlogListAdapter extends ArrayAdapter<BlogPost> implements View.OnClickListener {

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private ArrayList<BlogPost> blog_list;
    private Context mContext;

    public static class ViewHolder{

        TextView titleText;
        TextView descriptionText;
        TextView usernameOfPost;
        ImageView blogImage;

    }

    public BlogListAdapter(ArrayList<BlogPost> blogList, Context context){

        super(context, R.layout.blog_list_item, blogList);
        this.blog_list = blogList;
        this.mContext = context;

    }

    @Override
    public void onClick(View view) {

    }

    private int lastPosition = -1;

    public View getView(int position, View convertView, ViewGroup parent){

        BlogPost blogPost = getItem(position);

        final ViewHolder viewHolder;

        final View result;

        if(convertView == null){

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.blog_list_item, parent, false);
            viewHolder.titleText = (TextView) convertView.findViewById(R.id.blog_title);
            viewHolder.descriptionText = (TextView) convertView.findViewById(R.id.blog_description);
            viewHolder.blogImage = (ImageView) convertView.findViewById(R.id.blog_image);
//            viewHolder.usernameOfPost = (TextView) convertView.findViewById(R.id.username_of_post);

            result = convertView;
            convertView.setTag(viewHolder);
        }else{

            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        final String blogPostId = blog_list.get(position).BlogPostId;
        final String current_user = firebaseAuth.getCurrentUser().getUid();

        String descData = blog_list.get(position).getDescription();
        String titleData = blog_list.get(position).getTitle();
        String imageUrl = blog_list.get(position).getImage_url();
        String thumbUrl = blog_list.get(position).getThumb_url();


        viewHolder.titleText.setText(titleData);
        viewHolder.descriptionText.setText(descData);

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.image_placeholder);

        Glide.with(mContext).setDefaultRequestOptions(requestOptions).load(imageUrl).thumbnail(Glide.with(mContext).load(thumbUrl)).into(viewHolder.blogImage);

        return convertView;


    }


}
