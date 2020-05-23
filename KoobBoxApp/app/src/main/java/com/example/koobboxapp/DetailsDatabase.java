package com.example.koobboxapp;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DetailsDatabase extends SQLiteOpenHelper {
    public static final String database="details.db";
    public static final String table="details";
    public static final String col1="title";
    public static final String col2="subtitle";
    public static final String col3="date";
    public static final String col4="priority";
    public static final String col5="selection";
    public static final String col6="type";

    public DetailsDatabase(Context context) {
        super(context,database, null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table details(title TEXT,subtitle TEXT,date TEXT,priority TEXT,selection TEXT,type TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS details");
        onCreate(db);
    }
    public boolean insertData(String title,String subtitle,String date,String priority,String selection,String type){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(col1,title);
        cv.put(col2,subtitle);
        cv.put(col3,date);
        cv.put(col4,priority);
        cv.put(col5,selection);
        cv.put(col6,type);
        long result=db.insert(table,null,cv);
        if(result==-1)
            return false;
        else
            return true;
    }
    public Cursor allData(){
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor res=db.rawQuery("select * from details",null);
        return res;
    }
    public Integer deleteData(String title,String subtitle,String date,String priority,String selection,String type){
        SQLiteDatabase db=this.getWritableDatabase();
        String[] args={title,subtitle,date,priority,selection,type};
        return db.delete(table,"title=? and subtitle=? and date=? and priority=? and selection=? and type=?",args);
    }
    public void cleanData()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(table, null, null);
    }
}
