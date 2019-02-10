package edu.rose_hulman.goldacbj.photobucket;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {


    private EditText mPasswordView;
    private EditText mEmailView;
    private View mLoginForm;
    private View mProgressSpinner;
    private boolean mLoggingIn;
    private OnLoginListener mListener;
    private SignInButton mGoogleSignInButton;
    private CallbackManager mCallbackManager;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LoginFragment.
     */
    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().invalidateOptionsMenu();
        mLoggingIn = false;
        mCallbackManager = CallbackManager.Factory.create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        mEmailView = (EditText) rootView.findViewById(R.id.email);
        mPasswordView = (EditText) rootView.findViewById(R.id.password);
        mLoginForm = rootView.findViewById(R.id.login_form);
        mProgressSpinner = rootView.findViewById(R.id.login_progress);
        View loginButton = rootView.findViewById(R.id.email_sign_in_button);
        mGoogleSignInButton = (SignInButton) rootView.findViewById(R.id.google_sign_in_button);
        mEmailView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_NEXT) {
                    mPasswordView.requestFocus();
                    return true;
                }
                return false;
            }
        });
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_NULL) {
                    login();
                    return true;
                }
                return false;
            }
        });
        LoginButton FloginButton = (LoginButton) rootView.findViewById(R.id.facebook_login_button);
        FloginButton.setReadPermissions("email", "public_profile");
        // If using in a fragment
        FloginButton.setFragment(this);
        // Other app specific specialization

        // Callback registration
        FloginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                mListener.onFacebookLogin(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                onLoginError(exception.getMessage());
                // App code
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
        mGoogleSignInButton.setColorScheme(SignInButton.COLOR_LIGHT);
        mGoogleSignInButton.setSize(SignInButton.SIZE_WIDE);
        mGoogleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginWithGoogle();
            }
        });
        return rootView;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem myPicsItem = menu.findItem(R.id.mPics_action);
        myPicsItem.setVisible(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void onLoginError(String message) {
        new AlertDialog.Builder(getActivity())
                .setTitle(getActivity().getString(R.string.login_error))
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .create()
                .show();

        showProgress(false);
        mLoggingIn = false;
    }

    private void loginWithGoogle() {
        if (mLoggingIn) {
            return;
        }
        mEmailView.setError(null);
        mPasswordView.setError(null);

        showProgress(true);
        mLoggingIn = true;
        mListener.onGoogleLogin();
        hideKeyboard();
    }


    public void login() {
        if (mLoggingIn) {
            return;
        }

        mEmailView.setError(null);
        mPasswordView.setError(null);

        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancelLogin = false;
        View focusView = null;

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.invalid_password));
            focusView = mPasswordView;
            cancelLogin = true;
        }

        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.field_required));
            focusView = mEmailView;
            cancelLogin = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.invalid_email));
            focusView = mEmailView;
            cancelLogin = true;
        }

        if (cancelLogin) {
            // error in login
            focusView.requestFocus();
        } else {
            // show progress spinner, and start background task to login
            showProgress(true);
            mLoggingIn = true;
            mListener.onLogin(email, password);
            hideKeyboard();
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEmailView.getWindowToken(), 0);
    }

    private void showProgress(boolean show) {
        mProgressSpinner.setVisibility(show ? View.VISIBLE : View.GONE);
        mLoginForm.setVisibility(show ? View.GONE : View.VISIBLE);
        mGoogleSignInButton.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            mListener = (OnLoginListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnLoginListener {
        void onLogin(String email, String password);

        void onGoogleLogin();

        void onFacebookLogin(AccessToken token);

    }

}
