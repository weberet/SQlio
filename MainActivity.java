package com.ewebapps.ethan.sqlio;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ewebapps.ethan.sqlio.databinding.ActivityMainBinding;
import com.ewebapps.ethan.sqlio.databinding.DatabaseviewLayoutBinding;
import com.ewebapps.ethan.sqlio.databinding.SettingsLayoutBinding;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.channels.FileChannel;
import java.util.zip.Inflater;

import static com.ewebapps.ethan.sqlio.DownloadTools.SQLioDBName;
import static com.ewebapps.ethan.sqlio.DownloadTools.downloadtolocation;

public class MainActivity extends AppCompatActivity {

    public static final String MYPREFERENCES = "MyPrefs";
    public static String PACKAGE_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PACKAGE_NAME = getApplicationContext().getPackageName(); //Used in DownloadTools

        //Open initial layout with databinding.
        CheckPermissions(); //Check necessary permissions for download
        MainLayout();

        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

    }

    public void DoDownload(String downloadlocation, String downloadfile){
        Log.d("Debug","Attempting Download");
        //Create an instance of our DownloadTools class:
        DownloadTools myDownloadTools = new DownloadTools(this);
        myDownloadTools.DownloadSomething(downloadlocation+downloadfile); //Changed this 7/15 check if it worked
    }

    public Boolean CheckPermissions(){
        //Checks necessary permissions:
        if (PermissionCheck.InternetPerm(this) && PermissionCheck.WriteExternalPerm(this) && PermissionCheck.ReadExternalPerm(this)) {
            return true;
        }
        else{
            return false;
        }
    }

    public void MainLayout(){ //Navigate to main layout and do databinding.
        ActivityMainBinding mainbinding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        mainbinding.setMyHandler(new MyHandler());
    }

    public void SettingsLayout(){ //Navigate to settings layout and do databinding.
        SettingsLayoutBinding settingsbinding = DataBindingUtil.setContentView(this,R.layout.settings_layout);
        settingsbinding.setMyHandler(new MyHandler());
        settingsbinding.setSettingsViewBinding(settingsbinding);

        settingsbinding.txtServerLocation.setText(GetPreferenceString("downloadlocation"));
        settingsbinding.txtDBFileName.setText(GetPreferenceString("downloadfile"));
        settingsbinding.txtLocalStore.setText(GetPreferenceString("localstore"));
    }

    public void dbViewLayout(){
        DatabaseviewLayoutBinding dbviewbinding = DataBindingUtil.setContentView(this,R.layout.databaseview_layout);
        dbviewbinding.setMyHandler(new MyHandler());

        //Database init stuff.
        DatabaseHelper myDBHelper = new DatabaseHelper(this,GetPreferenceString("downloadfile"));
        SQLiteDatabase myDB = myDBHelper.getReadableDatabase();
        SQLioSQLManager mySQLManager = new SQLioSQLManager();
        //End database init stuff

        dbviewbinding.txtviewDbviewReadout.setText(mySQLManager.SQLioDBContentsToString(myDB));

    }

    public void initDB(){ //A guide on how to init and use Databases

        //DatabaseContext myDBContext = new DatabaseContext(this);

        DatabaseHelper myDBHelper = new DatabaseHelper(this,GetPreferenceString("downloadfile"));

        Log.d("Debug","DB name = " + myDBHelper.getDatabaseName());
        SQLiteDatabase myDB = myDBHelper.getReadableDatabase();

        SQLioSQLManager mySQLManager = new SQLioSQLManager();
        //Log.d("Debug",mySQLManager.SQLioDBContentsToString(myDB));
        //mySQLManager.printTableData(myDB);

    }

    public String GetPreferenceString(String preference){
        //Variables
        //Our Shared Preference file (save file)
        SharedPreferences sharedprefs = this.getSharedPreferences(MYPREFERENCES,Context.MODE_PRIVATE);
        //Location to retrieve file
        String downloadlocation = sharedprefs.getString("prefsdownloadlocation","http://192.168.0.158/");
        //Name of file to download (Needs extension)
        String downloadfile = sharedprefs.getString("prefsdownloadfile","FDIT.db");
        //Path to download to
        //Variable is currently not needed, hiding ui elements and changing default value.
        String localstore = sharedprefs.getString("localstore","");

        switch(preference){ //Identify which value to return based on input string.
            case "downloadlocation": Log.d("Debug","downloadlocation retreived, = " + downloadlocation); return downloadlocation;
            case "downloadfile": Log.d("Debug","downloadfile retreived, = " + downloadfile); return downloadfile;
            case "localstore": Log.d("Debug","localstore retreived, = " + localstore); return localstore;
            default: Log.d("Debug","Invalid variable, unable to retrieve");
            return "Error, invalid preference";
        }
    }

    public String DatabaseDownloadString(){
        String myDatabaseDownloadString = GetPreferenceString("downloadlocation") + GetPreferenceString("downloadfile");

        return myDatabaseDownloadString;
    }

    public void UpdatePreferencesString(String key, String value){

        SharedPreferences sharedprefs = this.getSharedPreferences(MYPREFERENCES,Context.MODE_PRIVATE);
        SharedPreferences.Editor prefseditor = sharedprefs.edit();

        prefseditor.putString(key,value); //Update preferences
        prefseditor.apply();
    }

    public void SQLioToast(String message){ //Creates toast message of message
        Context context = getApplicationContext();
        CharSequence text = message;
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public Boolean CheckValidURL(String checkurl){
        if(Patterns.WEB_URL.matcher(checkurl).matches()){
            Log.d("Debug",checkurl + " is valid URL.");
            return true;
        }
        else{
            Log.d("Debug",checkurl + " is not valid URL.");
            return false;
        }
    }

    public void FilePickFileIntent(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent,7);
    }

    public static void copyFile(File src, File dst) throws IOException
    {
        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dst).getChannel();
        try
        {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        }
        finally
        {
            if (inChannel != null)
                inChannel.close();
            if (outChannel != null)
                outChannel.close();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch(requestCode){
            case 7:
                if (resultCode == RESULT_OK){
                    String PathHolder = data.getData().getPath();
                    Toast.makeText(MainActivity.this, PathHolder, Toast.LENGTH_LONG).show();
                    UpdatePreferencesString("localstore", PathHolder);
                    SettingsLayout();
                }
                break;
        }
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Broadcast receiver stuff, move file from external to internal storage after download.
    BroadcastReceiver onComplete = new BroadcastReceiver() {

        public void onReceive(Context ctxt, Intent intent) {
            Toast.makeText(getApplicationContext(), "Download finished",Toast.LENGTH_SHORT).show();
        }
    };
    //End broadcast receiver stuff
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //Event Handlers for data-binding
    public class MyHandler{
        public void SettingsBackBtnClicked(){
            MainLayout();
        }

        public void ToSettingsMenuBtnClicked(){
            SettingsLayout();
        }

        public void SettingsApplyBtnClicked(SettingsLayoutBinding localsettingsbinding){
            UpdatePreferencesString("prefsdownloadlocation",localsettingsbinding.txtServerLocation.getText().toString());
            UpdatePreferencesString("prefsdownloadfile",localsettingsbinding.txtDBFileName.getText().toString());
            UpdatePreferencesString("localstore",localsettingsbinding.txtLocalStore.getText().toString());

            SQLioToast("Preferences updated:\n" +
                    "downloadlocation: " + localsettingsbinding.txtServerLocation.getText().toString() + "\n" +
                    "downloadfile: " + localsettingsbinding.txtDBFileName.getText().toString() + "\n" +
                    "localstore: " + localsettingsbinding.txtLocalStore.getText().toString()
            );
        }

        public void SyncDatabaseBtnClicked(){
            if (CheckValidURL(DatabaseDownloadString())){
                DoDownload(GetPreferenceString("downloadlocation"),GetPreferenceString("downloadfile"));
                SQLioToast("Syncing Database File");
            }
            else{SQLioToast("Unable to Sync Database File");}
        }

        public void dbViewBackBtnClicked(){
            MainLayout();
        }

        public void openDBbtnClicked(){
            dbViewLayout();
        }

        public void txtLocalStoreClicked(){
            FilePickFileIntent();
        }


    }


}
