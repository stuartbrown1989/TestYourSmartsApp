package com.example.testyoursmarts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.navigation.ui.AppBarConfiguration;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.BLUE;
import static android.graphics.Color.TRANSPARENT;
import static android.graphics.Color.WHITE;
import static android.provider.CalendarContract.CalendarCache.URI;
import static androidx.constraintlayout.widget.ConstraintProperties.BOTTOM;
import static androidx.constraintlayout.widget.ConstraintProperties.LEFT;
import static androidx.constraintlayout.widget.ConstraintProperties.PARENT_ID;
import static androidx.constraintlayout.widget.ConstraintProperties.RIGHT;
import static androidx.constraintlayout.widget.ConstraintProperties.TOP;
import static com.example.testyoursmarts.dbQuery.g_question_list;
import static com.example.testyoursmarts.dbQuery.pictureQ_List;
import static com.example.testyoursmarts.dbQuery.picture_firestore;
import static java.lang.Thread.sleep;

public class pictureQuizActivity extends AppCompatActivity {
    private RadioGroup buttonGroup;
    private RadioButton op1;
    private RadioButton op2;
    private RadioButton op3;
    private RadioButton op4;
    private boolean answered;
    private ColorStateList textColorDefaultRb;
    private Button confirmNext;
    private int correctAnswer;
    private TextView theDifficulty, headerTimer, changeImageView;
    private CountDownTimer cTimer = null;
    private String userName, checkUser;
    private int checkscore;
    private boolean isImageFitToScreen;
    public static FirebaseFirestore g_firestore;
    private ImageView pictureQuestion;
    private Dialog loadingDialog;
    private ArrayList<Integer> questionIDs = new ArrayList<Integer>();

    //UserStats Variables
    private String gettime;
    private String guestLog = "Guest";
    private TextView timetoanswer, clickingImageText;
    private double checkstoredaverage, calcNewAverage, calcNewTopicAverage;
    private Double checkstoredTopicAverage;
    private int addtime = 0;
    private int tallytime = 0;
    private int calculatetime;
    private double time;

    private ConstraintLayout pictureQuizLayout;
    private TextView textViewQuestion;
    private TextView textViewScore;
    private TextView textViewQuestionCount, timer;
    private int questionCount;
    private int questionTotal = 10;
    private int score = 0;
    private int potentialScore;
    private pictureQuizModel pictureCurrentQuestion;

