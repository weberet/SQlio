package com.ewebapps.ethan.sqlio;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;


public class PermissionCheck{

    public static boolean InternetPerm(Context context){
        //Check Permission Granted?
        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED){
            //No?: Request permission.
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.INTERNET}, 5);
            return false;
        }else{
            //Yes?:
            return true;
        }

    }

    public static boolean WriteExternalPerm(Context context) {
        //Check Permission Granted?
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //No?: Request permission.
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 5);
            return false;
        } else {
            //Yes?:
            return true;
        }
    }

    public static boolean ReadExternalPerm(Context context){
        //Check Permission Granted?
        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            //No?: Request permission.
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 5);
            return false;
        }else{
            //Yes?:
            return true;
        }

    }

    //Just like this you can implement rest of the permissions. 
}