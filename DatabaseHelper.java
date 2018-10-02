package com.ewebapps.ethan.sqlio;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Ethan on 7/9/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 3;


    DatabaseHelper(final Context context, String databaseName) {
        super(new DatabaseContext(context), databaseName, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database)
    {
        Log.d("CREATING TABLE :", "SUCCESS");
    }

    @Override
    public void onUpgrade(SQLiteDatabase database,int oldVersion,int newVersion)
    {

        Log.d("UPGRADING TABLE :", "SUCCESS");

        onCreate(database);
    }


}