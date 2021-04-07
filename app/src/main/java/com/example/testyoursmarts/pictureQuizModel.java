package com.example.testyoursmarts;

import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.widget.ImageView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.StorageReference;

import java.net.URL;

public class pictureQuizModel {

    private String question;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private int correctAnswer;

    private String theImage;



    public pictureQuizModel( String question, String option1, String option2, String option3, String option4, int correctAnswer, String theImage) {
        this.question = question;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
        this.correctAnswer = correctAnswer;
       this.theImage = theImage;

    }


    public String getPictureQuestion() {
        return question;
    }
    public void setPictureQuestion(String question) {
        this.question = question;
    }
    public String getPictureOption1() {
        return option1;
    }
    public void setOption1(String option1) {
        this.option1 = option1;
    }
    public String getPictureOption2() {
        return option2;
    }
    public void setPictureOption2(String option2) {
        this.option2 = option2;
    }
    public String getPictureOption3() {
        return option3;
    }
    public void setPictureOption3(String option3) {
        this.option3 = option3;
    }
    public String getPictureOption4() {
        return option4;
    }
    public void setOption4(String option4) {
        this.option4 = option4;
    }
    public int getPictureCorrectAnswer() {
        return correctAnswer;
    }
    public void setPictureCorrectAnswer(int correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
    public String getTheImage() {
        return theImage;
    }
    public void setTheImage(String theImage) {
        this.theImage = theImage;
    }
}
