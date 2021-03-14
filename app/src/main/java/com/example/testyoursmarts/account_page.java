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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.example.testyoursmarts.dbQuery.g_question_list;
import static com.example.testyoursmarts.dbQuery.statisticTime;
import static com.example.testyoursmarts.dbQuery.userScoreList;

public class account_page extends AppCompatActivity {


    public static FirebaseFirestore g_firestore;
    private String quizType;
    private Spinner spinner;
    private String userEmail;
    private String guestEmail = "123@hotmail.com";
    private static final String[] options = {
            "Select Quiz Type",
            "General Knowledge",
            "Picture",
            "Speed Run",
            "Topic - FilmTV",
            "Topic - Geography",
            "Topic - Music",
            "Topic - History",
            "Topic - Science",
            "Topic - Sports",
            "All Quizzes"};
    private AppBarConfiguration mAppBarConfiguration;
    private Button writingEasyAverage, writingMediumAverage, writingHardAverage;
    private DocumentReference alleasyref;
    private  DocumentReference allmediumref;
    private DocumentReference allhardref;
    private Double
        checkstoredaverage,
        checkstoredFilmAverage,
        checkstoredGKAverage,
        checkstoredGeoAverage,
        checkstoredHistoryAverage,
        checkstoredMusicAverage,
        checkstoredPictureAverage,
        checkstoredScienceAverage,
        checkstoredSportsAverage,
        newoverallAverage;

