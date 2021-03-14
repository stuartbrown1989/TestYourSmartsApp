package com.example.testyoursmarts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.ui.AppBarConfiguration;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import static com.example.testyoursmarts.dbQuery.g_question_list;

public class headtoheadQuizActivity extends AppCompatActivity {

    private RadioGroup buttonGroup;
    private RadioButton op1;
    private RadioButton op2;
    private RadioButton op3;
    private RadioButton op4;
    private boolean answered;
    private ColorStateList textColorDefaultRb;
    private Button confirmNext;
    private int correctAnswer;
    private TextView theDifficulty, headerTimer, whosTurn, textUsersName;
    private CountDownTimer cTimer = null;
    private String userName;
    public static FirebaseFirestore g_firestore;
    private ArrayList<Integer> questionIDs = new ArrayList<Integer>();

    private TextView textViewQuestion;
    private TextView textViewScore, textViewGuestScore;
    private TextView textViewQuestionCount, timer;
    private int questionCount;
    private int questionTotal = 20;
    private int userScore = 0;
    private int guestScore = 0;
    private int potentialScore;
    private questionsModel currentQuestion;
    private AppBarConfiguration mAppBarConfiguration;
    private BottomNavigationView bottomNavigationView;
    private FrameLayout main_frame;
    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemReselectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    switch (menuItem.getItemId()) {
                        case R.id.nav_home:
                            g_question_list.clear();
                            g_question_list.removeAll(Collections.emptyList());
                            Intent intentHome = new Intent(headtoheadQuizActivity.this, MainActivity.class);
                            startActivity(intentHome);
                            return true;

                        case R.id.nav_leaderboard:
                            g_question_list.clear();
                            g_question_list.removeAll(Collections.emptyList());
                            Intent intentLeaderboard= new Intent(headtoheadQuizActivity.this, leaderboardChoiceActivity.class);
                            startActivity(intentLeaderboard);
                            return true;

                        case R.id.nav_account:
                            Intent intentAccount = new Intent(headtoheadQuizActivity.this, account_page.class);
                            startActivity(intentAccount);
                            return true;

                        case R.id.nav_logout:
                            g_question_list.clear();
                            g_question_list.removeAll(Collections.emptyList());
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
        setContentView(R.layout.activity_headtohead_quiz);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Head to Head Quiz");
        bottomNavigationView = findViewById(R.id.top_nav_bar);
        main_frame = findViewById(R.id.main_frame);
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemReselectedListener);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userEmail = firebaseUser.getEmail();
        //If the current logged in user is a Guest, do this


