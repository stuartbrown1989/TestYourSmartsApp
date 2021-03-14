package com.example.testyoursmarts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class topicSelector extends AppCompatActivity {


    private AppBarConfiguration mAppBarConfiguration;
    private BottomNavigationView bottomNavigationView;
    private FrameLayout main_frame;
    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemReselectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    switch (menuItem.getItemId()) {
                        case R.id.nav_home:
                            Intent intentHome = new Intent(topicSelector.this, MainActivity.class);
                            startActivity(intentHome);
                            return true;

                        case R.id.nav_leaderboard:
                            Intent intentLeaderboard= new Intent(topicSelector.this, leaderboardChoiceActivity.class);
                            startActivity(intentLeaderboard);
                            return true;

                        case R.id.nav_account:
                            Intent intentAccount = new Intent(topicSelector.this, account_page.class);
                            startActivity(intentAccount);
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


    private Toolbar toolbar;
    private List <TestModel> testList;
    private GridView testView;
    private TextView quizValue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_selector);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Test Your Smarts: Select your Topic");
        bottomNavigationView = findViewById(R.id.top_nav_bar);
        main_frame = findViewById(R.id.main_frame);

        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemReselectedListener);
        testView = findViewById(R.id.cat_GridTopic);
        int cat_index = getIntent().getIntExtra("CAT_INDEX", 0);

        loadTestData();
        
        testAdapter adapter = new testAdapter(testList);
        testView.setAdapter(adapter);

        testView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0)
                {
                    String historyText = "This is a history Quiz - You will have 10 History related questions to answer, you will have 15 seconds " +
                            "to answer each question and you will be given 4 potential answers";
                    String historyID = "History Quiz";
                    Intent historyIntent = new Intent(topicSelector.this, confirmQuiz.class);
                    historyIntent.putExtra("Topic", historyText);
                    historyIntent.putExtra("ID",historyID);
                    startActivity(historyIntent);
                }
                if(position == 1)
                {
                    String scienceID = "Science Quiz";
                    String scienceText = "This is a Science Quiz - You will have 10 Science related questions to answer, you will have 15 seconds "  +
                            "to answer each question and you will be given 4 potential answers";
                    Intent scienceIntent = new Intent(topicSelector.this, confirmQuiz.class);
                    scienceIntent.putExtra("Topic", scienceText);
                    scienceIntent.putExtra("ID", scienceID);
                    startActivity(scienceIntent);
                }
                if(position == 2)
                {
                    String sportsID = "Sports Quiz";
                    String sportsText = "This is a Sports Quiz - You will have 10 Sports related questions to answer, you will have 15 seconds " +
                            "to answer each question and you will be given 4 potential answers";
                    Intent sportsIntent = new Intent(topicSelector.this, confirmQuiz.class);
                    sportsIntent.putExtra("Topic", sportsText);
                    sportsIntent.putExtra("ID", sportsID);
                    startActivity(sportsIntent);
                }
                if(position == 3)
                {
                    String filmTvID = "Film/TV Quiz";
                    String filmTvText = "This is a Film/TV Quiz - You will have 10 Film/TV related questions to answer, you will have 15 seconds " +
                    "to answer each question and you will be given 4 potential answers";
                    Intent filmTvIntent = new Intent(topicSelector.this, confirmQuiz.class);
                    filmTvIntent.putExtra("Topic", filmTvText);
                    filmTvIntent.putExtra("ID", filmTvID);
                    startActivity(filmTvIntent);
                }
                if(position == 4)
                {
                    String geographyID = "Geography Quiz";
                    String geographyText = "This is a Geography Quiz - You will have 10 Geography related questions to answer, you will have 15 seconds " +
                            "to answer each question and you will be given 4 potential answers\"";
                    Intent geographyIntent = new Intent(topicSelector.this, confirmQuiz.class);
                    geographyIntent.putExtra("Topic", geographyText);
                    geographyIntent.putExtra("ID", geographyID);
                    startActivity(geographyIntent);
                }
                if(position == 5)
                {
                    String musicID = "Music Quiz";
                    String musicText = "This is a Music Quiz - You will have 10 Music related questions to answer, you will have 15 seconds " +
                            "to answer each question and you will be given 4 potential answers\"";
                    Intent musicIntent = new Intent(topicSelector.this, confirmQuiz.class);
                    musicIntent.putExtra("Topic", musicText);
                    musicIntent.putExtra("ID", musicID);
                    startActivity(musicIntent);
                }


            }
        });

    }

    private void loadTestData()
    {
        testList = new ArrayList<>();
        testList.add(new TestModel("1","History"));
        testList.add(new TestModel("2","Science"));
        testList.add(new TestModel("3","Sports"));
        testList.add(new TestModel("4","Film/TV"));
        testList.add(new TestModel("5","Geography"));
        testList.add(new TestModel("6","Music"));
    }


}