package com.example.jakob.test1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by jakob on 14/08/16.
 */

public class PhoneBook extends Fragment implements
        View.OnClickListener
    {
        private ListView contacts_list_;
        private SimpleCursorAdapter cursor_adapter_;

        ContentResolver cResolver_;
        ContentProviderClient mCProviderClient_;

    private static final String ARG_SECTION_NUMBER = "section_number";

    public PhoneBook() {
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



        // set button action
        View mainView = container.getRootView();
        FloatingActionButton fab = (FloatingActionButton) mainView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnMainAction();
            }
        });
        System.out.println("set action to PhoneBookCopy View");


        return rootView;
    }

    public void OnMainAction() {
        TextView textView = (TextView) this.getView().findViewById(R.id.section_label);
        textView.setText("initiate");

        ArrayList<String> contacts = fetchContactsCProviderClient();
        if(contacts == null) return;
        for(int i = 0;i<contacts.size();++i)
            System.out.println("c: "+contacts.get(i));

    }

        private ArrayList<String> fetchContactsCProviderClient()
        {
            ArrayList<String> mContactList = null;
            try
            {
                Cursor mCursor = mCProviderClient_.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
                if (mCursor != null && mCursor.getCount() > 0)
                {
                    mContactList = new ArrayList<String>();
                    mCursor.moveToFirst();
                    while (!mCursor.isLast())
                    {
                        String displayName = mCursor.getString(mCursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                        mContactList.add(displayName);
                        mCursor.moveToNext();
                    }
                    if (mCursor.isLast())
                    {
                        String displayName = mCursor.getString(mCursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                        mContactList.add(displayName);
                    }
                }

                mCursor.close();
            }
            catch (RemoteException e)
            {
                e.printStackTrace();
                mContactList = null;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                mContactList = null;
            }
//

            return mContactList;
        }

        private boolean weHavePermissionToReadContacts() {
            return ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
        }

//        private void readTheContacts() {
//            ContactsContract.Contacts.getLookupUri(cResolver_, ContactsContract.Contacts.CONTENT_URI);
//        }

        private void requestReadContactsPermissionFirst() {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this.getActivity(), Manifest.permission.READ_CONTACTS)) {
                Toast.makeText(this.getActivity(), "We need permission so you can text your friends.", Toast.LENGTH_LONG).show();
                requestForResultContactsPermission();
            } else {
                requestForResultContactsPermission();
            }
        }

        private void requestForResultContactsPermission() {
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.READ_CONTACTS}, 123);
        }
//
//        @Override
//        public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//            if (requestCode == 123
//                    && grantResults.length > 0
//                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this.getActivity(), "Permission Granted", Toast.LENGTH_SHORT).show();
//                readTheContacts();
//            } else {
//                Toast.makeText(this.getActivity(), "Permission Denied", Toast.LENGTH_SHORT).show();
//            }
//        }

        @Override
        public void onClick(View view) {

        }
    } // end of Phonebook