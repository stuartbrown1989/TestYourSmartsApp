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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.example.testyoursmarts.dbQuery.g_question_list;

public class topicQuizActivity extends AppCompatActivity {
    private RadioGroup buttonGroup;
    private RadioButton op1;
    private RadioButton op2;
    private RadioButton op3;
    private RadioButton op4;
    private boolean answered;
    private ColorStateList textColorDefaultRb;
    private Button confirmNext;
    private int correctAnswer;
    private TextView theDifficulty, headerTimer;
    private CountDownTimer cTimer = null;
    private String userName, checkUser;
    private int checkscore;
    public static FirebaseFirestore g_firestore;
    public static List<questionsModel> g_question_list = new ArrayList<>();

    //UserStats Variables
    private String gettime, topicAverage;
    private String guestLog = "Guest";
    private TextView timetoanswer;
    private double checkstoredaverage, calcNewAverage, calcNewTopicAverage;
    private Double checkstoredTopicAverage ;
    private int addtime = 0;
    private int tallytime = 0;
    private int calculatetime;
    private double time;
    private String quizDifficulty;
    private String actualTopic;

    private Dialog loadingDialog;
    private ArrayList<Integer> questionIDs = new ArrayList<Integer>();

    private TextView textViewQuestion;
    private TextView textViewScore;
    private TextView textViewQuestionCount, timer;
    private int questionCount;
    private int questionTotal = 10;
    private int score = 0;
    private questionsModel currentQuestion;
    private int potentialScore;

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
                            Intent intentHome = new Intent(topicQuizActivity.this, MainActivity.class);
                            startActivity(intentHome);
                            return true;

                        case R.id.nav_leaderboard:
                            g_question_list.clear();
                            g_question_list.removeAll(Collections.emptyList());
                            Intent intentLeaderboard= new Intent(topicQuizActivity.this, leaderboardChoiceActivity.class);
                            startActivity(intentLeaderboard);
                            return true;