    private FirebaseAuth firebaseAuth;
    private TextView easytime, mediumtime, hardtime;
    private Double
            checkEasy, checkMedium, checkHard,
            checkFilmTVEasy, checkFilmTVMedium, checkFilmTVHard,
            checkGKEasy, checkGKMedium , checkGKHard,
            checkGeoEasy , checkGeoMedium, checkGeoHard,
            checkHistoryEasy, checkHistoryMedium, checkHistoryHard,
            checkMusicEasy, checkMusicMedium, checkMusicHard,
            checkPictureEasy, checkPictureMedium, checkPictureHard,
            checkScienceEasy, checkScienceMedium, checkScienceHard,
            checkSportsEasy, checkSportsMedium, checkSportsHard,
            checkSpeedEasy, checkSpeedMedium, checkSpeedHard;
    private BottomNavigationView bottomNavigationView;
    private FrameLayout main_frame;
    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemReselectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    switch (menuItem.getItemId()) {
                        case R.id.nav_home:
                            Intent intentHome = new Intent(account_page.this, MainActivity.class);
                            startActivity(intentHome);
                            return true;

                        case R.id.nav_leaderboard:
                            Intent intentleaderB = new Intent(account_page.this, leaderboardChoiceActivity.class);
                            startActivity(intentleaderB);
                            return true;

                        case R.id.nav_account:
                            Intent intentAccount = new Intent(account_page.this, account_page.class);
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
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userEmail = firebaseUser.getEmail();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_page);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Test Your Smarts: Statistics");
        bottomNavigationView = findViewById(R.id.top_nav_bar);
        main_frame = findViewById(R.id.main_frame);
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemReselectedListener);
        writingEasyAverage = findViewById(R.id.write_Easyaverage);
        writingMediumAverage = findViewById(R.id.write_Mediumaverage);
        writingHardAverage = findViewById(R.id.write_Hardaverage);
        g_firestore = FirebaseFirestore.getInstance();
        easytime = findViewById(R.id.account_easy_value);
        mediumtime = findViewById(R.id.account_medium_value);
        hardtime = findViewById(R.id.account_hard_value);
        spinner = findViewById(R.id.spinner_statistics);

        //Write Average buttons that are no longer visible - Used to ensure the average was right, as there was some issues during testing that are working now
        writingEasyAverage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeEasyAveragetoDB();
            }
        });
        writingMediumAverage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeMediumAveragetoDB();
            }
        });
        writingHardAverage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeHardAveragetoDB();
            }
        });
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(account_page.this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        //Initialize all times from function
        getcombinedTime();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DecimalFormat form = new DecimalFormat("0.0");
                //Based on selected item from Dropdown, setText to the appropriate values
                switch(position){

                    case 0:

                        easytime.setText("Select Quiz Type");
                        mediumtime.setText("Select Quiz Type");
                        hardtime.setText("Select Quiz Type");
                        break;
                    case 1:
                        guestLoggedinMsg();
                        easytime.setText(form.format(checkGKEasy));
                        mediumtime.setText(form.format(checkGKMedium));
                        hardtime.setText(form.format(checkGKHard));
                        break;
                    case 2:
                        guestLoggedinMsg();
                        easytime.setText(form.format(checkPictureEasy));
                        mediumtime.setText(form.format(checkPictureMedium));
                        hardtime.setText(form.format(checkPictureHard));
                        break;
                    case 3:
                        guestLoggedinMsg();
                        easytime.setText(form.format(checkSpeedEasy));
                        mediumtime.setText(form.format(checkSpeedMedium));
                        hardtime.setText(form.format(checkSpeedHard));
                        break;
                    case 4:
                        guestLoggedinMsg();
                        easytime.setText(form.format(checkFilmTVEasy));
                        mediumtime.setText(form.format(checkFilmTVMedium));
                        hardtime.setText(form.format(checkFilmTVHard));
                        break;
                    case 5:
                        guestLoggedinMsg();
                        easytime.setText(form.format(checkGeoEasy));
                        mediumtime.setText(form.format(checkGeoMedium));
                        hardtime.setText(form.format(checkGeoHard));
                        break;
                    case 6:
                        guestLoggedinMsg();
                        easytime.setText(form.format(checkMusicEasy));
                        mediumtime.setText(form.format(checkMusicMedium));
                        hardtime.setText(form.format(checkMusicHard));
                        break;
                    case 7:
                        guestLoggedinMsg();
                        easytime.setText(form.format(checkHistoryEasy));
                        mediumtime.setText(form.format(checkHistoryMedium));
                        hardtime.setText(form.format(checkHistoryHard));
                        break;
                    case 8:
                        guestLoggedinMsg();
                        easytime.setText(form.format(checkScienceEasy));
                        mediumtime.setText(form.format(checkScienceMedium));
                        hardtime.setText(form.format(checkScienceHard));
                        break;
                    case 9:
                        guestLoggedinMsg();
                        easytime.setText(form.format(checkSportsEasy));
                        mediumtime.setText(form.format(checkSportsMedium));
                        hardtime.setText(form.format(checkSportsHard));
                        break;
                    case 10:
                        guestLoggedinMsg();
                        easytime.setText(form.format(checkEasy));
                        mediumtime.setText(form.format(checkMedium));
                        hardtime.setText(form.format(checkHard));
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                easytime.setText("Select Quiz Type");
                mediumtime.setText("Select Quiz Type");
                hardtime.setText("Select Quiz Type");
            }
        });
    }
