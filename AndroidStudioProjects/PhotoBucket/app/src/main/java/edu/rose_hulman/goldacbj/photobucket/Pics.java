package edu.rose_hulman.goldacbj.photobucket;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.net.URL;

/**
 * Created by goldacbj on 7/23/2016.
 */
public class Pics implements Parcelable{
    String url;
    String caption;
    String uid;
    @Exclude
    String key;

    public Pics(String url, String cap, String id) {
        this.url = url;
        this.caption = cap;
        this.uid = id;
    }


    public Pics(String url, String cap) {
        this.url = url;
        this.caption = cap;
    }

    public Pics(String url, String cap, String key,String uid) {
        this.url = url;
        this.caption = cap;
        this.key = key;
        this.uid = uid;
    }

    public Pics() {

    }

    protected Pics(Parcel in) {
        url = in.readString();
        caption = in.readString();
        key = in.readString();
        uid = in.readString();
    }

    public static final Creator<Pics> CREATOR = new Creator<Pics>() {
        @Override
        public Pics createFromParcel(Parcel in) {
            return new Pics(in);
        }

        @Override
        public Pics[] newArray(int size) {
            return new Pics[size];
        }
    };

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setUid(String uid) {this.uid = uid;}

    public String getUid() {return this.uid;}

    @Exclude
    public String getKey() {
        return key;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(url);
        parcel.writeString(caption);
        parcel.writeString(key);
        parcel.writeString(this.uid);
    }
}
