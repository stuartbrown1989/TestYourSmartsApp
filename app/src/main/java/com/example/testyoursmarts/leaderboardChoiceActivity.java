package com.example.testyoursmarts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.ui.AppBarConfiguration;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class leaderboardChoiceActivity extends AppCompatActivity {

    private List<leaderboardModel> catList;
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
                            Intent intentHome = new Intent(leaderboardChoiceActivity.this, MainActivity.class);
                            startActivity(intentHome);
                            return true;

                        case R.id.nav_leaderboard:
                            Intent intentLeaderboard= new Intent(leaderboardChoiceActivity.this, leaderboardChoiceActivity.class);
                            startActivity(intentLeaderboard);
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
        setContentView(R.layout.activity_leaderboard_choice);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        bottomNavigationView = findViewById(R.id.top_nav_bar);
        main_frame = findViewById(R.id.main_frame);

        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemReselectedListener);

        catView = findViewById(R.id.cat_Gridleaderboard);
        loadCategories();
        leaderboardAdapter adapter = new leaderboardAdapter(catList);
        catView.setAdapter(adapter);

        catView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0)
                {
                    Intent gkIntent = new Intent(leaderboardChoiceActivity.this, leaderboardgk_Quiz.class);
                    startActivity(gkIntent);
                }

                if(position == 1)
                {
                    Intent pictureQIntent = new Intent(leaderboardChoiceActivity.this, leaderboardPictureQuiz.class);
                    startActivity(pictureQIntent);
                }

                if(position == 2)
                {
                    Intent speedRunIntent = new Intent(leaderboardChoiceActivity.this, leaderboardSpeedrunQuiz.class);
                    startActivity(speedRunIntent);
                }

                if(position == 3)
                {
                    Intent intentTopicQuiz = new Intent(leaderboardChoiceActivity.this, leaderboardTopicQuiz.class);
                    startActivity(intentTopicQuiz);
                }

            }
        });

    }


    private void loadCategories()
    {
        catList = new ArrayList<>();
        catList.add(new leaderboardModel("1", "General Knowledge Quiz"));
        catList.add(new leaderboardModel("2", "Picture Quiz"));
        catList.add(new leaderboardModel("3", "Speed Run Quiz"));
        catList.add(new leaderboardModel("4", "Topic Quiz"));

    }
}