        final AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);
        //Initializing Variables
        whosTurn = findViewById(R.id.headtohead_whosTurn);
        headerTimer = findViewById(R.id.HeadtoHeadtimeRemaining);
        theDifficulty = findViewById(R.id.HeadtoHead_difficulty);
        buttonGroup = findViewById(R.id.radio_group);
        op1 = findViewById(R.id.radio_button1);
        op2 = findViewById(R.id.radio_button2);
        op3 = findViewById(R.id.radio_button3);
        op4 = findViewById(R.id.radio_button4);
        textViewQuestion = findViewById(R.id.HeadtoHeadtext_view_question);
        textViewScore = findViewById(R.id.HeadtoHeadtext_view_user_score);
        textViewGuestScore = findViewById(R.id.HeadtoHeadtext_view_guest_score);
        textViewQuestionCount = findViewById(R.id.HeadtoHeadtext_view_question_count);
        textColorDefaultRb = op1.getTextColors();
        confirmNext = findViewById(R.id.HeadtoHeadbutton_confirm_next);
        timer = findViewById(R.id.HeadtoHeadtext_view_timer);
        textUsersName = findViewById(R.id.h2h_user_header);
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemReselectedListener);
        Intent intent = getIntent();
        //Getting the difficulty level set from the confirm quiz page
        String difficultSetting = intent.getStringExtra("Difficulty");
        theDifficulty.setText("Difficulty: " + difficultSetting);

        //Accessing firestore
        g_firestore = FirebaseFirestore.getInstance();
        getUserName();
        if(userName == null)
        {
            userName = "Player 1";
        }
        whosTurn.setBackgroundColor(Color.RED);
        whosTurn.setText("Who's Turn?\n" + userName);

        //Removes any existing elements in the array
        // (Shouldn't be any, but this covers if the back button is pressed and the user comes back to this activity
        g_question_list.clear();
        questionIDs.removeAll(Collections.emptyList());

        //Retrieving difficulty from Confirm Quiz page and setting appropriate questions

        switch (difficultSetting) {
            case "Easy":
                //Loading up questions
                getUserName();
                getEasyHeadtoHeadQuestions();
                potentialScore = questionTotal/2;
                textViewScore.setText("Score: " + userScore + "/" + potentialScore);
                textViewGuestScore.setText("Score: " + guestScore + "/" + potentialScore);
                break;
            case "Medium":
                getUserName();
                getMediumHeadtoHeadQuestions();
                potentialScore = questionTotal;
                textViewScore.setText("Score: " + userScore + "/" + potentialScore);
                textViewGuestScore.setText("Score: " + guestScore + "/" + potentialScore);

                break;
            case "Hard":
                getUserName();
                getHardHeadtoHeadQuestions();
                potentialScore = (questionTotal/2) * 3;
                textViewScore.setText("Score: " + userScore + "/" + potentialScore);
                textViewGuestScore.setText("Score: " + guestScore + "/" + potentialScore);
                break;
        }

        confirmNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                if (!answered) {
                    if (op1.isChecked() || op2.isChecked() || op3.isChecked() || op4.isChecked()) {
                        checkAnswer();
                    } else if (timer.getText().toString().equals("0")) {
                        checkAnswer();
                        setQuestion();
                        startTimer();
                    } else {
                        Toast.makeText(headtoheadQuizActivity.this, "Please select answer", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    setQuestion();
                    startTimer();
                }
            }
        });
    }

    //This function goes through all the collections under the document Easy and fills the array list
    private void getEasyHeadtoHeadQuestions()
    {
//Locates and gets the Collection Questions from firestore and designates it to the Questions model class


        g_firestore.collection("Questions").document("Easy").collection("GK")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        //Get collection info and store it to the list
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            g_question_list.add(new questionsModel(
                                    doc.getString("Question"),
                                    doc.getString("Option_a"),
                                    doc.getString("Option_b"),
                                    doc.getString("Option_c"),
                                    doc.getString("Option_d"),
                                    Objects.requireNonNull(doc.getLong("Answer")).intValue()
                            ));
                            Collections.shuffle(g_question_list);
                        }
                    }
                });
        g_firestore.collection("Questions").document("Easy").collection("History")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        //Get collection info and store it to the list
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            g_question_list.add(new questionsModel(
                                    doc.getString("Question"),
                                    doc.getString("Option_a"),
                                    doc.getString("Option_b"),
                                    doc.getString("Option_c"),
                                    doc.getString("Option_d"),
                                    Objects.requireNonNull(doc.getLong("Answer")).intValue()
                            ));
                            Collections.shuffle(g_question_list);
                        }
                    }
                });

        g_firestore.collection("Questions").document("Easy").collection("FilmTV")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        //Get collection info and store it to the list
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            g_question_list.add(new questionsModel(
                                    doc.getString("Question"),
                                    doc.getString("Option_a"),
                                    doc.getString("Option_b"),
                                    doc.getString("Option_c"),
                                    doc.getString("Option_d"),
                                    Objects.requireNonNull(doc.getLong("Answer")).intValue()
                            ));
                            Collections.shuffle(g_question_list);
                        }
                    }
                });

        g_firestore.collection("Questions").document("Easy").collection("Geography")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        //Get collection info and store it to the list
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            g_question_list.add(new questionsModel(
                                    doc.getString("Question"),
                                    doc.getString("Option_a"),
                                    doc.getString("Option_b"),
                                    doc.getString("Option_c"),
                                    doc.getString("Option_d"),
                                    Objects.requireNonNull(doc.getLong("Answer")).intValue()
                            ));
                            Collections.shuffle(g_question_list);
                        }
                    }
                });

        g_firestore.collection("Questions").document("Easy").collection("Music")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        //Get collection info and store it to the list
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            g_question_list.add(new questionsModel(
                                    doc.getString("Question"),
                                    doc.getString("Option_a"),
                                    doc.getString("Option_b"),
                                    doc.getString("Option_c"),
                                    doc.getString("Option_d"),
                                    Objects.requireNonNull(doc.getLong("Answer")).intValue()
                            ));
                            Collections.shuffle(g_question_list);
                        }
                    }
                });

        g_firestore.collection("Questions").document("Easy").collection("Science")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        //Get collection info and store it to the list
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            g_question_list.add(new questionsModel(
                                    doc.getString("Question"),
                                    doc.getString("Option_a"),
                                    doc.getString("Option_b"),
                                    doc.getString("Option_c"),
                                    doc.getString("Option_d"),
                                    Objects.requireNonNull(doc.getLong("Answer")).intValue()
                            ));
                            Collections.shuffle(g_question_list);
                        }
                    }
                });

        g_firestore.collection("Questions").document("Easy").collection("Sports")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        //Get collection info and store it to the list
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            g_question_list.add(new questionsModel(
                                    doc.getString("Question"),
                                    doc.getString("Option_a"),
                                    doc.getString("Option_b"),
                                    doc.getString("Option_c"),
                                    doc.getString("Option_d"),
                                    Objects.requireNonNull(doc.getLong("Answer")).intValue()
                            ));
                            Collections.shuffle(g_question_list);
                        }
                        getUserName();
                        setQuestion();
                        startTimer();
                    }
                });

    }

    private void getMediumHeadtoHeadQuestions()
    {
//Locates and gets the Collection Questions from firestore and designates it to the Questions model class
        g_firestore.collection("Questions").document("Medium").collection("GK")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        //Get collection info and store it to the list
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            g_question_list.add(new questionsModel(
                                    doc.getString("Question"),
                                    doc.getString("Option_a"),
                                    doc.getString("Option_b"),
                                    doc.getString("Option_c"),
                                    doc.getString("Option_d"),
                                    Objects.requireNonNull(doc.getLong("Answer")).intValue()
                            ));
                            Collections.shuffle(g_question_list);
                        }
                    }
                });

        g_firestore.collection("Questions").document("Medium").collection("History")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        //Get collection info and store it to the list
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            g_question_list.add(new questionsModel(
                                    doc.getString("Question"),
                                    doc.getString("Option_a"),
                                    doc.getString("Option_b"),
                                    doc.getString("Option_c"),
                                    doc.getString("Option_d"),
                                    Objects.requireNonNull(doc.getLong("Answer")).intValue()
                            ));
                            Collections.shuffle(g_question_list);
                        }
                    }
                });

        g_firestore.collection("Questions").document("Medium").collection("FilmTV")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        //Get collection info and store it to the list
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            g_question_list.add(new questionsModel(
                                    doc.getString("Question"),
                                    doc.getString("Option_a"),
                                    doc.getString("Option_b"),
                                    doc.getString("Option_c"),
                                    doc.getString("Option_d"),
                                    Objects.requireNonNull(doc.getLong("Answer")).intValue()
                            ));
                            Collections.shuffle(g_question_list);
                        }
                    }
                });

        g_firestore.collection("Questions").document("Medium").collection("Geography")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        //Get collection info and store it to the list
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            g_question_list.add(new questionsModel(
                                    doc.getString("Question"),
                                    doc.getString("Option_a"),
                                    doc.getString("Option_b"),
                                    doc.getString("Option_c"),
                                    doc.getString("Option_d"),
                                    Objects.requireNonNull(doc.getLong("Answer")).intValue()
                            ));
                            Collections.shuffle(g_question_list);
                        }
                    }
                });

        g_firestore.collection("Questions").document("Medium").collection("Music")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        //Get collection info and store it to the list
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            g_question_list.add(new questionsModel(
                                    doc.getString("Question"),
                                    doc.getString("Option_a"),
                                    doc.getString("Option_b"),
                                    doc.getString("Option_c"),
                                    doc.getString("Option_d"),
                                    Objects.requireNonNull(doc.getLong("Answer")).intValue()
                            ));
                            Collections.shuffle(g_question_list);
                        }
                    }
                });

        g_firestore.collection("Questions").document("Medium").collection("Science")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        //Get collection info and store it to the list
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            g_question_list.add(new questionsModel(
                                    doc.getString("Question"),
                                    doc.getString("Option_a"),
                                    doc.getString("Option_b"),
                                    doc.getString("Option_c"),
                                    doc.getString("Option_d"),
                                    Objects.requireNonNull(doc.getLong("Answer")).intValue()
                            ));
                            Collections.shuffle(g_question_list);
                        }
                    }
                });

        g_firestore.collection("Questions").document("Medium").collection("Sports")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        //Get collection info and store it to the list
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            g_question_list.add(new questionsModel(
                                    doc.getString("Question"),
                                    doc.getString("Option_a"),
                                    doc.getString("Option_b"),
                                    doc.getString("Option_c"),
                                    doc.getString("Option_d"),
                                    Objects.requireNonNull(doc.getLong("Answer")).intValue()
                            ));
                            Collections.shuffle(g_question_list);
                        }
                        getUserName();
                        setQuestion();
                        startTimer();
                    }
                });

    }
    //This function goes through all the collections under the document Hard and fills the array list
    private void getHardHeadtoHeadQuestions()
    {
//Locates and gets the Collection Questions from firestore and designates it to the Questions model class

        g_firestore.collection("Questions").document("Hard").collection("GK")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        //Get collection info and store it to the list
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            g_question_list.add(new questionsModel(
                                    doc.getString("Question"),
                                    doc.getString("Option_a"),
                                    doc.getString("Option_b"),
                                    doc.getString("Option_c"),
                                    doc.getString("Option_d"),
                                    Objects.requireNonNull(doc.getLong("Answer")).intValue()
                            ));
                        }
                    }
                });

        g_firestore.collection("Questions").document("Hard").collection("History")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        //Get collection info and store it to the list
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            g_question_list.add(new questionsModel(
                                    doc.getString("Question"),
                                    doc.getString("Option_a"),
                                    doc.getString("Option_b"),
                                    doc.getString("Option_c"),
                                    doc.getString("Option_d"),
                                    Objects.requireNonNull(doc.getLong("Answer")).intValue()
                            ));
                        }
                    }
                });

        g_firestore.collection("Questions").document("Hard").collection("FilmTV")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        //Get collection info and store it to the list
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            g_question_list.add(new questionsModel(
                                    doc.getString("Question"),
                                    doc.getString("Option_a"),
                                    doc.getString("Option_b"),
                                    doc.getString("Option_c"),
                                    doc.getString("Option_d"),
                                    Objects.requireNonNull(doc.getLong("Answer")).intValue()
                            ));
                        }
                    }
                });

        g_firestore.collection("Questions").document("Hard").collection("Geography")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        //Get collection info and store it to the list
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            g_question_list.add(new questionsModel(
                                    doc.getString("Question"),
                                    doc.getString("Option_a"),
                                    doc.getString("Option_b"),
                                    doc.getString("Option_c"),
                                    doc.getString("Option_d"),
                                    Objects.requireNonNull(doc.getLong("Answer")).intValue()
                            ));
                        }
                    }
                });

        g_firestore.collection("Questions").document("Hard").collection("Music")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        //Get collection info and store it to the list
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            g_question_list.add(new questionsModel(
                                    doc.getString("Question"),
                                    doc.getString("Option_a"),
                                    doc.getString("Option_b"),
                                    doc.getString("Option_c"),
                                    doc.getString("Option_d"),
                                    Objects.requireNonNull(doc.getLong("Answer")).intValue()
                            ));
                        }
                    }
                });

        g_firestore.collection("Questions").document("Hard").collection("Science")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        //Get collection info and store it to the list
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            g_question_list.add(new questionsModel(
                                    doc.getString("Question"),
                                    doc.getString("Option_a"),
                                    doc.getString("Option_b"),
                                    doc.getString("Option_c"),
                                    doc.getString("Option_d"),
                                    Objects.requireNonNull(doc.getLong("Answer")).intValue()
                            ));
                        }
                    }
                });

        g_firestore.collection("Questions").document("Hard").collection("Sports")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        //Get collection info and store it to the list
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            g_question_list.add(new questionsModel(
                                    doc.getString("Question"),
                                    doc.getString("Option_a"),
                                    doc.getString("Option_b"),
                                    doc.getString("Option_c"),
                                    doc.getString("Option_d"),
                                    Objects.requireNonNull(doc.getLong("Answer")).intValue()
                            ));
                            Collections.shuffle(g_question_list);
                        }
                        getUserName();
                        setQuestion();
                        startTimer();
                    }
                });
    }

    //Function to set the question based on the getQuestions functions that were grabbed from firestore
    private void setQuestion()
    {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String UserEmail = firebaseUser.getEmail();
        getUserName();
        op1.setTextColor(textColorDefaultRb);
        op2.setTextColor(textColorDefaultRb);
        op3.setTextColor(textColorDefaultRb);
        op4.setTextColor(textColorDefaultRb);
        buttonGroup.clearCheck();
        userName = textUsersName.getText().toString();
        String qText = "Question: ";
        String slash = "/";
        String confirm = "Confirm";
        if((questionCount % 2) == 0)
        {
            getUserName();
            whosTurn.setBackgroundColor(Color.RED);
            whosTurn.setText("Who's Turn?\n" + userName);
        }
        else{
            whosTurn.setBackgroundColor(Color.RED);
            whosTurn.setText("Who's Turn?\nGUEST");
        }
            //If current question number is less than total number of questions, keep going, otherwise finish
        if (questionCount < questionTotal) {
            for (int i = 0; i < buttonGroup.getChildCount(); i++) {
                buttonGroup.getChildAt(i).setEnabled(true);
            }
            currentQuestion = g_question_list.get(questionCount);
            textViewQuestion.setText(currentQuestion.getQuestion());
            op1.setText(currentQuestion.getOption1());
            op2.setText(currentQuestion.getOption2());
            op3.setText(currentQuestion.getOption3());
            op4.setText(currentQuestion.getOption4());
            correctAnswer = currentQuestion.getCorrectAnswer();
            questionCount++;
            textViewQuestionCount.setText(qText + questionCount + slash + questionTotal);
            answered = false;
            confirmNext.setText(confirm);
        }
    }

    //If correct answer from firestore matches selected button, increment score by 1, 2 or 3 depending on difficulty
    private void checkAnswer()
    {
        answered = true;
        RadioButton rbSelected = findViewById(buttonGroup.getCheckedRadioButtonId());
        int answerNr = buttonGroup.indexOfChild(rbSelected) + 1;
        Intent intent = getIntent();
        String difficultSetting = intent.getStringExtra("Difficulty");

        if(difficultSetting.equals("Easy")) {
            if ((questionCount % 2) == 0) {

                if (answerNr == correctAnswer) {
                    guestScore++;
                    potentialScore = (questionTotal/2);
                    textViewGuestScore.setText("Score: " + guestScore + "/" + potentialScore);
                }
            } else {
                if (answerNr == correctAnswer) {
                    userScore++;
                    potentialScore = (questionTotal/2);
                    textViewScore.setText("Score: " + userScore + "/" + potentialScore);;
                }
            }
        }
        if(difficultSetting.equals("Medium")){
            if ((questionCount % 2) == 0) {

                if (answerNr == correctAnswer) {
                    guestScore = guestScore + 2;
                    potentialScore = questionTotal;
                    textViewGuestScore.setText("Score: " + guestScore + "/" + potentialScore);
                }
            } else {
                if (answerNr == correctAnswer) {
                    userScore = userScore + 2;
                    potentialScore = questionTotal;
                    textViewScore.setText("Score: " + userScore + "/" + potentialScore);;
                }
            }
        }

        if(difficultSetting.equals("Hard")) {
            if ((questionCount % 2) == 0) {

                if (answerNr == correctAnswer) {
                  guestScore = guestScore + 3;
                    potentialScore = (questionTotal/2) * 3;
                    textViewGuestScore.setText("Score: " + guestScore + "/" + potentialScore);
                }
            } else {
                if (answerNr == correctAnswer) {
                    userScore = userScore + 3;
                    potentialScore = (questionTotal/2) * 3;
                    textViewScore.setText("Score: " + userScore + "/" + potentialScore);;
                }
            }
        }
        showSolution();
        cTimer.cancel();
    }

    //Function to display correct answer, while also highlighting red for an incorrect answer and green for a correct answer
    private void showSolution()
    {
        op1.setTextColor(Color.RED);
        op2.setTextColor(Color.RED);
        op3.setTextColor(Color.RED);
        op4.setTextColor(Color.RED);

        switch (currentQuestion.getCorrectAnswer())
        {
            case 1:
                op1.setTextColor(Color.GREEN);
                textViewQuestion.setText("Answer a is correct");
                break;
            case 2:
                op2.setTextColor(Color.GREEN);
                textViewQuestion.setText("Answer b is correct");
                break;
            case 3:
                op3.setTextColor(Color.GREEN);
                textViewQuestion.setText("Answer c is correct");
                break;
            case 4:
                op4.setTextColor(Color.GREEN);
                textViewQuestion.setText("Answer d is correct");
                break;
        }
        if  (questionCount < questionTotal)
        {
            for (int i = 0; i < buttonGroup.getChildCount(); i++) {
                buttonGroup.getChildAt(i).setEnabled(false);
            }
            confirmNext.setText("Next");
        }else
        {
            for (int i = 0; i < buttonGroup.getChildCount(); i++) {
                buttonGroup.getChildAt(i).setEnabled(false);
            }
            confirmNext.setText("Finish");
            headerTimer.setTextColor(Color.RED);
            headerTimer.setText("You have completed the Quiz! Press finish to see your results");
            confirmNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(headtoheadQuizActivity.this, resultPageActivity.class);
                    intent.putExtra("SCORE", userScore);
                    intent.putExtra("GUESTSCORE", guestScore);
                    startActivity(intent);
                    g_question_list.clear();
                    questionIDs.removeAll(Collections.emptyList());
                }
            });
        }
    }

//Timer for each question that gets set
    private void startTimer()
    {
        cTimer = new CountDownTimer(16000, 1000) {
            @Override
            public void onTick(long millisUntilFinished)
            {
                int seconds = (int) (millisUntilFinished / 1000);
                timer.setText(String.valueOf(seconds));
                if(timer.getText().toString().equals("0") || (timer.getText().toString().equals("1"))
                        || (timer.getText().toString().equals("2")) || (timer.getText().toString().equals("3"))
                        || (timer.getText().toString().equals("4")) || (timer.getText().toString().equals("5")))
                {
                    timer.setTextColor(Color.RED);
                }
                else{
                    timer.setTextColor(Color.GREEN);
                }
            }
            @Override
            public void onFinish()
            {
                showSolution();
            }
        }.start();
    }

//Function to get the Users name from Firestore
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
                                        userName = document.getString("NAME");

                                    }
                                    textUsersName.setText(userName);
                                } else {
                                    userName = "Player 1";
                                    textUsersName.setText(userName);
                                }
                            }
                        });
    }
}