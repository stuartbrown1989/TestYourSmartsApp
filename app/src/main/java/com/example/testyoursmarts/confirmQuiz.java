package com.example.testyoursmarts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.ui.AppBarConfiguration;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class confirmQuiz extends AppCompatActivity {

    private TextView idQuiz, difficultySelected;
    private TextView quizDescription;
    private Button btnStartQuiz;
    private Spinner spinner;
    private static final String[] difficulties = {"Easy", "Medium", "Hard"};


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

                        case R.id.nav_account:
                            Intent intentAccount = new Intent(confirmQuiz.this, account_page.class);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_quiz);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Test Your Smarts: Confirm Your Quiz");
        bottomNavigationView = findViewById(R.id.top_nav_bar);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userEmail = firebaseUser.getEmail();
        //If the current logged in user is a Guest, do this
        main_frame = findViewById(R.id.main_frame);
        //Creation of an animation when the Start Quiz button is pressed
        final AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemReselectedListener);
        difficultySelected = findViewById(R.id.text_view_Difficulty_Selected);
        spinner = findViewById(R.id.spinner_difficulty);
        ArrayAdapter<String>adapter = new ArrayAdapter<String>(confirmQuiz.this, android.R.layout.simple_spinner_item, difficulties);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);




        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position){
                    case 0:
                        difficultySelected.setText("Easy");
                        break;
                    case 1:
                        difficultySelected.setText("Medium");
                        break;
                    case 2:
                        difficultySelected.setText("Hard");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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
                v.startAnimation(buttonClick);
                //GK QUIZ CHOICE
                if(idQuiz.getText().toString().equals("General Knowledge Quiz"))
                {
                    Intent gkQuizIntent =new Intent (confirmQuiz.this, topicQuizActivity.class);
                    gkQuizIntent.putExtra("QuizTopic", "GK");
                    if(difficultySelected.getText() == "Easy")
                    {
                        gkQuizIntent.putExtra("Difficulty", "Easy");
                    }
                    if(difficultySelected.getText() == "Medium")
                    {
                        gkQuizIntent.putExtra("Difficulty", "Medium");
                    }
                    if(difficultySelected.getText() == "Hard")
                    {
                        gkQuizIntent.putExtra("Difficulty", "Hard");
                    }

                    startActivity(gkQuizIntent);
                }
            //PICTURE QUIZ CHOICE
                if(idQuiz.getText().toString().equals("Picture Quiz"))
                {
                    Intent pictureQuizIntent = new Intent (confirmQuiz.this, pictureQuizActivity.class);
                    if(difficultySelected.getText() == "Easy")
                    {
                        pictureQuizIntent.putExtra("Difficulty", "Easy");
                    }
                    if(difficultySelected.getText() == "Medium")
                    {
                        pictureQuizIntent.putExtra("Difficulty", "Medium");
                    }
                    if(difficultySelected.getText() == "Hard")
                    {
                        pictureQuizIntent.putExtra("Difficulty", "Hard");
                    }

                    startActivity(pictureQuizIntent);
                }
                //SPEED RUN QUIZ CHOICE
                if(idQuiz.getText().toString().equals("Speed Run Quiz"))
                {
                    Intent speedRunQuizIntent = new Intent (confirmQuiz.this, speedRunQuizActivity.class);
                    if(difficultySelected.getText() == "Easy")
                    {
                        speedRunQuizIntent.putExtra("Difficulty", "Easy");
                    }
                    if(difficultySelected.getText() == "Medium")
                    {
                        speedRunQuizIntent.putExtra("Difficulty", "Medium");
                    }
                    if(difficultySelected.getText() == "Hard")
                    {
                        speedRunQuizIntent.putExtra("Difficulty", "Hard");
                    }

                    startActivity(speedRunQuizIntent);
                }
                //HEAD TO HEAD QUIZ CHOICE
                if(idQuiz.getText().toString().equals("Head to Head Quiz"))
                {
                    Intent headtoheadQuizIntent = new Intent (confirmQuiz.this, headtoheadQuizActivity.class);
                    if(difficultySelected.getText() == "Easy")
                    {
                        headtoheadQuizIntent .putExtra("Difficulty", "Easy");
                    }
                    if(difficultySelected.getText() == "Medium")
                    {
                        headtoheadQuizIntent .putExtra("Difficulty", "Medium");
                    }
                    if(difficultySelected.getText() == "Hard")
                    {
                        headtoheadQuizIntent .putExtra("Difficulty", "Hard");
                    }
                    startActivity(headtoheadQuizIntent);
                }
                //HISTORY QUIZ CHOICE
                if(idQuiz.getText().toString().equals("History Quiz"))
                {
                    Intent historyQuizIntent = new Intent (confirmQuiz.this, topicQuizActivity.class);
                    historyQuizIntent.putExtra("QuizTopic", "History");
                    if(difficultySelected.getText() == "Easy")
                    {
                        historyQuizIntent.putExtra("Difficulty", "Easy");
                    }
                    if(difficultySelected.getText() == "Medium")
                    {
                        historyQuizIntent.putExtra("Difficulty", "Medium");
                    }
                    if(difficultySelected.getText() == "Hard")
                    {
                        historyQuizIntent.putExtra("Difficulty", "Hard");
                    }

                    startActivity(historyQuizIntent);
                }
                //SCIENCE QUIZ CHOICE
                if(idQuiz.getText().toString().equals("Science Quiz"))
                {
                    Intent scienceQuizIntent = new Intent (confirmQuiz.this, topicQuizActivity.class);
                    scienceQuizIntent.putExtra("QuizTopic", "Science");
                    if(difficultySelected.getText() == "Easy")
                    {
                        scienceQuizIntent.putExtra("Difficulty", "Easy");
                    }
                    if(difficultySelected.getText() == "Medium")
                    {
                        scienceQuizIntent.putExtra("Difficulty", "Medium");
                    }
                    if(difficultySelected.getText() == "Hard")
                    {
                        scienceQuizIntent.putExtra("Difficulty", "Hard");
                    }
                    startActivity(scienceQuizIntent);
                }
                //SPORTS QUIZ CHOICE
                if(idQuiz.getText().toString().equals("Sports Quiz"))
                {
                    Intent sportsQuizIntent = new Intent (confirmQuiz.this, topicQuizActivity.class);
                    sportsQuizIntent.putExtra("QuizTopic", "Sports");
                    if(difficultySelected.getText() == "Easy")
                    {
                        sportsQuizIntent.putExtra("Difficulty", "Easy");
                    }
                    if(difficultySelected.getText() == "Medium")
                    {
                        sportsQuizIntent.putExtra("Difficulty", "Medium");
                    }
                    if(difficultySelected.getText() == "Hard")
                    {
                        sportsQuizIntent.putExtra("Difficulty", "Hard");
                    }
                    startActivity(sportsQuizIntent);
                }
                //FILM/TV QUIZ CHOICE
                if(idQuiz.getText().toString().equals("Film/TV Quiz"))
                {
                    Intent filmTVQuizIntent = new Intent (confirmQuiz.this, topicQuizActivity.class);
                    filmTVQuizIntent.putExtra("QuizTopic", "FilmTV");
                    if(difficultySelected.getText() == "Easy")
                    {
                        filmTVQuizIntent.putExtra("Difficulty", "Easy");
                    }
                    if(difficultySelected.getText() == "Medium")
                    {
                        filmTVQuizIntent.putExtra("Difficulty", "Medium");
                    }
                    if(difficultySelected.getText() == "Hard")
                    {
                        filmTVQuizIntent.putExtra("Difficulty", "Hard");
                    }
                    startActivity(filmTVQuizIntent);
                }
                //GEOGRAPHY QUIZ CHOICE
                if(idQuiz.getText().toString().equals("Geography Quiz"))
                {

                    Intent geographyQuizIntent = new Intent (confirmQuiz.this, topicQuizActivity.class);
                    geographyQuizIntent.putExtra("QuizTopic", "Geography");
                    if(difficultySelected.getText() == "Easy")
                    {
                        geographyQuizIntent.putExtra("Difficulty", "Easy");
                    }
                    if(difficultySelected.getText() == "Medium")
                    {
                        geographyQuizIntent.putExtra("Difficulty", "Medium");
                    }
                    if(difficultySelected.getText() == "Hard")
                    {
                        geographyQuizIntent.putExtra("Difficulty", "Hard");
                    }
                    startActivity(geographyQuizIntent);
                }
                //MUSIC QUIZ CHOICE
                if(idQuiz.getText().toString().equals("Music Quiz"))
                {
                    Intent musicQuizIntent = new Intent (confirmQuiz.this, topicQuizActivity.class);
                    musicQuizIntent .putExtra("QuizTopic", "Music");
                    if(difficultySelected.getText() == "Easy")
                    {
                        musicQuizIntent .putExtra("Difficulty", "Easy");
                    }
                    if(difficultySelected.getText() == "Medium")
                    {
                        musicQuizIntent .putExtra("Difficulty", "Medium");
                    }
                    if(difficultySelected.getText() == "Hard")
                    {
                        musicQuizIntent .putExtra("Difficulty", "Hard");
                    }
                    startActivity(musicQuizIntent);
                }

            }
        });




    }


}