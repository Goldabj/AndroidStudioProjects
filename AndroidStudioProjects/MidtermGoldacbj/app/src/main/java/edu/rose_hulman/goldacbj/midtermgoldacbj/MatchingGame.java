package edu.rose_hulman.goldacbj.midtermgoldacbj;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;

import com.fasterxml.jackson.databind.deser.DataFormatReaders;

import java.util.ArrayList;
import java.util.Random;

import edu.rose_hulman.goldacbj.midtermgoldacbj.Utils.FileUtils;

/**
 * Created by goldacbj on 7/6/2016.
 */
public class MatchingGame implements Parcelable {
    private ArrayList<Word> words;
    private int score;
    private int maxPoints;
    private int currentLanguageToLearn;
    private final static String[] languages = new String[]{"English", "Spanish", "German", "Definition"};
    private int currentWordPos;
    private Word currentWord;
    private static Random mRandom = new Random();
    private gameUIController mUIController;
    private Context mContext;

    public MatchingGame(Context context, String currentLanguage) {
        mContext = context;
        words = (ArrayList<Word>) FileUtils.loadWords(mContext);
        score = 0;
        maxPoints = words.size() * 2;
        setCurrentLanguageToLearn(currentLanguage);
        currentWordPos = mRandom.nextInt(words.size());
        currentWord = words.get(currentWordPos);
        mUIController = (gameUIController) mContext;
    }

    protected MatchingGame(Parcel in) {
        score = in.readInt();
        maxPoints = in.readInt();
        currentLanguageToLearn = in.readInt();
        currentWordPos = in.readInt();
        in.readList(words, Word.class.getClassLoader());
        currentWord = words.get(currentWordPos);
    }

    public void setContextAndUI(Context context) {
        mContext = context;
        mUIController = (gameUIController) context;
    }

    public static final Creator<MatchingGame> CREATOR = new Creator<MatchingGame>() {
        @Override
        public MatchingGame createFromParcel(Parcel in) {
            return new MatchingGame(in);
        }

        @Override
        public MatchingGame[] newArray(int size) {
            return new MatchingGame[size];
        }
    };

    public static String[] getLanguages() {
        return languages;
    }

    public boolean checkForMatch(int position) {
        Word checking = words.get(position);
        if(checking.getEnglish().equals(currentWord.getEnglish())) {
            updateScore(2);
            words.remove(position);
            if(isGameOver()) {
                mUIController.showGameOver();
            }else {
                currentWord = words.get(mRandom.nextInt(words.size()));
            }
            mUIController.showNewScore();
            mUIController.showNewWord();

            return true;
        }else {
            updateScore(-1);
            mUIController.showNewScore();
            return false;
        }
    }

    public void hint() {
        score--;
    }

    public boolean isGameOver() {
        return (words.size() == 0);
    }

    private void updateScore(int pointsToAdd) {
        score += pointsToAdd;
    }

    public int getScore() {
        return score;
    }

    public Word getCurrentWord() {
        return currentWord;
    }

    public int getMaxPoints() {
        return maxPoints;
    }

    public int getCurrentLanguageToLearnNum() { return currentLanguageToLearn; }

    public int getCurrentWordPos() {
        return currentWordPos;
    }

    public ArrayList<Word> getWords() { return words; }

    public String getCurrentLanguageToLearn() {
        return languages[currentLanguageToLearn];
    }

    public void setCurrentLanguageToLearn(String currentLanguage) {
        int pos = 0;
        for(int i = 0; i < languages.length; i++) {
            if(languages[i].equals(currentLanguage)) {
                pos = i;
                break;
            }
        }
        currentLanguageToLearn = pos;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(score);
        parcel.writeInt(maxPoints);
        parcel.writeInt(currentLanguageToLearn);
        parcel.writeInt(currentWordPos);
        parcel.writeList(words);
    }

    public interface gameUIController {
        public void showNewScore();
        public void showGameOver();
        public void showHint();
        public void showNewWord();
    }



}