//Function getting overall average from Firestore
    private void getcombinedTime() {
        //If userEmail upon checking Current User is null, then it is a guest login, so change the email address to a default one, so all fields will be 0
if(userEmail == null)
{
    userEmail = "123@hotmail.com";
}
       if(!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches())
        {
            alleasyref = g_firestore.collection("Statistics").document("Easy").collection("Users").document(guestEmail);
        }
        if(Patterns.EMAIL_ADDRESS.matcher(userEmail).matches())
        {
            alleasyref = g_firestore.collection("Statistics").document("Easy").collection("Users").document(userEmail);
        }
        //Checking Statistics with document name of users email address
          alleasyref.get()
                  .addOnFailureListener(new OnFailureListener() {
                      @Override
                      public void onFailure(@NonNull Exception e) {
                          checkSpeedEasy = 0.0;
                          checkSportsEasy = 0.0;
                          checkScienceEasy = 0.0;
                          checkPictureEasy = 0.0;
                          checkMusicEasy = 0.0;
                          checkHistoryEasy = 0.0;
                          checkGeoEasy = 0.0;
                          checkGKEasy = 0.0;
                          checkFilmTVEasy = 0.0;
                          checkEasy = 0.0;
                          Toast.makeText(account_page.this, "Login with an account to record Statistics", Toast.LENGTH_SHORT).show();
                      }
                  })
                  .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                      @Override
                      public void onSuccess(DocumentSnapshot snapshot) {
                          //If document with email address as id exist - Show average stored
                          if (snapshot.exists()) {
                              checkEasy = snapshot.getDouble("Average Time");
                              checkFilmTVEasy = snapshot.getDouble("FilmTV Average");
                              checkGKEasy = snapshot.getDouble("GK Average");
                              checkGeoEasy = snapshot.getDouble("Geography Average");
                              checkHistoryEasy = snapshot.getDouble("History Average");
                              checkMusicEasy = snapshot.getDouble("Music Average");
                              checkPictureEasy = snapshot.getDouble("Picture Average");
                              checkScienceEasy = snapshot.getDouble("Science Average");
                              checkSportsEasy = snapshot.getDouble("Sports Average");
                              checkSpeedEasy = snapshot.getDouble("Speed Run Average");
                          }
                          //If any Topic is non existing in firestore, just set the value to 0.0
                          //Has to be given a formatted value, as the textview is formatted to this, and it will throw an exception if it is null
                          if (checkSpeedEasy == null) {
                              checkSpeedEasy = 0.0;
                          }
                          if (checkEasy == null) {
                              checkEasy = 0.0;
                          }
                          if (checkFilmTVEasy == null) {
                              checkFilmTVEasy = 0.0;
                          }
                          if (checkGKEasy == null) {
                              checkGKEasy = 0.0;
                          }
                          if (checkHistoryEasy == null) {
                              checkHistoryEasy = 0.0;
                          }
                          if (checkGeoEasy == null) {
                              checkGeoEasy = 0.0;
                          }
                          if (checkMusicEasy == null) {
                              checkMusicEasy = 0.0;
                          }
                          if (checkPictureEasy == null) {
                              checkPictureEasy = 0.0;
                          }
                          if (checkScienceEasy == null) {
                              checkScienceEasy = 0.0;
                          }
                          if (checkSportsEasy == null) {
                              checkSportsEasy = 0.0;
                          }
                          //If no doc exists, value is 0.0
                          if (!snapshot.exists()) {
                              checkSpeedEasy = 0.0;
                              checkSportsEasy = 0.0;
                              checkScienceEasy = 0.0;
                              checkPictureEasy = 0.0;
                              checkMusicEasy = 0.0;
                              checkHistoryEasy = 0.0;
                              checkGeoEasy = 0.0;
                              checkGKEasy = 0.0;
                              checkFilmTVEasy = 0.0;
                              checkEasy = 0.0;
                          }
                      }
                  });
        if(!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches())
        {
            allmediumref = g_firestore.collection("Statistics").document("Medium").collection("Users").document(guestEmail);
        }
        if(Patterns.EMAIL_ADDRESS.matcher(userEmail).matches())
        {
            allmediumref = g_firestore.collection("Statistics").document("Medium").collection("Users").document(userEmail);
        }
          allmediumref.get()
                  .addOnFailureListener(new OnFailureListener() {
                      @Override
                      public void onFailure(@NonNull Exception e) {
                          checkSpeedMedium = 0.0;
                          checkSportsMedium = 0.0;
                          checkScienceMedium = 0.0;
                          checkPictureMedium = 0.0;
                          checkMusicMedium = 0.0;
                          checkHistoryMedium = 0.0;
                          checkGeoMedium = 0.0;
                          checkGKMedium = 0.0;
                          checkFilmTVMedium = 0.0;
                          checkMedium = 0.0;
                          Toast.makeText(account_page.this, "Login with an account to record Statistics", Toast.LENGTH_SHORT).show();
                      }
                  })
                  .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                      @Override
                      public void onSuccess(DocumentSnapshot snapshot) {
                          //If document with email address as id exist - Show average stored
                          if (snapshot.exists()) {
                              checkMedium = snapshot.getDouble("Average Time");
                              checkFilmTVMedium = snapshot.getDouble("FilmTV Average");
                              checkGKMedium = snapshot.getDouble("GK Average");
                              checkGeoMedium = snapshot.getDouble("Geography Average");
                              checkHistoryMedium = snapshot.getDouble("History Average");
                              checkMusicMedium = snapshot.getDouble("Music Average");
                              checkPictureMedium = snapshot.getDouble("Picture Average");
                              checkScienceMedium = snapshot.getDouble("Science Average");
                              checkSportsMedium = snapshot.getDouble("Sports Average");
                              checkSpeedMedium = snapshot.getDouble("Speed Run Average");
                          }
                          //If any Topic is non existing in firestore, just set the value to 0.0
                          //Has to be given a formatted value, as the textview is formatted to this, and it will throw an exception if it is null
                          if (checkSpeedMedium == null) {
                              checkSpeedMedium = 0.0;
                          }
                          if (checkMedium == null) {
                              checkMedium = 0.0;
                          }
                          if (checkFilmTVMedium == null) {
                              checkFilmTVMedium = 0.0;
                          }
                          if (checkGKMedium == null) {
                              checkGKMedium = 0.0;
                          }
                          if (checkHistoryMedium == null) {
                              checkHistoryMedium = 0.0;
                          }
                          if (checkGeoMedium == null) {
                              checkGeoMedium = 0.0;
                          }
                          if (checkMusicMedium == null) {
                              checkMusicMedium = 0.0;
                          }
                          if (checkPictureMedium == null) {
                              checkPictureMedium = 0.0;
                          }
                          if (checkScienceMedium == null) {
                              checkScienceMedium = 0.0;
                          }
                          if (checkSportsMedium == null) {
                              checkSportsMedium = 0.0;
                          }
                          //If no doc exists, value is 0.0
                          if (!snapshot.exists()) {
                              checkSpeedMedium = 0.0;
                              checkSportsMedium = 0.0;
                              checkScienceMedium = 0.0;
                              checkPictureMedium = 0.0;
                              checkMusicMedium = 0.0;
                              checkHistoryMedium = 0.0;
                              checkGeoMedium = 0.0;
                              checkGKMedium = 0.0;
                              checkFilmTVMedium = 0.0;
                              checkMedium = 0.0;
                          }
                      }
                  });
        if(!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches())
        {
            allhardref = g_firestore.collection("Statistics").document("Hard").collection("Users").document(guestEmail);
        }
        if(Patterns.EMAIL_ADDRESS.matcher(userEmail).matches())
        {
            allhardref = g_firestore.collection("Statistics").document("Hard").collection("Users").document(userEmail);
        }
          allhardref.get()
                  .addOnFailureListener(new OnFailureListener() {
                      @Override
                      public void onFailure(@NonNull Exception e) {
                          checkSpeedHard = 0.0;
                          checkSportsHard = 0.0;
                          checkScienceHard = 0.0;
                          checkPictureHard = 0.0;
                          checkMusicHard = 0.0;
                          checkHistoryHard = 0.0;
                          checkGeoHard = 0.0;
                          checkGKHard = 0.0;
                          checkFilmTVHard = 0.0;
                          checkHard = 0.0;
                          Toast.makeText(account_page.this, "Login with an account to record Statistics", Toast.LENGTH_SHORT).show();
                      }
                  })
                  .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                      @Override
                      public void onSuccess(DocumentSnapshot snapshot) {
                          //If document with email address as id exist - Show average stored
                          if (snapshot.exists()) {
                              FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                              String userEmail = firebaseUser.getEmail();
                              checkHard = snapshot.getDouble("Average Time");
                              checkFilmTVHard = snapshot.getDouble("FilmTV Average");
                              checkGKHard = snapshot.getDouble("GK Average");
                              checkGeoHard = snapshot.getDouble("Geography Average");
                              checkHistoryHard = snapshot.getDouble("History Average");
                              checkMusicHard = snapshot.getDouble("Music Average");
                              checkPictureHard = snapshot.getDouble("Picture Average");
                              checkScienceHard = snapshot.getDouble("Science Average");
                              checkSportsHard = snapshot.getDouble("Sports Average");
                              checkSpeedHard = snapshot.getDouble("Speed Run Average");

                              //If any Topic is non existing in firestore, just set the value to 0.0
                              //Has to be given a formatted value, as the textview is formatted to this, and it will throw an exception if it is null
                              if (checkSpeedHard == null) {
                                  checkSpeedHard = 0.0;
                              }
                              if (checkHard == null) {
                                  checkHard = 0.0;
                              }
                              if (checkFilmTVHard == null) {
                                  checkFilmTVHard = 0.0;
                              }
                              if (checkGKHard == null) {
                                  checkGKHard = 0.0;
                              }
                              if (checkHistoryHard == null) {
                                  checkHistoryHard = 0.0;
                              }
                              if (checkGeoHard == null) {
                                  checkGeoHard = 0.0;
                              }
                              if (checkMusicHard == null) {
                                  checkMusicHard = 0.0;
                              }
                              if (checkPictureHard == null) {
                                  checkPictureHard = 0.0;
                              }
                              if (checkScienceHard == null) {
                                  checkScienceHard = 0.0;
                              }
                              if (checkSportsHard == null) {
                                  checkSportsHard = 0.0;
                              }
                          }
                          //If no doc exists, value is 0.0
                          if (!snapshot.exists()) {
                              checkSpeedHard = 0.0;
                              checkSportsHard = 0.0;
                              checkScienceHard = 0.0;
                              checkPictureHard = 0.0;
                              checkMusicHard = 0.0;
                              checkHistoryHard = 0.0;
                              checkGeoHard = 0.0;
                              checkGKHard = 0.0;
                              checkFilmTVHard = 0.0;
                              checkHard = 0.0;
                          }
                      }
                  });


        }

        //These Write Average Functions were used to clear up what didn't calculate during intial trial and error phase of calculating averages from quizzes
        private void writeEasyAveragetoDB()
        {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            String userEmail = firebaseUser.getEmail();
            //Checking if document exists with the same name as the user Email
            DocumentReference statsRef;
            statsRef = g_firestore.collection("Statistics").document("Easy").collection("Users").document(userEmail);
                statsRef.get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot snapshot) {
                                //If document with email address as id exist - Only update this document/override
                                if (snapshot.exists()) {

                                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                    String userEmail = firebaseUser.getEmail();
                                    checkstoredaverage = snapshot.getDouble("Average Time");
                                    checkstoredFilmAverage = snapshot.getDouble("FilmTV Average");
                                    checkstoredGKAverage = snapshot.getDouble("GK Average");
                                    checkstoredGeoAverage = snapshot.getDouble("Geography Average");
                                    checkstoredHistoryAverage = snapshot.getDouble("History Average");
                                    checkstoredMusicAverage = snapshot.getDouble("Music Average");
                                    checkstoredPictureAverage = snapshot.getDouble("Picture Average");
                                    checkstoredScienceAverage = snapshot.getDouble("Science Average");
                                    checkstoredSportsAverage = snapshot.getDouble("Sports Average");

                                    newoverallAverage = (checkstoredFilmAverage + checkstoredGKAverage + checkstoredGeoAverage + checkstoredHistoryAverage
                                            + checkstoredMusicAverage + checkstoredPictureAverage + checkstoredScienceAverage + checkstoredSportsAverage) / 8;
                                    g_firestore.collection("Statistics").document("Easy").collection("Users").document(userEmail).update("Average Time", newoverallAverage);

                                }
                            }

                        });
        }

        private void writeMediumAveragetoDB()
        {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            String userEmail = firebaseUser.getEmail();
            //Checking if document exists with the same name as the user Email
            DocumentReference statsRef;
            statsRef = g_firestore.collection("Statistics").document("Medium").collection("Users").document(userEmail);
            statsRef.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot snapshot) {
                            //If document with email address as id exist - Only update this document/override
                            if (snapshot.exists()) {

                                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                String userEmail = firebaseUser.getEmail();
                                checkstoredaverage = snapshot.getDouble("Average Time");
                                checkstoredFilmAverage = snapshot.getDouble("FilmTV Average");
                                checkstoredGKAverage = snapshot.getDouble("GK Average");
                                checkstoredGeoAverage = snapshot.getDouble("Geography Average");
                                checkstoredHistoryAverage = snapshot.getDouble("History Average");
                                checkstoredMusicAverage = snapshot.getDouble("Music Average");
                                checkstoredPictureAverage = snapshot.getDouble("Picture Average");
                                checkstoredScienceAverage = snapshot.getDouble("Science Average");
                                checkstoredSportsAverage = snapshot.getDouble("Sports Average");
                                newoverallAverage = (checkstoredFilmAverage + checkstoredGKAverage + checkstoredGeoAverage + checkstoredHistoryAverage
                                        + checkstoredMusicAverage + checkstoredPictureAverage + checkstoredScienceAverage + checkstoredSportsAverage) / 8;
                                g_firestore.collection("Statistics").document("Medium").collection("Users").document(userEmail).update("Average Time", newoverallAverage);
                            }
                        }

                    });
        }
        private void writeHardAveragetoDB()
        {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            String userEmail = firebaseUser.getEmail();
            //Checking if document exists with the same name as the user Email
            DocumentReference statsRef;
            statsRef = g_firestore.collection("Statistics").document("Medium").collection("Users").document(userEmail);
            statsRef.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot snapshot) {
                            //If document with email address as id exist - Only update this document/override
                            if (snapshot.exists()) {

                                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                String userEmail = firebaseUser.getEmail();
                                checkstoredaverage = snapshot.getDouble("Average Time");
                                checkstoredFilmAverage = snapshot.getDouble("FilmTV Average");
                                checkstoredGKAverage = snapshot.getDouble("GK Average");
                                checkstoredGeoAverage = snapshot.getDouble("Geography Average");
                                checkstoredHistoryAverage = snapshot.getDouble("History Average");
                                checkstoredMusicAverage = snapshot.getDouble("Music Average");
                                checkstoredPictureAverage = snapshot.getDouble("Picture Average");
                                checkstoredScienceAverage = snapshot.getDouble("Science Average");
                                checkstoredSportsAverage = snapshot.getDouble("Sports Average");

                                newoverallAverage = (checkstoredFilmAverage + checkstoredGKAverage + checkstoredGeoAverage + checkstoredHistoryAverage
                                        + checkstoredMusicAverage + checkstoredPictureAverage + checkstoredScienceAverage + checkstoredSportsAverage) / 8;
                                g_firestore.collection("Statistics").document("Medium").collection("Users").document(userEmail).update("Average Time", newoverallAverage);

                            }
                        }

                    });
        }
        private void guestLoggedinMsg()
        {
           if(userEmail.equals(guestEmail))
           {
               Toast.makeText(account_page.this, "Login or create an Account to get Statistics", Toast.LENGTH_SHORT).show();
           }

        }



    }