                        case R.id.nav_account:
                            Intent intentAccount = new Intent(topicQuizActivity.this, account_page.class);
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
        setContentView(R.layout.activity_science_quiz);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bottomNavigationView = findViewById(R.id.top_nav_bar);
        main_frame = findViewById(R.id.main_frame);
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemReselectedListener);
        final AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);
        //Initializing Variables
        timetoanswer = findViewById(R.id.Science_timetoanswer);
        headerTimer = findViewById(R.id.SciencetimeRemaining);
        theDifficulty = findViewById(R.id.Science_difficulty);
        buttonGroup = findViewById(R.id.radio_group);
        op1 = findViewById(R.id.radio_button1);
        op2 = findViewById(R.id.radio_button2);
        op3 = findViewById(R.id.radio_button3);
        op4 = findViewById(R.id.radio_button4);
        textViewQuestion = findViewById(R.id.Sciencetext_view_question);
        textViewScore = findViewById(R.id.Sciencetext_view_new_score);
        textViewQuestionCount = findViewById(R.id.Sciencetext_view_question_count);
        textColorDefaultRb = op1.getTextColors();
        confirmNext = findViewById(R.id.Sciencebutton_confirm_next);
        timer = findViewById(R.id.Sciencetext_view_timer);
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemReselectedListener);
        Intent intent = getIntent();
        //Getting the difficulty level set from the confirm quiz page
        String difficultSetting = intent.getStringExtra("Difficulty");
        actualTopic = intent.getStringExtra("TopicQuiz");

        theDifficulty.setText("Difficulty: " + difficultSetting);
        //Accessing firestore
        g_firestore = FirebaseFirestore.getInstance();
        getUserName();
        //Removes any existing elements in the array
        g_question_list.clear();
        questionIDs.removeAll(Collections.emptyList());
                //Loading up questions
                getTopicQuestions();
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
                        Toast.makeText(topicQuizActivity.this, "Please select answer", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    countTimetoAnswerQuestions();
                    setQuestion();
                    startTimer();
                }
            }
        });
    }
    //Get questions from Firestore
    private void getTopicQuestions()
    {
        Intent intent = getIntent();
        //Getting the Difficulty setting from confirm quiz
        String checkDifficulty = intent.getStringExtra("Difficulty");
        //Getting the topic selected from confirm quiz
        String theTopic = intent.getStringExtra("QuizTopic");
        //Setting the Action bar title based on what the Topic is
        getSupportActionBar().setTitle(theTopic + " Quiz");
        g_firestore.collection("Questions").document(checkDifficulty).collection(theTopic)
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
                        setQuestion();
                        startTimer();
                    }
                });
    }

    //Set the question function based on Retrieved documents
    private void setQuestion()
    {
        op1.setTextColor(textColorDefaultRb);
        op2.setTextColor(textColorDefaultRb);
        op3.setTextColor(textColorDefaultRb);
        op4.setTextColor(textColorDefaultRb);
        buttonGroup.clearCheck();
        String qText = "Question: ";
        String slash = "/";
        String confirm = "Confirm";
        //If current question number is less than total number of questions, re activate the Button group
        if (questionCount < questionTotal) {
            for (int i = 0; i < buttonGroup.getChildCount(); i++) {
                buttonGroup.getChildAt(i).setEnabled(true);
            }
            //Current question will be the question in the arraylist that is the number of the question count
            currentQuestion = g_question_list.get(questionCount);
            //Display question in text view and show options beside the radio buttons
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

    //If correct answer from firestore matches selected button, increment score by 1, 2 or 3 (Depending on Difficulty)
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
                potentialScore = questionTotal;
                textViewScore.setText("SCORE: " + score + "/" + potentialScore);
            }
        }
        if(difficultSetting.toString().equals("Medium")) {
            if (answerNr == correctAnswer) {
                score = score + 2;
                potentialScore = questionTotal * 2;
                textViewScore.setText("SCORE: " + score + "/" + potentialScore);
            }
        }
        if(difficultSetting.toString().equals("Hard")) {
            if (answerNr == correctAnswer) {
                score = score + 3;
                potentialScore = questionTotal *3;
                textViewScore.setText("SCORE: " + score + "/" + potentialScore);
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
            countTimetoAnswerQuestions();
            headerTimer.setTextColor(Color.RED);
            headerTimer.setText("You have completed the Quiz! Press finish to see your results");
            confirmNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getuserScores();
                }
            });
        }
    }


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

    private void getuserScores()
    {
        averageTime();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userEmail = firebaseUser.getEmail();
        Intent intent = getIntent();
        final String whatTopic = intent.getStringExtra("QuizTopic");

        //If the current logged in user is a Guest, do this - Writes no data to leader board
        if(userEmail == null)
        {
            userEmail = "Guest";
        }
        if(userEmail == guestLog)
        {
            Intent resultintent = new Intent(topicQuizActivity.this, resultPageActivity.class);
            resultintent.putExtra("SCORE", score);
            resultintent.putExtra("QUIZ", actualTopic +  " Quiz");
            resultintent.putExtra("TIME", time);
            g_question_list.clear();
            startActivity(resultintent);
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches())
        {
            Intent resultintent = new Intent(topicQuizActivity.this, resultPageActivity.class);
            resultintent.putExtra("SCORE", score);
            resultintent.putExtra("QUIZ", actualTopic +  " Quiz");
            resultintent.putExtra("TIME", time);
            g_question_list.clear();
            startActivity(resultintent);
        }

        final String whichDoc;
        //If topic is GK, then search Document of this name for collection, otherwise search for Topic
        if(whatTopic.equals("GK"))
        {
            whichDoc = "General Knowledge";
        }
        else
        {
            whichDoc = "Topic";
        }
        if(Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            //Checking if document exists with the same name as the user Email
            DocumentReference userRef = g_firestore.collection("Leaderboards").document(whichDoc).collection(whatTopic).document(userEmail);
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
                                Intent resultintent = new Intent(topicQuizActivity.this, resultPageActivity.class);
                                resultintent.putExtra("SCORE", score);
                                resultintent.putExtra("QUIZ", whatTopic + " Quiz");
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
                                    gkscore.put("QuizType", whatTopic);
                                    g_firestore.collection("Leaderboards").document(whichDoc).collection(whatTopic).document(userEmail).set(gkscore);
                                    setUserStatistics();
                                    startActivity(resultintent);
                                    g_question_list.clear();
                                    questionIDs.removeAll(Collections.emptyList());
                                } else {
                                    startActivity(resultintent);
                                    g_question_list.clear();
                                    questionIDs.removeAll(Collections.emptyList());
                                }
                            }
                            //If email doesn't exist, just add a new document with the name of the currently logged in user
                            if (!snapshot.exists()) {
                                getUserName();
                                Intent intent = getIntent();
                                String difficultSetting = intent.getStringExtra("Difficulty");
                                Intent resultintent = new Intent(topicQuizActivity.this, resultPageActivity.class);
                                resultintent.putExtra("SCORE", score);
                                resultintent.putExtra("QUIZ", whatTopic + " Quiz");
                                resultintent.putExtra("TIME", time);
                                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                String userEmail = firebaseUser.getEmail();
                                setUserStatistics();
                                Map<String, Object> gkscore = new HashMap<>();
                                gkscore.put("Username", userName);
                                gkscore.put("Score", score);
                                gkscore.put("Difficulty", difficultSetting);
                                gkscore.put("QuizType", whatTopic);
                                g_firestore.collection("Leaderboards").document(whichDoc).collection(whatTopic).document(userEmail).set(gkscore);
                                startActivity(resultintent);
                                g_question_list.clear();
                                questionIDs.removeAll(Collections.emptyList());
                                startActivity(resultintent);
                                g_question_list.clear();
                                questionIDs.removeAll(Collections.emptyList());

                            }
                        }
                    });
        }
    }
