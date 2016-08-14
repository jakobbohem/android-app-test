package com.example.jakob.test1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by jakob on 14/08/16.
 */
public class LoadImages extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private ArrayList<ImageView> images_;

    public LoadImages() {
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, 0); // some number
        this.setArguments(args);

        images_ = new ArrayList<ImageView>();
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
//    public static LoadImages newInstance(int sectionNumber) {
//        LoadImages fragment = new LoadImages();
//        Bundle args = new Bundle();
//        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.image_grid_layout, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        textView.setText(getString(R.string.message0, getArguments().getInt(ARG_SECTION_NUMBER)));

        // for not practical with ids?
        images_.add((ImageView) rootView.findViewById(R.id.imageView0));
        images_.add((ImageView) rootView.findViewById(R.id.imageView1));
        images_.add((ImageView) rootView.findViewById(R.id.imageView2));
        images_.add((ImageView) rootView.findViewById(R.id.imageView3));

        // set button action
        View mainView = container.getRootView();

        FloatingActionButton fab = (FloatingActionButton) mainView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnMainAction();
            }
        });
        System.out.println("set action to LoadImages View");

        return rootView;
    }

    public void OnMainAction() {
        String online_source =  "http://lorempixel.com/400/200/"; // getting random bmps off the Internet
        String broken_source =  "http://idont/exist";
        Bitmap loading = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_dialog_info);
        for(int i = 0; i < images_.size(); ++i) {
            images_.get(i).setImageBitmap(loading);
            if(i == images_.size() - 1)
                new LoadImageTask(images_.get(i)).execute(broken_source);
            else
                new LoadImageTask(images_.get(i)).execute(online_source);
        }
    }

    private class LoadImageTask extends AsyncTask<String, Void, Bitmap> {

        private Exception exception;
        private ImageView iv_;

        public LoadImageTask(ImageView iv) {
            // takes view to assign image to
            this.iv_ = iv;
        }

        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                URLConnection conn = url.openConnection();
                conn.setConnectTimeout(4000);
                Bitmap bmp = BitmapFactory.decodeStream(conn.getInputStream());
                return bmp;
            } catch (IOException e) {
                this.exception = e;
            }
            return null;
        }

        protected void onPostExecute(Bitmap bmp) {
            //  check this.exception
            if(this.exception != null || bmp == null) {
                System.err.println("couldn't download Image: "+exception.getMessage());
                Bitmap error = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_dialog_alert);
                iv_.setImageBitmap(error);
                return;
            }

            iv_.setImageBitmap(bmp);

        }
    }
} // end of LoadImages
