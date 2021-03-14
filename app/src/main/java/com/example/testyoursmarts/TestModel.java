package com.example.testyoursmarts;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

public class TestModel {

    private String docID;
    private String name;
    private int noOfTest;
    private ImageView image;
    private Drawable image1;


    public TestModel(String docID, String name) {
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

    public Drawable getImage() {
        return image1;
    }

    public void setImage(Drawable image1) {
        this.image1 = image1;
    }

}
