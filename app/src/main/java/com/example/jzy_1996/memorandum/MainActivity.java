package com.example.jzy_1996.memorandum;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.sql.DataTruncation;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ListView listView;
    private SQLiteDatabase db;
    private MyDatabaseHelper databaseHelper;
    private int MarkCondition=0;

    private ArrayList<HashMap<String, String>> getData() {
        ArrayList<HashMap<String, String>> data = new ArrayList<>();
        HashMap<String, String> map;
        Cursor cursor = db.query("memo_info", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(0);
                String time = cursor.getString(1);
                String content = cursor.getString(2);
                map = new HashMap<>();
                map.put("ItemTitle", content);
                map.put("ItemText", time);
                cursor.moveToNext();
                data.add(map);
            }
//            int temp=cursor.getCount();
//            for(int i=0;i<cursor.getCount();i++){
//                cursor.move(i);
//                int id = cursor.getInt(0);
//                String time=cursor.getString(1);
//                String content=cursor.getString(2);
////                System.out.println(id+":"+sname+":"+snumber);
//                map=new HashMap<>();
//                map.put("ItemTitle",content);
//                map.put("ItemText",time);
//                data.add(map);
//            }
        }
        //cursor.close();
        return data;
    }

    private ArrayList<HashMap<String, String>> getTrashData() {
        ArrayList<HashMap<String, String>> data = new ArrayList<>();
        HashMap<String, String> map;
        Cursor cursor = db.query("memo_trash", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(0);
                String time = cursor.getString(1);
                String content = cursor.getString(2);
                map = new HashMap<>();
                map.put("ItemTitle", content);
                map.put("ItemText", time);
                cursor.moveToNext();
                data.add(map);
            }
        }
        return data;
    }

    private void createDatabaseWithTable() {
        databaseHelper = new MyDatabaseHelper(this, "memo.db", null, 1);
        databaseHelper.getWritableDatabase();
        db = SQLiteDatabase.openOrCreateDatabase("/data/data/com.example.jzy_1996.memorandum/databases/memo.db", null);
//        String memo_drop_info="drop table if exists memo_info";
//        db.execSQL(memo_drop_info);
//        String memo_drop_trash="drop table if exists memo_trash";
//        db.execSQL(memo_drop_trash);
//        String memo_drop_remind="drop table if exists memo_remind";
//        db.execSQL(memo_drop_remind);
        String memo_info =
                "create table if not exists " +
                        "memo_info(" +
                        "_id integer primary key autoincrement," +
                        "time text," +
                        "content text)";
        String memo_trash =
                "create table if not exists " +
                        "memo_trash(" +
                        "_id integer primary key autoincrement, " +
                        "time text, " +
                        "content text, " +
                        "remindTime text)";
        String memo_remind =
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
        db.execSQL(memo_info);
        db.execSQL(memo_trash);
        db.execSQL(memo_remind);
    }

    private void createListView() {
        MarkCondition=0;
        listView = (ListView) findViewById(R.id.appbarMain).findViewById(R.id.list_item);
        SimpleAdapter adapter = new SimpleAdapter(this, getData(),
                R.layout.my_listitem,
                new String[]{"ItemTitle", "ItemText"},
                new int[]{R.id.ItemTitle, R.id.ItemText});
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String table = "memo_info";
                String[] columns = new String[]{"time", "content"};
                String selection = "_id=?";
                String[] selectionArgs = new String[]{String.valueOf(l + 1)};
                Cursor cursor = db.query(table, columns, selection, selectionArgs, null, null, null);
                String time = "";
                String content = "";
                if (cursor.moveToFirst()) {
                    while (!cursor.isAfterLast()) {
                        time = cursor.getString(0);
                        content = cursor.getString(1);
                        cursor.moveToNext();
                    }
                }
                Intent intent = new Intent();
                intent.putExtra("id", String.valueOf(l + 1));
                intent.putExtra("time", time);
                intent.putExtra("content", content);
                intent.setClass(MainActivity.this, EditActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            /* @return true if the callback consumed the long click, false otherwise */
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final String[] items = new String[]{"删除", "提醒"};
                final String[] selected = new String[]{"删除"};
                final String order = String.valueOf(l + 1);
                //final boolean[] selected = new boolean[]{true,false};
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("要执行的操作？")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setSingleChoiceItems(items, 0,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        selected[0] = items[i];
                                    }
                                })
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String[] columns = new String[]{"time", "content"};
                                String[] selectionArgs = new String[]{order};
                                String[] whereArgs = new String[]{order};
                                if (selected[0].equals("删除")) {
//                                    String time="";
//                                    String content="";
                                    Cursor cursor = db.query("memo_info", columns, "_id=?", selectionArgs, null, null, null);
                                    if (cursor.moveToFirst()) {
                                        while (!cursor.isAfterLast()) {
                                            ContentValues cValue = new ContentValues();
                                            cValue.put("time", cursor.getString(0));
                                            cValue.put("content", cursor.getString(1));
                                            db.insert("memo_trash", null, cValue);
                                            cursor.moveToNext();
                                        }
                                    }
                                    db.delete("memo_info", "_id=?", whereArgs);
                                    ArrayList<String[]> temp_info_list = new ArrayList<>();
                                    cursor = db.query("memo_info", columns, null, null, null, null, null);
                                    if (cursor.moveToFirst()) {
                                        while (!cursor.isAfterLast()) {
                                            String[] temp = new String[]{cursor.getString(0), cursor.getString(1)};
                                            temp_info_list.add(temp);
                                            cursor.moveToNext();
                                        }
                                    }
                                    String drop = "drop table if exists memo_info";
                                    db.execSQL(drop);
                                    String memo_info_new =
                                            "create table if not exists " +
                                                    "memo_info(" +
                                                    "_id integer primary key autoincrement," +
                                                    "time text," +
                                                    "content text)";
                                    db.execSQL(memo_info_new);
                                    for (int index = 0; index < temp_info_list.size(); index++) {
                                        ContentValues cValue = new ContentValues();
                                        cValue.put("time", temp_info_list.get(index)[0]);
                                        cValue.put("content", temp_info_list.get(index)[1]);
                                        db.insert("memo_info", null, cValue);
                                    }
                                    SimpleAdapter new_adapter = new SimpleAdapter(MainActivity.this, getData(),
                                            R.layout.my_listitem,
                                            new String[]{"ItemTitle", "ItemText"},
                                            new int[]{R.id.ItemTitle, R.id.ItemText});
                                    listView.setAdapter(new_adapter);
                                } else if (selected[0].equals("提醒")) {
                                    Cursor cursor = db.query("memo_info", columns, "_id=?", selectionArgs, null, null, null);
                                    if (cursor.moveToFirst()) {
                                        while (!cursor.isAfterLast()) {
                                            ContentValues cValue = new ContentValues();
                                            cValue.put("time", cursor.getString(0));
                                            cValue.put("content", cursor.getString(1));
                                            db.insert("memo_remind", null, cValue);
                                            cursor.moveToNext();
                                        }
                                    }
                                }
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                return true;
            }
        });
    }

    private void createTrashView() {
//        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                if (item.getItemId()==R.id.delete_all){
//                }
//                return true;
//            }
//        });
        MarkCondition=1;
        listView = (ListView) findViewById(R.id.appbarMain).findViewById(R.id.list_item);
        SimpleAdapter adapter = new SimpleAdapter(this, getTrashData(),
                R.layout.my_listitem,
                new String[]{"ItemTitle", "ItemText"},
                new int[]{R.id.ItemTitle, R.id.ItemText});
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final String[] items = new String[]{"恢复备忘", "彻底删除"};
                final String[] selected = new String[]{"恢复备忘"};
                final String order = String.valueOf(l + 1);
                //final boolean[] selected = new boolean[]{true,false};
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("要执行的操作？")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setSingleChoiceItems(items, 0,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        selected[0] = items[i];
                                    }
                                })
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                final String[] columns = new String[]{"time", "content"};
                                String[] selectionArgs = new String[]{order};
                                final String[] whereArgs = new String[]{order};
                                if (selected[0].equals("恢复备忘")) {
//                                    String time="";
//                                    String content="";
                                    Cursor cursor = db.query("memo_trash", columns, "_id=?", selectionArgs, null, null, null);
                                    if (cursor.moveToFirst()) {
                                        while (!cursor.isAfterLast()) {
                                            ContentValues cValue = new ContentValues();
                                            cValue.put("time", cursor.getString(0));
                                            cValue.put("content", cursor.getString(1));
                                            db.insert("memo_info", null, cValue);
                                            cursor.moveToNext();
                                        }
                                    }
                                    db.delete("memo_trash", "_id=?", whereArgs);
                                    ArrayList<String[]> temp_info_list = new ArrayList<>();
                                    cursor = db.query("memo_trash", columns, null, null, null, null, null);
                                    if (cursor.moveToFirst()) {
                                        while (!cursor.isAfterLast()) {
                                            String[] temp = new String[]{cursor.getString(0), cursor.getString(1)};
                                            temp_info_list.add(temp);
                                            cursor.moveToNext();
                                        }
                                    }
                                    String drop = "drop table if exists memo_trash";
                                    db.execSQL(drop);
                                    String memo_info_new =
                                            "create table if not exists " +
                                                    "memo_trash(" +
                                                    "_id integer primary key autoincrement," +
                                                    "time text," +
                                                    "content text)";
                                    db.execSQL(memo_info_new);
                                    for (int index = 0; index < temp_info_list.size(); index++) {
                                        ContentValues cValue = new ContentValues();
                                        cValue.put("time", temp_info_list.get(index)[0]);
                                        cValue.put("content", temp_info_list.get(index)[1]);
                                        db.insert("memo_trash", null, cValue);
                                    }
                                    SimpleAdapter new_adapter = new SimpleAdapter(MainActivity.this, getTrashData(),
                                            R.layout.my_listitem,
                                            new String[]{"ItemTitle", "ItemText"},
                                            new int[]{R.id.ItemTitle, R.id.ItemText});
                                    listView.setAdapter(new_adapter);
                                } else if (selected[0].equals("彻底删除")) {
                                    new AlertDialog.Builder(MainActivity.this)
                                            .setTitle("确定要彻底删除这条备忘吗？")
                                            .setIcon(android.R.drawable.ic_dialog_info)
                                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    db.delete("memo_trash", "_id=?", whereArgs);
                                                    ArrayList<String[]> temp_info_list = new ArrayList<>();
                                                    Cursor cursor = db.query("memo_trash", columns, null, null, null, null, null);
                                                    if (cursor.moveToFirst()) {
                                                        while (!cursor.isAfterLast()) {
                                                            String[] temp = new String[]{cursor.getString(0), cursor.getString(1)};
                                                            temp_info_list.add(temp);
                                                            cursor.moveToNext();
                                                        }
                                                    }
                                                    String drop = "drop table if exists memo_trash";
                                                    db.execSQL(drop);
                                                    String memo_info_new =
                                                            "create table if not exists " +
                                                                    "memo_trash(" +
                                                                    "_id integer primary key autoincrement," +
                                                                    "time text," +
                                                                    "content text)";
                                                    db.execSQL(memo_info_new);
                                                    for (int index = 0; index < temp_info_list.size(); index++) {
                                                        ContentValues cValue = new ContentValues();
                                                        cValue.put("time", temp_info_list.get(index)[0]);
                                                        cValue.put("content", temp_info_list.get(index)[1]);
                                                        db.insert("memo_trash", null, cValue);
                                                    }
                                                    SimpleAdapter new_adapter = new SimpleAdapter(MainActivity.this, getTrashData(),
                                                            R.layout.my_listitem,
                                                            new String[]{"ItemTitle", "ItemText"},
                                                            new int[]{R.id.ItemTitle, R.id.ItemText});
                                                    listView.setAdapter(new_adapter);
                                                }
                                            })
                                            .setNegativeButton("取消", null)
                                            .show();

                                }
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                return false;
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        createDatabaseWithTable();
        createListView();

