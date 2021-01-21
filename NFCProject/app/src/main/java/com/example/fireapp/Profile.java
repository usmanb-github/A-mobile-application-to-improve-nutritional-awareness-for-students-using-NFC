package com.example.fireapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Profile extends AppCompatActivity {

    private Button updateProfile;
    private EditText name, surname, emailAddress, gender, age;
    private DatabaseProfile userdata;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        name = findViewById(R.id.profileName);
        surname = findViewById(R.id.profileSurname);
        emailAddress = findViewById(R.id.profileEmail);
        gender = findViewById(R.id.profileGender);
        age = findViewById(R.id.profileAge);
        updateProfile = findViewById(R.id.updateProfile);

        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Calorie Count");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setData();

    }

    private void setData() {
        final DatabaseReference rootref;
        final String userid = getIntent().getStringExtra("loginUser");

        rootref = FirebaseDatabase.getInstance().getReferenceFromUrl("https://fireapp-dc6ac.firebaseio.com/");
        rootref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userdata = snapshot.child("profile").child(userid).getValue(DatabaseProfile.class);
                if(userdata == null) {
                    Toast.makeText(Profile.this, "Profile Empty", Toast.LENGTH_SHORT).show();
                } else {
                    //sets data
                    name.setText(userdata.getName());
                    surname.setText(userdata.getSurname());
                    emailAddress.setText(userdata.getGender());
                    gender.setText(userdata.getGender());
                    age.setText(userdata.getAge());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String nameText = name.getText().toString();
                final String surnameText = surname.getText().toString();
                final String emailAddressText = emailAddress.getText().toString();
                final String genderText = gender.getText().toString();
                final String ageText = age.getText().toString();

                rootref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!(snapshot.child("profile").child(userid).exists())) {
                            //updates profile using hashmap
                            HashMap<String, Object> profileDataMap = new HashMap<>();
                            profileDataMap.put("userid", userid);
                            profileDataMap.put("name", nameText);
                            profileDataMap.put("surname", surnameText);
                            profileDataMap.put("email_address", emailAddressText);
                            profileDataMap.put("gender", genderText);
                            profileDataMap.put("age", ageText);
                            rootref.child("profile").child(userid).updateChildren(profileDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Profile.this, R.string.profileUpdated, Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(Profile.this, R.string.profileNotUpdated, Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        } else {
                            HashMap<String, Object> profileDataMap = new HashMap<>();
                            profileDataMap.put("userid", userid);
                            profileDataMap.put("name", nameText);
                            profileDataMap.put("surname", surnameText);
                            profileDataMap.put("email_address", emailAddressText);
                            profileDataMap.put("gender", genderText);
                            profileDataMap.put("age", ageText);
                            rootref.child("profile").child(userid).updateChildren(profileDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(Profile.this, R.string.profileUpdated, Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(Profile.this, R.string.profileNotUpdated, Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }
}
