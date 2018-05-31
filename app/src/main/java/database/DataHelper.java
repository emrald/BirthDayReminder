package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import dataclass.DataList;

public class DataHelper {

    SQLiteDatabase sqlitedatabase;
    Context context;
    SQLiteHelper sqlitehelper;

    String databasename = "reminderDB";
    public String TableName = "reminder";
    int database_version = 2;
    ArrayList<String> arraylist;

    public ArrayList<DataList> arraylistdata = new ArrayList<>();

    public DataHelper(Context c) {
        context = c;
    }

    public DataHelper openToRead() throws SQLiteException {
        sqlitehelper = new SQLiteHelper(context, databasename, null, database_version);
        sqlitedatabase = sqlitehelper.getReadableDatabase();
        return this;

    }

    public DataHelper openToWrite() throws SQLiteException {
        sqlitehelper = new SQLiteHelper(context, databasename, null, database_version);
        sqlitedatabase = sqlitehelper.getWritableDatabase();
        return this;
    }

    public void close() {
        sqlitedatabase.close();
    }

    public long insert(String name, String image, String Bdate, String Btime,String year,String month,String day,String hour,String minute) {
        ContentValues contentvalues = new ContentValues();
        //	contentvalues.put("id", id);
        contentvalues.put("name", name);
        contentvalues.put("image", image);
        contentvalues.put("Bdate", Bdate);
        contentvalues.put("Btime", Btime);
        contentvalues.put("year", year);
        contentvalues.put("month", month);
        contentvalues.put("day", day);
        contentvalues.put("hour", hour);
        contentvalues.put("minute", minute);
        return sqlitedatabase.insert(TableName, null, contentvalues);
    }

    /*public long update(String name, String quantity) {
        ContentValues data = new ContentValues();
        data.put("quantity", quantity);
        return sqlitedatabase.update(TableName, data, "name = '" + name + "'", null);
    }*/
    public long updateData(String name, long Btime) {
        //  ContentValues contentvalues = new ContentValues();
        //	contentvalues.put("id", id);
        ContentValues data = new ContentValues();
        data.put("name", name);
        data.put("Btime", Btime);
        return sqlitedatabase.update(TableName, data, "name = '" + name + "'", null);
    }
    /*public String retriveQuantity(String name) {
        String quantity = null;
        Cursor cursor = sqlitedatabase.rawQuery("select quantity from cartview where name='" + name + "';", null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    quantity = cursor.getString(cursor.getColumnIndex("quantity"));
                }
                while (cursor.moveToNext());
            }
            cursor.close();

        } catch (Exception e) {

        } finally {
            cursor.close();
        }
        return quantity;

    }
    public String retriveTotalPrice(String name) {
        String strtotalprice = null;
        Cursor cursor = sqlitedatabase.rawQuery("select totalprice from cartview where name='" + name + "';", null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    strtotalprice = cursor.getString(cursor.getColumnIndex("totalprice"));
                }
                while (cursor.moveToNext());
            }
            cursor.close();

        } catch (Exception e) {

        } finally {
            cursor.close();
        }
        return strtotalprice;

    }
    public String retriveCatName(String name) {
        String strtotalprice = null;
        Cursor cursor = sqlitedatabase.rawQuery("select cat_name from cartview where name='" + name + "';", null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    strtotalprice = cursor.getString(cursor.getColumnIndex("cat_name"));
                }
                while (cursor.moveToNext());
            }
            cursor.close();

        } catch (Exception e) {

        } finally {
            cursor.close();
        }
        return strtotalprice;

    }
   public int retriveTotalQuantity(String name) {
       int strtotalprice=0;
       Cursor cursor = sqlitedatabase.rawQuery("select quantity from cartview where name='" + name + "';", null);
       try {
           if (cursor.moveToFirst()) {
               do {
                   strtotalprice = Integer.parseInt(cursor.getString(cursor.getColumnIndex("quantity")));
               }
               while (cursor.moveToNext());
           }
           cursor.close();

       } catch (Exception e) {

       } finally {
           cursor.close();
       }
       return strtotalprice;
   }
    public int retriveSubTotalPrice(String name) {
        int strtotalprice=0;
        Cursor cursor = sqlitedatabase.rawQuery("select totalprice from cartview where name='" + name + "';", null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    strtotalprice = Integer.parseInt(cursor.getString(cursor.getColumnIndex("totalprice")));
                }
                while (cursor.moveToNext());
            }
            cursor.close();

        } catch (Exception e) {

        } finally {
            cursor.close();
        }
        return strtotalprice;

    }*/
    public ArrayList<DataList> retrivedata() {

        arraylistdata.clear();
        Cursor cursor = sqlitedatabase.rawQuery("select * from reminder;", null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    DataList data = new DataList();
                    //	data.setId(cursor.getString(1));
                    data.setName(cursor.getString(1));
                    data.setImage(cursor.getString(2));
                    data.setBdate(cursor.getString(3));
                    data.setReminderTime(cursor.getString(4));
                    data.setYear(cursor.getString(5));
                    data.setMonth(cursor.getString(6));
                    data.setDay(cursor.getString(7));
                    data.setHour(cursor.getString(8));
                    data.setMinute(cursor.getString(9));
                    arraylistdata.add(data);
                }
                while (cursor.moveToNext());
            }
            cursor.close();
            return arraylistdata;
        } catch (Exception e) {

        } finally {
            cursor.close();
        }
        return arraylistdata;

    }
    public ArrayList<String> retrivedataName() {
String name;
        Cursor cursor = sqlitedatabase.rawQuery("select name from reminder;", null);
        try {
            if (cursor.moveToFirst()) {
                do {
                   name = cursor.getString(cursor.getColumnIndex("name"));
                    arraylist.add(name);
                }
                while (cursor.moveToNext());
            }
            cursor.close();
            return arraylist;
        } catch (Exception e) {

        } finally {
            cursor.close();
        }
        return arraylist;

    }
    public boolean removeFromList(String name){
        try {
            ContentValues value=new ContentValues();
            //	value.put("fav", 0);
            int i = sqlitedatabase.delete(TableName, "name = '" + name + "'", null);
            //int i=sqlitedatabase.update(TableName, value, "id = "+quote_id,null);
            if(i>0){
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public void removeAllData(){
        try {
            sqlitedatabase.delete(TableName,null,null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public class SQLiteHelper extends SQLiteOpenHelper {

        public SQLiteHelper(Context context, String name, CursorFactory factory,
                            int version) {
            super(context, name, factory, version);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // TODO Auto-generated method stub
            db.execSQL("create table " + TableName + "(id INTEGER PRIMARY KEY,name text,image text,Bdate text,Btime text,year text,month text,day text,hour text,minute text);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO Auto-generated method stub
            db.execSQL("DROP TABLE IF EXISTS " + TableName);
            onCreate(db);
        }
    }
}