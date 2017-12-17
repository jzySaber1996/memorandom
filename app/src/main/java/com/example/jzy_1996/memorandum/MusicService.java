package com.example.jzy_1996.memorandum;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by jzy_1996 on 2017/12/17.
 */

public class MusicService extends Service {
    private MediaPlayer player;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        player = MediaPlayer.create(this, R.raw.music);
        player.setLooping(true);
        player.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (player!=null){
            player.stop();
        }
    }
}
