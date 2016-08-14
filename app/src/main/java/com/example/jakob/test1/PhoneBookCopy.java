package com.example.jakob.test1;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
/**
 * Created by jakob on 14/08/16.
 */

public class PhoneBookCopy extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
//        AdapterView.OnItemClickListener,
        View.OnClickListener
    {
        private ListView contacts_list_;
        private SimpleCursorAdapter cursor_adapter_;

        /*
     * Defines an array that contains column names to move from
     * the Cursor to the ListView.
     */
        @SuppressLint("InlinedApi")
        private final static String[] FROM_COLUMNS = {
                Build.VERSION.SDK_INT
                        >= Build.VERSION_CODES.HONEYCOMB ?
                        ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :
                        ContactsContract.Contacts.DISPLAY_NAME
        };
        /*
         * Defines an array that contains resource ids for the layout views
         * that get the Cursor column contents. The id is pre-defined in
         * the Android framework, so it is prefaced with "android.R.id"
         */
        private final static int[] TO_IDS = {
                android.R.id.text1
        };

        // Define variables for the contact the user selects
        // The contact's _ID value
        long selected_contact_id_;
        // The contact's LOOKUP_KEY
        String selected_lookup_key;
        // A content URI for the selected contact
//        Uri mContactUri;


    private static final String ARG_SECTION_NUMBER = "section_number";

    public PhoneBookCopy() {
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

        // Gets the ListView from the View list of the parent activity
        contacts_list_ =
                (ListView) getActivity().findViewById(R.id.contact_list);
        // Gets a CursorAdapter
        cursor_adapter_ = new SimpleCursorAdapter(
                getActivity(),
                R.layout.contact_list_item,
                null,
                FROM_COLUMNS, TO_IDS,
                0);
        // Sets the adapter for the ListView
        contacts_list_.setAdapter(cursor_adapter_);
        contacts_list_.setOnClickListener(this);

        return rootView;
    }

    public void OnMainAction() {
        TextView textView = (TextView) this.getView().findViewById(R.id.section_label);
        textView.setText("initiate");
    }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }

        @Override // bound to list listener
        public void onClick(View view) {

        }
    } // end of Phonebook