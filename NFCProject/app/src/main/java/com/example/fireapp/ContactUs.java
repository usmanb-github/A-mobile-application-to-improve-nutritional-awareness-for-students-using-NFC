package com.example.fireapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ContactUs extends AppCompatActivity {
    private EditText name;
    private EditText subject;
    private EditText message;
    private Button button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        name = findViewById(R.id.contact_Name);
        subject = findViewById(R.id.contact_subject);
        message = findViewById(R.id.contact_message);
        button = findViewById(R.id.submitMessageButton);

        Toolbar toolbar = findViewById(R.id.toolbar6);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Contact Us");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setData();
    }

    private void setData() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameString = name.getText().toString();
                String subjectString = subject.getText().toString();
                String messageString = message.getText().toString();

                //sending to email outlook ensuring that the template is ready for users to contact developers
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"usman.b@live.co.uk", "usman123972@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, subjectString);
                intent.putExtra(Intent.EXTRA_TEXT, messageString + "\n" +
                        nameString);
                intent.setType("*/*");
                startActivity(intent);
            }
        });
    }
}
