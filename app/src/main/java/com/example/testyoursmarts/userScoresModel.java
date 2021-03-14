package com.example.testyoursmarts;

public class userScoresModel {

private String userName;
private String Difficulty, quiztype;
private int score;



    public userScoresModel ()
    {

    }

    public userScoresModel(String userName, String Difficulty, String quiztype,  int score) {


        this.userName = userName;
        this.Difficulty = Difficulty;
        this.score = score;
        this.quiztype = quiztype;


    }
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDifficulty() {
        return Difficulty;
    }

    public void setDifficulty(String difficulty) {
        Difficulty = difficulty;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getQuiztype() {
        return quiztype;
    }

    public void setQuiztype(String quiztype) {
        this.quiztype = quiztype;
    }
}
