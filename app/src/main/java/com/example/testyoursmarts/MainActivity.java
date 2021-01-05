package com.example.testyoursmarts;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.navigation.ui.AppBarConfiguration;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN;

public class MainActivity extends AppCompatActivity {


    private List <CategoryModel> catList;
    private GridView catView;
    private AppBarConfiguration mAppBarConfiguration;
    private BottomNavigationView bottomNavigationView;
    private FrameLayout main_frame;
    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemReselectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    switch (menuItem.getItemId()) {
                        case R.id.nav_home:
                            Intent intentHome = new Intent(MainActivity.this, MainActivity.class);
                            startActivity(intentHome);
                            return true;

                        case R.id.nav_leaderboard:
                            Intent intentleaderB = new Intent(MainActivity.this,leaderboardChoiceActivity.class);
                            startActivity(intentleaderB);
                            return true;

                        case R.id.nav_logout:
                            FirebaseAuth.getInstance().signOut();
                            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                    .requestIdToken(getString(R.string.default_web_client_id))
                                    .requestEmail()
                                    .build();
                            GoogleSignInClient mGoogleClient = GoogleSignIn.getClient(getBaseContext(), gso);
                            mGoogleClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Intent intentLogout = new Intent(getBaseContext(), LoginActivity.class);
                                    intentLogout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intentLogout);
                                      finish();
                                }
                            });


                    }
                    return false;
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        bottomNavigationView = findViewById(R.id.top_nav_bar);
        main_frame = findViewById(R.id.main_frame);

        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemReselectedListener);



        catView = findViewById(R.id.cat_GridCategory);

        loadCategories();
        CategoryAdapter adapter = new CategoryAdapter(catList);
        catView.setAdapter(adapter);


        catView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0)
                {
                    String gkTest = "This is a General Knowledge Quiz";
                    String gkID = "General Knowledge Quiz";
                    Intent gkIntent = new Intent(MainActivity.this, confirmQuiz.class);
                    gkIntent.putExtra("Topic", gkTest);
                    gkIntent.putExtra("ID",gkID);
                    startActivity(gkIntent);
                }
                if(position == 1)
                {
                    String pictureQuiz = "This is a Picture Quiz";
                    String pQID = "Picture Quiz";
                    Intent pictureQIntent = new Intent(MainActivity.this, confirmQuiz.class);
                    pictureQIntent.putExtra("Topic", pictureQuiz);
                    pictureQIntent.putExtra("ID", pQID);
                    startActivity(pictureQIntent);
                }
                if(position == 2)
                {
                    String speedRunQuiz = "This is a speed Run Quiz";
                    String speedRunID = "Speed Run Quiz";
                    Intent speedRunIntent = new Intent(MainActivity.this, confirmQuiz.class);
                    speedRunIntent.putExtra("Topic", speedRunQuiz);
                    speedRunIntent.putExtra("ID", speedRunID);
                    startActivity(speedRunIntent);
                }
                if(position == 3)
                {
                    Intent intentTopicQuiz = new Intent(MainActivity.this, topicSelector.class);
                    startActivity(intentTopicQuiz);
                }

                if(position == 4)
                {
                    String headToHeadQuiz = "This is a head to head Quiz";
                    String headToHeadID = "Head to Head Quiz";
                    Intent headtoheadIntent = new Intent(MainActivity.this, confirmQuiz.class);
                    headtoheadIntent.putExtra("Topic", headToHeadQuiz);
                    headtoheadIntent.putExtra("ID", headToHeadID);
                    startActivity(headtoheadIntent);
                }

            }
        });

    }



    private void loadCategories()
    {
        catList = new ArrayList<>();
        catList.add(new CategoryModel("1", "General Knowledge Quiz", 20));
        catList.add(new CategoryModel("2", "Picture Quiz", 30));
        catList.add(new CategoryModel("3", "Speed Run Quiz", 10));
        catList.add(new CategoryModel("4", "Topic Quiz", 25));
        catList.add(new CategoryModel("5", "Head to Head Quiz", 11));
    }

}