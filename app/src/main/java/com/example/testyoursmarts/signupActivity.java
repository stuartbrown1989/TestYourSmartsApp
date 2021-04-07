package com.example.testyoursmarts;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import java.util.regex.Pattern;

public class signupActivity extends AppCompatActivity {

    private EditText textName, textEmail, textPassword, textConfirmPassword;
    private Button buttonSignUp;
    private ImageView backB;
    private FirebaseAuth mAuth;
    private String emailString, passString, confirmPassString, nameString;
    private Dialog progressDialog;
    private TextView dialogText;


    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         //at least 1 digit
                    "(?=.*[a-z])" +         //at least 1 lower case letter
                    "(?=.*[A-Z])" +         //at least 1 upper case letter
                    //"(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[@#$%^&+!=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{6,}" +               //at least 6 characters
                    "$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        //Initializing Variables
        textName = findViewById(R.id.edittext_username);
        textEmail = findViewById(R.id.text_emailID);
        textPassword = findViewById(R.id.text_password);
        textConfirmPassword = findViewById(R.id.text_confirmPassword);
        buttonSignUp = findViewById(R.id.button_signUp);
        backB = findViewById(R.id.image_back);
        mAuth = FirebaseAuth.getInstance();
        backB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        dialogText = findViewById(R.id.text_dialog);
        progressDialog = new Dialog(signupActivity.this);
        progressDialog.setContentView(R.layout.dialog_layout);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogText = progressDialog.findViewById(R.id.text_dialog);
        dialogText.setText("Registering User... ");

        //Signup Button
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate())
                {
                    signupNewUser();
                }
            }
        });


    }
    //Validating Username and Password
    private boolean validate()
    {
        nameString = textName.getText().toString().trim();
        passString = textPassword.getText().toString().trim();
        emailString = textEmail.getText().toString().trim();
        confirmPassString = textConfirmPassword.getText().toString().trim();

        if(nameString.isEmpty())
        {
            textName.setError("Enter Your Name");
            return false;
        }
        if(emailString.isEmpty())
        {
            textEmail.setError("Enter your email");
            return false;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(emailString).matches())
        {
            textEmail.setError("Email address is not valid");
            return false;
        }
        if(passString.isEmpty())
        {
            textPassword.setError("Enter your password");
            return false;
        }
        if(confirmPassString.isEmpty())
        {
            textConfirmPassword.setError("Enter confirm Password");
            return false;
        }
        if(passString.compareTo(confirmPassString) != 0)
        {
            Toast.makeText(signupActivity.this, "Password and confirm password do not match", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!PASSWORD_PATTERN.matcher(passString).matches())
        {
            textPassword.setError("Password must contain at least one of each - Special character, digit, Uppercase, lowercase.  And must be at least 6 character long");
            return false;
        }

        return true;
    }

    private void signupNewUser()
    {
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(emailString, passString)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(signupActivity.this, "Signup Successful ", Toast.LENGTH_SHORT).show();
                            //Creating new User data to Firestore
                            dbQuery.createUserData(emailString, nameString, new myCompleteListener() {
                                @Override
                                public void onSuccess() {
                                    progressDialog.dismiss();
                                    Intent intent = new Intent(signupActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    signupActivity.this.finish();
                                }
                                @Override
                                public void onFailure()
                                {
                                    Toast.makeText(signupActivity.this, "Something went wrong! Please try again later", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            });
                        } else {
                            // If sign in fails, display a message to the user.
                            progressDialog.dismiss();
                            Toast.makeText(signupActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}