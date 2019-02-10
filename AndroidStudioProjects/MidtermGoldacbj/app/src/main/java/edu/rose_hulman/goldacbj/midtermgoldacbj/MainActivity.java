package edu.rose_hulman.goldacbj.midtermgoldacbj;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.AlteredCharSequence;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Collections;

public class MainActivity extends AppCompatActivity
        implements MatchingGame.gameUIController, VehicleAdapter.MainUIController{
    private VehicleAdapter mAdapter;
    private TextView mScoreText;
    private TextView mCurrentWordText;
    private MatchingGame mGame;
    private int tempCurrentLangNum;

    private final String KEY_GAME = "GAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String currentLangugage = getResources().getString(R.string.english);
        tempCurrentLangNum = 0;

        mScoreText = (TextView) findViewById(R.id.scoreTextView);
        mCurrentWordText = (TextView) findViewById(R.id.currentWordTextView);

        // TODO: if savedsate also reset context on mGame
        if(savedInstanceState != null) {
            mGame = savedInstanceState.getParcelable(KEY_GAME);
        }else {
            mGame = new MatchingGame(this, currentLangugage);
        }

        RecyclerView recycler = (RecyclerView) findViewById(R.id.recyclerView);
        mAdapter = new VehicleAdapter(recycler, this, mGame);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setHasFixedSize(true);
        recycler.setAdapter(mAdapter);

        showNewWord();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_reset:
                resetGame();
                break;
            case R.id.action_shuffle:
                shuffleWords();
                break;
            case R.id.action_settings:
                changeLanguageDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putParcelable(KEY_GAME, mGame);
    }

    //--------------- Game UI Controller Methods -------------------//
    public void changeLanguageDialog() {
        DialogFragment df = new DialogFragment() {
            @NonNull
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                super.onCreateDialog(savedInstanceState);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                tempCurrentLangNum = mGame.getCurrentLanguageToLearnNum();
                builder.setSingleChoiceItems(mGame.getLanguages(), mGame.getCurrentLanguageToLearnNum(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        tempCurrentLanguageChange(i);
                    }
                });
                builder.setTitle(R.string.toLearn);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        changeLanguage(tempCurrentLangNum);
                    }
                });

                return builder.create();
            }
        };

        df.show(getSupportFragmentManager(), "ChangeLanguage");
    }

    public void tempCurrentLanguageChange(int i) {
        tempCurrentLangNum = i;
    }

    public void changeLanguage(int pos){
        String language = MatchingGame.getLanguages()[pos];
        mGame = new MatchingGame(this, language);
        mAdapter.resetGame(mGame);
        showNewScore();
        showNewWord();
    }

    public void resetGame() {
        mGame = new MatchingGame(this, mGame.getCurrentLanguageToLearn());
        mAdapter.resetGame(mGame);
        showNewScore();
        showNewWord();
    }

    public void shuffleWords() {
        Collections.shuffle(mGame.getWords());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showNewScore() {
        mScoreText.setText(getResources().getQuantityString(R.plurals.score, mGame.getScore(), mGame.getScore()));
    }

    @Override
    public void showGameOver() {
        DialogFragment df = new DialogFragment() {
            @NonNull
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                super.onCreateDialog(savedInstanceState);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(getResources().getString(R.string.congratulations));
                String scoreString = getResources().getString(R.string.yourScore);
                scoreString += "   " + String.valueOf(mGame.getScore())+ "/" + String.valueOf(mGame.getMaxPoints());
                builder.setMessage(scoreString);

                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        resetGame();
                    }
                });
                return builder.create();
            }
        };
        df.show(getSupportFragmentManager(), "GameOver");
    }

    @Override
    public void showHint() {
        //TODO: show score hint
    }

    @Override
    public void showNewWord() {
        String newWord = "blah";
        switch (mGame.getCurrentLanguageToLearn()) {
            case "English":
                newWord = mGame.getCurrentWord().getEnglish();
                break;
            case "Spanish":
                newWord = mGame.getCurrentWord().getLanguage2();
                break;
            case "German":
                newWord = mGame.getCurrentWord().getLanguage3();
                break;
            case "Definition":
                newWord = mGame.getCurrentWord().getDefinition();
                break;
        }
        mCurrentWordText.setText(newWord);
    }

    @Override
    public void itemClicked(boolean matched, int position) {
        String message;
        if(matched) {
            message = getResources().getString(R.string.matched) + " ";
        }else {
            message = getResources().getString(R.string.clicked);
            switch (mGame.getCurrentLanguageToLearn()) {
            case "English":
                message += " " + mGame.getWords().get(position).getEnglish();
                break;
            case "Spanish":
                message += " " + mGame.getWords().get(position).getLanguage2();
                break;
            case "German":
                message += " " + mGame.getWords().get(position).getLanguage3();
                break;
            case "Definition":
                message += " " + mGame.getWords().get(position).getDefinition();
                break;
            }
        }

        Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void itemLongClicked(final int position) {
        DialogFragment df = new DialogFragment() {
            @NonNull
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                super.onCreateDialog(savedInstanceState);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setTitle(getResources().getString(R.string.hint));
                View view = getActivity().getLayoutInflater().inflate(R.layout.hint_layout, null);

                TextView englishView = (TextView) view.findViewById(R.id.englishNameTextView);
                TextView spanishView = (TextView) view.findViewById(R.id.spanishNameTextView);
                TextView germanView = (TextView) view.findViewById(R.id.germanNameTextView);
                TextView definitionView = (TextView) view.findViewById(R.id.defitionTextView);

                englishView.setText(mGame.getWords().get(position).getEnglish());
                spanishView.setText(mGame.getWords().get(position).getLanguage2());
                germanView.setText(mGame.getWords().get(position).getLanguage3());
                definitionView.setText(mGame.getWords().get(position).getDefinition());

                builder.setView(view);
                return builder.create();
            }
        };

        df.show(getSupportFragmentManager(), "HINT");
        showNewScore();
    }
}
