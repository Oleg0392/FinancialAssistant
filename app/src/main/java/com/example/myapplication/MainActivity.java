package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.icu.util.TimeUnit;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Timer;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button commit_btn, balance_ok_btn, hist_btn;
    ImageButton settings_btn;
    EditText etAmount, etBalance;
    TextView balance_tv, type_exp1, type_exp2, type_exp3, type_exp4, type_exp5, type_exp6, type_exp7,
            type_exp8, type_exp9, type_exp10, type_exp11, type_exp12, type_exp13, type_exp14, type_exp15;
    SharedPreferences sPrefs;
    final String BALANCE = "balance";
    final String TYPE_OF_COST = "type_of_cost";
    SwitchMaterial type_cost_switch;
    DBHelper dbHelper;
    SQLiteDatabase dbHist;
    Animation anim1, anim2, anim3, anim4, anim5, anim6, anim7, anim8, anim9, anim10, anim11, anim12, anim13, anim14, anim15;
    String SELECTED_EXPENSE = "";
    int SELECTED_TV_ID = 15;
    String[] expenses_types_str = {"Бензин","Вещи","Продукты","Сигареты","Алкоголь","Хоз.товары","Фастфуд","Услуги","Быт.Техника","Комуналка","Кредиты"};
    String[] income_types_str = {"Зар.плата","Аванс","Перевод","На кредитку"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        commit_btn = (Button) findViewById(R.id.commit_btn);
        etAmount = (EditText) findViewById(R.id.etAmount);
        balance_tv = (TextView) findViewById(R.id.balance_tv);
        balance_ok_btn = (Button) findViewById(R.id.balance_ok_btn);
        etBalance = (EditText) findViewById(R.id.etBalance);
        type_cost_switch = (SwitchMaterial) findViewById(R.id.type_cost_switch);
        hist_btn = (Button) findViewById(R.id.hist_btn);
        settings_btn = (ImageButton) findViewById(R.id.settings_btn);

        LoadExpensesTypes();
        LoadAnimations();
        SetStartOffsetAnimation();

        commit_btn.setOnClickListener(this);
        balance_tv.setOnClickListener(this);
        balance_ok_btn.setOnClickListener(this);
        etAmount.setOnClickListener(this);
        hist_btn.setOnClickListener(this);
        settings_btn.setOnClickListener(this);

        type_exp1.setOnClickListener(this); type_exp2.setOnClickListener(this); type_exp3.setOnClickListener(this);
        type_exp4.setOnClickListener(this); type_exp5.setOnClickListener(this); type_exp6.setOnClickListener(this);
        type_exp7.setOnClickListener(this); type_exp8.setOnClickListener(this); type_exp9.setOnClickListener(this);
        type_exp10.setOnClickListener(this); type_exp11.setOnClickListener(this); type_exp12.setOnClickListener(this);
        type_exp13.setOnClickListener(this); type_exp14.setOnClickListener(this); type_exp15.setOnClickListener(this);

        dbHelper = new DBHelper(this);
        dbHist = dbHelper.getWritableDatabase();

        CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                UpdateMode(b);
                LoadBalance();
                SetStartOffsetAnimation();
                ClearItems();
                StartAnimationForAll(b);
                SELECTED_EXPENSE = "";
                SELECTED_TV_ID = 15;
            }
        };

        type_cost_switch.setOnCheckedChangeListener(listener);

        sPrefs = getPreferences(MODE_PRIVATE);

        LoadBalance();
        StartAnimationForAll(type_cost_switch.isChecked());

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        SetStartOffsetAnimation();
        //ClearItems();
        StartAnimationForAll(type_cost_switch.isChecked());
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.commit_btn:
                if (etAmount.getText().toString().equalsIgnoreCase("")) {
                    break;
                }
                InsertRecordToHistory();
                UpdateBalance();
                etAmount.setText("");
                ExpensesAnimation(SELECTED_TV_ID,SELECTED_EXPENSE);
                break;
            case R.id.balance_tv:
                etBalance.setVisibility(View.VISIBLE);
                balance_ok_btn.setVisibility(View.VISIBLE);
                break;
            case R.id.balance_ok_btn:
                if (etBalance.getText().toString().equalsIgnoreCase("")) {
                    etBalance.setVisibility(View.INVISIBLE);
                    balance_ok_btn.setVisibility(View.INVISIBLE);
                    break;
                }
                UpdateBalance();
                etBalance.setVisibility(View.INVISIBLE);
                balance_ok_btn.setVisibility(View.INVISIBLE);
                break;
            case R.id.etAmount:
                //etAmount.setHint("");
                etBalance.setVisibility(View.INVISIBLE);
                balance_ok_btn.setVisibility(View.INVISIBLE);
                break;
            case R.id.hist_btn:
                Toast.makeText(MainActivity.this,"Загрузка истории...",Toast.LENGTH_LONG).show();
                intent = new Intent(this, HistoryActivity.class);
                startActivity(intent);
                break;
            case R.id.settings_btn:
                intent = new Intent(this,SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.type_exp1:
                if (type_exp1.getText().toString().equalsIgnoreCase("")) {
                    break;
                }
                ResetStartOffsetAnimation();
                ExpensesAnimation(SELECTED_TV_ID,SELECTED_EXPENSE);
                if (SELECTED_TV_ID != 0) {
                    type_exp1.setBackgroundColor(getColor(R.color.background13));
                    type_exp1.setTextColor(getColor(R.color.text13));
                }
                SELECTED_TV_ID = 0;
                SELECTED_EXPENSE = type_exp1.getText().toString();
                break;
            case R.id.type_exp2:
                if (type_exp2.getText().toString().equalsIgnoreCase("")) {
                    break;
                }
                ResetStartOffsetAnimation();
                ExpensesAnimation(SELECTED_TV_ID,SELECTED_EXPENSE);
                if (SELECTED_TV_ID != 1) {
                    type_exp2.setBackgroundColor(getColor(R.color.background13));
                    type_exp2.setTextColor(getColor(R.color.text13));
                }
                SELECTED_TV_ID = 1;
                SELECTED_EXPENSE = type_exp2.getText().toString();
                break;
            case R.id.type_exp3:
                if (type_exp3.getText().toString().equalsIgnoreCase("")) {
                    break;
                }
                ResetStartOffsetAnimation();
                ExpensesAnimation(SELECTED_TV_ID,SELECTED_EXPENSE);
                if (SELECTED_TV_ID != 2) {
                    type_exp3.setBackgroundColor(getColor(R.color.background13));
                    type_exp3.setTextColor(getColor(R.color.text13));
                }
                SELECTED_TV_ID = 2;
                SELECTED_EXPENSE = type_exp3.getText().toString();
                break;
            case R.id.type_exp4:
                if (type_exp4.getText().toString().equalsIgnoreCase("")) {
                    break;
                }
                ResetStartOffsetAnimation();
                ExpensesAnimation(SELECTED_TV_ID,SELECTED_EXPENSE);
                if (SELECTED_TV_ID != 3) {
                    type_exp4.setBackgroundColor(getColor(R.color.background13));
                    type_exp4.setTextColor(getColor(R.color.text13));
                }
                SELECTED_TV_ID = 3;
                SELECTED_EXPENSE = type_exp4.getText().toString();
                break;
            case R.id.type_exp5:
                if (type_exp5.getText().toString().equalsIgnoreCase("")) {
                    break;
                }
                ResetStartOffsetAnimation();
                ExpensesAnimation(SELECTED_TV_ID,SELECTED_EXPENSE);
                if (SELECTED_TV_ID != 4) {
                    type_exp5.setBackgroundColor(getColor(R.color.background13));
                    type_exp5.setTextColor(getColor(R.color.text13));
                }
                SELECTED_TV_ID = 4;
                SELECTED_EXPENSE = type_exp5.getText().toString();
                break;
            case R.id.type_exp6:
                if (type_exp6.getText().toString().equalsIgnoreCase("")) {
                    break;
                }
                ResetStartOffsetAnimation();
                ExpensesAnimation(SELECTED_TV_ID,SELECTED_EXPENSE);
                if (SELECTED_TV_ID != 5) {
                    type_exp6.setBackgroundColor(getColor(R.color.background13));
                    type_exp6.setTextColor(getColor(R.color.text13));
                }
                SELECTED_TV_ID = 5;
                SELECTED_EXPENSE = type_exp6.getText().toString();
                break;
            case R.id.type_exp7:
                if (type_exp7.getText().toString().equalsIgnoreCase("")) {
                    break;
                }
                ResetStartOffsetAnimation();
                ExpensesAnimation(SELECTED_TV_ID,SELECTED_EXPENSE);
                if (SELECTED_TV_ID != 6) {
                    type_exp7.setBackgroundColor(getColor(R.color.background13));
                    type_exp7.setTextColor(getColor(R.color.text13));
                }
                SELECTED_TV_ID = 6;
                SELECTED_EXPENSE = type_exp7.getText().toString();
                break;
            case R.id.type_exp8:
                if (type_exp8.getText().toString().equalsIgnoreCase("")) {
                    break;
                }
                ResetStartOffsetAnimation();
                ExpensesAnimation(SELECTED_TV_ID,SELECTED_EXPENSE);
                if (SELECTED_TV_ID != 7) {
                    type_exp8.setBackgroundColor(getColor(R.color.background13));
                    type_exp8.setTextColor(getColor(R.color.text13));
                }
                SELECTED_TV_ID = 7;
                SELECTED_EXPENSE = type_exp8.getText().toString();
                break;
            case R.id.type_exp9:
                if (type_exp9.getText().toString().equalsIgnoreCase("")) {
                    break;
                }
                ResetStartOffsetAnimation();
                ExpensesAnimation(SELECTED_TV_ID,SELECTED_EXPENSE);
                if (SELECTED_TV_ID != 8) {
                    type_exp9.setBackgroundColor(getColor(R.color.background13));
                    type_exp9.setTextColor(getColor(R.color.text13));
                }
                SELECTED_TV_ID = 8;
                SELECTED_EXPENSE = type_exp9.getText().toString();
                break;
            case R.id.type_exp10:
                if (type_exp10.getText().toString().equalsIgnoreCase("")) {
                    break;
                }
                ResetStartOffsetAnimation();
                ExpensesAnimation(SELECTED_TV_ID,SELECTED_EXPENSE);
                if (SELECTED_TV_ID != 9) {
                    type_exp10.setBackgroundColor(getColor(R.color.background13));
                    type_exp10.setTextColor(getColor(R.color.text13));
                }
                SELECTED_TV_ID = 9;
                SELECTED_EXPENSE = type_exp10.getText().toString();
                break;
            case R.id.type_exp11:
                if (type_exp11.getText().toString().equalsIgnoreCase("")) {
                    break;
                }
                ResetStartOffsetAnimation();
                ExpensesAnimation(SELECTED_TV_ID,SELECTED_EXPENSE);
                if (SELECTED_TV_ID != 10) {
                    type_exp11.setBackgroundColor(getColor(R.color.background13));
                    type_exp11.setTextColor(getColor(R.color.text13));
                }
                SELECTED_TV_ID = 10;
                SELECTED_EXPENSE = type_exp11.getText().toString();
                break;
            case R.id.type_exp12:
                if (type_exp12.getText().toString().equalsIgnoreCase("")) {
                    break;
                }
                ResetStartOffsetAnimation();
                ExpensesAnimation(SELECTED_TV_ID,SELECTED_EXPENSE);
                if (SELECTED_TV_ID != 11) {
                    type_exp12.setBackgroundColor(getColor(R.color.background13));
                    type_exp12.setTextColor(getColor(R.color.text13));
                }
                SELECTED_TV_ID = 11;
                SELECTED_EXPENSE = type_exp12.getText().toString();
                break;
            case R.id.type_exp13:
                if (type_exp13.getText().toString().equalsIgnoreCase("")) {
                    break;
                }
                ResetStartOffsetAnimation();
                ExpensesAnimation(SELECTED_TV_ID,SELECTED_EXPENSE);
                if (SELECTED_TV_ID != 12) {
                    type_exp13.setBackgroundColor(getColor(R.color.background13));
                    type_exp13.setTextColor(getColor(R.color.text13));
                }
                SELECTED_TV_ID = 12;
                SELECTED_EXPENSE = type_exp13.getText().toString();
                break;
            case R.id.type_exp14:
                if (type_exp14.getText().toString().equalsIgnoreCase("")) {
                    break;
                }
                ResetStartOffsetAnimation();
                ExpensesAnimation(SELECTED_TV_ID,SELECTED_EXPENSE);
                if (SELECTED_TV_ID != 13) {
                    type_exp14.setBackgroundColor(getColor(R.color.background13));
                    type_exp14.setTextColor(getColor(R.color.text13));
                }
                SELECTED_TV_ID = 13;
                SELECTED_EXPENSE = type_exp14.getText().toString();
                break;
            case R.id.type_exp15:
                if (type_exp15.getText().toString().equalsIgnoreCase("")) {
                    break;
                }
                ResetStartOffsetAnimation();
                ExpensesAnimation(SELECTED_TV_ID,SELECTED_EXPENSE);
                if (SELECTED_TV_ID != 14) {
                    type_exp15.setBackgroundColor(getColor(R.color.background13));
                    type_exp15.setTextColor(getColor(R.color.text13));
                }
                SELECTED_TV_ID = 14;
                SELECTED_EXPENSE = type_exp15.getText().toString();
                break;
            default:
                break;
        }
    }

    private void UpdateBalance() {
        float new_balance = 0.00f;
        float diff = 0.00f;
        float old_balance = sPrefs.getFloat(BALANCE,0.00f);

        if (!(etAmount.getText().toString().equalsIgnoreCase(""))) {
            diff = Float.valueOf(etAmount.getText().toString());
            if (type_cost_switch.isChecked()) {
                new_balance = old_balance + diff;
            } else {
                new_balance = old_balance - diff;
            }
        }

        if (!(etBalance.getText().toString().equalsIgnoreCase(""))) {
            new_balance = Float.valueOf(etBalance.getText().toString());
        }

        SharedPreferences.Editor editor;
        editor = sPrefs.edit();
        editor.putFloat(BALANCE,new_balance);
        editor.commit();

        balance_tv.setText(String.valueOf(new_balance));
    }

    private void UpdateMode(boolean b) {
        SharedPreferences.Editor editor;
        editor = sPrefs.edit();
        editor.putBoolean(TYPE_OF_COST,b);
        editor.commit();
    }

    private void LoadBalance() {
        float b;
        boolean t = false;
        b = sPrefs.getFloat(BALANCE, 0f);
        balance_tv.setText(String.valueOf(b));
        t = sPrefs.getBoolean(TYPE_OF_COST, false);

        if (t) {
            type_cost_switch.setChecked(true);
            type_cost_switch.setText(getResources().getText(R.string.income));
            type_cost_switch.setTextColor(getColor(R.color.for_inc));
            commit_btn.setText(getText(R.string.bnt_commit_i));
        } else {
            type_cost_switch.setChecked(false);
            type_cost_switch.setText(getResources().getText(R.string.expenses));
            type_cost_switch.setTextColor(getColor(R.color.for_exp));
            commit_btn.setText(getText(R.string.bnt_commit_e));
        }

    }

    private void InsertRecordToHistory() {
        String balance, delta, type, desc, dt_str;
        ContentValues dbValues = null;
        type = type_cost_switch.getText().toString();
        balance = balance_tv.toString();
        delta = etAmount.getText().toString();
        desc = SELECTED_EXPENSE;
        SimpleDateFormat sFormat = new SimpleDateFormat("dd.MM.yyyy");
        Date dt = new Date();
        dt_str = sFormat.format(dt);
        dbValues = new ContentValues();
        dbValues.put(dbHelper.HIST_DT,dt_str);
        dbValues.put(dbHelper.HIST_TYPE,type);
        dbValues.put(dbHelper.HIST_AMOUNT,delta);
        dbValues.put(dbHelper.HIST_BAL,balance);
        dbValues.put(dbHelper.HIST_DESC,desc);
        dbHist.insert(dbHelper.TABLE_HIST, null, dbValues);
        //dbHist.close();
    }

    private void LoadExpensesTypes() {
        type_exp1 = (TextView) findViewById(R.id.type_exp1);  type_exp2 = (TextView) findViewById(R.id.type_exp2);
        type_exp3 = (TextView) findViewById(R.id.type_exp3);  type_exp4 = (TextView) findViewById(R.id.type_exp4);
        type_exp5 = (TextView) findViewById(R.id.type_exp5);  type_exp6 = (TextView) findViewById(R.id.type_exp6);
        type_exp7 = (TextView) findViewById(R.id.type_exp7);  type_exp8 = (TextView) findViewById(R.id.type_exp8);
        type_exp9 = (TextView) findViewById(R.id.type_exp9);  type_exp10 = (TextView) findViewById(R.id.type_exp10);
        type_exp11 = (TextView) findViewById(R.id.type_exp11);  type_exp12 = (TextView) findViewById(R.id.type_exp12);
        type_exp13 = (TextView) findViewById(R.id.type_exp13);  type_exp14 = (TextView) findViewById(R.id.type_exp14);
        type_exp15 = (TextView) findViewById(R.id.type_exp15);
    }

    private void LoadAnimations() {
        anim1 = AnimationUtils.loadAnimation(this,R.anim.expenses_type_scale);
        anim2 = AnimationUtils.loadAnimation(this,R.anim.expenses_type_scale);
        anim3 = AnimationUtils.loadAnimation(this,R.anim.expenses_type_scale);
        anim4 = AnimationUtils.loadAnimation(this,R.anim.expenses_type_scale);
        anim5 = AnimationUtils.loadAnimation(this,R.anim.expenses_type_scale);
        anim6 = AnimationUtils.loadAnimation(this,R.anim.expenses_type_scale);
        anim7 = AnimationUtils.loadAnimation(this,R.anim.expenses_type_scale);
        anim8 = AnimationUtils.loadAnimation(this,R.anim.expenses_type_scale);
        anim9 = AnimationUtils.loadAnimation(this,R.anim.expenses_type_scale);
        anim10 = AnimationUtils.loadAnimation(this,R.anim.expenses_type_scale);
        anim11 = AnimationUtils.loadAnimation(this,R.anim.expenses_type_scale);
        anim12 = AnimationUtils.loadAnimation(this,R.anim.expenses_type_scale);
        anim13 = AnimationUtils.loadAnimation(this,R.anim.expenses_type_scale);
        anim14 = AnimationUtils.loadAnimation(this,R.anim.expenses_type_scale);
        anim15 = AnimationUtils.loadAnimation(this,R.anim.expenses_type_scale);

        anim1.setDuration(300);  anim2.setDuration(300);  anim3.setDuration(300);
        anim4.setDuration(300);  anim5.setDuration(300);  anim6.setDuration(300);
        anim7.setDuration(300);  anim8.setDuration(300);  anim9.setDuration(300);
        anim10.setDuration(300);  anim11.setDuration(300);  anim12.setDuration(300);
        anim13.setDuration(300);  anim14.setDuration(300);  anim15.setDuration(300);
    }

    private void ExpensesAnimation(int i, String tv_title) {
        TextView textView;
        String str_id = "";
        int res_id = 0;
        if (i < 15) {
            str_id = String.valueOf(i + 1);
            str_id = "type_exp" + str_id;
            res_id = getResources().getIdentifier(str_id, "id", "com.example.myapplication");
            textView = (TextView) findViewById(res_id);
            textView.setText(tv_title);
        }else return;

        switch (i) {
            case 0:
                textView.setBackgroundColor(getColor(R.color.text1));
                textView.setTextColor(getColor(R.color.background1));
                textView.startAnimation(anim1);
                break;
            case 1:
                textView.setBackgroundColor(getColor(R.color.text2));
                textView.setTextColor(getColor(R.color.background2));
                textView.startAnimation(anim2);
                break;
            case 2:
                textView.setBackgroundColor(getColor(R.color.text3));
                textView.setTextColor(getColor(R.color.background3));
                textView.startAnimation(anim3);
                break;
            case 3:
                textView.setBackgroundColor(getColor(R.color.text4));
                textView.setTextColor(getColor(R.color.background4));
                textView.startAnimation(anim4);
                break;
            case 4:
                textView.setBackgroundColor(getColor(R.color.text5));
                textView.setTextColor(getColor(R.color.background5));
                textView.startAnimation(anim5);
                break;
            case 5:
                textView.setBackgroundColor(getColor(R.color.text6));
                textView.setTextColor(getColor(R.color.background6));
                textView.startAnimation(anim6);
                break;
            case 6:
                textView.setBackgroundColor(getColor(R.color.text7));
                textView.setTextColor(getColor(R.color.background7));
                textView.startAnimation(anim7);
                break;
            case 7:
                textView.setBackgroundColor(getColor(R.color.text8));
                textView.setTextColor(getColor(R.color.background8));
                textView.startAnimation(anim8);
                break;
            case 8:
                textView.setBackgroundColor(getColor(R.color.text9));
                textView.setTextColor(getColor(R.color.background9));
                textView.startAnimation(anim9);
                break;
            case 9:
                textView.setBackgroundColor(getColor(R.color.text10));
                textView.setTextColor(getColor(R.color.background10));
                textView.startAnimation(anim10);
                break;
            case 10:
                textView.setBackgroundColor(getColor(R.color.text11));
                textView.setTextColor(getColor(R.color.background11));
                textView.startAnimation(anim11);
                break;
            case 11:
                textView.setBackgroundColor(getColor(R.color.text12));
                textView.setTextColor(getColor(R.color.background12));
                textView.startAnimation(anim12);
                break;
            case 12:
                textView.setBackgroundColor(getColor(R.color.text13));
                textView.setTextColor(getColor(R.color.background13));
                textView.startAnimation(anim13);
                break;
            case 13:
                textView.setBackgroundColor(getColor(R.color.text14));
                textView.setTextColor(getColor(R.color.background14));
                textView.startAnimation(anim14);
                break;
            case 14:
                textView.setBackgroundColor(getColor(R.color.text15));
                textView.setTextColor(getColor(R.color.background15));
                textView.startAnimation(anim15);
                break;
            default:
                break;
        }
    }

    private void ResetStartOffsetAnimation() {
        anim2.setStartOffset(0); anim3.setStartOffset(0);
        anim4.setStartOffset(0); anim5.setStartOffset(0); anim6.setStartOffset(0);
        anim7.setStartOffset(0); anim8.setStartOffset(0); anim9.setStartOffset(0);
        anim10.setStartOffset(0); anim11.setStartOffset(0); anim12.setStartOffset(0);
        anim13.setStartOffset(0); anim14.setStartOffset(0); anim15.setStartOffset(0);
    }

    private void SetStartOffsetAnimation() {
        anim2.setStartOffset(150); anim3.setStartOffset(300);
        anim4.setStartOffset(450); anim5.setStartOffset(600); anim6.setStartOffset(750);
        anim7.setStartOffset(900); anim8.setStartOffset(1050); anim9.setStartOffset(1200);
        anim10.setStartOffset(1350); anim11.setStartOffset(1500); anim12.setStartOffset(1650);
        anim13.setStartOffset(1800); anim14.setStartOffset(1950); anim15.setStartOffset(2100);
    }

    private void StartAnimationForAll(boolean income_or_expenses) {
        if (income_or_expenses) {
            for (int i = 0; i < income_types_str.length - 1; i++) {
                ExpensesAnimation(i,income_types_str[i]);
                Log.d("mLog", "dynamic textView created. step:" + i);  //non-actual
            }
        } else {
            for (int i = 0; i < expenses_types_str.length - 1; i++) {
                ExpensesAnimation(i,expenses_types_str[i]);
                Log.d("mLog", "dynamic textView created. step:" + i);  //non-actual
            }
        }
    }

    private void ClearItems() {
        TextView textView;
        String str_id = "";
        for (int i = 0; i < 15; i++ ) {
            int res_id = 0;
            str_id = String.valueOf(i + 1);
            str_id = "type_exp" + str_id;
            res_id = getResources().getIdentifier(str_id, "id", "com.example.myapplication");
            textView = (TextView) findViewById(res_id);
            textView.setText("");
            textView.setBackgroundColor(getColor(R.color.deleted_item));
        }
    }
}
