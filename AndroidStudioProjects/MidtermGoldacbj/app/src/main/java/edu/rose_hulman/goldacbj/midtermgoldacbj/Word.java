package edu.rose_hulman.goldacbj.midtermgoldacbj;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by goldacbj on 7/6/2016.
 */
public class Word implements Parcelable{
    private String english;
    private String language2;
    private String language3;
    private String unicode;
    private String definition;

    public Word() {
    }

    protected Word(Parcel in) {
        english = in.readString();
        language2 = in.readString();
        language3 = in.readString();
        unicode = in.readString();
        definition = in.readString();
    }

    public static final Creator<Word> CREATOR = new Creator<Word>() {
        @Override
        public Word createFromParcel(Parcel in) {
            return new Word(in);
        }

        @Override
        public Word[] newArray(int size) {
            return new Word[size];
        }
    };

    @Override
    public String toString() {
        return "Word{" +
                "english='" + english + '\'' +
                ", language2='" + language2 + '\'' +
                ", language3='" + language3 + '\'' +
                ", unicode='" + unicode + '\'' +
                ", definition=" + definition +
                '}';
    }

    public String getEnglish() {
        return english;
    }

    public void setEnglish(String english) {
        this.english = english;
    }

    public String getLanguage2() {
        return language2;
    }

    public void setLanguage2(String language2) {
        this.language2 = language2;
    }

    public String getLanguage3() {
        return language3;
    }

    public void setLanguage3(String language3) {
        this.language3 = language3;
    }

    public String getUnicode() {
        return unicode;
    }

    public void setUnicode(String unicode) {
        this.unicode = unicode;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(english);
        parcel.writeString(language2);
        parcel.writeString(language3);
        parcel.writeString(unicode);
        parcel.writeString(definition);
    }
}
