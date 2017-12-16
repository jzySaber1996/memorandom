package com.example.jzy_1996.memorandum;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jzy_1996 on 2017/12/10.
 */

public class RemindActivity extends AppCompatActivity {

    private SQLiteDatabase db;
    private ListView listView;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private String tempRemind="";
    private MyAdapter adapter;

    private ArrayList<HashMap<String, String>> getData() {
        ArrayList<HashMap<String, String>> data = new ArrayList<>();
        HashMap<String, String> map;
        Cursor cursor = db.query("memo_remind", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(0);
                String time_remind = cursor.getString(3);
                String content = cursor.getString(2);
                map = new HashMap<>();
                map.put("ItemTitle", content);
                if (time_remind==null) {
                    map.put("ItemText", "未设置");
                }
                else {
                    map.put("ItemText",time_remind);
                }
                cursor.moveToNext();
                data.add(map);
            }
        }
        //cursor.close();
        return data;
    }

    private void createListView() {
        listView = (ListView) findViewById(R.id.list_item);
        adapter = new MyAdapter(RemindActivity.this, getData());
        listView.setAdapter(adapter);
        //ListView item的点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
        adapter.setOnItemAlarmClickListener(new MyAdapter.OnItemAlarmClickListener() {
            @Override
            public void onAlarmClick(int i) {
                datePickerDialog=new DatePickerDialog(RemindActivity.this, new MyDateSetListener(i+1), 2000, 1, 1);
                datePickerDialog.show();
            }
        });
    }

    public class MyTimeSetListener implements TimePickerDialog.OnTimeSetListener {
        int order;

        public MyTimeSetListener(int order){
            this.order=order;
        }
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // TODO Auto-generated method stub
            ContentValues cValue = new ContentValues();
            cValue.put("remindHour", hourOfDay);
            tempRemind+=String.valueOf(hourOfDay);
            tempRemind+="时";
            cValue.put("remindMinute", minute);
            tempRemind+=String.valueOf(minute);
            tempRemind+="分提醒";
            cValue.put("remindTime",tempRemind);
            cValue.put("remind",1);
            //cValue.put("remindDay",dayOfMonth);
            db.update("memo_remind",cValue,"_id=?",new String[]{String.valueOf(order)});
            adapter.setmList(getData());
            listView.setAdapter(adapter);
            //Toast.makeText(Activity01.this, "hourOfDay:"+hourOfDay+" minute"+minute,Toast.LENGTH_SHORT).show();
        }
    }

    public class MyDateSetListener implements DatePickerDialog.OnDateSetListener {
        int order;

        public MyDateSetListener(int order){
            this.order=order;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            tempRemind="";
            ContentValues cValue = new ContentValues();
            cValue.put("remindYear", year);
            tempRemind+=String.valueOf(year);
            tempRemind+="年";
            cValue.put("remindMonth", monthOfYear+1);
            tempRemind+=String.valueOf(monthOfYear+1);
            tempRemind+="月";
            cValue.put("remindDay",dayOfMonth);
            tempRemind+=String.valueOf(dayOfMonth);
            tempRemind+="日 ";
            db.update("memo_remind",cValue,"_id=?",new String[]{String.valueOf(order)});
            timePickerDialog=new TimePickerDialog(RemindActivity.this,new MyTimeSetListener(order),0,0,true);
            timePickerDialog.show();
            //Toast.makeText(Activity01.this, "year:"+year+" monthOfYear"+(monthOfYear+1)+" dayOfMonth"+dayOfMonth,Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.remind_main);
        db=SQLiteDatabase.openOrCreateDatabase("/data/data/com.example.jzy_1996.memorandum/databases/memo.db", null);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setTitle("待办提醒");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RemindActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                finish();
            }
        });
        createListView();
    }
}
