package com.ewebapps.ethan.sqlio;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Ethan on 7/8/2018.
 */

public class SQLioSQLManager {

    //Currently Deprecated:
    public SQLiteDatabase SQLioDatabase(String path){
        //Can open in modes: OPEN_READWRITE or OPEN_READONLY
        SQLiteDatabase DBtoreturn;

        //Check file exists at path:

        DBtoreturn = SQLiteDatabase.openDatabase(path,null,SQLiteDatabase.OPEN_READWRITE);

        String TestingTableName = SQLioDBTableName(DBtoreturn);
        String TestingDBContentString = SQLioDBContentsToString(DBtoreturn);

        return DBtoreturn;
    }

    public String SQLioDBContentsToString(SQLiteDatabase myDatabase){
        int fixedstringlength = 14;

        String DBContents = "";

        Cursor resultSet = myDatabase.rawQuery("SELECT * FROM FDITtable",null);
        resultSet.moveToFirst();

        //Get column names:
        for (int myiterator = 0; myiterator < resultSet.getColumnCount(); myiterator++){
            DBContents += fixedLengthString(resultSet.getColumnName(myiterator),fixedstringlength);
        }

        DBContents += "\n";

        if (resultSet.getCount() != 0) { //If DB not empty
            resultSet.moveToFirst();
            do{
                String row_values = "  ";

                for(int i = 0; i < resultSet.getColumnCount(); i++){
                    //Format string
                    String formattingstring = fixedLengthString(resultSet.getString(i),fixedstringlength);

                    //Add to resulting print string:
                    row_values += formattingstring;
                }
                row_values += "\n"; //Newline after end of row.
                DBContents += row_values;
            }while(resultSet.moveToNext());

        }

        Log.d("Debug",DBContents);

        return DBContents;
    }

    public static String fixedLengthString(String string, int length) {
        return String.format("%1$-"+length+ "s", string);
    }

    public String[] getTableColumnNames(SQLiteDatabase myDatabase, String tablename){
        Cursor dbCursor = myDatabase.query(tablename, null, null, null, null, null, null);
        String[] columnNames = dbCursor.getColumnNames();
        return columnNames;
    }

    public String SQLioDBTableName(SQLiteDatabase myDatabase){
        String tablename;

        Cursor resultSet = myDatabase.rawQuery("SELECT name FROM sqlite_master WHERE type='table'",null);
        resultSet.moveToFirst();

        tablename = resultSet.getString(0);
        Log.d("Debug","Tablename = " + tablename);

        return  tablename;
    }



    public void printTableData(SQLiteDatabase myDatabase){
        SQLiteDatabase db = myDatabase;

        Cursor cur = db.rawQuery("SELECT * FROM " + "FDITtable", null);

        if(cur.getCount() != 0){
            cur.moveToFirst();

            do{
                String row_values = "";

                for(int i = 0 ; i < cur.getColumnCount(); i++){
                    row_values = row_values + " || " + cur.getString(i);
                }

                Log.d("LOG_TAG_HERE", row_values);

            }while (cur.moveToNext());
        }
    }



}