//        listView=new ListView(this);
//        listView.setAdapter(new ArrayAdapter<String>(this,R.layout.content_main,getData()));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (MarkCondition==0){
            menu.findItem(R.id.action_settings).setVisible(true);
            menu.findItem(R.id.delete_all).setVisible(false);
        }
        else if (MarkCondition==1) {
            menu.findItem(R.id.action_settings).setVisible(false);
            menu.findItem(R.id.delete_all).setVisible(true);
        }
        invalidateOptionsMenu();
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.delete_all) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("确认要清空回收站？")
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String drop_trash=
                                    "drop table if exists memo_trash";
                            db.execSQL(drop_trash);
                            String memo_trash =
                                    "create table if not exists " +
                                            "memo_trash(" +
                                            "_id integer primary key autoincrement, " +
                                            "time text, " +
                                            "content text, " +
                                            "remindTime text)";
                            db.execSQL(memo_trash);
                            SimpleAdapter new_adapter = new SimpleAdapter(MainActivity.this, getTrashData(),
                                    R.layout.my_listitem,
                                    new String[]{"ItemTitle", "ItemText"},
                                    new int[]{R.id.ItemTitle, R.id.ItemText});
                            listView.setAdapter(new_adapter);
                        }
                    })
                    .setNegativeButton("取消",null)
                    .show();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_new) {
            Intent intent = new Intent(MainActivity.this, NewActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        } else if (id == R.id.nav_remind) {
            Intent intent = new Intent(MainActivity.this,RemindActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        } else if (id == R.id.nav_library) {
            createListView();
        } else if (id == R.id.nav_person) {

        } else if (id == R.id.nav_delete) {
            createTrashView();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
