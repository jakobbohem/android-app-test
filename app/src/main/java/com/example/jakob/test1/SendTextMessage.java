package com.example.jakob.test1;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by jakob on 14/08/16.
 */
public class SendTextMessage extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    EditText phone_number_view_;
    EditText message_view_;


    public SendTextMessage() {
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, 0); // some number
        this.setArguments(args);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_text, container, false);

        // check for store data
        SharedPreferences shared = getActivity().getSharedPreferences("info", Context.MODE_PRIVATE);
        String number = shared.getString("last_phone_number", "0702945592");
        String message = shared.getString("last_text_message", "");

        phone_number_view_ = (EditText) rootView.findViewById(R.id.phone_display);
        phone_number_view_.setText(number);

        message_view_ = (EditText) rootView.findViewById(R.id.textMessage);
        message_view_.setText(message);

        // set button action
        Button button = (Button) rootView.findViewById(R.id.sendButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnMainAction();
            }
        });

        return rootView;
    }

    private boolean havePhoneCallPermission() {
        return ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestMakePhoneCallPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this.getActivity(), Manifest.permission.SEND_SMS)) {
            Toast.makeText(this.getActivity(), "Need permission to send text", Toast.LENGTH_LONG).show();
            requestForResultPhonePermission();
        } else {
            requestForResultPhonePermission();
        }
    }

    private void requestForResultPhonePermission() {
        ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.SEND_SMS}, 123);
    }

    public void OnMainAction() {
        String phone = phone_number_view_.getText().toString();
        String message = message_view_.getText().toString();


        try {
            if(!havePhoneCallPermission())
            {
                requestMakePhoneCallPermission();
            }
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phone, null, message, null, null);
            Toast.makeText(getActivity(), "SMS Sent!",
                    Toast.LENGTH_LONG).show();

            // save data for app close state
            SharedPreferences pref;
            pref = getActivity().getSharedPreferences("info", Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = pref.edit();
            editor.putString("last_phone_number",phone);
            editor.putString("last_text_message",message);
            editor.commit();

        } catch (Exception e) {
            Toast.makeText(getActivity(),
                    "Sending text faild,",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

//        SmsManager smsManager = SmsManager.getDefault();
//        smsManager.sendTextMessage(phone, null, message, null, null);

    }

} // end of SendTextMessage