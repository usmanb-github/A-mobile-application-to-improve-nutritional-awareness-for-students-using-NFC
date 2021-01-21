package com.example.fireapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Register extends AppCompatActivity {


    private Button bRegister;
    private EditText eUsername;
    private EditText ePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Firebase.setAndroidContext(this);
        bRegister = findViewById(R.id.registerButton);
        eUsername = findViewById(R.id.registerUsername);
        ePassword = findViewById(R.id.registerPassword);
        setData();
    }

    private void setData() {
        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = eUsername.getText().toString();
                final String password = ePassword.getText().toString();

                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(Register.this, R.string.usernameempty, Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(Register.this, R.string.passwordempty, Toast.LENGTH_SHORT).show();
                } else {
                    final DatabaseReference rootref;
                    rootref = FirebaseDatabase.getInstance().getReferenceFromUrl("https://fireapp-dc6ac.firebaseio.com/");
                    rootref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (!(dataSnapshot.child("users").child(username).exists())) {
                                //registers login using hashmap
                                HashMap<String, Object> usersdatamap = new HashMap<>();
                                usersdatamap.put("username", username);
                                usersdatamap.put("password", password);

                                rootref.child("users").child(username).updateChildren(usersdatamap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Intent intent = new Intent(Register.this, Login.class);
                                                    startActivity(intent);
                                                    Toast.makeText(Register.this, R.string.accountMade, Toast.LENGTH_LONG).show();
                                                } else {
                                                    Toast.makeText(Register.this, R.string.accountNotMade, Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                            } else if (dataSnapshot.child("users").child(username).exists()) {
                                Toast.makeText(Register.this, R.string.exists, Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }
}
