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
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.protobuf.NullValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static com.example.testyoursmarts.dbQuery.g_question_list;

public class speedRunQuizActivity extends AppCompatActivity {

    private RadioGroup buttonGroup;
    private RadioButton op1;
    private RadioButton op2;
    private RadioButton op3;
    private RadioButton op4;
    private boolean answered;
    private ColorStateList textColorDefaultRb;
    private Button confirmNext;
    private int correctAnswer;
    private TextView showdifficulty;

    private CountDownTimer cTimer = null;
    private Timer timetoanswerTimer = null;
    private Timer testTimer = null;
    private long millisUntilFinished;
    public static FirebaseFirestore g_firestore;
    private List<questionsModel> questionslist;
    private Dialog loadingDialog;
    private String checkUser, userName;
    private int checkscore;
    private int setTime = 61;
    private TimerTask timetoanswertask = null;
    private TextView SRDifficulty, headertime;
    private TextView textViewQuestion;
    private TextView textViewScore;
    private TextView textViewQuestionCount, timer;
    private int questionCount = 1;
    private int questionScoreCount = 0;
    private int testingQuestionCount = - 1;
    private int questionTotal = 0;
    private int potentialScore;
    private int score = 0;

    //UserStats Variables
    private String gettime;
    private String guestLog = "Guest";
    private TextView timetoanswer, checkTimer;
    private double checkstoredaverage, calcNewAverage, calcNewTopicAverage;
    private Double checkstoredTopicAverage;
    private int addtime = 0;
    private int tallytime = 0;

    private double time;


    private List<questionsModel> questionList;
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
                            Intent intentHome = new Intent(speedRunQuizActivity.this, MainActivity.class);
                            startActivity(intentHome);
                            return true;

                        case R.id.nav_leaderboard:
                            Intent intentLeaderboard= new Intent(speedRunQuizActivity.this, leaderboardChoiceActivity.class);
                            startActivity(intentLeaderboard);
                            return true;

