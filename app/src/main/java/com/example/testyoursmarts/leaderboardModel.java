package com.example.testyoursmarts;

public class leaderboardModel {
    private String docID;
    private String name;
    private int noOfTest;


    public leaderboardModel(String docID, String name) {
        this.docID = docID;
        this.name = name;

    }

    public String getDocID() {
        return docID;
    }

    public void setDocID(String docID) {
        this.docID = docID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
