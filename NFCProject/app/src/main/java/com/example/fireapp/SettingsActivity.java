package com.example.fireapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;

import java.util.List;

public class SettingsActivity extends AppCompatActivity {
    private static Dialog dialogPattern;
    private static PatternLockView patternLockView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        dialogPattern = new Dialog(SettingsActivity.this);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        setData();
    }

    private void setData() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            Preference preference = findPreference("adminmode");

            preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(final Preference preference) {
                    final CheckBoxPreference switchPref = findPreference("adminmode");
                    boolean admin = preference.getSharedPreferences().getBoolean("adminmode", false);
                    if (admin == true) {
                        Toast.makeText(getActivity(), "On", Toast.LENGTH_SHORT).show();
                        switchPref.setChecked(false);

                        dialogPattern.setContentView(R.layout.activity_pin);
                        dialogPattern.show();
                        patternLockView = dialogPattern.findViewById(R.id.pattern_lock_view);
                        patternLockView.addPatternLockListener(new PatternLockViewListener() {
                            @Override
                            public void onStarted() {

                            }
                            @Override
                            public void onProgress(List<PatternLockView.Dot> progressPattern) {

                            }
                            @Override
                            public void onComplete(List<PatternLockView.Dot> pattern) {

                                if (PatternLockUtils.patternToString(patternLockView, pattern).equalsIgnoreCase("1236")) {
                                    patternLockView.setViewMode(PatternLockView.PatternViewMode.CORRECT);
                                    Toast.makeText(getActivity(), R.string.correctPin, Toast.LENGTH_SHORT).show();
                                    switchPref.setChecked(true);
                                    dialogPattern.dismiss();
                                } else if (PatternLockUtils.patternToString(patternLockView, pattern).equals(null)) {
                                    Toast.makeText(getActivity(), R.string.empty, Toast.LENGTH_SHORT).show();
                                    switchPref.setChecked(false);
                                } else {
                                    patternLockView.setViewMode(PatternLockView.PatternViewMode.WRONG);
                                    switchPref.setChecked(false);
                                    Toast.makeText(getActivity(), R.string.UncorrectPin, Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCleared() {

                            }
                        });
                    } else {

                    }
                    return true;
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(SettingsActivity.this, HomeActivity.class);
        startActivity(intent);
    }
}