                        case R.id.nav_account:
                            Intent intentAccount = new Intent(speedRunQuizActivity.this, account_page.class);
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
        setContentView(R.layout.activity_speed_run_quiz);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Speed Run Quiz");
        bottomNavigationView = findViewById(R.id.top_nav_bar);
        main_frame = findViewById(R.id.main_frame);
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemReselectedListener);
        //Initializing Variables
        showdifficulty = findViewById(R.id.speed_run_difficulty);
        headertime = findViewById(R.id.SR_text_view_headerTimer);
        buttonGroup = findViewById(R.id.radio_group);
        op1 = findViewById(R.id.radio_button1);
        op2 = findViewById(R.id.radio_button2);
        op3 = findViewById(R.id.radio_button3);
        op4 = findViewById(R.id.radio_button4);
        textViewQuestion = findViewById(R.id.text_view_question);
        textViewScore = findViewById(R.id.text_view_new_scoreSR);
        textViewQuestionCount = findViewById(R.id.text_view_question_count);
       textColorDefaultRb = op1.getTextColors();
        confirmNext = findViewById(R.id.button_confirm_nextSR);
        timer = findViewById(R.id.text_view_timer_SR);
        checkTimer = findViewById(R.id.SRTimer_for_check);
        timetoanswer = findViewById(R.id.SR_timetoanswer);
        //Initialize connection to firestore
        g_firestore = FirebaseFirestore.getInstance();
        getUserName();
        //Grab difficulty from confirm quiz
        Intent intent = getIntent();
        String difficultSetting = intent.getStringExtra("Difficulty");
        showdifficulty.setText("Difficulty: " + difficultSetting);

        //Removes any existing elements in the array
        // (Shouldn't be any, but this covers if the back button is pressed and the user comes back to this activity
        g_question_list.clear();
        g_question_list.removeAll(Collections.emptyList());

        //Start 3 minute timer

        //Retrieving difficulty from Confirm Quiz page and setting appropriate questions + Setting Both Timers
        switch (difficultSetting) {
            case "Easy":
                //Loading up questions
                newTimer();
                newCheckTimer();
                getEasySRQuestions();
                break;
            case "Medium":
                newTimer();
                newCheckTimer();
                getMediumSRQuestions();
                break;
            case "Hard":
                newTimer();
                newCheckTimer();
                getHardSRQuestions();
                break;
        }

        //On Click listener for the Next button
        confirmNext.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (!answered)
                {
                    if(op1.isChecked() || op2.isChecked() || op3.isChecked() || op4.isChecked())
                    {
                        checkAnswer();
                        timetoanswerTimer.cancel();
                        timetoanswerTimer.purge();
                    }
                    else if(timer.getText().toString().equals("00:00"))
                    {
                        Toast.makeText(speedRunQuizActivity.this, "Time is up!  Press finish to see your result", Toast.LENGTH_SHORT).show();
                        timetoanswerTimer.cancel();
                        timetoanswerTimer.purge();
                    }
                    else
                    {
                        Toast.makeText(speedRunQuizActivity.this, "Please select answer", Toast.LENGTH_SHORT).show();
                    }
                } else
                {
                    timetoanswerTimer.cancel();
                    timetoanswerTimer.purge();
                    countTimetoAnswerQuestions();
                    setQuestion();
                    newCheckTimer();
                }
            }
        });
        Collections.shuffle(g_question_list);
    }
    //This function goes through all the collections under the document Easy and fills the array list
    private void getEasySRQuestions()
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
                        Collections.shuffle(g_question_list);
                        setQuestion();
                    }
                });
    }

    //This function goes through all the collections under the document Medium and fills the array list
    private void getMediumSRQuestions()
    {
//Locates and gets the Collection Questions from firestore and designates it to the Questions model class
        questionList = new ArrayList<>();
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
                        Collections.shuffle(g_question_list);
                        setQuestion();
                    }
                });

    }
    //This function goes through all the collections under the document Hard and fills the array list
    private void getHardSRQuestions()
    {
//Locates and gets the Collection Questions from firestore and designates it to the Questions model class
        questionList = new ArrayList<>();
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
                            Collections.shuffle(g_question_list);
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
                            Collections.shuffle(g_question_list);
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
                            Collections.shuffle(g_question_list);
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
                            Collections.shuffle(g_question_list);
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
                            Collections.shuffle(g_question_list);
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
                            Collections.shuffle(g_question_list);
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

                        Collections.shuffle(g_question_list);
                        setQuestion();
                    }
                });
    }

    //Gets the values sent to the Questions model and shows them in the relevant fields
    private void setQuestion() {

        //Make all radio buttons clickable when setting a question
        for (int i = 0; i < buttonGroup.getChildCount(); i++) {
            buttonGroup.getChildAt(i).setEnabled(true);
        }
        op1.setTextColor(textColorDefaultRb);
        op2.setTextColor(textColorDefaultRb);
        op3.setTextColor(textColorDefaultRb);
        op4.setTextColor(textColorDefaultRb);
        buttonGroup.clearCheck();
        String qText = "Question: ";
        String confirm = "Confirm";

        //Questions keep coming - Set Text to information retrieved from Firestore
        questionTotal = g_question_list.size();
        currentQuestion = g_question_list.get(questionCount);
        textViewQuestion.setText(currentQuestion.getQuestion());
        op1.setText(currentQuestion.getOption1());
        op2.setText(currentQuestion.getOption2());
        op3.setText(currentQuestion.getOption3());
        op4.setText(currentQuestion.getOption4());
        correctAnswer = currentQuestion.getCorrectAnswer();
        textViewQuestionCount.setText(qText + questionCount);
        questionCount++;
        questionScoreCount++;
        testingQuestionCount++;

        answered = false;
        confirmNext.setText(confirm);
    }

    //If correct answer from firestore matches selected button, increment score by 1
    private void checkAnswer()
    {
        answered = true;
        RadioButton rbSelected = findViewById(buttonGroup.getCheckedRadioButtonId());
        int answerNr = buttonGroup.indexOfChild(rbSelected) + 1;

        Intent intent = getIntent();
        String difficultSetting = intent.getStringExtra("Difficulty");
        if(difficultSetting.toString().equals("Easy")) {
            if (answerNr == correctAnswer) {
                score++;
                potentialScore = questionScoreCount;
                textViewScore.setText("Score: " + score + "/" + potentialScore);
            }
        }

        if(difficultSetting.toString().equals("Medium")) {
            if (answerNr == correctAnswer) {
                score = score + 2;
                potentialScore = questionScoreCount * 2;
                textViewScore.setText("Score: " + score + "/" + potentialScore);
            }
        }

        if(difficultSetting.toString().equals("Hard")) {
            if (answerNr == correctAnswer) {
                score = score + 3;
                potentialScore = questionScoreCount * 3;
                textViewScore.setText("Score: " + score + "/" + potentialScore);
            }
        }
        timetoanswerTimer.cancel();
        timetoanswerTimer.purge();
        showSolution();


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
        }

        else
        {
            for (int i = 0; i < buttonGroup.getChildCount(); i++) {
                buttonGroup.getChildAt(i).setEnabled(false);
            }
            confirmNext.setText("Finish");
            confirmNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                     getuserScores();
                }
            });
        }
    }

    //Set Timer task - Will start from 300 seconds (3 minutes)
    private void newTimer ()
    {
        testTimer = new Timer();
       TimerTask task = new TimerTask() {
            int i = setTime;
            //Formats timer to work in minutes/seconds
            String newTime = String.format("%02d:%02d", i / 60, i % 60);
            @Override
            public void run() {
                if(i > 0)
                {
                    i--;
                    newTime = String.format("%02d:%02d", i / 60, i % 60);
                    timer.setText(newTime);
                }
                else if (i == 0){
                    testTimer.cancel();
                    testTimer.purge();
                    timetoanswerTimer.cancel();
                    timetoanswerTimer.purge();
                    headertime.setText("TIME IS UP! Press Finish to see your result");
                    headertime.setTextColor(Color.RED);
                    for (int i = 0; i < buttonGroup.getChildCount(); i++) {
                        buttonGroup.getChildAt(i).setEnabled(false);
                    }
                    confirmNext.setText("Finish");
                    confirmNext.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            g_question_list.clear();
                            g_question_list.removeAll(Collections.emptyList());
                            getuserScores();

                        }
                    });
                }
            }
        };
       //Timer delay to be 1 second
        testTimer.scheduleAtFixedRate(task, 0, 1000);
    }
