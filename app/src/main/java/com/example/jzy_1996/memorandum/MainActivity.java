package com.example.jzy_1996.memorandum;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ListView listView;
    private SQLiteDatabase db;
    private MyDatabaseHelper databaseHelper;

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

    private void createDatabaseWithTable() {
        databaseHelper = new MyDatabaseHelper(this, "memo.db", null, 1);
        databaseHelper.getWritableDatabase();
        db = SQLiteDatabase.openOrCreateDatabase("/data/data/com.example.jzy_1996.memorandum/databases/memo.db", null);
//        String memo_drop="drop table if exists memo_info";
//        db.execSQL(memo_drop);
        String memo_info =
                "create table if not exists " +
                        "memo_info(" +
                        "_id integer primary key autoincrement," +
                        "time text," +
                        "content text)";
        db.execSQL(memo_info);

    }

    private void createListView() {
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
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
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
        } else if (id == R.id.nav_view) {

        } else if (id == R.id.nav_delete) {

        } else if (id == R.id.nav_person) {

        } else if (id == R.id.nav_share) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
