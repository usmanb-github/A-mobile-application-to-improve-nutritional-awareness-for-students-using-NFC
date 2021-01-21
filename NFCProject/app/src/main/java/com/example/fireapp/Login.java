package com.example.fireapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {


    private Button bLogin;
    private EditText eUsername;
    private EditText ePassword;
    private TextView tRegisterText;
    LoginDetails userdata;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        eUsername = findViewById(R.id.loginUsername);
        ePassword = findViewById(R.id.loginPassword);
        bLogin = findViewById(R.id.loginButton);
        tRegisterText = findViewById(R.id.registerText);
        setData();
    }
    private void setData() {

        tRegisterText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });
        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = eUsername.getText().toString();
                final String password = ePassword.getText().toString();
                final DatabaseReference rootref;
                rootref = FirebaseDatabase.getInstance().getReferenceFromUrl("https://fireapp-dc6ac.firebaseio.com/");

                rootref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child("users").child(username).exists()) {
                            userdata = dataSnapshot.child("users").child(username).getValue(LoginDetails.class);

                            if (userdata.getUsername().equals(username)) { //username equal to database
                                if (userdata.getPassword().equals(password)) { //password equal to database

                                    Toast.makeText(Login.this, R.string.correct, Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(Login.this, HomeActivity.class);
                                    SharedPreferences sp = getSharedPreferences("key", Context.MODE_PRIVATE); //making sure name is private and key
                                    SharedPreferences.Editor ed = sp.edit(); //edit it
                                    ed.putString("loginUser", username); //put value
                                    ed.commit(); //make the change
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(Login.this, R.string.incorrect, Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }
}
