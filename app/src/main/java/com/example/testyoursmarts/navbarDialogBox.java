package com.example.testyoursmarts;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Collections;

import static com.example.testyoursmarts.dbQuery.g_question_list;


public class navbarDialogBox extends AppCompatActivity
{


    public void openQuitDialogHome(final Context context) {
        final Context c;
        c=context;

        AlertDialog.Builder builder = new AlertDialog.Builder(c, R.style.AlertDialogTheme);
        builder.setTitle("Exit");
        builder.setMessage("Are you sure you wish to leave the quiz?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                g_question_list.clear();
                g_question_list.removeAll(Collections.emptyList());
                Intent intent = getHomeIntentDialog(context, MainActivity.class);
                startActivity(intent);

                dialog.dismiss();

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Do nothing
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
        Button noButton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        noButton.setBackgroundColor(Color.RED);
        Button yesButton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        yesButton.setBackgroundColor(Color.GREEN);

    }

    public static Intent getHomeIntentDialog(final Context current, final Class next)
    {

                Intent homeintent = new Intent (current, next);
                 return homeintent;

    }
    private void openQuitDialogLeaderBoard() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        builder.setTitle("Exit");
        builder.setMessage("Are you sure you wish to leave the quiz?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                g_question_list.clear();
                // Do nothing but close the dialog
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Do nothing
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
        Button noButton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        noButton.setBackgroundColor(Color.RED);
        Button yesButton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        yesButton.setBackgroundColor(Color.GREEN);
    }

    private void openQuitDialogLogout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        builder.setTitle("Exit");
        builder.setMessage("Are you sure you wish to leave the quiz?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                g_question_list.clear();
                dialog.dismiss();

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Do nothing
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
        Button noButton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        noButton.setBackgroundColor(Color.RED);
        Button yesButton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        yesButton.setBackgroundColor(Color.GREEN);
    }

}
