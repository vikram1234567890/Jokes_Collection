package oj.com.ownjokes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBtemp extends SQLiteOpenHelper {



        public static final String DATABASE_NAME = "dbtemp.db";
        public static final String email_TABLE_NAME = "e";
    public static final String email_COLUMN_NAME = "email";
        public DBtemp(Context context) {
            super(context, DATABASE_NAME, null, 1);
        }



    @Override
        public void onCreate(SQLiteDatabase db) {
            // TODO Auto-generated method stub
            db.execSQL(
                    "create table "+email_TABLE_NAME+" ("+email_COLUMN_NAME+")"

            );

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO Auto-generated method stub
            db.execSQL("DROP TABLE IF EXISTS "+email_TABLE_NAME);
            onCreate(db);

        }

        public boolean insertEmail(String e) {
            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            deleteAllEmail();
            contentValues.put(email_COLUMN_NAME, e);
      
            db.insert(email_TABLE_NAME, null, contentValues);

            return true;
        }


        public boolean deleteAllEmail() {

            SQLiteDatabase db = this.getWritableDatabase();
           db.execSQL("delete from "+ email_TABLE_NAME);

            return true;
        }

        public String  getEmail() {
           String  s="";

            //hp = new HashMap();
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.rawQuery("select "+email_COLUMN_NAME+" from "+email_TABLE_NAME, null);
            res.moveToFirst();

            while (res.isAfterLast() == false) {
                s=res.getString(res.getColumnIndex(email_COLUMN_NAME));
                res.moveToNext();
            }

            return s;
        }


}

