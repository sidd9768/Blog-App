package com.example.blogapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private androidx.appcompat.widget.Toolbar mainToolbar;

    protected ImageView ofsLogoMainActivity;
    private FirebaseAuth firebaseAuth;
    private ListView blogListView;
    private ArrayList<BlogPost> blog_list;
    private BlogListAdapter blogListAdapter;
    private ArrayList<String> arrayList;
    private ArrayAdapter arrayAdapter;
    private FloatingActionButton newPostBtn;
    private FirebaseFirestore firebaseFirestore;
    private DocumentSnapshot lastVisible;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private TextView navHeaderUsername;
    private TextView navHeaderUserEmail;
    private LayoutInflater factory;
    private NavigationView navigationView;
    private View headerNav;
    private Boolean isFirstPageLoad = true;

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        MenuInflater menuInflater = getMenuInflater();
//        menuInflater.inflate(R.menu.main_menu, menu);
//
//        return super.onCreateOptionsMenu(menu);
//    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()){

            case R.id.account:
                accountIntent();
                break;

            case R.id.logout:
                signout();
                break;

        }

        mDrawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    public void setNavigationViewListener(){

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(mToggle.onOptionsItemSelected(item)){

            return true;

        }

//        else if(item.getItemId() == R.id.logout){
//
//            signout();
//
//        }else if(item.getItemId() == R.id.account){
//
//            accountIntent();
//
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        firebaseAuth = FirebaseAuth.getInstance();
        newPostBtn = (FloatingActionButton) findViewById(R.id.new_post_btn);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mainToolbar,  R.string.Open, R.string.Close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setTitle("");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        navigationView = (NavigationView) findViewById(R.id.navigation_view);

        headerNav = navigationView.getHeaderView(0);

        mainToolbar.setNavigationIcon(R.drawable.menu_interface_new);

        navHeaderUsername = (TextView) headerNav.findViewById(R.id.nav_header_username);
        navHeaderUserEmail = (TextView) headerNav.findViewById(R.id.nav_header_user_email);

        if(firebaseAuth.getCurrentUser() != null){

            setNavigationViewListener();

            blog_list = new ArrayList<>();
            blogListView = (ListView) findViewById(R.id.list_view);

            blogListAdapter = new BlogListAdapter(blog_list, getApplicationContext());
            blogListView.setAdapter(blogListAdapter);

            firebaseFirestore = FirebaseFirestore.getInstance();

            SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.blogapp", Context.MODE_PRIVATE);

            navHeaderUsername.setText(sharedPreferences.getString("fullname_user", "Not found"));
            navHeaderUserEmail.setText(sharedPreferences.getString("email_user", "Not found"));

//            blogListView.setOnScrollListener(new AbsListView.OnScrollListener() {
//                @Override
//                public void onScrollStateChanged(AbsListView absListView, int i) {
//
//                    Boolean isReachedBottom = !absListView.canScrollVertically(1);
//                    if(isReachedBottom){
//
//                        loadMorePost();
//
//                    }
//
//                }
//
//                @Override
//                public void onScroll(AbsListView absListView, int i, int i1, int i2) {
//
//                }
//            });

            Query firstQuery = firebaseFirestore.collection("Posts").orderBy("timestamp", Query.Direction.DESCENDING).limit(8);

            firstQuery.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    if (e != null) {

                        Log.d("Error: ", e.toString());
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    } else {

                        if (isFirstPageLoad) {

                            lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                            blog_list.clear();

                        }
                        for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {

                            if (doc.getType() == DocumentChange.Type.ADDED) {

                                String blogPostId = doc.getDocument().getId();
                                BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withId(blogPostId);

                                if (isFirstPageLoad) {

                                    blog_list.add(blogPost);

                                } else {

                                    blog_list.add(0, blogPost);

                                }

                                blogListAdapter.notifyDataSetChanged();

                            }

                        }

                        isFirstPageLoad = false;
                    }
                }
            });

            //==================== FLOATING BUTTON =================+

            newPostBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    newPostActivityIntent();

                }
            });

            Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();


            //================== LISTVIEW ONCLICKLISTENER ==============

            blogListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    Intent blogDetailsIntent = new Intent(MainActivity.this, PostDetailsActivity.class);
//                    blogDetailsIntent.putExtra("username", firebaseAuth.getCurrentUser().getUid());
                    blogDetailsIntent.putExtra("blogId", i);
                    blogDetailsIntent.putExtra("user_id", firebaseAuth.getCurrentUser().getUid());
                    blogDetailsIntent.putExtra("blogTitle", blog_list.get(i).getTitle());
                    blogDetailsIntent.putExtra("blogDescription", blog_list.get(i).getDescription());
                    blogDetailsIntent.putExtra("blogImageUrl", blog_list.get(i).getImage_url());
                    blogDetailsIntent.putExtra("blogThumbUrl", blog_list.get(i).getThumb_url());
                    startActivity(blogDetailsIntent);

                }
            });


        }else{

            loginPageIntent();

        }

    }

    public void newPostActivityIntent(){

        Intent newPostIntet = new Intent(MainActivity.this, NewPost.class);
        startActivity(newPostIntet);

    }

    public void loginPageIntent(){

        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();

    }

    public void accountIntent (){

        Intent accountIntent = new Intent(MainActivity.this, AccountActivity.class);
        startActivity(accountIntent);

    }

    public void signout(){

        firebaseAuth.signOut();
        loginPageIntent();

    }

    public void loadMorePost(){

        Query nextQuery = firebaseFirestore.collection("Posts").orderBy("timestamp", Query.Direction.DESCENDING).limit(5);
        nextQuery.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if(!documentSnapshots.isEmpty()){

                    lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                    for (DocumentChange doc: documentSnapshots.getDocumentChanges()){

                        if(doc.getType() == DocumentChange.Type.ADDED){

                            String blogPostId = doc.getDocument().getId();
                            BlogPost blogPost = doc.getDocument().toObject(BlogPost.class).withId(blogPostId);
                            blog_list.add(blogPost);

                            blogListAdapter.notifyDataSetChanged();

                        }

                    }

                }

            }
        });

    }
}
