package com.ewebapps.ethan.sqlio;

import android.app.DownloadManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import static com.ewebapps.ethan.sqlio.MainActivity.PACKAGE_NAME;

/**
 * Created by Ethan on 6/18/2018.
 */

public class DownloadTools {

    //Needs context for "getSystemService" method in method DownloadSomething();
    Context mContext;

    public static String SQLioDBName = "FDIT.db";
    public ArrayList<Long> refidlist = new ArrayList<>();
    public static String downloadtolocation = File.separator+ "databases" + File.separator;


    public DownloadTools(Context mContext){
        this.mContext = mContext;
    }
    //End context

    /**
     * @param context used to check the device version and DownloadManager information
     * @return true if the download manager is available
     */
    public static boolean isDownloadManagerAvailable(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return true;
        }
        return false;
    }

    public void DownloadSomething(String myurl){

        //Put file in sdcard/databases/ directory. This is where sql files are later read.

        //Clear previous database file:
        File internalstorage = Environment.getExternalStorageDirectory();
        File deletefile = new File(internalstorage.getAbsolutePath() + File.separator + downloadtolocation + File.separator + SQLioDBName);
        Log.d("Debug","Delete file path: " + deletefile.getAbsolutePath().toString());
        deletefile.delete();


        String url = myurl;
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription("Downloading SQLioDB Database");
        request.setTitle("SQLioDB");
        // in order for this if to run, you must use the android 3.2 to compile app
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }

        /* NEEDS TO BE DOWNLOADED TO INTERNAL 'databases' DIRECTORY*/
        request.setDestinationInExternalPublicDir(downloadtolocation, SQLioDBName);

        // get download service and enqueue file
        DownloadManager manager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);

        //manager.enqueue(request); //Previous, adding new stuff to track refid
        Long refid = manager.enqueue(request);
        refidlist.add(refid);
    }



}
