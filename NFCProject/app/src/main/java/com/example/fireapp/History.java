package com.example.fireapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.appcompat.widget.Toolbar;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import java.util.ArrayList;

public class History extends AppCompatActivity {

    private GridView gridView;
    private ArrayList<String> list;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Toolbar toolbar = findViewById(R.id.toolbar5);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");


        ImageView imageView = findViewById(R.id.backButton1);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setData();

    }

    private void setData() {
        gridView = findViewById(R.id.gridView3);
        list = new ArrayList<>();
      /*  SharedPreferences sp = getSharedPreferences("key", Context.MODE_PRIVATE);
        final String value = sp.getString("users", "");*/
        Bundle extras = getIntent().getExtras();
        String value = extras.getString("hours");
        //gets value and adds it to listView
        list.add(value);

        ArrayAdapter adapter = new ArrayAdapter<String>(History.this,
                android.R.layout.simple_list_item_1, list);
        gridView.setAdapter(adapter);
        gridView.clearFocus();
        adapter.notifyDataSetChanged();
    }


    private static String value(String key, Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString("users", "");
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
