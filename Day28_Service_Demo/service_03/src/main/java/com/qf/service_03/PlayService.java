package com.qf.service_03;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.IBinder;

public class PlayService extends Service {

    private MediaPlayer player;

    private SeekReceiver receiver = new SeekReceiver();

    @Override
    public void onCreate() {
        super.onCreate();

        //创建mp3播放的对象
        player = MediaPlayer.create(this,R.raw.wlxq);

        //如果对象创建成功, 则表示当前的MediaPlayer进入prepared状态, 可以播放音乐

        //注册广播接收者(接收Activity发送的进度值)
        registerReceiver(receiver,new IntentFilter(Config.ACTION_PROGRESS));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //播放音乐, 暂停音乐

        //判断 :  如果音乐正在播放, 则暂停,  否则播放
        if (player.isPlaying())
        {
            player.pause();//暂停
        }else
        {
            player.start();//播放
        }

        //启动线程, 实时发送当前播放的进度
        new ProgressThread().start();

        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //停止音乐播放
        player.stop();
        //回收资源
        player.release();

        //取消广播注册
        unregisterReceiver(receiver);
    }

    /**
     * 获取当前音乐播放的进度, 同时发送广播  , 携带进度值
     * */
    class  ProgressThread extends  Thread
    {
        @Override
        public void run() {

            //1, 得到播放的总长度
            int maxLen = player.getDuration();

            while (player!=null && player.isPlaying())
            {
                //获取当前播放的长度
                int curLen = player.getCurrentPosition();

                //通过广播 , 将音乐的总长度和当前播放的长度 发送给Activity
                Intent intent = new Intent(Config.ACTION_PLAY);
                intent.putExtra("maxLen",maxLen);
                intent.putExtra("curLen",curLen);
                sendBroadcast(intent);
            }

        }
    }


    /**
     * 自定义广播接收者  用来接收Activity发送过来定位的位置
     */
    class SeekReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {

            //获取传入的参数
            int progress = intent.getIntExtra("progress",0);

            //根据传入的参数,在指定的位置上播放音乐
            if (player!=null)
            {
                player.seekTo(progress);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {

        return  null;
    }
}
