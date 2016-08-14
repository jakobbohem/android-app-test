package com.example.jakob.test1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by jakob on 14/08/16.
 */
public class DialNumber extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    String number_;

    public DialNumber() {
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, 0); // some number
        this.setArguments(args);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dial, container, false);
        EditText textView = (EditText) rootView.findViewById(R.id.phone_display);
        number_ = ((MainActivity)getActivity()).selected_phone_number_;
        System.out.println("got number "+number_);
        textView.setText(number_);

        Button dialButton = (Button) rootView.findViewById(R.id.dialButton);
        dialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnMainAction();
            }
        });

        return rootView;
    }

    private boolean havePhoneCallPermission() {
        return ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestMakePhoneCallPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this.getActivity(), Manifest.permission.CALL_PHONE)) {
            Toast.makeText(this.getActivity(), "Need permission to call", Toast.LENGTH_LONG).show();
            requestForResultPhonePermission();
        } else {
            requestForResultPhonePermission();
        }
    }

    private void requestForResultPhonePermission() {
        ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.CALL_PHONE}, 123);
    }

    public void OnMainAction() {
        Uri number = Uri.parse("tel:"+number_);
        Intent callIntent = new Intent(Intent.ACTION_CALL, number);

        if(havePhoneCallPermission())
            startActivity(callIntent);
        else
            requestMakePhoneCallPermission();
    }


} // end of DialNumber