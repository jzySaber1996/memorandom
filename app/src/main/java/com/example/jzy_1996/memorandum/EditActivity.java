package com.example.jzy_1996.memorandum;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by jzy_1996 on 2017/12/8.
 */

public class EditActivity extends AppCompatActivity {

    //    private static final int XSPEED_MIN = 200;
//
//    private static final int XDISTANCE_MIN = 150;
//
//    private float xDown;
//
//    private float xMove;
//
//    private VelocityTracker mVelocityTracker;
    private SQLiteDatabase db;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_main);
        db = SQLiteDatabase.openOrCreateDatabase("/data/data/com.example.jzy_1996.memorandum/databases/memo.db", null);
        Intent intent = getIntent();
        final EditText editText = (EditText) findViewById(R.id.editMain);
        final TextView textView = (TextView) findViewById(R.id.textTime);
        final String id=intent.getStringExtra("id");
        String time=intent.getStringExtra("time");
        String content=intent.getStringExtra("content");
        textView.setText(time);
        editText.setText(content);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setTitle("编辑");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content_new= String.valueOf(editText.getText());
                String time_new= (String) textView.getText();
                ContentValues contentValues=new ContentValues();
                contentValues.put("time",time_new);
                contentValues.put("content",content_new);
                String[] stringArgs=new String[]{id};
                db.update("memo_info",contentValues,"_id=?",stringArgs);
                Intent intent = new Intent(EditActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
                finish();
            }
        });


        //editText.setOnTouchListener(this);
    }

//    @Override
//    public boolean onTouch(View view, MotionEvent motionEvent) {
//        createVelocityTrack(motionEvent);
//        switch (motionEvent.getAction()) {
//            case MotionEvent.ACTION_MOVE:
//                xMove = motionEvent.getRawX();
//                //活动的距离
//                int distanceX = (int) (xMove - xDown);
//                //获取顺时速度
//                int xSpeed = getScrollVelocity();
//                //当滑动的距离大于我们设定的最小距离且滑动的瞬间速度大于我们设定的速度时，返回到上一个activity
//                if(distanceX > XDISTANCE_MIN && xSpeed > XSPEED_MIN) {
//                    Intent intent=new Intent(EditActivity.this,MainActivity.class);
//                    startActivity(intent);
//                    finish();
//                    overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
//                }
//                break;
//            default:
//                break;
//        }
//        return true;
//    }
//
//    private void createVelocityTrack(MotionEvent event){
//        if (mVelocityTracker == null) {
//            mVelocityTracker = VelocityTracker.obtain();
//        }
//        mVelocityTracker.addMovement(event);
//    }
//
//    private void recycleVelocityTracker() {
//        mVelocityTracker.recycle();
//        mVelocityTracker = null;
//    }
//
//    private int getScrollVelocity() {
//        mVelocityTracker.computeCurrentVelocity(1000);
//        int velocity = (int) mVelocityTracker.getXVelocity();
//        return Math.abs(velocity);
//    }
}
