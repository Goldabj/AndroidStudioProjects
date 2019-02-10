package edu.rose_hulman.goldacbj.midtermgoldacbj.Utils;

import android.content.Context;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import edu.rose_hulman.goldacbj.midtermgoldacbj.R;
import edu.rose_hulman.goldacbj.midtermgoldacbj.Word;

/**
 * Created by goldacbj on 7/6/2016.
 */
public class FileUtils {
    public static List<Word> loadWords(Context context) {
        InputStream is = context.getResources().openRawResource(R.raw.vehicle_words);
        List<Word> words = null;
        try {
            words = new ObjectMapper().readValue(is, new TypeReference<List<Word>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return words;
    }

}
