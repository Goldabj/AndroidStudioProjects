package edu.rose_hulman.goldacbj.photobucket;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;

/**
 * Created by goldacbj on 7/23/2016.
 */
public class Utils {
    public static String randomImageUrl() {
        String[] urls = new String[]{
                "https://upload.wikimedia.org/wikipedia/commons/1/1a/Dszpics1.jpg",
                "https://pixabay.com/static/uploads/photo/2015/09/14/15/52/lightning-939737_960_720.jpg",
                "http://s0.geograph.org.uk/geophotos/01/56/26/1562673_9088a4c9.jpg",
                "https://upload.wikimedia.org/wikipedia/commons/6/61/Hurricane_Hugo_15_September_1989_1105z.png",
                "https://upload.wikimedia.org/wikipedia/commons/d/dc/P5med.jpg"
        };
        return urls[(int) (Math.random() * urls.length)];
    }

    public static String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {
            //getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();

            //Retriving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);

            Log.e("Package Name=", context.getApplicationContext().getPackageName());

            for (android.content.pm.Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));

                // String key = new String(Base64.encodeBytes(md.digest()));
                Log.e("Key Hash=", key);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("Name not found", e1.toString());
        }
        catch (NoSuchAlgorithmException e) {
            Log.e("No such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }

        return key;
    }
}
