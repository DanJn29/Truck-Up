package com.example.truckup;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    EditText editTextEmail, editTextPassword, editTextUsername, repeatPassword;
    Button buttonReg;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textView;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && currentUser.isEmailVerified()) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.email_et);
        editTextPassword = findViewById(R.id.password_et);
        buttonReg = findViewById(R.id.sign_up);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.sign_in_now);
        editTextUsername = findViewById(R.id.username_et);
        repeatPassword = findViewById(R.id.repeatPassword_et);



        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (Character.isWhitespace(source.charAt(i)) || source.charAt(i) == '$' || source.charAt(i) == '%' || source.charAt(i) == '>') {
                        return "";
                    }
                }
                return null;
            }
        };

        editTextUsername.setFilters(new InputFilter[] { filter });
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String email, password, username, repeatPass;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());
                username = String.valueOf(editTextUsername.getText());
                repeatPass = String.valueOf(repeatPassword.getText());

                boolean isValid = true;

                if (TextUtils.isEmpty(username)) {
                    editTextUsername.setError("Please enter your username");
                    progressBar.setVisibility(View.GONE);
                    isValid = false;
                }


                if (!email.contains("@") || !email.contains(".")) {
                    editTextEmail.setError("Please enter a valid email address.");
                    progressBar.setVisibility(View.GONE);
                    isValid = false;
                }


                if (password.length() < 5) {
                    editTextPassword.setError("Password must be more than 5 characters");
                    progressBar.setVisibility(View.GONE);
                    isValid = false;
                }

                if (password.contains(" ")) {
                    editTextPassword.setError("Your password should not contain spaces.");
                    progressBar.setVisibility(View.GONE);
                    isValid = false;
                }

                if (password.length() > 64) {
                    editTextPassword.setError("Your password can have at most 64 characters.");
                    progressBar.setVisibility(View.GONE);
                    isValid = false;
                }
                if(!password.equals(repeatPass)) {
                    repeatPassword.setError("Passwords do not match");
                    progressBar.setVisibility(View.GONE);
                    isValid = false;
                }

                  if(isValid){mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);

                                if (task.isSuccessful()) {
                                    sendEmailVerification();
                                    Toast.makeText(Register.this, "Account created. Please check your email for verification.",
                                            Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(getApplicationContext(), Login.class);{
                                        startActivity(intent);
                                        finish();
                                    }
                                } else {
                                    editTextEmail.setError(task.getException().getMessage());
                                }
                            }
                        });}
            }
        });
    }

    private void sendEmailVerification() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        if (firebaseUser != null) {
            firebaseUser.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Get the username of the current user
                                String uid = firebaseUser.getUid();
                                String username = editTextUsername.getText().toString();

                                // Create a User object
                                User user = new User(username);

                                // Create a DatabaseReference to the Firebase Realtime Database
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

                                // Use the uid as the key and the User object as the value to store in the database
                                databaseReference.child("users").child(uid).setValue(user);

                            } else {
                                // Failed to send verification email
                                Toast.makeText(Register.this, "Failed to send verification email: " + task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
