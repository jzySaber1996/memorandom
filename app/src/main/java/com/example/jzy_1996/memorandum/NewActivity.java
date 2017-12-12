package com.example.jzy_1996.memorandum;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jzy_1996 on 2017/12/12.
 */

public class NewActivity extends AppCompatActivity {
    private SQLiteDatabase db;

    private void getDatabase(){
        db=SQLiteDatabase.openOrCreateDatabase("/data/data/com.example.jzy_1996.memorandum/databases/memo.db",null);
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_main);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);

        TextView textTime=(TextView)findViewById(R.id.textTime);
        textTime.setText(str);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setTitle("编辑");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(NewActivity.this,MainActivity.class);
                startActivity(intent);
                //overridePendingTransition(R.anim.in_from_left,R.anim.out_to_right);
                finish();
            }
        });

    }
}
