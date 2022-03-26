package com.example.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "financial_db";
    public static final int DB_VERSION = 1;
    public static final String TABLE_HIST = "history";
    public static final String HIST_ID = "_id";
    public static final String HIST_DT = "rec_dt";
    public static final String HIST_TYPE = "rec_type";
    public static final String HIST_AMOUNT = "amount";
    public static final String HIST_BAL = "balance";
    public static final String HIST_DESC = "rec_desc";
    public static final String TABLE_EXPENSES = "EXPENSES";
    public static final String EXP_ID = "exp_id";
    public static final String EXP_NAME = "exp_name";
    public static final String EXP_LIMIT = "exp_limit";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDb) {
        sqLiteDb.execSQL("CREATE TABLE " + TABLE_HIST + " (" + HIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
        + HIST_DT + " TEXT, " + HIST_TYPE + " TEXT, " + HIST_AMOUNT + " TEXT, " + HIST_BAL + " TEXT, " + HIST_DESC + " TEXT);");

        sqLiteDb.execSQL("CREATE TABLE " + TABLE_EXPENSES + " (" + EXP_ID + " INTEGER PRIMARY KEY, " + EXP_NAME + " TEXT, " + EXP_LIMIT + " REAL );");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_HIST);
        onCreate(sqLiteDatabase);
    }
}
