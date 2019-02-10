package edu.rose_hulman.goldacbj.jerseys;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by goldacbj on 6/14/2016.
 */
public class JerseyInfo {
    private String jerseyName;
    private int jerseyNum;
    private boolean isRed;

    public JerseyInfo() {
        jerseyName = "ANDROID";
        jerseyNum = 35;
        isRed = true;
    }

    public JerseyInfo(String name, int number, boolean red) {
        jerseyName = name;
        jerseyNum = number;
        isRed = red;
    }

    public String getJerseyName() {
        return jerseyName;
    }

    public int getJerseyNum() {
        return jerseyNum;
    }

    public boolean getIsRed() {
        return isRed;
    }


}
