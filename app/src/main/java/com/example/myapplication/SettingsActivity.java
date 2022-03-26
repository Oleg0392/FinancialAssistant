package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity{

    ListView set_list;
    ArrayAdapter<CharSequence> adapter;
    String[] menu_items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        set_list = (ListView) findViewById(R.id.set_list);
        adapter = ArrayAdapter.createFromResource(this,R.array.settings_strings,android.R.layout.simple_list_item_1);
        set_list.setAdapter(adapter);
        menu_items = getResources().getStringArray(R.array.settings_strings);

        set_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("MyLog",String.valueOf(i));
                Toast.makeText(SettingsActivity.this,"Выбран пункт: " + menu_items[i],Toast.LENGTH_SHORT).show();
            }
        });
    }
}