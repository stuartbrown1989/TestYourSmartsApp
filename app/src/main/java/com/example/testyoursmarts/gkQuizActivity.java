package com.example.testyoursmarts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.navigation.ui.AppBarConfiguration;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.se.omapi.Session;
import android.util.Log;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.example.testyoursmarts.dbQuery.g_catList;
import static com.example.testyoursmarts.dbQuery.g_question_list;
import static com.example.testyoursmarts.dbQuery.g_selected_cat_index;
import static com.example.testyoursmarts.dbQuery.loadquestions;

public class gkQuizActivity extends AppCompatActivity {

    private RadioGroup buttonGroup;
    private RadioButton op1;
    private RadioButton op2;
    private RadioButton op3;
    private RadioButton op4;
    private boolean answered;
    private ColorStateList textColorDefaultRb;
    private Button confirmNext;
    private int correctAnswer;
    private TextView displayifCorrect;


    public static FirebaseFirestore g_firestore;
    private List <questionsModel> questionslist;
    private Dialog loadingDialog;

    private TextView textViewQuestion;
    private TextView textViewScore;
    private TextView textViewQuestionCount;
    private int questionCount;
    private int questionTotal = 10;
    private int score = 0;

    private List<questionsModel> questionList;
    private questionsModel currentQuestion;

    private Button finishBtn;
    private AppBarConfiguration mAppBarConfiguration;
    private BottomNavigationView bottomNavigationView;
    private FrameLayout main_frame;
    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemReselectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    switch (menuItem.getItemId()) {
                        case R.id.nav_home:
                            Intent intentHome = new Intent(gkQuizActivity.this, MainActivity.class);
                            startActivity(intentHome);
                            return true;

                        case R.id.nav_leaderboard:
                            Intent intentLeaderboard= new Intent(gkQuizActivity.this, leaderboardChoiceActivity.class);
                            startActivity(intentLeaderboard);
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
        setContentView(R.layout.activity_gk_quiz);

        //Dummy button TO BE REMOVED UPON COMPLETION
        finishBtn = findViewById(R.id.button_finish);

        //Initializing Variables
        buttonGroup = findViewById(R.id.radio_group);
        op1 = findViewById(R.id.radio_button1);
        op2 = findViewById(R.id.radio_button2);
        op3 = findViewById(R.id.radio_button3);
        op4 = findViewById(R.id.radio_button4);
        textViewQuestion = findViewById(R.id.text_view_question);
        textViewScore = findViewById(R.id.text_view_current_score);
        textViewQuestionCount = findViewById(R.id.text_view_question_count);
        textColorDefaultRb = op1.getTextColors();
        displayifCorrect = findViewById(R.id.co)

        //Accessing firestore
        g_firestore = FirebaseFirestore.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        bottomNavigationView = findViewById(R.id.top_nav_bar);
        main_frame = findViewById(R.id.main_frame);
        confirmNext = findViewById(R.id.button_confirm_next);

        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemReselectedListener);
        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent result = new Intent(gkQuizActivity.this, resultPageActivity.class);
                startActivity(result);
            }
        });

        getGKQuestions();



        confirmNext.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (!answered)
                {
                    if(op1.isChecked() || op2.isChecked() || op3.isChecked() || op4.isChecked())
                    {
                        checkAnswer();
                    }else
                    {
                        Toast.makeText(gkQuizActivity.this, "Please select answer", Toast.LENGTH_SHORT).show();
                    }
                } else
                {
                    getGKQuestions();
                }
            }
        });
    }

    private void getGKQuestions()
    {

        g_firestore.collection("Questions")
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

                        setQuestion();
                    }
                });
    }

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

    //If current question number is less than total number of questions, keep going, otherwise finish
    if(questionCount < questionTotal) {

        currentQuestion = g_question_list.get(questionCount);
        textViewQuestion.setText(currentQuestion.getQuestion());
        op1.setText(currentQuestion.getOption1());
        op2.setText(currentQuestion.getOption2());
        op3.setText(currentQuestion.getOption3());
        op4.setText(currentQuestion.getOption4());
        correctAnswer = g_question_list.get(0).getCorrectAnswer();
        questionCount++;
        textViewQuestionCount.setText(qText + questionCount + slash + questionTotal);
        answered = false;
        confirmNext.setText(confirm);
    }


}
//If correct answer from firestore matches selected button, increment score by 1
private void checkAnswer()
{


    answered = true;
    RadioButton rbSelected = findViewById(buttonGroup.getCheckedRadioButtonId());
    int answerNr = buttonGroup.indexOfChild(rbSelected) + 1;

    if(answerNr == correctAnswer)
    {
        String scoreText = "SCORE: ";
        score++;
        textViewScore.setText(scoreText + score);

    }
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
            confirmNext.setText("Next");
        }else
        {
            confirmNext.setText("Finish");
            Intent intent = new Intent(gkQuizActivity.this, resultPageActivity.class);
            intent.putExtra("SCORE", score);
            startActivity(intent);
            g_question_list.clear();

        }
    }

}

