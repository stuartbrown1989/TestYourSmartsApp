package com.example.testyoursmarts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.ui.AppBarConfiguration;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class confirmQuiz extends AppCompatActivity {

    private TextView idQuiz;
    private TextView quizDescription;
    private Button btnStartQuiz;



    private AppBarConfiguration mAppBarConfiguration;
    private BottomNavigationView bottomNavigationView;
    private FrameLayout main_frame;
    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemReselectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    switch (menuItem.getItemId()) {
                        case R.id.nav_home:
                            Intent intentHome = new Intent(confirmQuiz.this, MainActivity.class);
                            startActivity(intentHome);
                            return true;

                        case R.id.nav_leaderboard:
                            Intent intentLeaderboard= new Intent(confirmQuiz.this, leaderboardChoiceActivity.class);
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
        setContentView(R.layout.activity_confirm_quiz);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        bottomNavigationView = findViewById(R.id.top_nav_bar);
        main_frame = findViewById(R.id.main_frame);

        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemReselectedListener);



        idQuiz = findViewById(R.id.text_quizID);
        quizDescription = findViewById(R.id.quizDescription);
        Intent quizTextValues = getIntent();
        String quizText;
        final String quizID;
        quizText= quizTextValues.getStringExtra("Topic");
        quizID = quizTextValues.getStringExtra("ID");
        quizDescription.setText(quizText);
        idQuiz.setText(quizID);

        btnStartQuiz = findViewById(R.id.button_start_quiz);

        btnStartQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(idQuiz.getText().toString().equals("General Knowledge Quiz"))
                {
                    Intent gkQuizIntent = new Intent (confirmQuiz.this, gkQuizActivity.class);
                    startActivity(gkQuizIntent);
                }

                if(idQuiz.getText().toString().equals("Picture Quiz"))
                {
                    Intent pictureQuizIntent = new Intent (confirmQuiz.this, pictureQuizActivity.class);
                    startActivity(pictureQuizIntent);
                }
                if(idQuiz.getText().toString().equals("Speed Run Quiz"))
                {
                    Intent speedRunQuizIntent = new Intent (confirmQuiz.this, speedRunQuizActivity.class);
                    startActivity(speedRunQuizIntent);
                }
                if(idQuiz.getText().toString().equals("Head to Head Quiz"))
                {
                    Intent headtoheadQuizIntent = new Intent (confirmQuiz.this, headtoheadQuizActivity.class);
                    startActivity(headtoheadQuizIntent);
                }
                if(idQuiz.getText().toString().equals("History Quiz"))
                {
                    Intent historyQuizIntent = new Intent (confirmQuiz.this, historyQuizActivity.class);
                    startActivity(historyQuizIntent);
                }
                if(idQuiz.getText().toString().equals("Science Quiz"))
                {
                    Intent scienceQuizIntent = new Intent (confirmQuiz.this, scienceQuizActivity.class);
                    startActivity(scienceQuizIntent);
                }
                if(idQuiz.getText().toString().equals("Sports Quiz"))
                {
                    Intent sportsQuizIntent = new Intent (confirmQuiz.this, sportsQuizActivity.class);
                    startActivity(sportsQuizIntent);
                }
                if(idQuiz.getText().toString().equals("Film/TV Quiz"))
                {
                    Intent filmTVQuizIntent = new Intent (confirmQuiz.this, filmTVQuizActivity.class);
                    startActivity(filmTVQuizIntent);
                }
                if(idQuiz.getText().toString().equals("Geography Quiz"))
                {
                    Intent geographyQuizIntent = new Intent (confirmQuiz.this, geographyQuizActivity.class);
                    startActivity(geographyQuizIntent);
                }
                if(idQuiz.getText().toString().equals("Music Quiz"))
                {
                    Intent musicQuizIntent = new Intent (confirmQuiz.this, musicQuizActivity.class);
                    startActivity(musicQuizIntent);
                }

            }
        });




    }


}