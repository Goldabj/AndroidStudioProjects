package edu.rose_hulman.goldacbj.comicviewer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by goldacbj on 6/28/2016.
 */
public class Comic implements Parcelable {
    private int color;
    private int issueNumber;
    private Xkcd comic;

    private static int[] colors = new int[]{android.R.color.holo_blue_light, android.R.color.holo_green_light,
                                    android.R.color.holo_red_light, android.R.color.holo_orange_light};
    private static int number = 0;

    public Comic() {
        color = colors[number++%4];
        issueNumber = Utils.getRandomCleanIssue();
        comic = null;
    }

    public Xkcd getComic() {
        return comic;
    }

    public void setComic(Xkcd xkcd) {
        comic = xkcd;
    }


    protected Comic(Parcel in) {
        color = in.readInt();
        issueNumber = in.readInt();
    }

    public static final Creator<Comic> CREATOR = new Creator<Comic>() {
        @Override
        public Comic createFromParcel(Parcel in) {
            return new Comic(in);
        }

        @Override
        public Comic[] newArray(int size) {
            return new Comic[size];
        }
    };

    public int getColor() {return color;}


    public int getIssueNumber() {return issueNumber;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(color);
        parcel.writeInt(issueNumber);
    }
}

