package com.example.jakob.test1;

import android.Manifest;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by jakob on 14/08/16.
 */

public class ContactAccessor extends Fragment
//        implements
//        View.OnClickListener
{
    ArrayList<String>contacts_;
    int current_id_ = 0;
    ContentResolver cResolver_;
    ContentProviderClient mCProviderClient_;

    private static final String ARG_SECTION_NUMBER = "section_number";

    public ContactAccessor() {
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, 0); // some number
        this.setArguments(args);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.contact_list_view, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        textView.setText(getString(R.string.message0, getArguments().getInt(ARG_SECTION_NUMBER)));

        if (weHavePermissionToReadContacts()) {
            cResolver_ = getActivity().getContentResolver();
            mCProviderClient_ = cResolver_.acquireContentProviderClient(ContactsContract.Contacts.CONTENT_URI);

        } else {
            requestReadContactsPermissionFirst();
            cResolver_ = getActivity().getContentResolver();
            mCProviderClient_ = cResolver_.acquireContentProviderClient(ContactsContract.Contacts.CONTENT_URI);
        }

        Button button = (Button) rootView.findViewById(R.id.startButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnMainAction();
            }
        });
        Button nextButton = (Button) rootView.findViewById(R.id.goNext);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoToDialDialogue();
            }
        });

        return rootView;
    }

    public void GoToDialDialogue() {
//        getActivity().mSectionsPagerAdapter.

    }


    public void OnMainAction() {
        TextView textView = (TextView) this.getView().findViewById(R.id.section_label);
        textView.setText("loading contacts...");


        contacts_ = fetchContactsCProviderClient();
        if (contacts_ == null) return;

        for (int i = 0; i < contacts_.size(); ++i)
            System.out.println("c: " + contacts_.get(i) + ", #");

        ListView listView = (ListView) this.getView().findViewById(R.id.contact_list);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this.getActivity(),
                R.layout.contact_list_item,
                contacts_);

        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("clicked item: " + view + ", " + position + "," + id);
                current_id_ = position;
                String item = contacts_.get(position);
                UpdatePhoneDisplay(item.split("#")[1]);
            }
        });
    }

    private void UpdatePhoneDisplay(String number) {
        TextView phoneDisplay = (TextView) this.getView().findViewById(R.id.phone_display);
        phoneDisplay.setText(number);
        ((MainActivity)getActivity()).selected_phone_number_ = number;

    }

    private ArrayList<String>fetchContactsCProviderClient() {
        ArrayList<String>mContactList = null;
        try {
            Cursor mCursor = mCProviderClient_.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            if (mCursor != null && mCursor.getCount() > 0) {
                mContactList = new ArrayList<>();
                mCursor.moveToFirst();
                while (!mCursor.isLast()) {
                    String displayName = mCursor.getString(mCursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                    String contact_id = mCursor.getString(mCursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

                    Cursor phones = cResolver_.query(Phone.CONTENT_URI, null,
                            Phone.CONTACT_ID + " = " + contact_id, null, null);
                    while (phones.moveToNext()) {
                        String number = phones.getString(phones.getColumnIndex(Phone.NUMBER));
                        int type = phones.getInt(phones.getColumnIndex(Phone.TYPE));
                        switch (type) {
                            case Phone.TYPE_HOME:
                            case Phone.TYPE_MOBILE:
                            case Phone.TYPE_WORK:
                        }
                        displayName += " #"+number;
                    }
                    phones.close();

                    mContactList.add(displayName);
                    mCursor.moveToNext();
                }
                if (mCursor.isLast()) {
                    String displayName = mCursor.getString(mCursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                    String contact_id = mCursor.getString(mCursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

                    Cursor phones = cResolver_.query(Phone.CONTENT_URI, null,
                            Phone.CONTACT_ID + " = " + contact_id, null, null);
                    while (phones.moveToNext()) {
                        String number = phones.getString(phones.getColumnIndex(Phone.NUMBER));
                        int type = phones.getInt(phones.getColumnIndex(Phone.TYPE));
                        switch (type) {
                            case Phone.TYPE_HOME:
                            case Phone.TYPE_MOBILE:
                            case Phone.TYPE_WORK:
                        }
                        displayName += " #" +
                                ""+number;
                    }
                    phones.close();

                    mContactList.add(displayName);
                }
            }

            mCursor.close();
        } catch (RemoteException e) {
            e.printStackTrace();
            mContactList = null;
        } catch (Exception e) {
            e.printStackTrace();
            mContactList = null;
        }
        return mContactList;
    }

    private boolean weHavePermissionToReadContacts() {
        return ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestReadContactsPermissionFirst() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this.getActivity(), Manifest.permission.READ_CONTACTS)) {
            Toast.makeText(this.getActivity(), "Need permission to access contacts.", Toast.LENGTH_LONG).show();
            requestForResultContactsPermission();
        } else {
            requestForResultContactsPermission();
        }
    }

    private void requestForResultContactsPermission() {
        ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.READ_CONTACTS}, 123);
    }


} // end of Phonebook