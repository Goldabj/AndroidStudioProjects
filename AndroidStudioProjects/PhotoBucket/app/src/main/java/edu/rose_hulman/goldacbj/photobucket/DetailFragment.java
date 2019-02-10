package edu.rose_hulman.goldacbj.photobucket;


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private final static String ARG_PIC = "CURRENT_PIC";

    private Pics mCurrentPic;
    private ImageView mImageView;


    public DetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailFragment newInstance(Pics currentPic) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PIC, currentPic);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCurrentPic = getArguments().getParcelable(ARG_PIC);
        }else {
            mCurrentPic = new Pics();
        }
        new LoadImageTask(getContext()).execute(mCurrentPic.url);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        mImageView = (ImageView) view.findViewById(R.id.imageView);
        TextView captionView = (TextView) view.findViewById(R.id.detial_caption);
        captionView.setText(mCurrentPic.caption);

        return view;
    }

    public void setImage(Bitmap image) {
        mImageView.setImageBitmap(image);
    }


    public class LoadImageTask extends AsyncTask<String, Void, Bitmap> {

        private Context context;
        private ProgressDialog progressBar;

        public LoadImageTask(Context cont) {
            context = cont;
            progressBar = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            progressBar.show();
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            String urlstring = strings[0];
            InputStream in = null;
            try {
                URL url = new URL(urlstring);
                in = url.openStream();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Bitmap image = BitmapFactory.decodeStream(in);
            return image;
        }

        @Override
        protected void onPostExecute(Bitmap image) {
            super.onPostExecute(image);
            progressBar.hide();
            progressBar.dismiss();
            setImage(image);
        }

    }


}
