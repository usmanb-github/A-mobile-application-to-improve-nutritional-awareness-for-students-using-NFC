package com.example.fireapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;


public class CalorieIntake extends AppCompatActivity {

    private ProgressBar progressBar;
    private Button calorieButton;
    private Button resetButton;
    private TextView progressText;
    private TextView calorieText;
    private double result = 0;
    private Toolbar toolbar;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calorie);

        calorieButton = findViewById(R.id.addCaloriesButton);
        progressText = findViewById(R.id.progressText);
        progressBar = findViewById(R.id.progressBar);
        calorieText = findViewById(R.id.calorieText);
        resetButton = findViewById(R.id.resetButton);
        toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.calorieCount);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getData();
    }

    private void getData(){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int calorie_goal = Integer.parseInt(settings.getString("calorie_goal", null));
        //gets calorie goal within settings

        double progress = Double.parseDouble(settings.getString("calorie_current", "0"));
        double subName = getIntent().getDoubleExtra("calorie", 0);
        double sentResult = Math.round(subName + progress);
        double progressCount = (sentResult / calorie_goal);
        result = (int) (progressCount * 100);

        //validates data
        if (result >= 100) {
            Toast.makeText(CalorieIntake.this, "You have met your goal", Toast.LENGTH_SHORT).show();
        } else if (result <= 100) {
            progressBar.setProgress((int) result);
            progressText.setText(result + "%");
            calorieText.setText(sentResult + "/" + calorie_goal);
            settings.edit().putString("calorie_current", String.valueOf(sentResult)).apply();
        }
        calorieButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentStart = new Intent(CalorieIntake.this, CalorieSearch.class);
                startActivity(intentStart);
                //starts Search
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //resets progress bar and settings
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                progressBar.setProgress(0);
                pref.edit().putString("calorie_current", String.valueOf(0)).apply();
                Intent refreshActivity = new Intent(CalorieIntake.this, CalorieIntake.class);
                startActivity(refreshActivity);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.date, menu);

        MenuItem menuItem = menu.findItem(R.id.dateItem);
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //adds to Calendar
                Intent calendarIntent = new Intent(Intent.ACTION_EDIT);
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                double progress = Double.parseDouble(pref.getString("calorie_current", "0"));
                int calorie_goal = Integer.parseInt(pref.getString("calorie_goal", null));
                calendarIntent.setType("vnd.android.cursor.item/event"); //gets Google Calendar
                calendarIntent.putExtra("allDay", false); //how long
                calendarIntent.putExtra("description", "by: Healthy Tag Application"); //description
                calendarIntent.putExtra("title", "Calories Gained Today: " + progress + " out of " + calorie_goal);
                startActivity(calendarIntent); //using intent to store data in Google Calendar
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}