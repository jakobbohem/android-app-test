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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by jakob on 14/08/16.
 */

public class PhoneBook extends Fragment
//        implements
//        View.OnClickListener
{
    ArrayList<Contact> contacts_;
    int current_id_ = 0;
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

        View mainView = container.getRootView();
        FloatingActionButton fab = (FloatingActionButton) mainView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnMainAction();
            }
        });
        System.out.println("set action to Phonebook View");
        Button button = (Button) rootView.findViewById(R.id.startButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnMainAction();
            }
        });
        System.out.println("set action to Phonebook View");
        return rootView;
    }

    public void OnMainAction() {
        TextView textView = (TextView) this.getView().findViewById(R.id.section_label);
        textView.setText("initiate");

        TextView phoneDisplay = (TextView) this.getView().findViewById(R.id.phone_display);
        phoneDisplay.setText("phone #");

        contacts_ = fetchContactsCProviderClient();
        if (contacts_ == null) return;

        for (int i = 0; i < contacts_.size(); ++i)
            System.out.println("c: " + contacts_.get(i).name + ", #" + contacts_.get(i).phone);

        ListView listView = (ListView) this.getView().findViewById(R.id.contact_list);
        ContactArrayAdapter arrayAdapter = new ContactArrayAdapter(
                this.getActivity(),
                R.layout.contact_list_item,
                contacts_);

        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("clicked item: " + view + ", " + position + "," + id);
                current_id_ = position;
//                UpdatePhoneDisplay(contacts_.get(position).name);
            }
        });
    }

    private ArrayList<Contact> fetchContactsCProviderClient() {
        ArrayList<Contact> mContactList = null;
        try {
            Cursor mCursor = mCProviderClient_.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            if (mCursor != null && mCursor.getCount() > 0) {
                mContactList = new ArrayList<>();
                mCursor.moveToFirst();
                while (!mCursor.isLast()) {
                    String displayName = mCursor.getString(mCursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                    String contact_id = mCursor.getString(mCursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                    Contact contact = new Contact(contact_id);
                    contact.name = displayName;
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
                        contact.phone = number;
                    }
                    phones.close();

                    mContactList.add(contact);
                    mCursor.moveToNext();
                }
                if (mCursor.isLast()) {
                    String displayName = mCursor.getString(mCursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                    String contact_id = mCursor.getString(mCursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
                    Contact contact = new Contact(contact_id);
                    contact.name = displayName;
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
                        contact.phone = number;
                    }
                    phones.close();

                    mContactList.add(contact);
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

    public void UpdatePhoneDisplay(String message) {
        TextView phoneDisplay = (TextView) this.getView().findViewById(R.id.phone_display);
        phoneDisplay.setText(message);
        ((MainActivity)getActivity()).selected_phone_number_ = message;
    }

    public class Contact {
        Contact(String id) {
            this.id = id;
        }

        private String id;
        String name;
        String phone;

        @Override
        public String toString() {
            return name + ": " + phone;
        }
    }

} // end of Phonebook