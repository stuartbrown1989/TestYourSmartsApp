package com.example.testyoursmarts;

public class statisticsModel {

    private String userName;
    private String Difficulty, quiztype;
    private double time;



    public statisticsModel ()
    {

    }

    public statisticsModel(double time) {

        this.time = time;

    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

}
