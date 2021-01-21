package com.example.fireapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CalorieSearch extends AppCompatActivity {

    private GridView GridView;
    private AlertDialog.Builder alertDialog;
    private MaterialSearchView searchView;
    private String brand_name, item_name, calories, fat, sugars, protein, serving;
    private ArrayList<String> list = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caloriesearch);

        GridView = findViewById(R.id.GridView1);
        searchView = findViewById(R.id.searchView1);

        Toolbar toolbar = findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("API Search");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        alertDialog = new AlertDialog.Builder(this);
        setData();

    }

    private void setData() {
        //sends the calorie information across to add to intake
        GridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                alertDialog.setTitle(R.string.addProduct);
                alertDialog.setMessage(R.string.selectProduct);
                alertDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String text = ((TextView) view).getText().toString();
                        double result = Double.parseDouble(text.substring(text.indexOf(":") + 1, text.indexOf("Total Fat")));
                        Intent intent = new Intent(CalorieSearch.this, CalorieIntake.class);
                        intent.putExtra("calorie", result);
                        startActivity(intent);
                        Toast.makeText(CalorieSearch.this, "You have added this product onto your calorie goal", Toast.LENGTH_SHORT).show();

                    }
                });
                alertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);
        MenuItem menuItem = menu.findItem(R.id.searchID);
        searchView.setMenuItem(menuItem);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                getJSON(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    private void getJSON(final String search) {

        String url = "https://api.nutritionix.com/v1_1/search/" +
                search + "?" +
                "results=0:50&" +
                "fields=item_type,item_name,brand_name,item_id," +
                "nf_calories,nf_sugars,nf_total_fat,nf_protein,nf_serving_size_qty" +
                "&" +
                "appId=186e7a27" +
                "&appKey=d9f13447577408bb145684b8e1c27ab2" +
                "&Content-Type=application/json";

        String urlAmended = url.replaceAll(" ", "%20");

        RequestQueue requestQueue = Volley.newRequestQueue(CalorieSearch.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlAmended,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            list.clear();
                            JSONArray JSONArray = response.getJSONArray("hits");
                            int i = 0;
                            while (!JSONArray.isNull(i)) { //returns true if the reference point has a value
                                JSONObject fieldsObject = JSONArray.getJSONObject(i).getJSONObject("fields");
                                brand_name = fieldsObject.getString("brand_name");
                                item_name = fieldsObject.getString("item_name");
                                calories = fieldsObject.getString("nf_calories");
                                fat = fieldsObject.getString("nf_total_fat");
                                sugars = fieldsObject.getString("nf_sugars");
                                protein = fieldsObject.getString("nf_protein");
                                serving = fieldsObject.getString("nf_serving_size_qty");

                                i++; //loops it until no return value of i.
                                list.add(brand_name + " " + item_name + " " + "\n"
                                        + "Total Calories: " + calories + "\n"
                                        + "Total Fat: " + fat + "g" + "\n"
                                        + "Total Sugars: " + sugars + "g" + "\n"
                                        + "Total Protein: " + protein + "g" + "\n"
                                        + "Serving Size: " + serving + "g" + "\n"
                                );
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ArrayAdapter adapter = new ArrayAdapter<String>(CalorieSearch.this,
                                android.R.layout.simple_list_item_1, list);
                        GridView.setAdapter(adapter);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CalorieSearch.this, R.string.match, Toast.LENGTH_SHORT).show();

            }
        });
        requestQueue.add(jsonObjectRequest);
    }

}

