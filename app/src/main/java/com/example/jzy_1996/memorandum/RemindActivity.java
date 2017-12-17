package com.example.jzy_1996.memorandum;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.TimeUtils;
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
import java.util.Calendar;
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
    private AlarmManager alarmManager;
    private Calendar cal=Calendar.getInstance();
    private PendingIntent pi=null;

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
                timePickerDialog=new TimePickerDialog(RemindActivity.this, new MyTimeSetListener(i+1),0,0,true);
                timePickerDialog.show();
            }
        });
        adapter.setOnItemDeleteClickListener(new MyAdapter.OnItemDeleteClickListener() {
            @Override
            public void onDeleteClick(int i) {
                final String order=String.valueOf(i+1);
                new AlertDialog
                        .Builder(RemindActivity.this)
                        .setTitle("确定要删除这一项？")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i1) {
                                Intent intent = new Intent(RemindActivity.this, CallAlarmReceiver.class);
                                alarmManager=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
                                pi=PendingIntent.getBroadcast(RemindActivity.this,0,intent,PendingIntent.FLAG_ONE_SHOT);
                                alarmManager.cancel(pi);
                                pi=null;
                                alarmManager=null;
                                db.delete("memo_remind", "_id=?", new String[]{order});
                                ArrayList<String[]> temp_info_list = new ArrayList<>();
                                String[] columns=new String[]{"time","content","remindTime","remindYear","remindMonth","remindDay",
                                        "remindHour","remindMinute","remindSecond","finish","remind"};
                                Cursor cursor = db.query("memo_remind", columns, null, null, null, null, null);
                                if (cursor.moveToFirst()) {
                                    while (!cursor.isAfterLast()) {
                                        String[] temp = new String[]{cursor.getString(0), cursor.getString(1),cursor.getString(2),
                                                cursor.getString(3),cursor.getString(4),cursor.getString(5),
                                                cursor.getString(6),cursor.getString(7),cursor.getString(8),
                                                cursor.getString(9),cursor.getString(10)};
                                        temp_info_list.add(temp);
                                        cursor.moveToNext();
                                    }
                                }
                                String drop = "drop table if exists memo_remind";
                                db.execSQL(drop);
                                String memo_remind_new =
                                        "create table if not exists " +
                                                "memo_remind(" +
                                                "_id integer primary key autoincrement, " +
                                                "time text, " +
                                                "content text, " +
                                                "remindTime text, " +
                                                "remindYear int, " +
                                                "remindMonth int, " +
                                                "remindDay int, " +
                                                "remindHour int, " +
                                                "remindMinute int, " +
                                                "remindSecond int, " +
                                                "finish int, " +
                                                "remind int)";
                                db.execSQL(memo_remind_new);
                                for (int index = 0; index < temp_info_list.size(); index++) {
                                    ContentValues cValue = new ContentValues();
                                    cValue.put("time", temp_info_list.get(index)[0]);
                                    cValue.put("content", temp_info_list.get(index)[1]);
                                    cValue.put("remindTime",temp_info_list.get(index)[2]);
                                    cValue.put("remindYear",temp_info_list.get(index)[3]);
                                    cValue.put("remindMonth",temp_info_list.get(index)[4]);
                                    cValue.put("remindDay",temp_info_list.get(index)[5]);
                                    cValue.put("remindHour",temp_info_list.get(index)[6]);
                                    cValue.put("remindMinute",temp_info_list.get(index)[7]);
                                    cValue.put("remindSecond",temp_info_list.get(index)[8]);
                                    cValue.put("finish",temp_info_list.get(index)[9]);
                                    cValue.put("remind",temp_info_list.get(index)[10]);
                                    db.insert("memo_remind", null, cValue);
                                }
                                adapter.setmList(getData());
                                listView.setAdapter(adapter);
                            }
                        })
                        .setNegativeButton("取消",null)
                        .show();
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
            tempRemind="";
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
            Calendar c=Calendar.getInstance();
            c.setTimeInMillis(System.currentTimeMillis());
            c.set(Calendar.HOUR_OF_DAY, hourOfDay);
            c.set(Calendar.DATE,cal.get(Calendar.DATE));
            c.set(Calendar.MINUTE, minute);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            Intent intent = new Intent(RemindActivity.this, CallAlarmReceiver.class);
            alarmManager=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
            pi=PendingIntent.getBroadcast(RemindActivity.this,0,intent,PendingIntent.FLAG_ONE_SHOT);
//            long selectTime=c.getTimeInMillis();
//            long systemTime=System.currentTimeMillis();
            if(c.getTimeInMillis()<System.currentTimeMillis()){
                if(Build.VERSION.SDK_INT>=19) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() + 24 * 60 * 60 * 1000, pi);
                }else{
                    alarmManager.set(AlarmManager.RTC_WAKEUP,     c.getTimeInMillis() + 24 * 60 * 60 * 1000, pi);
                }
            }else{
                if(Build.VERSION.SDK_INT>=19) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
                }else{
                    alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi);
                }
            }
        }
    }

//    public class MyDateSetListener implements DatePickerDialog.OnDateSetListener {
//        int order;
//
//        public MyDateSetListener(int order){
//            this.order=order;
//        }
//
//        @Override
//        public void onDateSet(DatePicker view, int year, int monthOfYear,
//                              int dayOfMonth) {
//            // TODO Auto-generated method stub
//            tempRemind="";
//            ContentValues cValue = new ContentValues();
//            cValue.put("remindYear", year);
//            tempRemind+=String.valueOf(year);
//            tempRemind+="年";
//            cValue.put("remindMonth", monthOfYear+1);
//            tempRemind+=String.valueOf(monthOfYear+1);
//            tempRemind+="月";
//            cValue.put("remindDay",dayOfMonth);
//            tempRemind+=String.valueOf(dayOfMonth);
//            tempRemind+="日 ";
//            db.update("memo_remind",cValue,"_id=?",new String[]{String.valueOf(order)});
//            timePickerDialog=new TimePickerDialog(RemindActivity.this,new MyTimeSetListener(order),0,0,true);
//            timePickerDialog.show();
//            //Toast.makeText(Activity01.this, "year:"+year+" monthOfYear"+(monthOfYear+1)+" dayOfMonth"+dayOfMonth,Toast.LENGTH_SHORT).show();
//        }
//
//    }

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
        //setAlarm();
    }
}
