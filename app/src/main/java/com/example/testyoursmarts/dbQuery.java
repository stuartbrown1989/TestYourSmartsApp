package com.example.testyoursmarts;

import android.os.Build;
import android.util.ArrayMap;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class dbQuery {
    // Access a Cloud Firestore instance from your Activity

  public static FirebaseFirestore g_firestore;
  public static FirebaseFirestore picture_firestore;
  public static List<CategoryModel> g_catList = new ArrayList<>();
  public static int g_selected_cat_index = 0;
  public static List<questionsModel> g_question_list = new ArrayList<>();
  public static List<pictureQuizModel> pictureQ_List = new ArrayList<>();
    private List <questionsModel> questionslist;
    public static List<userScoresModel> userScoreList = new ArrayList<>();
    public static List<statisticsModel> statisticTime = new ArrayList<>();
  public static List<TestModel> g_testList = new ArrayList<>();
  public static int g_selected_test_index = 0;


  public static void createUserData(String email, String name, final myCompleteListener completeListener)
  {
      Map<String, Object> userData =  new android.util.ArrayMap<>();
      userData.put("EMAIL_ID", email);
      userData.put("NAME", name);
      userData.put("TOTAL_SCORE", 0);
      DocumentReference userDoc = g_firestore.collection("USERS").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
      WriteBatch batch = g_firestore.batch();
      batch.set(userDoc,userData);

      DocumentReference countDoc = g_firestore.collection("USERS").document("TOTAL_USERS");
      batch.update(countDoc, "COUNT", FieldValue.increment(1));

      batch.commit()
            .addOnSuccessListener(new OnSuccessListener<Void>(){
                @Override
                public void onSuccess(Void aVoid)
                {
                    completeListener.onSuccess();

                }

            })
              .addOnFailureListener(new OnFailureListener() {
                  @Override
                  public void onFailure(@NonNull Exception e)
                  {
                    completeListener.onFailure();
                  }
              });
  }

    public static void loadquestions(final myCompleteListener completeListenerQuestion)
    {
        g_firestore.collection("Questions")
                .whereEqualTo("Category", g_catList.get(g_selected_cat_index).getDocID())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        //Get collection info and store it to the list
                        for(DocumentSnapshot doc : queryDocumentSnapshots)
                        {
                            g_question_list.add(new questionsModel(

                                    doc.getString("Question"),
                                    doc.getString("Option_a"),
                                    doc.getString("Option_b"),
                                    doc.getString("Option_c"),
                                    doc.getString("Option_d"),
                                    doc.getLong("Answer").intValue()

                            ));
                        }
                        completeListenerQuestion.onSuccess();
                        Collections.shuffle(g_question_list);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        completeListenerQuestion.onFailure();
                    }
                });

    }


public static void getUserScores(final myCompleteListener completeListenerQuestion)
{
    g_firestore.collection("Questions")

            .get()
            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    //Get collection info and store it to the list
                    for(DocumentSnapshot doc : queryDocumentSnapshots)
                    {
                        userScoreList.add(new userScoresModel(

                                doc.getString("Difficulty"),
                                doc.getString("Username"),
                                doc.getString("QuizType"),
                                doc.getLong("Score").intValue()

                        ));
                    }
                    completeListenerQuestion.onSuccess();

                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    completeListenerQuestion.onFailure();
                }
            });

}

    public static void getUserTime(final myCompleteListener completeListenerQuestion)
    {
        g_firestore.collection("Questions")

                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        //Get collection info and store it to the list
                        for(DocumentSnapshot doc : queryDocumentSnapshots)
                        {
                            statisticTime .add(new statisticsModel(
                                    doc.getLong("Average Time").doubleValue()

                            ));
                        }
                        completeListenerQuestion.onSuccess();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        completeListenerQuestion.onFailure();
                    }
                });

    }




}
