package com.example.jzy_1996.memorandum;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import java.util.Calendar;

/**
 * Created by jzy_1996 on 2017/12/17.
 */

public class NotifySMSReceived extends AppCompatActivity {
    private static final String LOG_TAG = "SMSReceiver";
    public static final int NOTIFICATION_ID_RECEIVED = 0x1221;
    static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
    MediaPlayer mp=null;
    private PowerManager.WakeLock mWakelock;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // hide title
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        winParams.flags |= (WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        startMedia();
        startVibrator();
        createDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 唤醒屏幕
        acquireWakeLock();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseWakeLock();
    }

    private void acquireWakeLock() {
        if (mWakelock == null) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            mWakelock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
                    | PowerManager.SCREEN_DIM_WAKE_LOCK, this.getClass()
                    .getCanonicalName());
            mWakelock.acquire();
        }
    }

    private void releaseWakeLock() {
        if (mWakelock != null && mWakelock.isHeld()) {
            mWakelock.release();
            mWakelock = null;
        }
    }

    private void startMedia() {
        mp=MediaPlayer.create(this,R.raw.music);
        try {
//            mp.setDataSource();
            mp.prepare();
            mp.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void startVibrator() {
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = { 500, 1000, 500, 1000 }; // 停止 开启 停止 开启
        vibrator.vibrate(pattern, 0);
    }

    private void createDialog() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle("您有待完成的备忘！")
                .setPositiveButton("我了解了！", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        mp.stop();
                        vibrator.cancel();
                        finish();
                    }
                })
                .show();
    }

    /**
     * 推迟10分钟提醒
     */
    private void tenMRemind(){
        //设置时间
        Calendar calendar_now =Calendar.getInstance();


        calendar_now.setTimeInMillis(System.currentTimeMillis());
        calendar_now.set(Calendar.HOUR_OF_DAY, calendar_now.get(Calendar.HOUR_OF_DAY));
        calendar_now.set(Calendar.MINUTE, calendar_now.get(Calendar.MINUTE)+10);
        calendar_now.set(Calendar.SECOND, 0);
        calendar_now.set(Calendar.MILLISECOND, 0);

        //时间选择好了
        Intent intent = new Intent(this, CallAlarmReceiver.class);
        //注册闹钟广播
        PendingIntent sender = PendingIntent.getBroadcast(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager am;
        am = (AlarmManager) this.getSystemService(this.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, calendar_now.getTimeInMillis(), sender);
    }
}
