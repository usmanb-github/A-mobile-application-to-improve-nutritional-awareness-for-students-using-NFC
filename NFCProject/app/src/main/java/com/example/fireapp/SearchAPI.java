package com.example.fireapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

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

import java.io.IOException;
import java.util.ArrayList;

public class SearchAPI extends AppCompatActivity implements Listener {

    private ArrayList<String> list = new ArrayList<>();
    private GridView GridView;
    private  MaterialSearchView searchView;
    private Dialog dialogNFC;
    private NFCWriteFragment mNFCWriteFragment;
    private boolean isDialogDisplayed = false;
    private NfcAdapter mNfcAdapter;
    private AlertDialog.Builder alertDialog;
    private String message, brand_name, item_name, calories, fat, sugars, protein, serving, secondMessage;
    private boolean filter = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api);

        GridView = findViewById(R.id.GridView);
        searchView = findViewById(R.id.searchView);
        dialogNFC = new Dialog(this);

        Toolbar toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("API Search");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));

        setData();
    }
    private void setData() {
        alertDialog = new AlertDialog.Builder(this);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        GridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                alertDialog.setTitle(R.string.selectNFC);
                alertDialog.setMessage(R.string.selectProduct);
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showWriteFragment();
                        message = (String) parent.getItemAtPosition(position);
                        secondMessage += message;
                    }
                });
                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.show();
            }
        });
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
        //replaces all spaces with %20 to be readable for it.


        //https://developer.nutritionix.com/docs/v1_1
        //Advanced Query Full Example

        RequestQueue requestQueue = Volley.newRequestQueue(SearchAPI.this);
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
                                list.add("Product Name: " + brand_name + " " + item_name + " " + "\n"
                                        + "Total Calories: " + calories + "\n"
                                        + "Total Fat: " + fat + "\n"
                                        + "Total Sugars: " + sugars + "\n"
                                        + "Total Protein: " + protein + "\n"
                                        + "Serving Size: " + serving + "g" + "\n"
                                );

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ArrayAdapter adapter = new ArrayAdapter<String>(SearchAPI.this,
                                android.R.layout.simple_list_item_1, list);
                        GridView.setAdapter(adapter);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SearchAPI.this, R.string.match, Toast.LENGTH_SHORT).show();

            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    private void getFilteredJSON(final String search) {
        String url = "https://api.nutritionix.com/v1_1/search/" +
                search + "?" +
                "results=0:50&" +
                "fields=item_type,item_name,brand_name,item_id," +
                "nf_calories,nf_sugars,nf_total_fat,nf_protein,nf_serving_size_qty&" +
                "&appId=186e7a27" +
                "&appKey=d9f13447577408bb145684b8e1c27ab2" +
                "&Content-Type=application/json";
        //replaces all spaces with %20 to be readable for it.


        //https://developer.nutritionix.com/docs/v1_1
        //Advanced Query Full Example

        String urlAmended = url.replaceAll(" ", "%20");
        RequestQueue requestQueue = Volley.newRequestQueue(SearchAPI.this);
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlAmended,
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
                                String brand_nameFilerted = fieldsObject.getString("brand_name");
                                String item_nameFilerted = fieldsObject.getString("item_name");
                                double caloriesFilerted = Double.parseDouble(fieldsObject.getString("nf_calories"));
                                String fatFilerted = fieldsObject.getString("nf_total_fat");
                                double sugarsFilerted = Double.parseDouble(fieldsObject.getString("nf_sugars"));
                                double proteinFilerted = Double.parseDouble(fieldsObject.getString("nf_protein"));
                                String servingFilerted = fieldsObject.getString("nf_serving_size_qty");
                                i++; //loops it until no return value of i.

                                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                int calorieSettings = Integer.parseInt(pref.getString("calorie_intake", null));
                                if (caloriesFilerted <= calorieSettings) {
                                    list.add("Filtered:-\n"
                                            + "Product Name: " + brand_nameFilerted + " " + item_nameFilerted + " " + "\n"
                                            + "Total Calories: " + caloriesFilerted + "\n"
                                            + "Total Fat: " + fatFilerted + "\n"
                                            + "Total Sugars: " + sugarsFilerted + "\n"
                                            + "Total Protein: " + proteinFilerted + "\n"
                                            + "Serving Size: " + servingFilerted + "g" + "\n");
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ArrayAdapter adapter = new ArrayAdapter<String>(SearchAPI.this,
                                android.R.layout.simple_list_item_1, list);
                        GridView.setAdapter(adapter);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SearchAPI.this, R.string.match, Toast.LENGTH_SHORT).show();

            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);
        MenuItem menuItem = menu.findItem(R.id.searchID);

        MenuItem menuFilter = menu.findItem(R.id.filter);

        menuFilter.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SearchAPI.this);
                builder.setTitle("Filter Settings");
                builder.setMessage("Do you want to add the filter settings to the search?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        filter = true;
                    }
                });
                builder.setNegativeButton("Clear", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        filter = false;
                    }
                });

                builder.show();

                return true;
            }
        });
        searchView.setMenuItem(menuItem);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                getJSON(query);

                if (filter == true) {
                    getFilteredJSON(query); //filter calories
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    private void showWriteFragment() {
        mNFCWriteFragment = (NFCWriteFragment) getFragmentManager().findFragmentByTag(NFCWriteFragment.class.getSimpleName());

        if (mNFCWriteFragment == null) {
            mNFCWriteFragment = NFCWriteFragment.newInstance();
        }
        mNFCWriteFragment.show(getFragmentManager(), NFCWriteFragment.class.getSimpleName());
        //shows the relevant fragment
    }

    @Override
    public void onDialogDisplayed() {
        isDialogDisplayed = true;
    }


    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        IntentFilter techDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        IntentFilter[] nfcIntentFilter = new IntentFilter[]{techDetected, tagDetected, ndefDetected};

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        if (mNfcAdapter != null)
            mNfcAdapter.enableForegroundDispatch(this, pendingIntent, nfcIntentFilter, null);
        //enable once in use

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mNfcAdapter != null)
            mNfcAdapter.disableForegroundDispatch(this); //disable whilst not in use
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        try {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            Ndef ndef = Ndef.get(tag);
            Toast.makeText(SearchAPI.this, R.string.nfcDetected, Toast.LENGTH_SHORT).show();

            if (tag != null) {
                mNFCWriteFragment = (NFCWriteFragment) getFragmentManager().findFragmentByTag(NFCWriteFragment.class.getSimpleName());
                if (mNFCWriteFragment == null) {
                    //detect Tag
                    Toast.makeText(SearchAPI.this, R.string.nfcDetected, Toast.LENGTH_SHORT).show();
                } else {

                    Toast.makeText(SearchAPI.this, R.string.complete, Toast.LENGTH_SHORT).show();

                    String messageAmended = secondMessage.replaceAll("null", " ");
                    NdefMessage ndefMessage = mNFCWriteFragment.createMessage(messageAmended);
                    ndef.connect();
                    ndef.writeNdefMessage(ndefMessage); //write to tag
                    ndef.close();
                }
            }
        } catch (FormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
