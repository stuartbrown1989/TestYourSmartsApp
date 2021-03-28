package com.example.testyoursmarts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.ui.AppBarConfiguration;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageTask;


import org.w3c.dom.Text;

import java.text.DecimalFormat;

public class resultPageActivity extends AppCompatActivity {
    private String userName;
    private FirebaseAuth mAuth;
    public static FirebaseFirestore g_firestore;
    private TextView resultScore, GUESTSCORE, textViewUserName, quizName, whoWon;
    private Button btnQuizSelect;
    private AppBarConfiguration mAppBarConfiguration;
    private BottomNavigationView bottomNavigationView;
    private FrameLayout main_frame;
    private TextView averageTime;
    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemReselectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    switch (menuItem.getItemId()) {
                        case R.id.nav_home:
                            Intent intentHome = new Intent(resultPageActivity.this, MainActivity.class);
                            startActivity(intentHome);
                            return true;

                        case R.id.nav_leaderboard:
                            Intent intentLeaderboard= new Intent(resultPageActivity.this, leaderboardChoiceActivity.class);
                            startActivity(intentLeaderboard);
                            return true;

                        case R.id.nav_account:
                            Intent intentAccount = new Intent(resultPageActivity.this, account_page.class);
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
    private FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_page);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Test Your Smarts: Results");
        bottomNavigationView = findViewById(R.id.top_nav_bar);
        main_frame = findViewById(R.id.main_frame);
        btnQuizSelect = findViewById(R.id.button_playQuiz);
        whoWon = findViewById(R.id.text_well_done);
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemReselectedListener);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        resultScore = findViewById(R.id.text_view_new_score);
        GUESTSCORE = findViewById(R.id.Resulttext_view_guest_score);
        quizName = findViewById(R.id.result_quizID);
        averageTime = findViewById(R.id.show_average);

        //Accessing firestore
        g_firestore = FirebaseFirestore.getInstance();
        String currentuserEmail = firebaseUser.getEmail();
        //Getting Users Name
        Task<QuerySnapshot> easyRef =
                g_firestore.collection("USERS").
                        whereEqualTo("EMAIL_ID", currentuserEmail)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        userName = document.getString("NAME");
                                    }
                                    //If user is a Guest/Anonymously signed in
                                    if(userName == null)
                                    {
                                        userName = "Player 1";
                                    }
                                    Intent intent = getIntent();
                                    int scoreValue= intent.getIntExtra("SCORE", 0);
                                    int guestScoreValue = intent.getIntExtra("GUESTSCORE", -1);
                                    double Time = intent.getDoubleExtra("TIME", 0.0);
                                    DecimalFormat form = new DecimalFormat("0.0");
                                    String quizID = intent.getStringExtra("QUIZ");

                                    resultScore.setText(userName + "\nSCORE: " + Integer.toString(scoreValue));
                                    quizName.setText("Quiz Name:\n" + quizID);
                                    averageTime.setText("Average Time to answer a Question:\n" + form.format(Time) + " Seconds");

                                    //If the Guest score is greater than its default value, show the Guest Score
                                        //As the Head to Head quiz is the only quiz that passes an actual value, it means its the only quiz that will show this
                                        if(guestScoreValue > -1)
                                        {
                                        GUESTSCORE.setText("Player 2:\n" +"SCORE: " + Integer.toString(guestScoreValue));
                                        averageTime.setText("");
                                            if(scoreValue > guestScoreValue)
                                            {
                                                whoWon.setText(userName + " Wins");
                                            }
                                            if(guestScoreValue > scoreValue)
                                            {
                                                whoWon.setText("Player 2 Wins");
                                            }
                                            if(guestScoreValue == scoreValue)
                                            {
                                                whoWon.setText("It's a Draw!");
                                            }
                                        }
                                    //Otherwise show nothing
                                        else{
                                        GUESTSCORE.setText("");
                                            }

                                } else {

                                }
                            }
                        });

        btnQuizSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnToQuizzes = new Intent(resultPageActivity.this, MainActivity.class);
                startActivity(returnToQuizzes);
            }
        });
    }
    //Searches Collection USERS - Which has basic information of the user, and grabs the currently logged in users name


}