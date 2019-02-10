package edu.rose_hulman.goldacbj.jerseys;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    private JerseyInfo mCurrentJersey;
    private Drawable[] jerseys = new Drawable[2];
    private TextView mJerseyName;
    private TextView mJerseyNum;
    private ImageView mJerseyImage;

    private static final String PREFS_KEY = "PREFS";
    private static final String JERSEY_NAME_KEY = "JERSEY_NAME";
    private static final String JERSEY_NUM_KEY = "JERSEY_NUM";
    private static final String JERSEY_COLOR_KEY = "JERSEY_COLOR";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Todo
        // change settings


        // 1. caputre vies
        mJerseyName = (TextView) findViewById(R.id.name_text_view);
        mJerseyNum = (TextView) findViewById(R.id.number_text_view);
        mJerseyImage = (ImageView) findViewById(R.id.jersey_image_view);
        jerseys[0] = (getResources().getDrawable(R.drawable.blue_jersey));
        jerseys[1] = (getResources().getDrawable(R.drawable.red_jersey));

        SharedPreferences prefs = getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        String name = prefs.getString(JERSEY_NAME_KEY, getString(R.string.default_jersey_name));
        int number = prefs.getInt(JERSEY_NUM_KEY, 35);
        boolean isRed = prefs.getBoolean(JERSEY_COLOR_KEY, true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               editJesery();
            }
        });


        mCurrentJersey = new JerseyInfo(name, number, isRed);


        showCurrentJersey();

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
            startActivity(new Intent(Settings.ACTION_LOCALE_SETTINGS));
            return true;
        }else if(id == R.id.reset_action) {
            resetJersey();
        }

        return super.onOptionsItemSelected(item);
    }

    private void showCurrentJersey() {
        // 1. Name
        // 2. Number
        // 3. Jersey color
        int isRed = (mCurrentJersey.getIsRed()) ? 1 : 0;
        mJerseyImage.setImageDrawable(jerseys[isRed]);
        mJerseyName.setText(mCurrentJersey.getJerseyName());
        mJerseyNum.setText(Integer.toString(mCurrentJersey.getJerseyNum()));
    }

    private void resetJersey() {
        // TODO
        // 1. Dialog
        // 2. ok actoion resets Jerseyinfo
        // 2.b show current jersey
        DialogFragment df = new DialogFragment() {
            @NonNull
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(getString(R.string.confirmation_dialog_message));
                builder.setTitle(getString(R.string.reset));
                builder.setNegativeButton(android.R.string.cancel, null);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mCurrentJersey = new JerseyInfo();
                        showCurrentJersey();
                    }
                });
                return builder.create();
            }
        };
        df.show(getSupportFragmentManager(), "Reset");
    }

    private void editJesery() {
        // Todo
        // 0. Dialog for editing
        // 1. change name
        // 2. change number
        // 3. change jersey Image
        // 4. check for valid inputs
        // 5. show new jersey
        DialogFragment df = new DialogFragment() {
            @NonNull
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_change_jersey, null);
                builder.setView(view);

                final EditText jerseyNameInput = (EditText) view.findViewById(R.id.player_name_input);
                final EditText jerseyNumberInput = (EditText) view.findViewById(R.id.player_number_input);
                Switch redOrBlueSwitch = (Switch) view.findViewById(R.id.jersey_switch);
                final boolean[] isRed = {true};
                if(mCurrentJersey.getIsRed()) {
                    redOrBlueSwitch.setChecked(true);
                }

                redOrBlueSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked) {
                            isRed[0] = true;
                        }else {
                            isRed[0] = false;
                        }
                    }
                });


                builder.setNegativeButton(android.R.string.cancel, null);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = jerseyNameInput.getText().toString();
                        int number = 0;
                        try {
                           number = Integer.parseInt(jerseyNumberInput.getText().toString());
                        }catch (Exception e) {
                            number = 0;
                        }
                        mCurrentJersey = new JerseyInfo(name, number, isRed[0]);
                        showCurrentJersey();
                    }
                });

                return builder.create();
            }
        };
        df.show(getSupportFragmentManager(), "Edit Jersey");
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences prefs = getSharedPreferences("PREFS", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(JERSEY_NAME_KEY, mCurrentJersey.getJerseyName());
        editor.putInt(JERSEY_NUM_KEY, mCurrentJersey.getJerseyNum());
        editor.putBoolean(JERSEY_COLOR_KEY, mCurrentJersey.getIsRed());

        editor.commit();

    }
}
