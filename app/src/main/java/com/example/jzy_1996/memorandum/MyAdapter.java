package com.example.jzy_1996.memorandum;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jzy_1996 on 2017/12/16.
 */

public class MyAdapter extends BaseAdapter {
    private Context mContext;
    private List<HashMap<String, String>> mList = new ArrayList<>();
    private TimePickerDialog timePickerDialog;
    private DatePickerDialog datePickerDialog;
    private SQLiteDatabase db=SQLiteDatabase.openOrCreateDatabase("/data/data/com.example.jzy_1996.memorandum/databases/memo.db", null);

    public MyAdapter(Context context, List<HashMap<String, String>> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void setmList(List<HashMap<String, String>> mList){
        this.mList=mList;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.remind_listitem, null);
            viewHolder.mImageView = (ImageView) view.findViewById(R.id.image_alarm_time);
            viewHolder.mInformText = (LinearLayout) view.findViewById(R.id.remind_text);

            viewHolder.mFinish = (LinearLayout) view.findViewById(R.id.finish_choose);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.mItemTitle=viewHolder.mInformText.findViewById(R.id.ItemTitle);
        viewHolder.mItemText=viewHolder.mInformText.findViewById(R.id.ItemText);
        viewHolder.mItemTitle.setText(mList.get(i).get("ItemTitle"));
        viewHolder.mItemText.setText(mList.get(i).get("ItemText"));
        Cursor cursor=db.query("memo_remind",null,"_id=?",new String[]{String.valueOf(i+1)},null,null,null);
        cursor.moveToFirst();
        if (cursor.getInt(11)==1) {
            viewHolder.mImageView.setColorFilter(Color.BLUE);
        }
        viewHolder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemAlarmClickListener.onAlarmClick(i);
            }
        });
        return view;
    }
    /**
     * 删除按钮的监听接口
     */
    public interface OnItemAlarmClickListener {
        void onAlarmClick(int i);
    }

    private OnItemAlarmClickListener mOnItemAlarmClickListener;

    public void setOnItemAlarmClickListener( OnItemAlarmClickListener mOnItemAlarmClickListener) {
        this.mOnItemAlarmClickListener = mOnItemAlarmClickListener;
    }

    class ViewHolder {
        ImageView mImageView;
        LinearLayout mInformText;
        TextView mItemTitle;
        TextView mItemText;
        LinearLayout mFinish;
    }

}
