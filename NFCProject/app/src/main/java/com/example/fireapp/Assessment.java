package com.example.fireapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class Assessment extends AppCompatActivity {

    private RatingBar ratingBar;
    private EditText nameEditText;
    private EditText calorieEditText;
    private EditText fatEditText;
    private EditText sugarEditText;
    private EditText proteinEditText;
    private String calorie, sugars, protein, fat, name;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc_read_assess);

        nameEditText = findViewById(R.id.nameEditText);
        calorieEditText = findViewById(R.id.caloriesEditText);
        fatEditText = findViewById(R.id.fatEditText);
        sugarEditText = findViewById(R.id.sugarEditText);
        proteinEditText = findViewById(R.id.proteinEditText);


        Toolbar toolbar = findViewById(R.id.toolbar4);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.nfcAssessment);
        ImageView imageView = findViewById(R.id.backButton);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        assessmentNFCTag();

    }

    private void assessmentNFCTag() {
        String text = getIntent().getStringExtra("read"); //gets data scanned


        //splits data
        name = text.substring(text.indexOf("Product Name: ") + 14, text.indexOf("Total Calories"));
        calorie = text.substring(text.indexOf("Total Calories:") + 15, text.indexOf("Total Fat"));
        fat = text.substring(text.indexOf("Total Fat:") + 10, text.indexOf("Total Sugars"));
        sugars = text.substring(text.indexOf("Total Sugars:") + 13, text.indexOf("Total Protein"));
        protein = text.substring(text.indexOf("Total Protein:") + 14, text.indexOf("Serving Size"));


        //validates data and sets it to the editText
        if (TextUtils.isEmpty(text)) {
            fatEditText.setText("0");
        } else {
            nameEditText.setText(name);
            calorieEditText.setText(calorie + "");
            fatEditText.setText(fat + "");
            sugarEditText.setText(sugars + "");
            proteinEditText.setText(protein + "");
        }
        ratingBar = findViewById(R.id.ratingBar);
        ratingBar.setRating(3);

        //configures data using ratingBar
        try {
            double calorieDouble = Double.parseDouble(calorieEditText.getText().toString());
            double fatDouble = Double.parseDouble(fatEditText.getText().toString());
            double sugarsDouble = Double.parseDouble(sugarEditText.getText().toString());
            double proteinDouble = Double.parseDouble(proteinEditText.getText().toString());

            //addmore
            if (sugarsDouble <= 100 && fatDouble <= 10) {
                ratingBar.setRating(4);
            } else if (sugarsDouble >= 15 && fatDouble >= 15) {
                ratingBar.setRating(3);
            } else if (sugarsDouble <= 10 && fatDouble <= 10 && proteinDouble <= 10 && calorieDouble <= 10) {
                ratingBar.setRating(5);
            } else if (sugarsDouble >= 10 && sugarsDouble <= 20
                    && fatDouble >= 10 && fatDouble <= 20
                    && proteinDouble >= 10 && proteinDouble <= 20) {
                ratingBar.setRating(3);

            }
        } catch (NumberFormatException e) {

        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