//Setting the Average time taken to answer questions statistics based on difficulty/Topic
    private void setUserStatistics()
    {
        Intent intent = getIntent();
        final String difficultSetting = intent.getStringExtra("Difficulty");
        final String actualTopic = intent.getStringExtra("QuizTopic");
        if(actualTopic.equals("GK"))
        {
            topicAverage = "GK Average";
        }
        if(actualTopic.equals("FilmTV"))
        {
            topicAverage = "FilmTV Average";
        }
        if(actualTopic.equals("Science"))
        {
            topicAverage = "Science Average";
        }
        if(actualTopic.equals("Geography"))
        {
            topicAverage = "Geography Average";
        }
        if(actualTopic.equals("History"))
        {
            topicAverage = "History Average";
        }
        if(actualTopic.equals("Music"))
        {
            topicAverage = "Music Average";
        }
        if(actualTopic.equals("Sports"))
        {
            topicAverage = "Sports Average";
        }
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userEmail = firebaseUser.getEmail();
        //Checking if document exists with the same name as the user Email
        DocumentReference statsRef;
            statsRef = g_firestore.collection("Statistics").document(difficultSetting).collection("Users").document(userEmail);
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
                                checkstoredTopicAverage = snapshot.getDouble(topicAverage);
                                //If there is no Topic Average stored, just add the time for this quiz only to a new field for the Selected Topic
                                if(checkstoredTopicAverage == null)
                                {
                                    g_firestore.collection("Statistics").document(difficultSetting).collection("Users").document(userEmail).update(topicAverage, time);
                                }
                                //If there is a Topic Average Score stored, then calculate the new average
                                if(checkstoredTopicAverage != null)
                                {
                                    calcNewTopicAverage = (time + checkstoredTopicAverage) / 2;
                                    g_firestore.collection("Statistics").document(difficultSetting).collection("Users").document(userEmail).update(topicAverage, calcNewTopicAverage);
                                }
                                //Always calculate the overall average  - This field will always be here if the snapshot exists, so no need for conditional null statement
                                calcNewAverage = (time + checkstoredaverage) / 2;
                                g_firestore.collection("Statistics").document(difficultSetting).collection("Users").document(userEmail).update("Average Time", calcNewAverage);
                            }
                            //If email doesn't exist, just add a new document with the name of the currently logged in user
                            if (!snapshot.exists()) {
                                getUserName();
                                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                String userEmail = firebaseUser.getEmail();
                                Map<String, Object> gkstats = new HashMap<>();
                                gkstats.put("Username", userName);
                                gkstats.put("Average Time", time);
                                gkstats.put(topicAverage, time);
                                g_firestore.collection("Statistics").document(difficultSetting).collection("Users").document(userEmail).set(gkstats);
                            }
                        }
                    });
        }

    private void countTimetoAnswerQuestions()
    {
        //Getting the time it takes to answer a question
        gettime = timer.getText().toString();
        addtime = Integer.parseInt(gettime);
        calculatetime = 15 - addtime;
        tallytime = tallytime + calculatetime;
        timetoanswer.setText(String.valueOf(tallytime));
    }

    private void averageTime()
    {
        time = (float)tallytime / 10;
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
                                        userName = document.getString("NAME");
                                    }
                                } else {
                                }
                            }
                        });
    }


}