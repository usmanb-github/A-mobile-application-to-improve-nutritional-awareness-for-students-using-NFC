package com.example.fireapp;

import android.app.DialogFragment;
import android.content.Context;
import android.nfc.NdefRecord;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;

public class NFCReadFragment extends DialogFragment {

    public static NFCReadFragment newInstance() {

        return new NFCReadFragment(); //singleton
    }

    private TextView readTextView;
    private Listener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_nfc_read, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        readTextView = view.findViewById(R.id.readTextView);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (HomeActivity) context; //gets context
        listener.onDialogDisplayed();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public String getTextFromNdefRecord(NdefRecord ndefRecord) {
        String tagContent = null;
        try {
            byte[] payload = ndefRecord.getPayload();
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
            int languageSize = payload[0] & 0063;
            tagContent = new String(payload, languageSize + 1,
                    payload.length - languageSize - 1, textEncoding);
        } catch (UnsupportedEncodingException e) {
            Log.e("getTextFromNdefRecord", e.getMessage(), e);
        }
        return tagContent;
        //Reference below:
        //Subtil, V., 2014. Near Field Communication With Android Cookbook. Birmingham, UK: Packt Pub.
    }
}
