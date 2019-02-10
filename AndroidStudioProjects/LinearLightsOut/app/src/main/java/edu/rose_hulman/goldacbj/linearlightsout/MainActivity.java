package edu.rose_hulman.goldacbj.linearlightsout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    final int NUMBUTTONS = 7;
    int turnsTake = 0;
    boolean isGameOver = false;
    LinearGame mGame = new LinearGame();
    Button[] mLights = new Button[NUMBUTTONS];
    Button mNewGameButton;
    TextView mMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNewGameButton = (Button) findViewById(R.id.newGameButton);
        mMessage = (TextView) findViewById(R.id.message_text);

        for (int i = 0; i < NUMBUTTONS; i++) {
            int id = getResources().getIdentifier("button0" + i, "id", getPackageName());
            mLights[i] = (Button) findViewById(id);
            mLights[i].setOnClickListener(this);
        }

        if(savedInstanceState != null) {
            mGame = savedInstanceState.getParcelable("mGame");
            turnsTake = savedInstanceState.getInt("turnsTake");
            mMessage.setText(getResources().getQuantityString(R.plurals.turns_taken, turnsTake, turnsTake));
            isGameOver = savedInstanceState.getBoolean("isGameOver");
        }

        setLightText();

        mNewGameButton.setOnClickListener(this);
        if(isGameOver) {
            gameOver();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == mNewGameButton.getId()) {
            resetGame();
        }else {
            // light click
            turnsTake++;
            mMessage.setText(getResources().getQuantityString(R.plurals.turns_taken, turnsTake, turnsTake));

            // get the index of the button
            int index = 0;
            for (int i = 0; i < NUMBUTTONS; i++) {
                if (v.getId() == mLights[i].getId()) {
                    index = i;
                    break;
                }
            }
            // click button and check if game is over
            boolean gameOver = mGame.lightClickedAt(index);
            setLightText();
            if (gameOver) {
                gameOver();
            }
        }
    }

    /**
     * set the text of the buttons based on the games light value
     */
    private void setLightText() {
        for (int i = 0; i < NUMBUTTONS; i++) {
            // set the light text to the correct value
            String lightText;
            if (mGame.getLightValue(i) == 1) {
                lightText = getResources().getString(R.string.X);
            } else {
                lightText = getResources().getString(R.string.O);
            }
            mLights[i].setText(lightText);
        }
    }

    /**
     * disables the buttons and prints the correct text
     */
    private void gameOver() {
        mMessage.setText(getResources().getString(R.string.Won));
        for (int i = 0; i < NUMBUTTONS; i++)
            mLights[i].setEnabled(false);
    }

    /**
     * reset the game
     *  - enables the buttons
     *  - creates a new game
     *  - sets the buttons text
     *  - resets the message text
     */
    private void resetGame() {
        mGame = new LinearGame();
        for(int i = 0; i < NUMBUTTONS; i++) {
            mLights[i].setEnabled(true);
        }
        setLightText();
        turnsTake = 0;
        mMessage.setText(getResources().getString(R.string.Lights_start));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("turnsTake", turnsTake);
        outState.putParcelable("mGame", mGame);
        outState.putBoolean("isGameOver", isGameOver);

        // Save off data using outState.putXX(key, value)
        // Hint: you will use the appropriate methods to store int[] and ints,
        // maybe a String.
    }

}
