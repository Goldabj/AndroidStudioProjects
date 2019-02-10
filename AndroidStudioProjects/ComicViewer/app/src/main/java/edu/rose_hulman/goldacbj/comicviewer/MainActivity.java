package edu.rose_hulman.goldacbj.comicviewer;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.PagerTitleStrip;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Switch;
import android.widget.TextView;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

import uk.co.senab.photoview.PhotoViewAttacher;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link FragmentStatePagerAdapter}.
     */
    private ComicPagerAdapter mComicPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mComicPagerAdapter = new ComicPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mComicPagerAdapter);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mComicPagerAdapter.add();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class ComicholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String COMIC = "comic";
        private static final String XKCD = "xkcd";
        private PhotoViewAttacher zoomer;
        private ImageView mImageView;
        private TextView mTextView;
        Comic mComic = null;
        Bitmap mImage = null;
        String mTitle = null;
        private AlertDialog dialog;

        public ComicholderFragment() {
            setRetainInstance(true);
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static ComicholderFragment newInstance(Comic comic) {
            ComicholderFragment fragment = new ComicholderFragment();
            Bundle args = new Bundle();
            args.putParcelable(COMIC, comic);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            rootView.setBackgroundColor(getResources().getColor(mComic.getColor()));
            mImageView = (ImageView) rootView.findViewById(R.id.comic_image);
            mTextView = (TextView) rootView.findViewById(R.id.comic_text);
            if(mImage != null) {
                mImageView.setImageBitmap(mImage);
            }
            if(mTitle != null) {
                mTextView.setText(mTitle);
            }
            zoomer = new PhotoViewAttacher(mImageView);
            return rootView;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);
            if (getArguments() != null) {
                mComic = getArguments().getParcelable(COMIC);
                String urlString = String.format(Locale.US, "http://xkcd.com/%d/info.0.json", mComic.getIssueNumber());
                (new GetComicTask(this)).execute(urlString);
            }
        }

        public Comic getmComic() {
            return mComic;
        }

        public void setImage(Bitmap image) {
            mImage = image;
            mImageView.setImageBitmap(mImage);
            zoomer.update();
        }

        public void setTitle(String title) {
            mTitle = title;
            mTextView.setText(mTitle);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch(item.getItemId()) {
                case R.id.info_menu:
                    DialogFragment df = new DialogFragment() {
                        @NonNull
                        @Override
                        public Dialog onCreateDialog(Bundle savedInstanceState) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage(mComic.getComic().getAlt());
                            dialog = builder.create();
                            setRetainInstance(true);
                            return builder.create();
                        }
                    };
                    df.show(getFragmentManager(), "info");
                    return true;
            }

            return super.onOptionsItemSelected(item);
        }

        @Override
        public void onDestroyView() {
            if (dialog != null && getRetainInstance())
                dialog.setDismissMessage(null);
            super.onDestroyView();
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class ComicPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<ComicholderFragment> fragments = new ArrayList<>();
        private int count;

        public ComicPagerAdapter(FragmentManager fm) {
            super(fm);
            count = 5;
        }

        public void add() {
            fragments.add(ComicholderFragment.newInstance(new Comic()));
            count++;
            notifyDataSetChanged();
        }

        @Override
        public Fragment getItem(int position) {
            if (position >= fragments.size()) {
                fragments.add(ComicholderFragment.newInstance(new Comic()));
            }
            return fragments.get(position);
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
        }

        @Override
        public int getCount() {
            // Show 5 total pages.
            return count;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            ComicholderFragment frag = (ComicholderFragment) getItem(position);
            Comic comic;
            if (frag.getArguments() != null) {
                comic = (Comic) (frag.getArguments().getParcelable(ComicholderFragment.COMIC));
            } else {
                comic = frag.mComic;
            }
            int number = comic.getIssueNumber();
            return getResources().getString(R.string.Resource_Number) + String.valueOf(number);
        }
    }


    public static class GetComicTask extends AsyncTask<String, Void, Xkcd> {
        ComicholderFragment mFragment;
        String urlString;
        public GetComicTask(ComicholderFragment frag) {
            mFragment = frag;
        }

        @Override
        protected Xkcd doInBackground(String... strings) {
            urlString = strings[0];
            Xkcd comic = null;
            try {
                comic = (new ObjectMapper()).readValue(new URL(urlString), Xkcd.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return comic;
        }

        @Override
        protected void onPostExecute(Xkcd xkcd) {
            super.onPostExecute(xkcd);
            mFragment.getmComic().setComic(xkcd);
            mFragment.setTitle(xkcd.getSafe_title());
            (new LoadImageTask(mFragment)).execute(xkcd.getImg());
        }

    }

    public static class LoadImageTask extends AsyncTask<String, Void, Bitmap>{
        ComicholderFragment mFragment;
        String urlString;

        public LoadImageTask(ComicholderFragment frag) {
            mFragment = frag;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            urlString = strings[0];
            InputStream in = null;
            try {
                in = new URL(urlString).openStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Bitmap bitmap = BitmapFactory.decodeStream(in);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            mFragment.setImage(bitmap);
        }
    }
}