    private AppBarConfiguration mAppBarConfiguration;
    private BottomNavigationView bottomNavigationView;
    private FrameLayout main_frame;
    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemReselectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    switch (menuItem.getItemId()) {
                        case R.id.nav_home:
                            pictureQ_List.clear();
                            pictureQ_List.removeAll(Collections.emptyList());
                            Intent intentHome = new Intent(pictureQuizActivity.this, MainActivity.class);
                            startActivity(intentHome);
                            return true;

                        case R.id.nav_leaderboard:
                            pictureQ_List.clear();
                            pictureQ_List.removeAll(Collections.emptyList());
                            Intent intentLeaderboard= new Intent(pictureQuizActivity.this, leaderboardChoiceActivity.class);
                            startActivity(intentLeaderboard);
                            return true;

                        case R.id.nav_account:
                            Intent intentAccount = new Intent(pictureQuizActivity.this, account_page.class);
                            startActivity(intentAccount);
                            return true;

                        case R.id.nav_logout:
                            pictureQ_List.clear();
                            pictureQ_List.removeAll(Collections.emptyList());
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
        setContentView(R.layout.activity_picture_quiz);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Picture Quiz");
        bottomNavigationView = findViewById(R.id.top_nav_bar);
        main_frame = findViewById(R.id.main_frame);
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemReselectedListener);
        final AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);
        //Initializing Variables
        timetoanswer = findViewById(R.id.picture_timetoanswer);
        pictureQuizLayout = findViewById(R.id.pictureQuiz_constraint);
        clickingImageText = findViewById(R.id.pictureQ_click_image);
        pictureQuestion = findViewById(R.id.pictureQ_image_view);
        headerTimer = findViewById(R.id.PictureQtimeRemaining);
        changeImageView = findViewById(R.id.pictureQ_click_image);
        theDifficulty = findViewById(R.id.PictureQ_difficulty);
        buttonGroup = findViewById(R.id.radio_group);
        op1 = findViewById(R.id.radio_button1);
        op2 = findViewById(R.id.radio_button2);
        op3 = findViewById(R.id.radio_button3);
        op4 = findViewById(R.id.radio_button4);
        textViewQuestion = findViewById(R.id.PictureQtext_view_question);
        textViewScore = findViewById(R.id.PictureQtext_view_new_score);
        textViewQuestionCount = findViewById(R.id.PictureQtext_view_question_count);
        textColorDefaultRb = op1.getTextColors();
        confirmNext = findViewById(R.id.PictureQbutton_confirm_next);
        timer = findViewById(R.id.PictureQtext_view_timer);
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemReselectedListener);
        Intent intent = getIntent();
        //Getting the difficulty level set from the confirm quiz page
        String difficultSetting = intent.getStringExtra("Difficulty");
        theDifficulty.setText("Difficulty: " + difficultSetting);
        //Accessing firestore
        g_firestore = FirebaseFirestore.getInstance();
        getUserName();
        //Removes any existing elements in the array
        // (Shouldn't be any, but this covers if the back button is pressed and the user comes back to this activity
        pictureQ_List.clear();
        pictureQ_List.removeAll(Collections.emptyList());
        //Loading up questions
        getAllPictureQuestions();
        //Retrieving difficulty from Confirm Quiz
        switch (difficultSetting) {
            case "Easy":

                potentialScore = questionTotal;
                textViewScore.setText("Score: " + score + "/" + potentialScore);
                break;
            case "Medium":
                potentialScore = questionTotal * 2;
                textViewScore.setText("Score: " + score + "/" + potentialScore);
                break;
            case "Hard":
                potentialScore = questionTotal * 3;
                textViewScore.setText("Score: " + score + "/" + potentialScore);
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
                    }
                    else {
                        Toast.makeText(pictureQuizActivity.this, "Please select answer", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    countTimetoAnswerQuestions();
                    setQuestion();
                    startTimer();
                }
            }
        });
    }

    //Get all the Easy Picture Questions
    private void getAllPictureQuestions()
    {
        Intent intent = getIntent();
        //Getting the difficulty level set from the confirm quiz page
        String difficultSetting = intent.getStringExtra("Difficulty");
        String whichQuestions = "";
        if(difficultSetting.equals("Easy"))
        {
            whichQuestions = "Easy Questions";
        }
        if(difficultSetting.equals("Medium"))
        {
            whichQuestions = "Medium Questions";
        }
        if(difficultSetting.equals("Hard"))
        {
            whichQuestions = "Hard Questions";
        }
        g_firestore.collection("Picture Questions").document(difficultSetting).collection(whichQuestions)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        //Get collection info and store it to the list
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            pictureQ_List.add(new pictureQuizModel(
                                    doc.getString("Question"),
                                    doc.getString("Option_a"),
                                    doc.getString("Option_b"),
                                    doc.getString("Option_c"),
                                    doc.getString("Option_d"),
                                    Objects.requireNonNull(doc.getLong("Answer")).intValue(),
                                    doc.getString("Image")
                            ));
                            Collections.shuffle(pictureQ_List);
                        }
                        setQuestion();
                        startTimer();
                    }
                });
    }


    private void setQuestion()
    {
        smallImageQuestionView();
        isImageFitToScreen=false;
        ConstraintLayout constraintLayout = findViewById(R.id.pictureQuiz_constraint);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        constraintSet.connect(R.id.pictureQ_image_view,ConstraintSet.LEFT, PARENT_ID,ConstraintSet.LEFT, 100);
        constraintSet.connect(R.id.pictureQ_image_view,ConstraintSet.RIGHT, PARENT_ID,ConstraintSet.RIGHT, 100);
        constraintSet.connect(R.id.pictureQ_image_view,ConstraintSet.BOTTOM, clickingImageText.getId() , ConstraintSet.TOP, 0);
        constraintSet.constrainHeight(R.id.pictureQ_image_view, 500);
        constraintSet.constrainWidth(R.id.pictureQ_image_view, 760);

        constraintSet.applyTo(constraintLayout);
        op1.setTextColor(textColorDefaultRb);
        op2.setTextColor(textColorDefaultRb);
        op3.setTextColor(textColorDefaultRb);
        op4.setTextColor(textColorDefaultRb);
        buttonGroup.clearCheck();
        String qText = "Question: ";
        String slash = "/";
        String confirm = "Confirm";

        //If current question number is less than total number of questions, keep going, otherwise finish
        if (questionCount < questionTotal) {
//
            //Allows all buttons to be clickable
            for (int i = 0; i < buttonGroup.getChildCount(); i++) {
                buttonGroup.getChildAt(i).setEnabled(true);
            }
            pictureCurrentQuestion = pictureQ_List.get(questionCount);
            textViewQuestion.setText(pictureCurrentQuestion.getPictureQuestion());
            op1.setText(pictureCurrentQuestion.getPictureOption1());
            op2.setText(pictureCurrentQuestion.getPictureOption2());
            op3.setText(pictureCurrentQuestion.getPictureOption3());
            op4.setText(pictureCurrentQuestion.getPictureOption4());

            //Converting the URL via Picasso to get the image that is being referenced
          Picasso.get()
                  .load(pictureCurrentQuestion.getTheImage()).noFade()
                   .fit()
                   //.centerCrop()
                   .into(pictureQuestion);
        pictureQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isImageFitToScreen) {
                    isImageFitToScreen=false;
                    ConstraintLayout constraintLayout = findViewById(R.id.pictureQuiz_constraint);
                    ConstraintSet constraintSet = new ConstraintSet();
                    constraintSet.clone(constraintLayout);
                    constraintSet.connect(R.id.pictureQ_image_view,ConstraintSet.LEFT, PARENT_ID,ConstraintSet.LEFT, 100);
                    constraintSet.connect(R.id.pictureQ_image_view,ConstraintSet.RIGHT, PARENT_ID,ConstraintSet.RIGHT, 100);
                    constraintSet.connect(R.id.pictureQ_image_view,ConstraintSet.BOTTOM, clickingImageText.getId() , ConstraintSet.TOP, 0);
                    constraintSet.constrainHeight(R.id.pictureQ_image_view, 500);
                    constraintSet.constrainWidth(R.id.pictureQ_image_view, 760);
                    constraintSet.applyTo(constraintLayout);
                    smallImageQuestionView();


                }else{
                    isImageFitToScreen=true;
                    pictureQuestion.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT));
                    pictureQuestion.setScaleType(ImageView.ScaleType.FIT_XY);
                    fullImageQuestionView();
            }

            }
        });
            correctAnswer = pictureCurrentQuestion.getPictureCorrectAnswer();
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

        if(difficultSetting.toString().equals("Easy")) {
            if (answerNr == correctAnswer) {
                score++;
                potentialScore = questionTotal;
                textViewScore.setText("Score: " + score + "/" + potentialScore);
            }
        }
        if(difficultSetting.toString().equals("Medium")) {
            if (answerNr == correctAnswer) {
                score = score + 2;
                potentialScore = questionTotal * 2;
                textViewScore.setText("Score: " + score + "/" + potentialScore);
            }
        }
        if(difficultSetting.toString().equals("Hard")) {
            if (answerNr == correctAnswer) {
                score = score + 3;
                potentialScore = questionTotal * 3;
                textViewScore.setText("Score: " + score + "/" + potentialScore);
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

        switch (pictureCurrentQuestion.getPictureCorrectAnswer())
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
            confirmNext.setText("Next");
            //Disables the radio buttons - So the user can't select the right answer after being shown the correct answer
            for (int i = 0; i < buttonGroup.getChildCount(); i++) {
                buttonGroup.getChildAt(i).setEnabled(false);
            }
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
        cTimer = new CountDownTimer(16000, 1000)
        {

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

    //Fetches the Leaderboards data
    private void getuserScores()
    {
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
            Intent resultintent = new Intent(pictureQuizActivity.this, resultPageActivity.class);
            resultintent.putExtra("SCORE", score);
            resultintent.putExtra("QUIZ", "Picture");
            resultintent.putExtra("TIME", time);
            g_question_list.clear();
            startActivity(resultintent);
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches())
        {
            Intent resultintent = new Intent(pictureQuizActivity.this, resultPageActivity.class);
            resultintent.putExtra("SCORE", score);
            resultintent.putExtra("QUIZ", "Picture");
            resultintent.putExtra("TIME", time);
            g_question_list.clear();
            startActivity(resultintent);
        }

        if(Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            //Checking if document exists with the same name as the user Email
            DocumentReference userRef = g_firestore.collection("Leaderboards").document("Picture").collection("Scores").document(userEmail);
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
                                Intent resultintent = new Intent(pictureQuizActivity.this, resultPageActivity.class);
                                resultintent.putExtra("SCORE", score);
                                resultintent.putExtra("QUIZ", "Picture");
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
                                    gkscore.put("QuizType", "Picture");
                                    g_firestore.collection("Leaderboards").document("Picture").collection("Scores").document(userEmail).set(gkscore);
                                    setUserStatistics();
                                    startActivity(resultintent);
                                    pictureQ_List.clear();
                                    pictureQ_List.removeAll(Collections.emptyList());
                                    questionIDs.removeAll(Collections.emptyList());
                                } else {
                                    startActivity(resultintent);
                                    pictureQ_List.clear();
                                    pictureQ_List.removeAll(Collections.emptyList());
                                    questionIDs.removeAll(Collections.emptyList());
                                }

                            }
                            //If email doesn't exist, just add a new document with the name of the currently logged in user
                            if (!snapshot.exists()) {
                                getUserName();
                                Intent intent = getIntent();
                                String difficultSetting = intent.getStringExtra("Difficulty");
                                Intent resultintent = new Intent(pictureQuizActivity.this, resultPageActivity.class);
                                resultintent.putExtra("SCORE", score);
                                resultintent.putExtra("QUIZ", "Picture");
                                resultintent.putExtra("TIME", time);
                                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                String userEmail = firebaseUser.getEmail();
                                //If the score on Firestore is less than the score just achieved, the info gets overridden as it is now the new high score

                                Map<String, Object> gkscore = new HashMap<>();
                                gkscore.put("Username", userName);
                                gkscore.put("Score", score);
                                gkscore.put("Difficulty", difficultSetting);
                                gkscore.put("QuizType", "Picture");
                                g_firestore.collection("Leaderboards").document("Picture").collection("Scores").document(userEmail).set(gkscore);
                                setUserStatistics();
                                startActivity(resultintent);
                                pictureQ_List.clear();
                                pictureQ_List.removeAll(Collections.emptyList());
                                questionIDs.removeAll(Collections.emptyList());

                            }
                        }
                    });
        }
    }

    private void setUserStatistics()
    {

        Intent intent = getIntent();
        final String difficultSetting = intent.getStringExtra("Difficulty");
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
                                checkstoredTopicAverage = snapshot.getDouble("Picture Average");
                                //If there is no Topic Average stored, just add the time for this quiz only to a new field for the Selected Topic
                                if(checkstoredTopicAverage == null)
                                {
                                    g_firestore.collection("Statistics").document(difficultSetting).collection("Users").document(userEmail).update("Picture Average", time);
                                }
                                //If there is a Topic Average Score stored, then calculate the new average
                                if(checkstoredTopicAverage != null)
                                {
                                    calcNewTopicAverage = (time + checkstoredTopicAverage) / 2;
                                    g_firestore.collection("Statistics").document(difficultSetting).collection("Users").document(userEmail).update("Picture Average", calcNewTopicAverage);
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
                                gkstats.put("Picture Average", time);
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
                                } else {

                                }
                            }
                        });
    }

    private void fullImageQuestionView()
    {
        textViewQuestion.setVisibility(View.INVISIBLE);
        op1.setVisibility(View.INVISIBLE);
        op2.setVisibility(View.INVISIBLE);
        op3.setVisibility(View.INVISIBLE);
        op4.setVisibility(View.INVISIBLE);
        textViewQuestionCount.setVisibility(View.INVISIBLE);
        theDifficulty.setVisibility(View.INVISIBLE);
        textViewScore.setVisibility(View.INVISIBLE);
        timer.setBackgroundColor(getResources().getColor(R.color.clearWhite));
        timer.setTextSize(30);
        headerTimer.setBackgroundColor(getResources().getColor(R.color.clearWhite));
        headerTimer.setTextSize(20);
        changeImageView.setText("CLICK IMAGE TO GO BACK TO\n ORIGINAL SIZE AND QUESTION");
        changeImageView.setBackgroundColor(getResources().getColor(R.color.clearWhite));
    }

    private void smallImageQuestionView()
    {
        textViewQuestion.setVisibility(View.VISIBLE);
        op1.setVisibility(View.VISIBLE);
        op2.setVisibility(View.VISIBLE);
        op3.setVisibility(View.VISIBLE);
        op4.setVisibility(View.VISIBLE);
        textViewQuestionCount.setVisibility(View.VISIBLE);
        theDifficulty.setVisibility(View.VISIBLE);
        textViewScore.setVisibility(View.VISIBLE);
        timer.setBackgroundColor(TRANSPARENT);
        timer.setTextSize(12);
        headerTimer.setBackgroundColor(TRANSPARENT);
        headerTimer.setTextSize(12);
        changeImageView.setText("CLICK IMAGE\n FOR FULLSCREEN");
        changeImageView.setBackgroundColor(TRANSPARENT);


    }
}