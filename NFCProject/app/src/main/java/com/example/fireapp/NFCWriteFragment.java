package com.example.fireapp;

import android.app.DialogFragment;
import android.content.Context;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class NFCWriteFragment extends DialogFragment {

    public static NFCWriteFragment newInstance() {

        return new NFCWriteFragment(); //singleton
    }

    private TextView writeTextView;
    private Listener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_nfc_write, container, false);
        writeTextView = view.findViewById(R.id.writeTextView);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (SearchAPI) context; //gets Context
        listener.onDialogDisplayed();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        dismiss();
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public NdefRecord createTextRecord(String content) {
        byte[] language;
        language = Locale.getDefault().getLanguage().
                getBytes(StandardCharsets.UTF_8);
        final byte[] text = content.getBytes(StandardCharsets.UTF_8);
        final int languageSize = language.length;
        final int textLength = text.length;
        final ByteArrayOutputStream payload = new
                ByteArrayOutputStream(1 + languageSize + textLength);
        payload.write((byte) (languageSize & 0x1F));
        payload.write(language, 0, languageSize);
        payload.write(text, 0, textLength);
        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
                NdefRecord.RTD_TEXT, new byte[0], payload.toByteArray());
        //Subtil, V., 2014. Near Field Communication With Android Cookbook. Birmingham, UK: Packt Pub.
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public NdefMessage createMessage(String context) {
        NdefRecord ndefRecord = createTextRecord(context);
        NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{ndefRecord});
        return ndefMessage;
        //Subtil, V., 2014. Near Field Communication With Android Cookbook. Birmingham, UK: Packt Pub.
    }

}
