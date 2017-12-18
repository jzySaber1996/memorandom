package com.example.jzy_1996.memorandum;

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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jzy_1996 on 2017/12/17.
 */

public class PersonalInfoActivity extends AppCompatActivity {
    ListView listView;
    private SQLiteDatabase db;
    private ContentValues cvUpdate=new ContentValues();

    private ArrayList<HashMap<String, String>> getPersonData() {
        ArrayList<HashMap<String, String>> data = new ArrayList<>();
        Cursor cursor = db.query("memo_person", null, "_id=?", new String[]{String.valueOf(1)}, null, null, null);
        cursor.moveToFirst();
        HashMap<String, String> map = new HashMap<>();
        map.put("ItemTitle", "用户名：");
        map.put("ItemText", cursor.getString(1));
        cvUpdate.put("username",cursor.getString(1));
        data.add(map);
        map = new HashMap<>();
        map.put("ItemTitle", "手机：");
        map.put("ItemText", cursor.getString(2));
        cvUpdate.put("phoneNumber",cursor.getString(2));
        data.add(map);
        map = new HashMap<>();
        map.put("ItemTitle", "姓名：");
        map.put("ItemText", cursor.getString(3));
        cvUpdate.put("realName",cursor.getString(3));
        data.add(map);
        map = new HashMap<>();
        map.put("ItemTitle", "性别：");
        map.put("ItemText", cursor.getString(4));
        cvUpdate.put("sex",cursor.getString(4));
        data.add(map);
        map = new HashMap<>();
        map.put("ItemTitle", "邮箱：");
        map.put("ItemText", cursor.getString(5));
        cvUpdate.put("mailbox",cursor.getString(5));
        data.add(map);
        return data;
    }

    private void createListView() {
        listView = (ListView) findViewById(R.id.list_item);
        final SimpleAdapter[] adapter = {new SimpleAdapter(this, getPersonData(),
                R.layout.personal_listitem,
                new String[]{"ItemTitle", "ItemText"},
                new int[]{R.id.ItemTitle, R.id.ItemText})};
        listView.setAdapter(adapter[0]);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                LayoutInflater inflater = LayoutInflater.from(PersonalInfoActivity.this);
                View textEntryView = inflater.inflate(R.layout.dialog_layout,null);
                switch (i) {
                    case 0:
                        AlertDialog.Builder builder = new AlertDialog.Builder(PersonalInfoActivity.this);
                        builder.setTitle("设置用户名");
                        builder.setIcon(android.R.drawable.ic_dialog_info);
                        final EditText editText = (EditText) textEntryView.findViewById(R.id.edtInput);
                        String username=getPersonData().get(0).get("ItemText");
                        editText.setText(username);
                        builder.setView(textEntryView);
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                cvUpdate.put("username",editText.getText().toString());
                                db.update("memo_person",cvUpdate,"_id=?",new String[]{String.valueOf(1)});
                                listView = (ListView) findViewById(R.id.list_item);
                                adapter[0] = new SimpleAdapter(PersonalInfoActivity.this, getPersonData(),
                                        R.layout.personal_listitem,
                                        new String[]{"ItemTitle", "ItemText"},
                                        new int[]{R.id.ItemTitle, R.id.ItemText});
                                listView.setAdapter(adapter[0]);
                            }
                        });
                        builder.setNegativeButton("取消", null);
                        builder.show();
                        break;
                    case 1:
                        builder = new AlertDialog.Builder(PersonalInfoActivity.this);
                        builder.setTitle("设置手机号：");
                        builder.setIcon(android.R.drawable.ic_dialog_info);
                        final EditText editText1 = (EditText) textEntryView.findViewById(R.id.edtInput);
                        String phoneNumber=getPersonData().get(1).get("ItemText");
                        editText1.setText(phoneNumber);
                        builder.setView(textEntryView);
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                cvUpdate.put("phoneNumber",editText1.getText().toString());
                                db.update("memo_person",cvUpdate,"_id=?",new String[]{String.valueOf(1)});
                                adapter[0] = new SimpleAdapter(PersonalInfoActivity.this, getPersonData(),
                                        R.layout.personal_listitem,
                                        new String[]{"ItemTitle", "ItemText"},
                                        new int[]{R.id.ItemTitle, R.id.ItemText});
                                listView.setAdapter(adapter[0]);
                            }
                        });
                        builder.setNegativeButton("取消", null);
                        builder.show();
                        break;
                    case 2:
                        builder = new AlertDialog.Builder(PersonalInfoActivity.this);
                        builder.setTitle("修改姓名：");
                        builder.setIcon(android.R.drawable.ic_dialog_info);
                        final EditText editText2 = (EditText) textEntryView.findViewById(R.id.edtInput);
                        String name=getPersonData().get(2).get("ItemText");
                        editText2.setText(name);
                        builder.setView(textEntryView);
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                cvUpdate.put("realName",editText2.getText().toString());
                                db.update("memo_person",cvUpdate,"_id=?",new String[]{String.valueOf(1)});
                                adapter[0] = new SimpleAdapter(PersonalInfoActivity.this, getPersonData(),
                                        R.layout.personal_listitem,
                                        new String[]{"ItemTitle", "ItemText"},
                                        new int[]{R.id.ItemTitle, R.id.ItemText});
                                listView.setAdapter(adapter[0]);
                            }
                        });
                        builder.setNegativeButton("取消", null);
                        builder.show();
                        break;
                    case 3:
                        final String[] items=new String[]{"男","女"};
                        final String[] selected = new String[]{"男"};
                        new AlertDialog.Builder(PersonalInfoActivity.this)
                                .setTitle("修改性别")
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        selected[0]=items[i];
                                    }
                                })
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        cvUpdate.put("sex",selected[0]);
                                        db.update("memo_person",cvUpdate,"_id=?",new String[]{String.valueOf(1)});
                                        adapter[0] = new SimpleAdapter(PersonalInfoActivity.this, getPersonData(),
                                                R.layout.personal_listitem,
                                                new String[]{"ItemTitle", "ItemText"},
                                                new int[]{R.id.ItemTitle, R.id.ItemText});
                                        listView.setAdapter(adapter[0]);
                                    }
                                })
                                .setNegativeButton("取消", null)
                                .show();
                        break;
                    case 4:
                        builder = new AlertDialog.Builder(PersonalInfoActivity.this);
                        builder.setTitle("设置手机号：");
                        builder.setIcon(android.R.drawable.ic_dialog_info);
                        final EditText editText3 = (EditText) textEntryView.findViewById(R.id.edtInput);
                        String mailbox=getPersonData().get(4).get("ItemText");
                        editText3.setText(mailbox);
                        builder.setView(textEntryView);
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                cvUpdate.put("mailbox",editText3.getText().toString());
                                db.update("memo_person",cvUpdate,"_id=?",new String[]{String.valueOf(1)});
                                adapter[0] = new SimpleAdapter(PersonalInfoActivity.this, getPersonData(),
                                        R.layout.personal_listitem,
                                        new String[]{"ItemTitle", "ItemText"},
                                        new int[]{R.id.ItemTitle, R.id.ItemText});
                                listView.setAdapter(adapter[0]);
                            }
                        });
                        builder.setNegativeButton("取消", null);
                        builder.show();
                    default:
                        break;
                }
            }
        });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_main);
        db = SQLiteDatabase.openOrCreateDatabase("/data/data/com.example.jzy_1996.memorandum/databases/memo.db", null);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setTitle("个人信息");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PersonalInfoActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                finish();
            }
        });
        createListView();
    }
}
