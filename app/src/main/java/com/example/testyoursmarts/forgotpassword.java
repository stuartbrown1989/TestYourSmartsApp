package com.example.testyoursmarts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class forgotpassword extends AppCompatActivity {

    private Button resetPass;
    private EditText email;
    private FirebaseAuth mAuth;
    private ImageView backB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);
        resetPass = findViewById(R.id.button_send_password);
        email = findViewById(R.id.edit_text_forgot_password);
         mAuth = FirebaseAuth.getInstance();
         backB = findViewById(R.id.forgotPass_backButton);

        backB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        resetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailRequest = email.getText().toString().trim();
                if(emailRequest.isEmpty()) {
                    Toast.makeText(forgotpassword.this, "Please enter an email Address", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.sendPasswordResetEmail(emailRequest)
                        .addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(forgotpassword.this, "Check your email to reset your password", Toast.LENGTH_SHORT).show();
                                    Intent intentLogin = new Intent(forgotpassword.this, LoginActivity.class);
                                    startActivity(intentLogin);
                                } else {
                                    Toast.makeText(forgotpassword.this, "Failure to send email, please confirm your email address and try again", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

    }
}