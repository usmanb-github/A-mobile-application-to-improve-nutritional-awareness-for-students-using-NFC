package com.example.fireapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AboutAPI extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        startIntent();
    }

    private void startIntent() {
        TextView textView = findViewById(R.id.clickHere);
        textView.setTextIsSelectable(true); //makes text selectable
        Intent intentView = new Intent(Intent.ACTION_VIEW);
        intentView.setData(Uri.parse(textView.getText().toString())); //makes it URL available
        startActivity(intentView);
    }
}
