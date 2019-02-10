package edu.rose_hulman.goldacbj.tictactoe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TicTacToeGame mGame = new TicTacToeGame(this);
    private TextView mGameTicTacToeStateText;
    private Button[][] mGameTicTacToeButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mGameTicTacToeStateText = (TextView) findViewById(R.id.message_text);

        Button newGameButton = (Button) findViewById(R.id.new_game_button);
        newGameButton.setOnClickListener(this);

        mGameTicTacToeButtons = new Button[TicTacToeGame.NUM_ROWS][TicTacToeGame.NUM_COLUMNS];

        for(int i = 0; i < TicTacToeGame.NUM_ROWS; i++) {
            for(int j = 0; j < TicTacToeGame.NUM_COLUMNS; j++) {
                int id = getResources().getIdentifier("button" + i + j, "id", getPackageName());
                mGameTicTacToeButtons[i][j] = (Button) findViewById(id);
                mGameTicTacToeButtons[i][j].setOnClickListener(this);
            }
        }

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.new_game_button) {
            mGame.resetGame();
        }

        for(int i = 0; i < TicTacToeGame.NUM_ROWS; i++) {
            for (int j = 0; j < TicTacToeGame.NUM_COLUMNS; j++) {
                if(v.getId() == mGameTicTacToeButtons[i][j].getId()) {
                    Log.d("TTT", "button pressed at row " +i + "colum " +j);
                    mGame.pressedButtonAtLocation(i, j);
                }
                mGameTicTacToeButtons[i][j].setText(mGame.stringForButtonAtLocation(i, j));
            }
        }

        mGameTicTacToeStateText.setText(mGame.stringForGameState());
    }

}
