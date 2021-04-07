package com.example.testyoursmarts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.ui.AppBarConfiguration;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MenuItem;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.example.testyoursmarts.dbQuery.g_question_list;
import static com.example.testyoursmarts.dbQuery.userScoreList;

public class leaderboardgk_Quiz extends AppCompatActivity {

    private TextView userView, scoreDifficultyView;
    public static FirebaseFirestore g_firestore;

    private AppBarConfiguration mAppBarConfiguration;
    private BottomNavigationView bottomNavigationView;
    private FrameLayout main_frame;
    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemReselectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    switch (menuItem.getItemId()) {
                        case R.id.nav_home:
                            Intent intentHome = new Intent(leaderboardgk_Quiz.this, MainActivity.class);
                            startActivity(intentHome);
                            return true;

                        case R.id.nav_leaderboard:
                            Intent intentleaderB = new Intent(leaderboardgk_Quiz.this,leaderboardChoiceActivity.class);
                            startActivity(intentleaderB);
                            return true;

                        case R.id.nav_account:
                            Intent intentAccount = new Intent(leaderboardgk_Quiz.this, account_page.class);
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
        setContentView(R.layout.activity_leaderboardgk__quiz);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Test Your Smarts: LeaderBoards");
        bottomNavigationView = findViewById(R.id.top_nav_bar);
        main_frame = findViewById(R.id.main_frame);
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemReselectedListener);
        g_firestore = FirebaseFirestore.getInstance();
        userView = findViewById(R.id.gkleader_leaderboard_view_username);
        scoreDifficultyView = findViewById(R.id.gkleader_leaderboard_view_score);
        userScoreList.clear();
        userScoreList.removeAll(Collections.emptyList());
        getGKLeaderBoard();

    }


//Formats the data in the array list userScoreList into text fields - Uses the userScoresModel class to get these values
private void getAllRecords()
{
    String userinfo = "";
    String scoreinfo = "";
    for(userScoresModel usr : userScoreList)
    {
        String userName = usr.getUserName();
        int Score = usr.getScore();
        String Difficulty = usr.getDifficulty();
        userinfo =  userinfo +  userName + "\n_________________________________\n";
        scoreinfo = scoreinfo  + Score + " - " + Difficulty + "\n_____________________________\n";
    }
    userView.setText(userinfo);
    scoreDifficultyView.setText(scoreinfo);

}

    //Gets all records of this leaderboard on firestore - Also puts the order of the records that come out in descending order base on the score field(10, 9, 8 etc)
    private void getGKLeaderBoard()
    {
        Task<QuerySnapshot> easyRef =
                g_firestore.collection("Leaderboards").document("General Knowledge").collection("Scores")
                        .orderBy("Score", Query.Direction.DESCENDING)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                //Get collection info and store it to the list
                                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                                    userScoreList.add(new userScoresModel(
                                            doc.getString("Username"),
                                            doc.getString("Difficulty"),
                                            doc.getString("QuizType"),
                                            Objects.requireNonNull(doc.getLong("Score")).intValue()
                                    ));
                                }
                                getAllRecords();
                            }

                        });
    }
}