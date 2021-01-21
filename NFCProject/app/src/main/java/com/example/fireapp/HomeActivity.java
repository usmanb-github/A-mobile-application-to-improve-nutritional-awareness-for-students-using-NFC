package com.example.fireapp;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements Listener {

    private FloatingActionButton mBtRead;
    private NFCReadFragment nfcReadFragment;
    private boolean isDialogDisplayed = false;
    private NfcAdapter nfcAdapter;
    private DrawerLayout drawerLayout;
    private Dialog dialogAbout;
    private String tagContent;
    private ArrayList<String> arrayList = new ArrayList<>();
    TextView welcome;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mBtRead = findViewById(R.id.readBtn1);
        mBtRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReadFragment();
            }
        });
        nfcAdapter = NfcAdapter.getDefaultAdapter(HomeActivity.this);
        drawerLayout = findViewById(R.id.drawer_layout);
        dialogAbout = new Dialog(this);
        welcome = (TextView) findViewById(R.id.welcome);
        navigationBar(savedInstanceState);
        setData();
    }

    private void setData() {
        welcome.setText("Welcome:" + getName());
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
     /*   if (!nfcAdapter.isEnabled()) {
            Toast.makeText(HomeActivity.this, "NFC is not enabled", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
        } else {
            Toast.makeText(HomeActivity.this, "NFC is enabled", Toast.LENGTH_SHORT).show();

        }*/

    }

    private String getName() {
        //gets name from login
        SharedPreferences sp = getSharedPreferences("key", Context.MODE_PRIVATE);
        final String value = sp.getString("loginUser", "");
        return value;
    }

    private void navigationBar(Bundle savedInstanceState) {
        //navigation bar
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {

                    case R.id.navi_profile:
                        Intent profileIntent = new Intent(HomeActivity.this, Profile.class);
                        String text = ((TextView) welcome).getText().toString();
                        String[] result =  text.split(":");
                        String result2 = result[1];
                        Toast.makeText(HomeActivity.this, "" + result2, Toast.LENGTH_SHORT).show();
                        profileIntent.putExtra("loginUser", result2);
                        startActivity(profileIntent);
                        break;
                    case R.id.navi_search:
                        Intent searchIntent = new Intent(HomeActivity.this, SearchAPI.class);
                        startActivity(searchIntent);
                        break;
                    case R.id.navi_calories:
                        Intent calorieIntent = new Intent(HomeActivity.this, CalorieIntake.class);
                        startActivity(calorieIntent);
                        break;

                    case R.id.navi_settings:
                        Intent settingsIntent = new Intent(HomeActivity.this, SettingsActivity.class);
                        startActivity(settingsIntent);
                        break;
                    case R.id.navi_about:
                        dialogAbout.setContentView(R.layout.activity_about);
                        dialogAbout.show();
                        break;

                    case R.id.navi_exit:
                        Intent exitIntent = new Intent(HomeActivity.this, Login.class);
                        startActivity(exitIntent);
                        break;

                    case R.id.navi_share:
                        Intent intentInvite = new Intent(Intent.ACTION_SEND);
                        intentInvite.setType("text/plain");
                        String body = "https://drive.google.com/file/d/1DJoOzoKNMbGITJ-MvP5R8E7zrv5IZhd5/view?usp=sharing";
                        String subject = "Download my link here: ";
                        intentInvite.putExtra(Intent.EXTRA_SUBJECT, subject);
                        intentInvite.putExtra(Intent.EXTRA_TEXT, "Download Healthy Tag through here: " + body);
                        startActivity(Intent.createChooser(intentInvite, "Share using"));
                        break;
                    case R.id.navi_contact:
                        Intent intentContact = new Intent(HomeActivity.this, ContactUs.class);
                        startActivity(intentContact);
                        break;
                }
                return true;
            }
        });

        if (savedInstanceState == null) {
            navigationView.setCheckedItem(R.id.navi_search);
        }
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean adminMode = pref.getBoolean("adminmode", false);

        if (adminMode == true) {
            navigationView.getCheckedItem().setVisible(true);
            Toast.makeText(HomeActivity.this, R.string.adminModeEnabled, Toast.LENGTH_SHORT).show();
        } else {
            navigationView.getCheckedItem().setVisible(false);
        }
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawer(Gravity.START);
        } else {
            super.onBackPressed();
        }
    }

    private void showReadFragment() {
        nfcReadFragment = (NFCReadFragment) getFragmentManager().findFragmentByTag(NFCReadFragment.class.getSimpleName());

        if (nfcReadFragment == null) {
            nfcReadFragment = NFCReadFragment.newInstance();
        }
        nfcReadFragment.show(getFragmentManager(), NFCReadFragment.class.getSimpleName());
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
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, nfcIntentFilter, null);
            //re-ennables NFC Adapter
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null)
            nfcAdapter.disableForegroundDispatch(this); //disables it within the background
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        try {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            Ndef ndef = Ndef.get(tag);
            Toast.makeText(HomeActivity.this, R.string.nfcDetected, Toast.LENGTH_SHORT).show();
            if (tag != null) {
                nfcReadFragment = (NFCReadFragment) getFragmentManager().findFragmentByTag(NFCReadFragment.class.getSimpleName());
                if (nfcReadFragment == null) {
                    //detects NFC tag whilst reading
                    Toast.makeText(HomeActivity.this, R.string.nfcDetected, Toast.LENGTH_SHORT).show();
                } else {
                    ndef.connect();
                    Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
                    if (intent == null) {
                        Toast.makeText(HomeActivity.this, R.string.nfcDetected, Toast.LENGTH_SHORT).show();
                    } else if (parcelables == null) {
                        //found empty
                        Toast.makeText(HomeActivity.this, R.string.empty, Toast.LENGTH_SHORT).show();
                    } else if (parcelables.length > 0) {
                        //reads NFC tag
                        readTextFromTag((NdefMessage) parcelables[0]);
                        Intent assessmentIntent = new Intent(HomeActivity.this, Assessment.class);
                        assessmentIntent.putExtra("read", tagContent);
                        startActivity(assessmentIntent);
                        arrayList.add(tagContent);

                    }
                    ndef.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readTextFromTag(NdefMessage message) {
        NdefRecord[] ndefRecords = message.getRecords();
        if (ndefRecords != null & ndefRecords.length > 0) {
            NdefRecord ndefRecord = ndefRecords[0];
            tagContent = nfcReadFragment.getTextFromNdefRecord(ndefRecord); //reads content
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.history_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.history);


        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent AssessmentIntent = new Intent(HomeActivity.this, History.class);
               /* SharedPreferences sp = getSharedPreferences("key", Context.MODE_PRIVATE); //making sure name is private and key
                SharedPreferences.Editor ed = sp.edit(); //edit it
                ed.putString("users", String.valueOf(arrayList)); //put value
                ed.commit(); //make the change*/
                AssessmentIntent.putExtra("hours", String.valueOf(arrayList));
                startActivity(AssessmentIntent);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}
