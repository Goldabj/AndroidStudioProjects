package edu.rose_hulman.goldacbj.pointofsale;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private TextView mNameText;
    private TextView mQuantityText;
    private TextView mTimeText;
    private Item mCurrentItem;
    private ArrayList<Item> mItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mNameText = (TextView) findViewById(R.id.name_text);
        mQuantityText = (TextView) findViewById(R.id.quantity_text);
        mTimeText = (TextView) findViewById(R.id.date_text);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem(false);
            }
        });

        registerForContextMenu(mNameText);
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
        switch (id) {
            case R.id.reset_action:
                final Item clearedItem = mCurrentItem;
                mCurrentItem = new Item();
                showCurrentItem();
                Snackbar.make(findViewById(R.id.coordinate_layout), "item removed", Snackbar.LENGTH_LONG).setAction("UNDO",
                       new View.OnClickListener() {
                           @Override
                           public void onClick(View v) {
                               mCurrentItem = clearedItem;
                               showCurrentItem();
                               Snackbar.make(findViewById(R.id.coordinate_layout), "item Restored", Snackbar.LENGTH_SHORT).show();
                           }
                       }).show();

                return true;
            case R.id.action_settings:
                startActivity(new Intent(Settings.ACTION_SETTINGS));
                return true;
            case R.id.search_actoin:
                showSearchDialog();
                return true;
            case R.id.clearAll_action:
                clearAll();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void showCurrentItem() {
        mNameText.setText(mCurrentItem.getName());
        mQuantityText.setText(getString(R.string.quantity_format, mCurrentItem.getQuantity()));
        mTimeText.setText(getString(R.string.date_format, mCurrentItem.getDeliveryDateString()));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_context_edit:
                addItem(true);
                return true;
            case R.id.menu_context_remove:
                mItems.remove(mCurrentItem);
                mCurrentItem = new Item();
                showCurrentItem();
                return true;
        }

        return super.onContextItemSelected(item);

    }

    private void addItem(final boolean isEditing) {
        DialogFragment df = new DialogFragment() {
            @NonNull
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_add, null);
                builder.setView(view);
                // caputre
                final EditText nameText = (EditText)view.findViewById(R.id.edit_name);
                final EditText quantityText = (EditText)view.findViewById(R.id.quantity_text_input);
                final CalendarView calendar = (CalendarView)view.findViewById(R.id.calendarView);

                if(isEditing) {
                    nameText.setText(mCurrentItem.getName());
                    quantityText.setText(String.valueOf(mCurrentItem.getQuantity()));
                    calendar.setDate(mCurrentItem.getDeliveryDateTime());
                }

                //buttons
                builder.setNegativeButton(android.R.string.cancel, null);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = nameText.getText().toString();
                        int quantity = Integer.parseInt(quantityText.getText().toString());
                        final long deliveryDate = calendar.getDate();

                        if(isEditing) {
                            mCurrentItem.setName(name);
                            mCurrentItem.setQuantity(quantity);
                            mCurrentItem.setDeliveryDate(new Date(deliveryDate));
                        }else {
                            mCurrentItem = new Item(name, quantity, new Date(deliveryDate));
                            mItems.add(mCurrentItem);
                        }
                        showCurrentItem();
                    }
                });

                return builder.create();
            }
        };
        df.show(getSupportFragmentManager(), "add");

    }

    private void showSearchDialog() {
        DialogFragment df = new DialogFragment() {
            @NonNull
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Choose and Item");
                builder.setItems(getNames(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mCurrentItem = mItems.get(which);
                        showCurrentItem();
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, null);

                return builder.create();
            }
        };
        df.show(getSupportFragmentManager(), "Search");
    }


    private String[] getNames() {
        String[] names = new String[mItems.size()];
        for(int i = 0; i < mItems.size(); i++) {
            names[i] = (mItems.get(i).getName());
        }
        return names;
    }

    private void clearAll() {
        DialogFragment df = new DialogFragment() {
            @NonNull
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getString(R.string.remove));
                builder.setMessage("Are you Sure you want to clear all items");
                builder.setNegativeButton(android.R.string.cancel, null);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mItems = new ArrayList<Item>();
                        mCurrentItem = new Item();
                        showCurrentItem();
                    }
                });

                return builder.create();
            }
        };
        df.show(getSupportFragmentManager(), "ClearAll");
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_menu, menu);
    }

}
