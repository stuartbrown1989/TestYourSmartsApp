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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.Collections;
import java.util.Objects;

import static com.example.testyoursmarts.dbQuery.userScoreList;

public class leaderboardTopicQuiz extends AppCompatActivity {

    private TextView userView, scoreDifficultyView;
    public static FirebaseFirestore g_firestore;
    private String quizType;
    private Spinner spinner;
    private static final String[] Topics = {"Select Topic", "History", "Science", "Sports", "FilmTV", "Geography", "Music"};
    private AppBarConfiguration mAppBarConfiguration;
    private BottomNavigationView bottomNavigationView;
    private FrameLayout main_frame;
    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemReselectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    switch (menuItem.getItemId()) {
                        case R.id.nav_home:
                            Intent intentHome = new Intent(leaderboardTopicQuiz.this, MainActivity.class);
                            startActivity(intentHome);
                            return true;

                        case R.id.nav_leaderboard:
                            Intent intentleaderB = new Intent(leaderboardTopicQuiz.this,leaderboardChoiceActivity.class);
                            startActivity(intentleaderB);
                            return true;

                        case R.id.nav_account:
                            Intent intentAccount = new Intent(leaderboardTopicQuiz.this, account_page.class);
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
        setContentView(R.layout.activity_leaderboard_topic_quiz);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Test Your Smarts: LeaderBoards");
        bottomNavigationView = findViewById(R.id.top_nav_bar);
        main_frame = findViewById(R.id.main_frame);
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemReselectedListener);
        g_firestore = FirebaseFirestore.getInstance();
        userView = findViewById(R.id.Topicleader_leaderboard_view_username);
        scoreDifficultyView = findViewById(R.id.Topicleader_leaderboard_view_score);
        spinner = findViewById(R.id.spinner_leaderboard_topic);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(leaderboardTopicQuiz.this, android.R.layout.simple_spinner_item, Topics);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        //Clears any existing data
        userScoreList.clear();
        userScoreList.removeAll(Collections.emptyList());

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch(position){
                    case 0:

                        break;
                    case 1:
                        userScoreList.clear();
                        userScoreList.removeAll(Collections.emptyList());
                        getHistoryLeaderBoard();
                        break;
                    case 2:
                        userScoreList.clear();
                        userScoreList.removeAll(Collections.emptyList());
                        getScienceLeaderBoard();
                        break;
                    case 3:
                        userScoreList.clear();
                        userScoreList.removeAll(Collections.emptyList());
                        getSportsLeaderBoard();
                        break;
                    case 4:
                        userScoreList.clear();
                        userScoreList.removeAll(Collections.emptyList());
                        getFilmTVLeaderBoard();
                        break;
                    case 5:
                        userScoreList.clear();
                        userScoreList.removeAll(Collections.emptyList());
                        getGeographyLeaderBoard();
                        break;
                    case 6:
                        userScoreList.clear();
                        userScoreList.removeAll(Collections.emptyList());
                        getMusicLeaderBoard();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    //Function to get all records and put them into an array list
    private void getAllRecords()
    {

        userView.setText("");
        scoreDifficultyView.setText("");
        String userinfo = "";
        String scoreinfo = "";
        for(userScoresModel usr : userScoreList)
        {
            String userName = usr.getUserName();
            String QuizType = usr.getQuiztype();
            int Score = usr.getScore();
            String Difficulty = usr.getDifficulty();
            userinfo =  userinfo +  userName + "\n____________________________\n";
            scoreinfo = scoreinfo  + QuizType + " - " + Score + " - " + Difficulty + "\n________________________\n";
        }
        userView.setText(userinfo);
        scoreDifficultyView.setText(scoreinfo);

    }

    //Gets all records of this leaderboard on firestore - Also puts the order of the records that come out in descending order base on the score field(10, 9, 8 etc)
    private void getHistoryLeaderBoard()
    {
        Task<QuerySnapshot> easyRef =
                g_firestore.collection("Leaderboards").document("Topic").collection("History")
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

    private void getScienceLeaderBoard()
    {
        Task<QuerySnapshot> easyRef =
                g_firestore.collection("Leaderboards").document("Topic").collection("Science")
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

    private void getSportsLeaderBoard()
    {
        Task<QuerySnapshot> easyRef =
                g_firestore.collection("Leaderboards").document("Topic").collection("Sports")
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

    private void getFilmTVLeaderBoard()
    {
        Task<QuerySnapshot> easyRef =
                g_firestore.collection("Leaderboards").document("Topic").collection("FilmTV")
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
    private void getGeographyLeaderBoard()
    {
        Task<QuerySnapshot> easyRef =
                g_firestore.collection("Leaderboards").document("Topic").collection("Geography")
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

    private void getMusicLeaderBoard()
    {
        Task<QuerySnapshot> easyRef =
                g_firestore.collection("Leaderboards").document("Topic").collection("Music")
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