package com.example.sakshi.todolist_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.R.attr.data;
import static android.R.attr.id;
import static android.R.attr.name;
import static android.R.id.list;

/**
 * Created by sakshi on 7/26/2017.
 */

public class DataHandler extends SQLiteOpenHelper {
    public static  final String DATABASE_NAME="tododata";        //create database name
    public static  final String TABLE_NAME="todolist";       //create table name
    public static  final String KEY_TITLE="Title";                  //col2
    public static final String KEY_DESCRIPTION="Description";
    public static final String KEY_DATE ="date" ;
    public static final String KEY_ID ="id" ;
    private Context context;
    public static  final int DATABASE_VERSON=1;             //optional
    public DataHandler(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSON);
        this.context = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+TABLE_NAME+"(Id INTEGER PRIMARY KEY AUTOINCREMENT, Title TEXT NOT NULL, Description TEXT ,date text NOT NULL, status INTEGER DEFAULT 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists "+TABLE_NAME);
    }

    public boolean insert_Data(String title,String description, String date)
    {
        SQLiteDatabase db=getWritableDatabase();            //to write data into the table
        ContentValues values=new ContentValues();
        Data data = new Data();
        values.put(KEY_TITLE,title);             //putting the values into ContentValue
        values.put(KEY_DESCRIPTION,description);
        values.put(KEY_DATE,date);
        values.put(KEY_ID,data.getId());
        long res=db.insert(TABLE_NAME,null,values);     //Create a temp variable
        if(res==-1)
        {
            return  false;      //if data is not inserted return false
        }
        else
        {
            return  true;           //else return true
        }
    }
    public ArrayList<Data> getAlldata(String sort){
        String sortorder;
        switch (sort){
            case "Alphabetically":
               sortorder = KEY_TITLE+" Asc";
                break;
            case "Old Tasks first":
                sortorder = KEY_DATE+" Desc";
                break;
            default:
                sortorder = KEY_DATE+ " asc";
                break;
        }
        ArrayList<Data> list = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "select * from "+TABLE_NAME+" where status = 0 order by "+sortorder;
        Cursor cursor = db.rawQuery(query,null);
        Data data = null;
        if(cursor!=null && cursor.getCount()>0){
            cursor.moveToFirst();
            do{
                data = new Data();
                data.setTitle(cursor.getString(1));
                data.setDescription(cursor.getString(2));
                data.setDate(cursor.getString(3));
                data.setId(cursor.getString(0));
                list.add(data);

            }while(cursor.moveToNext());
        }
        return list;
    }
    public ArrayList<Data> getcompletedtaskdata(){
        ArrayList<Data> list = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "select * from "+TABLE_NAME+" where status = 1 order by "+KEY_DATE+" ASC";
        Cursor cursor = db.rawQuery(query,null);
        Data data = null;
        if(cursor!=null && cursor.getCount()>0){
            cursor.moveToFirst();
            do{
                data = new Data();
                data.setTitle(cursor.getString(1));
                data.setDescription(cursor.getString(2));
                data.setDate(cursor.getString(3));
                data.setId(cursor.getString(0));
                list.add(data);

            }while(cursor.moveToNext());
        }
        return list;
    }
    public int completedrowcount(){
        SQLiteDatabase db = this.getReadableDatabase();
        int rowcount=0;
        Cursor cursor =  db.query(false,TABLE_NAME,new String[]{"status"},"status = 1",null,null,null,null,null);
        if(cursor!=null){
            cursor.moveToFirst();
            rowcount = cursor.getCount();
        }
        return rowcount;
    }
    public int rowcount(){
        SQLiteDatabase db = this.getReadableDatabase();
        int rowcount=0;
        Cursor cursor =  db.query(false,TABLE_NAME,new String[]{"status"},"status = 0",null,null,null,null,null);
        if(cursor!=null){
            cursor.moveToFirst();
            rowcount = cursor.getCount();
        }
        return rowcount;
    }
    public boolean deletetask(String id){

        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, KEY_ID+" = "+id,null) > 0;
    }
    public boolean changestatus(String id){

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select * from "+TABLE_NAME+" where "+KEY_ID+" is "+id;
        Cursor cursor = db.rawQuery(query,null);
        String status =  null;
        if(cursor!=null && cursor.getCount()>0 && cursor.moveToFirst()) {

            status = cursor.getString(cursor.getColumnIndex("status"));
        }

        if(status.equals("1")){
            ContentValues newValues = new ContentValues();
            newValues.put("status", "0");
            db.update(TABLE_NAME, newValues,"id = "+id, null);
            Toast.makeText(context, "Task Incomplete", Toast.LENGTH_SHORT).show();
            return true;
        }
        else if(status.equals("0")){
            ContentValues newValues = new ContentValues();
            newValues.put("status", "1");
            db.update(TABLE_NAME, newValues,"id = "+id, null);
            Toast.makeText(context, "Task completed", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    public boolean updatedata(String title, String desc,String date, String id){

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select * from "+TABLE_NAME+" where "+KEY_ID+" is "+id;
        Cursor cursor = db.rawQuery(query,null);
        String getid = null;
            ContentValues newValues = new ContentValues();
            newValues.put("title",title );
            newValues.put("description",desc);
            newValues.put("date",date);
            if(db.update(TABLE_NAME, newValues,"id ="+id, null)>0){
                Toast.makeText(context, "Task Updated", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
    }




}
