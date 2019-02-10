package edu.rose_hulman.goldacbj.photobucket;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements PicFragment.OnListFragmentInteractionListener,
                                            LoginFragment.OnLoginListener, GoogleApiClient.OnConnectionFailedListener {

    // my Local vars
    PicFragment mPicFrag;
    MyPicRecyclerViewAdapter mAdapter;
    LoginFragment mLoginFrag;
    MenuItem myPicsMenuItem;

    // Firebase vars
    DatabaseReference mPhotoDatabase;
    FirebaseAuth mAuth;
    FirebaseUser mCurrentUser;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    OnCompleteListener mOnCompletionListener;
    GoogleApiClient mGoogleApiClient;

    // Constants
    final static int RC_SIGN_IN = 1;

    // TODO: add login options
    // TODO: options menu Fix
    // TODO: layout short Fix

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mPhotoDatabase = FirebaseDatabase.getInstance().getReference().child("pictures");
        mPhotoDatabase.addChildEventListener(new PictureChildListener());

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = null;
        mAdapter = new MyPicRecyclerViewAdapter(this, "");

            mLoginFrag = new LoginFragment();
            invalidateOptionsMenu();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.frame_holder, mLoginFrag, "Login");
            transaction.commit();

        // mAuth state listener initalization
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mCurrentUser = firebaseAuth.getCurrentUser();
                if(mCurrentUser != null) {
                    changeToPhotoFragment();
                    if(mAdapter != null) {
                        mAdapter.setUser(mCurrentUser.getUid());
                    }
                }else {
                    changeToLoginFragment();
                }
            }
        };

        mOnCompletionListener = new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(!(task.isSuccessful())) {
                    showLoginError(task.getException().getMessage());
                }
            }
        };

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    public void changeToPhotoFragment() {
        mPicFrag = new PicFragment().newInstance(mAdapter, mCurrentUser.getUid());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_holder, mPicFrag);
        transaction.commit();
    }

    public void changeToLoginFragment() {
        mLoginFrag = new LoginFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_holder, mLoginFrag, "Login");
        transaction.commit();
        invalidateOptionsMenu();
    }

    public void showLoginError(String message) {
        LoginFragment loginFragment = (LoginFragment) getSupportFragmentManager().findFragmentByTag("Login");
        loginFragment.onLoginError(message);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        myPicsMenuItem = menu.findItem(R.id.mPics_action);
        return true;

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthStateListener != null && mAuth != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }

        assert mAuth != null;
        mAuth.signOut();
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
        } else if(id == R.id.action_logout) {
            logout();
        }
        else {
            if(myPicsMenuItem.getTitle().equals(getString(R.string.show_my_pictures))) {
                myPicsMenuItem.setTitle(getString(R.string.show_all));
                mAdapter.setShowAll(false);
            }else {
                myPicsMenuItem.setTitle(getString(R.string.show_my_pictures));
                mAdapter.setShowAll(true);
            }

        }

        return super.onOptionsItemSelected(item);
    }

    public void logout() {
        if(mCurrentUser != null) {
            mAuth.signOut();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                mAuth.signInWithCredential(credential).addOnCompleteListener(mOnCompletionListener);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }

    @Override
    public void showAddItem() {
        DialogFragment df = new DialogFragment() {
            @NonNull
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                View view = getActivity().getLayoutInflater().inflate(R.layout.editpic_dialog, null);
                builder.setView(view);
                builder.setTitle("Add to Weather Pics");

                final EditText captionText = (EditText) view.findViewById(R.id.edit_caption);
                final EditText urlText = (EditText) view.findViewById(R.id.edit_urltext);

                builder.setNegativeButton(android.R.string.cancel, null);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String caption = captionText.getText().toString();
                        String url = urlText.getText().toString();
                        if(url.equals("")) {
                            url = Utils.randomImageUrl();
                        }
                        if(caption.equals("")) {
                            caption = "caption";
                        }
                        mPhotoDatabase.push().setValue(new Pics(url, caption, mCurrentUser.getUid()));
            }
        });

                return builder.create();
            }
        };
        df.show(getSupportFragmentManager(), "add to Weather Pics");
    }


    @Override
    public void onListFragmentInteraction(Pics item) {
        FragmentManager manager = getSupportFragmentManager();
        DetailFragment detailfrag = DetailFragment.newInstance(item);
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.frame_holder, detailfrag);
        transaction.addToBackStack("listFragment");
        transaction.commit();
    }

    @Override
    public void onLongFragmentClick(Pics item) {
        if(mCurrentUser.getUid().equals(item.getUid())) {
            showEditPic(item);
        }else {
            showToast("Cannot edit, this is not your picture");
        }
    }

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }


    //______________ local methods______________//

    public void showEditPic(final Pics picture) {
        DialogFragment df = new DialogFragment() {
            @NonNull
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                View view = getActivity().getLayoutInflater().inflate(R.layout.editpic_dialog, null);
                builder.setView(view);
                builder.setTitle("Edit Pic");
                builder.setNeutralButton(android.R.string.cancel, null);

                final EditText captionText = (EditText) view.findViewById(R.id.edit_caption);
                final EditText urlText = (EditText) view.findViewById(R.id.edit_urltext);

                captionText.setText(picture.caption);
                urlText.setText(picture.url);

                builder.setNegativeButton(getResources().getString(R.string.delete), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mPhotoDatabase.child(picture.getKey()).removeValue();
                    }
                });

                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String url = urlText.getText() != null ? urlText.getText().toString() : Utils.randomImageUrl();
                        String caption = captionText.getText() != null ? captionText.getText().toString() : "Caption";
                        Pics newPicture = new Pics(url, caption, picture.key, mCurrentUser.getUid());
                        if(newPicture.url.equals("")) {
                            newPicture.url = Utils.randomImageUrl();
                        }
                        if(newPicture.caption.equals("")) {
                            newPicture.caption = "caption";
                        }
                        mPhotoDatabase.child(picture.key).setValue(newPicture);
                    }
                });
                return builder.create();
            }
        };
        df.show(getSupportFragmentManager(), "Edit Weather Pic");
    }

    @Override
    public void onLogin(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(mOnCompletionListener);
    }

    @Override
    public void onGoogleLogin() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onFacebookLogin(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(mOnCompletionListener);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("photoBucket", "cannot connect to google api client");
    }

    //____________________ INNER CLASSES ____________________________//

    public class PictureChildListener implements ChildEventListener {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            Pics newPictures = dataSnapshot.getValue(Pics.class);
            newPictures.setKey(dataSnapshot.getKey());
            mAdapter.addNewPhoto(newPictures);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            Pics newPicture = dataSnapshot.getValue(Pics.class);
            newPicture.setKey(dataSnapshot.getKey());
            mAdapter.editPic(newPicture);
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            Pics newPicture = dataSnapshot.getValue(Pics.class);
            newPicture.setKey(dataSnapshot.getKey());
            mAdapter.removePhoto(newPicture);
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e("photoBucket", databaseError.getMessage());
        }
    }


}
