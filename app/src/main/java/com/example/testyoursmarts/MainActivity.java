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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.navigation.ui.AppBarConfiguration;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN;

public class MainActivity extends AppCompatActivity {

    public static FirebaseFirestore g_firestore;
    private List <CategoryModel> catList;
    private GridView catView;
    private AppBarConfiguration mAppBarConfiguration;
    private BottomNavigationView bottomNavigationView;
    private FrameLayout main_frame;
    private String userName = "GUEST";
    private TextView whosLogged;
    private String searchName;
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

                        case R.id.nav_account:
                            Intent intentAccount = new Intent(MainActivity.this, account_page.class);
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
        g_firestore = FirebaseFirestore.getInstance();
        getUserName();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Test Your Smarts: Select your Quiz");
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
                    String gkTest = "This is a General Knowledge Quiz - You will have 10 random questions to answer, you will have 15 seconds " +
                            "to answer each question and you will be given 4 potential answers";
                    String gkID = "General Knowledge Quiz";
                    Intent gkIntent = new Intent(MainActivity.this, confirmQuiz.class);
                    gkIntent.putExtra("Topic", gkTest);
                    gkIntent.putExtra("ID",gkID);
                    startActivity(gkIntent);
                }
                if(position == 1)
                {
                    String pictureQuiz = "This is a Picture Quiz - You will be shown 10 pictures which will have a question below it.  You will have 15 seconds " +
                            "to answer each question and you will be given 4 potential answers";
                    String pQID = "Picture Quiz";
                    Intent pictureQIntent = new Intent(MainActivity.this, confirmQuiz.class);
                    pictureQIntent.putExtra("Topic", pictureQuiz);
                    pictureQIntent.putExtra("ID", pQID);
                    startActivity(pictureQIntent);
                }
                if(position == 2)
                {
                    String speedRunQuiz = "This is a speed Run Quiz - You will have 3 minutes to answer as many questions as you possibly can. You will not be  " +
                            "timed for each question and you will be given 4 potential answers";
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
                    String headToHeadQuiz = "This is a head to head Quiz - Each player will have 10 questions to answer, each will player will have 15 seconds " +
                            "to answer each question and there will be 4 possible answers.  Player 1 will get their question first, then Player 2 will get their question " +
                            "and so on until each player has answered 20 Questions";
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
        catList.add(new CategoryModel("1", "General Knowledge Quiz"));
        catList.add(new CategoryModel("2", "Picture Quiz"));
        catList.add(new CategoryModel("3", "Speed Run Quiz"));
        catList.add(new CategoryModel("4", "Topic Quiz"));
        catList.add(new CategoryModel("5", "Head to Head Quiz"));
    }
    private void getUserName()
    {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentuserEmail = firebaseUser.getEmail();
        Task<QuerySnapshot> easyRef =
                g_firestore.collection("USERS").
                        whereEqualTo("EMAIL_ID", currentuserEmail)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                       searchName = document.getString("NAME");
                                    }
                                } else {
                                   searchName = "GUEST";
                                }
                            }
                        });
    }
}