package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

public class HistoryActivity extends AppCompatActivity implements View.OnClickListener {

    DBHelper dbHelper;
    SQLiteDatabase dbHist;
    Cursor cursor;
    LayoutInflater inflater;
    Button btn_filter_all, btn_filter_exp, btn_filter_inc, btn_filter_clear;
    SQLiteStatement sqlSt, datasetCount;
    String[] select_columns;
    SimpleCursorAdapter scAdapter;
    CursorAdapter cAdapter;
    int[] tvIds;
    int adapterFlags;
    ViewBinder binder;
    LinearLayout HR_Layout;
    View item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        btn_filter_all = (Button) findViewById(R.id.btn_filter_all);
        btn_filter_exp = (Button) findViewById(R.id.btn_filter_exp);
        btn_filter_inc = (Button) findViewById(R.id.btn_filter_inc);
        btn_filter_clear = (Button) findViewById(R.id.btn_clear_hist);
        btn_filter_all.setOnClickListener(this);
        btn_filter_exp.setOnClickListener(this);
        btn_filter_inc.setOnClickListener(this);
        btn_filter_clear.setOnClickListener(this);


        inflater = getLayoutInflater();
        HR_Layout = (LinearLayout) findViewById(R.id.HR_Layout);

        dbHelper = new DBHelper(this);
        dbHist = dbHelper.getReadableDatabase();
        adapterFlags = 0;

        cursor = null;


        select_columns = new String[] {dbHelper.HIST_ID,dbHelper.HIST_TYPE,dbHelper.HIST_DESC,dbHelper.HIST_AMOUNT,dbHelper.HIST_DT};
        int id_tv_Num = R.id.tv_Num;
        int id_tv_rType = R.id.tv_rType;
        int id_tv_rName = R.id.tv_rName;
        int id_tv_Amount = R.id.tv_Amount;
        int id_tv_Dt = R.id.tv_Dt;
        tvIds = new int[] {id_tv_Num,id_tv_rType,id_tv_rName,id_tv_Amount,id_tv_Dt};

        LoadHistory(null, null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        cursor.close();
        dbHist.close();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_filter_all:
                HR_Layout.removeAllViews();
                LoadHistory(null, null);
                break;
            case R.id.btn_filter_exp:
                HR_Layout.removeAllViews();
                LoadHistory(dbHelper.HIST_TYPE + " = ?","Расход");
                break;
            case R.id.btn_filter_inc:
                HR_Layout.removeAllViews();
                LoadHistory(dbHelper.HIST_TYPE + " = ?","Приход");
                break;
            case R.id.btn_clear_hist:
                HR_Layout.removeAllViews();
                break;
        }
    }

    private void LoadHistory(String WhereColumn, String WhereArgs) {
        int[] ItemColors = new int[2];
        ItemColors[0] = Color.parseColor("#5DB6AE");
        ItemColors[1] = Color.parseColor("#B0EDE8");
        String[] where_args = null;
        if (WhereArgs != null) {
            where_args = new String[1];
            where_args[0] = WhereArgs;
        }

        cursor = dbHist.query(dbHelper.TABLE_HIST,select_columns,WhereColumn,where_args,null,null,null);

        if (cursor != null) {
            Log.d("MyLOG","in if cursor != null");
            if (cursor.moveToFirst()) {
                Log.d("MyLOG","in if cursor.moveToFirst");
                for (int j = 0; j < cursor.getCount(); j++) {

                     int rType = cursor.getColumnIndex(select_columns[1]);
                    int rName = cursor.getColumnIndex(select_columns[2]);
                    int Amt = cursor.getColumnIndex(select_columns[3]);
                    int Dt = cursor.getColumnIndex(select_columns[4]);

                    String str_id = String.valueOf(j+1);
                    if (j < 9) str_id = "0" + str_id;
                    item = inflater.inflate(R.layout.item,HR_Layout,false);

                    TextView tv_Num = (TextView) item.findViewById(R.id.tv_Num);
                    TextView tv_rType = (TextView) item.findViewById(R.id.tv_rType);
                    TextView tv_rName = (TextView) item.findViewById(R.id.tv_rName);
                    TextView tv_Amount = (TextView) item.findViewById(R.id.tv_Amount);
                    TextView tv_Dt = (TextView) item.findViewById(R.id.tv_Dt);


                        tv_Num.setText(str_id);
                        tv_rType.setText(cursor.getString(rType));
                        tv_rName.setText(cursor.getString(rName));
                        tv_Amount.setText(cursor.getString(Amt));
                        tv_Dt.setText(cursor.getString(Dt));
                        item.setBackgroundColor(ItemColors[j % 2]);
                        tv_Num.setTextColor(getColor(R.color.black));
                        tv_rType.setTextColor(getColor(R.color.black));
                        tv_rName.setTextColor(getColor(R.color.black));
                        tv_Amount.setTextColor(getColor(R.color.black));
                        tv_Dt.setTextColor(getColor(R.color.black));

                    item.getLayoutParams().width = ConstraintLayout.LayoutParams.MATCH_PARENT;
                    HR_Layout.addView(item);
                    cursor.moveToNext();
                }
            }

        } else Log.d("MyLOG","cursor is null");

    }
}