//Time it takes to answer question timer
    private void newCheckTimer()
    {
        timetoanswerTimer = new Timer();
        timetoanswertask = new TimerTask() {
            int i = -1;
            //Formats timer to work in minutes/seconds
            String newTime = String.valueOf(i);
            @Override
            public void run() {
                if(i >= -1)
                {
                    i++;
                    newTime = String.valueOf(i);
                    checkTimer.setText(newTime);
                }
                else if (i == setTime){
                    timetoanswerTimer.cancel();
                    timetoanswerTimer.purge();

                }
            }
        };
        //Timer delay to be 1 second
       timetoanswerTimer.scheduleAtFixedRate(timetoanswertask, 0, 1000);
    }

    //Fetches the Leaderboards data
    private void getuserScores() {
        averageTime();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userEmail = firebaseUser.getEmail();
        //If the current logged in user is a Guest, do this - Writes no data to leader board
        if(userEmail == null)
        {
            userEmail = "Guest";
        }
        if(userEmail == guestLog)
        {
            Intent resultintent = new Intent(speedRunQuizActivity.this, resultPageActivity.class);
            resultintent.putExtra("SCORE", score);
            resultintent.putExtra("QUIZ", "Speed Run");
            resultintent.putExtra("TIME", time);
            g_question_list.clear();
            startActivity(resultintent);
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches())
        {
            Intent resultintent = new Intent(speedRunQuizActivity.this, resultPageActivity.class);
            resultintent.putExtra("SCORE", score);
            resultintent.putExtra("QUIZ", "Speed Run");
            resultintent.putExtra("TIME", time);
            g_question_list.clear();
            startActivity(resultintent);
        }

        if(Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            //Checking if document exists with the same name as the user Email
            DocumentReference userRef = g_firestore.collection("Leaderboards").document("Speed Run").collection("Scores").document(userEmail);
            userRef.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot snapshot) {
                            //If document with email address as id exist - Only update this document/override
                            if (snapshot.exists()) {
                                getUserName();
                                checkscore = snapshot.getLong("Score").intValue();
                                checkUser = snapshot.getString("Username");
                                Intent intent = getIntent();
                                String difficultSetting = intent.getStringExtra("Difficulty");
                                Intent resultintent = new Intent(speedRunQuizActivity.this, resultPageActivity.class);
                                resultintent.putExtra("SCORE", score);
                                resultintent.putExtra("QUIZ", "Speed Run");
                                resultintent.putExtra("TIME", time);
                                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                String userEmail = firebaseUser.getEmail();
                                setUserStatistics();
                                //If the score on Firestore is less than the score just achieved, the info gets overridden as it is now the new high score
                                if (checkscore < score) {
                                    Map<String, Object> gkscore = new HashMap<>();
                                    gkscore.put("Username", userName);
                                    gkscore.put("Score", score);
                                    gkscore.put("Difficulty", difficultSetting);
                                    gkscore.put("QuizType", "Speed Run");
                                    g_firestore.collection("Leaderboards").document("Speed Run").collection("Scores").document(userEmail).set(gkscore);
                                    setUserStatistics();
                                    startActivity(resultintent);
                                    g_question_list.clear();
                                } else {
                                    g_question_list.clear();
                                    startActivity(resultintent);
                                }
                            }
                            //If email doesn't exist, just add a new document with the name of the currently logged in user
                            if (!snapshot.exists()) {
                                getUserName();
                                Intent intent = getIntent();
                                String difficultSetting = intent.getStringExtra("Difficulty");
                                Intent resultintent = new Intent(speedRunQuizActivity.this, resultPageActivity.class);
                                resultintent.putExtra("SCORE", score);
                                resultintent.putExtra("QUIZ", "Speed Run");
                                resultintent.putExtra("TIME", time);
                                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                String userEmail = firebaseUser.getEmail();
                                //If the score on Firestore is less than the score just achieved, the info gets overridden as it is now the new high score
                                Map<String, Object> gkscore = new HashMap<>();
                                gkscore.put("Username", userName);
                                gkscore.put("Score", score);
                                gkscore.put("Difficulty", difficultSetting);
                                gkscore.put("QuizType", "Speed Run");
                                g_firestore.collection("Leaderboards").document("Speed Run").collection("Scores").document(userEmail).set(gkscore);
                                setUserStatistics();
                                startActivity(resultintent);
                                g_question_list.clear();
                            }
                        }

                    });
        }
    }

    //Get/Write users average time to answer questions
    private void setUserStatistics()
    {

        Intent intent = getIntent();
        final String difficultSetting = intent.getStringExtra("Difficulty");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userEmail = firebaseUser.getEmail();
        //Checking if document exists with the same name as the user Email
        DocumentReference statsRef;
        if(difficultSetting.toString().equals("Easy"))
        {
            statsRef = g_firestore.collection("Statistics").document("Easy").collection("Users").document(userEmail);
            statsRef.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot snapshot) {
                            //If document with email address as id exist - Only update this document/override
                            if (snapshot.exists()) {
                                getUserName();
                                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                String userEmail = firebaseUser.getEmail();
                                checkstoredaverage = snapshot.getDouble("Average Time");
                                checkstoredTopicAverage = snapshot.getDouble("Speed Run Average");
                                //If there is no Topic Average stored, just add the time for this quiz only to a new field for the Selected Topic
                                if(checkstoredTopicAverage == null)
                                {
                                    g_firestore.collection("Statistics").document("Easy").collection("Users").document(userEmail).update("Speed Run Average", time);
                                }
                                //If there is a Topic Average Score stored, then calculate the new average
                                if(checkstoredTopicAverage != null)
                                {
                                    calcNewTopicAverage = (time + checkstoredTopicAverage) / 2;
                                    g_firestore.collection("Statistics").document("Easy").collection("Users").document(userEmail).update("Speed Run Average", calcNewTopicAverage);
                                }
                                //Always calculate the overall average  - This field will always be here if the snapshot exists, so no need for conditional null statement
                                calcNewAverage = (time + checkstoredaverage) / 2;
                                g_firestore.collection("Statistics").document("Easy").collection("Users").document(userEmail).update("Average Time", calcNewAverage);
                            }
                            //If email doesn't exist, just add a new document with the name of the currently logged in user
                            if (!snapshot.exists()) {
                                getUserName();
                                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                String userEmail = firebaseUser.getEmail();
                                Map<String, Object> gkstats = new HashMap<>();
                                gkstats.put("Username", userName);
                                gkstats.put("Average Time", time);
                                gkstats.put("Speed Run Average", time);
                                g_firestore.collection("Statistics").document("Easy").collection("Users").document(userEmail).set(gkstats);
                            }
                        }
                    });
        }
        if(difficultSetting.toString().equals("Medium"))
        {
            statsRef = g_firestore.collection("Statistics").document("Medium").collection("Users").document(userEmail);
            statsRef.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot snapshot) {
                            //If document with email address as id exist - Only update this document/override
                            if (snapshot.exists()) {
                                getUserName();
                                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                String userEmail = firebaseUser.getEmail();
                                checkstoredaverage = snapshot.getDouble("Average Time");
                                checkstoredTopicAverage = snapshot.getDouble("Speed Run Average");
                                //If there is no Topic Average stored, just add the time for this quiz only to a new field for the Selected Topic
                                if(checkstoredTopicAverage == null)
                                {
                                    g_firestore.collection("Statistics").document("Medium").collection("Users").document(userEmail).update("Speed Run Average", time);
                                }
                                //If there is a Topic Average Score stored, then calculate the new average
                                if(checkstoredTopicAverage != null)
                                {
                                    calcNewTopicAverage = (time + checkstoredTopicAverage) / 2;
                                    g_firestore.collection("Statistics").document("Medium").collection("Users").document(userEmail).update("Speed Run Average", calcNewTopicAverage);
                                }
                                //Always calculate the overall average  - This field will always be here if the snapshot exists, so no need for conditional null statement
                                calcNewAverage = (time + checkstoredaverage) / 2;
                                g_firestore.collection("Statistics").document("Medium").collection("Users").document(userEmail).update("Average Time", calcNewAverage);
                            }
                            //If email doesn't exist, just add a new document with the name of the currently logged in user
                            if (!snapshot.exists()) {
                                getUserName();
                                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                String userEmail = firebaseUser.getEmail();
                                Map<String, Object> gkstats = new HashMap<>();
                                gkstats.put("Username", userName);
                                gkstats.put("Average Time", time);
                                gkstats.put("Speed Run Average", time);
                                g_firestore.collection("Statistics").document("Medium").collection("Users").document(userEmail).set(gkstats);
                            }
                        }
                    });
        }
        if(difficultSetting.toString().equals("Hard"))
        {
            statsRef = g_firestore.collection("Statistics").document("Hard").collection("Users").document(userEmail);
            statsRef.get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot snapshot) {
                            //If document with email address as id exist - Only update this document/override
                            if (snapshot.exists()) {
                                getUserName();
                                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                String userEmail = firebaseUser.getEmail();
                                checkstoredaverage = snapshot.getDouble("Average Time");
                                checkstoredTopicAverage = snapshot.getDouble("Speed Run Average");
                                //If there is no Topic Average stored, just add the time for this quiz only to a new field for the Selected Topic
                                if(checkstoredTopicAverage == null)
                                {
                                    g_firestore.collection("Statistics").document("Hard").collection("Users").document(userEmail).update("Speed Run Average", time);
                                }
                                //If there is a Topic Average Score stored, then calculate the new average
                                if(checkstoredTopicAverage != null)
                                {
                                    calcNewTopicAverage = (time + checkstoredTopicAverage) / 2;
                                    g_firestore.collection("Statistics").document("Hard").collection("Users").document(userEmail).update("Speed Run Average", calcNewTopicAverage);
                                }
                                //Always calculate the overall average  - This field will always be here if the snapshot exists, so no need for conditional null statement
                                calcNewAverage = (time + checkstoredaverage) / 2;
                                g_firestore.collection("Statistics").document("Hard").collection("Users").document(userEmail).update("Average Time", calcNewAverage);
                            }
                            //If email doesn't exist, just add a new document with the name of the currently logged in user
                            if (!snapshot.exists()) {
                                getUserName();
                                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                String userEmail = firebaseUser.getEmail();
                                Map<String, Object> gkstats = new HashMap<>();
                                gkstats.put("Username", userName);
                                gkstats.put("Average Time", time);
                                gkstats.put("Speed Run Average", time);
                                g_firestore.collection("Statistics").document("Hard").collection("Users").document(userEmail).set(gkstats);
                            }
                        }
                    });
        }
    }
    private void countTimetoAnswerQuestions()
    {
        //Getting the time it takes to answer a question
        gettime = checkTimer.getText().toString();
        addtime = Integer.parseInt(gettime);
        tallytime = tallytime + addtime;
        timetoanswer.setText(String.valueOf(tallytime));
    }

    private void averageTime()
    {
        time = (float)tallytime / testingQuestionCount;
    }

    //Searches Collection USERS - Which has basic information of the user, and grabs the currently logged in users name
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
                                } else
                                    {

                                }
                            }
                        });
    }


}

