package com.example.jzy_1996.memorandum;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jzy_1996 on 2017/12/12.
 */

public class NewActivity extends AppCompatActivity {
    private SQLiteDatabase db;

    private void getDatabase() {
        db = SQLiteDatabase.openOrCreateDatabase("/data/data/com.example.jzy_1996.memorandum/databases/memo.db", null);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_main);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss");
        getDatabase();
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        final String str = formatter.format(curDate);

        TextView textTime = (TextView) findViewById(R.id.textTime);
        textTime.setText(str);

        final EditText editText=(EditText)findViewById(R.id.editMain);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setTitle("编辑");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String time = str;
                String content= String.valueOf(editText.getText());
//
//                Cursor cursor=db.query("memo_info",null,null,null,null,null,null);
//                int temp=cursor.getCount();

                ContentValues cValue = new ContentValues();
                cValue.put("time", time);
                cValue.put("content", content);
                db.insert("memo_info", null, cValue);
//
//                cursor=db.query("memo_info",null,null,null,null,null,null);
//                temp=cursor.getCount();

                Intent intent = new Intent(NewActivity.this, MainActivity.class);
                startActivity(intent);
                //overridePendingTransition(R.anim.in_from_left,R.anim.out_to_right);
                finish();
            }
        });

    }
}
