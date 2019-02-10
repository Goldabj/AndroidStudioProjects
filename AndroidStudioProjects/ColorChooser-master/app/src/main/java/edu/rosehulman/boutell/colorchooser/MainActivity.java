package edu.rosehulman.boutell.colorchooser;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    static final String EXTRA_COLOR = "EXTRA_COLOR";
    static final int REQUEST_COLOR = 1;


    private RelativeLayout mLayout;
    private TextView mTextView;
    private String mMessage = "This is your phone. Please rescue me!";
    private int mBackgroundColor = Color.GREEN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Send an email with the message field as the body
                sendEmail();
            }
        });

        // Capture
        mLayout = (RelativeLayout) findViewById(R.id.content_main_layout);
        mTextView = (TextView) findViewById(R.id.content_main_message);

        // Set color and text
        updateUI();
    }

    private void sendEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, "xyz@gmail.com");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Help me");
        emailIntent.putExtra(Intent.EXTRA_TEXT, mMessage);
        startActivity(emailIntent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_COLOR && resultCode == RESULT_OK) {
            mBackgroundColor = data.getIntExtra(EXTRA_COLOR, Color.GRAY);
            mMessage = data.getStringExtra(EXTRA_MESSAGE);
            updateUI();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_info:
                // TODO: Launch a new Info Activity that is a ScrollingActivity.
                Intent infoIntent = new Intent(this, InfoActivity.class);
                startActivity(infoIntent);
                return true;

            case R.id.action_change_color:
                // TODO: Launch the InputActivity to get a result
                Intent inputIntent = new Intent(this, InputActivity.class);
                inputIntent.putExtra(EXTRA_COLOR, mBackgroundColor);
                inputIntent.putExtra(EXTRA_MESSAGE, mMessage);
                startActivityForResult(inputIntent, REQUEST_COLOR);
                return true;

            case R.id.action_settings:
                startActivity(new Intent(Settings.ACTION_LOCALE_SETTINGS));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateUI() {
        mTextView.setText(mMessage);
        mLayout.setBackgroundColor(mBackgroundColor);
    }


}
