package com.example.truckup;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SettingsActivity extends AppCompatActivity {


    FirebaseAuth auth;
    Button button;
    TextView textView;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        auth = FirebaseAuth.getInstance();
        button = findViewById(R.id.logout);
        textView = findViewById(R.id.user_details);
        user = auth.getCurrentUser();
        if(user == null){
            Intent intent = new Intent(SettingsActivity.this, Login.class);
            startActivity(intent);
            SettingsActivity.this.finish();
        }
        else{
            textView.setText(user.getEmail());
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(SettingsActivity.this, Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        // Inside your onCreate method, after initializing the changeUsername button
        Button changeUsername = findViewById(R.id.changeUsername);
        changeUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Inflate the dialog layout
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_change_username, null);
                final EditText newUsername = dialogView.findViewById(R.id.newUsername);

                // Create the AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setTitle("Change Username")
                        .setView(dialogView)
                        .setPositiveButton("OK", null) // Note the null here
                        .setNegativeButton("Cancel", null); // Note the null here

                // Create and show the AlertDialog
                final AlertDialog alertDialog = builder.create();
                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        // Change the color of the OK button
                        Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        positiveButton.setTextColor(ContextCompat.getColor(SettingsActivity.this, R.color.dark_green));

                        // Change the color of the Cancel button
                        Button negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                        negativeButton.setTextColor(ContextCompat.getColor(SettingsActivity.this, R.color.dark_green));

                        // Set the onClickListener for the OK button
                        positiveButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Handle the new username here
                                String username = newUsername.getText().toString();

                                // Get a reference to the user's data in the database
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());

                                // Update the username in the database
                                ref.child("username").setValue(username);

                                // Dismiss the dialog
                                alertDialog.dismiss();
                            }
                        });

                        // Set the onClickListener for the Cancel button
                        negativeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // User cancelled the dialog
                                alertDialog.dismiss();
                            }
                        });
                    }
                });
                alertDialog.show();
            }
        });

        // Inside your onCreate method, after initializing the changePassword button
        Button changePassword = findViewById(R.id.changePassword);
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Inflate the dialog layout
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_change_password, null);
                final EditText oldPassword = dialogView.findViewById(R.id.oldPassword);
                final EditText newPassword = dialogView.findViewById(R.id.newPassword);

                // Create the AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setTitle("Change Password")
                        .setView(dialogView)
                        .setPositiveButton("OK", null) // Note the null here
                        .setNegativeButton("Cancel", null); // Note the null here

                // Create and show the AlertDialog
                final AlertDialog alertDialog = builder.create();
                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        // Change the color of the OK button
                        Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        positiveButton.setTextColor(ContextCompat.getColor(SettingsActivity.this, R.color.dark_green));

                        // Change the color of the Cancel button
                        Button negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                        negativeButton.setTextColor(ContextCompat.getColor(SettingsActivity.this, R.color.dark_green));

                        // Set the onClickListener for the OK button
                        positiveButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Handle the new password here
                                String oldPass = oldPassword.getText().toString();
                                String newPass = newPassword.getText().toString();

                                // Re-authenticate the user
                                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPass);
                                user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            // Update the password
                                            user.updatePassword(newPass)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(SettingsActivity.this, "Password updated", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                Toast.makeText(SettingsActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        } else {
                                            Toast.makeText(SettingsActivity.this, "Old password is incorrect", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                                // Dismiss the dialog
                                alertDialog.dismiss();
                            }
                        });

                        // Set the onClickListener for the Cancel button
                        negativeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // User cancelled the dialog
                                alertDialog.dismiss();
                            }
                        });
                    }
                });
                alertDialog.show();
            }
        });


        // Inside your onCreate method, after initializing the deleteAccount button
        Button deleteAccount = findViewById(R.id.deleteAccount);
        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create the AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setTitle("Delete Account")
                        .setMessage("Are you sure you want to delete your account?")
                        .setPositiveButton("Yes", null) // Note the null here
                        .setNegativeButton("No", null); // Note the null here

                // Create and show the AlertDialog
                final AlertDialog alertDialog = builder.create();
                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        // Change the color of the OK button
                        Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        positiveButton.setTextColor(ContextCompat.getColor(SettingsActivity.this, R.color.dark_green));

                        // Change the color of the Cancel button
                        Button negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                        negativeButton.setTextColor(ContextCompat.getColor(SettingsActivity.this, R.color.dark_green));

                        // Set the onClickListener for the OK button
                        positiveButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Get a reference to the user's data in the database
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());

                                // Delete the user's data in the database
                                ref.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            // Delete the user's account
                                            user.delete()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(SettingsActivity.this, "Account deleted", Toast.LENGTH_SHORT).show();
                                                                // Redirect to login activity
                                                                Intent intent = new Intent(SettingsActivity.this, Login.class);
                                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                startActivity(intent);
                                                            } else {
                                                                Toast.makeText(SettingsActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        } else {
                                            Toast.makeText(SettingsActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                                // Dismiss the dialog
                                alertDialog.dismiss();
                            }
                        });

                        // Set the onClickListener for the Cancel button
                        negativeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // User cancelled the dialog
                                alertDialog.dismiss();
                            }
                        });
                    }
                });
                alertDialog.show();
            }
        });

    }
}