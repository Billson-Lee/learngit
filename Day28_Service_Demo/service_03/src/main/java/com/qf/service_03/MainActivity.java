package com.qf.service_03;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Intent intent ;

    private Button button;

    private SeekBar seekBar;

    private TextView tv;

    private boolean isPlay = false;//判断当前是否正在播放

    private ProgressReceiver receiver;//接收当前音乐播放的进度的广播

    private boolean isStop = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.play);
        seekBar = (SeekBar)findViewById(R.id.seekBar);
        tv = (TextView) findViewById(R.id.tv);

        intent = new Intent(MainActivity.this,PlayService.class);

        //注册广播接收者
        receiver = new ProgressReceiver();
        registerReceiver(receiver,new IntentFilter(Config.ACTION_PLAY));

        //SeekBar的事件处理
        event();
    }

    /**
     * SeekBar的事件处理
     */
    public void event()
    {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //拖拽发生变化
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //开始拖拽
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //停止拖拽
                //1, 得到当前拖拽的进度值
                int progress = seekBar.getProgress();

                //2, 发送广播  携带进度值
                Intent service = new Intent(Config.ACTION_PROGRESS);
                service.putExtra("progress",progress);
                sendBroadcast(service);
            }
        });
    }

    //播放按钮
   public void play(View v)
   {
       isStop  = false;

       //开启服务, 播放音乐
       if (!isPlay)
       {
           button.setText("暂停");

       }else
       {
           button.setText("播放");
       }

       isPlay = !isPlay;

       startService(intent);
   }

    //停止
    public void stop(View v)
    {
        isStop = true;//是否点击了停止按钮
        //控制点击停止按钮后, 播放按钮显示的文字
        isPlay = false;
        button.setText("播放");
        //停止服务,停止音乐的播放
        stopService(intent);
        //停止播放后, 进度条归零
        seekBar.setProgress(0);

    }


    /**
     * 自定义广播接收者
     * 用于接收服务 发送过来的  音乐的总长度和当前播放的长度
     */
    class ProgressReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (!isStop)//如果没有点击停止按钮, 那么可以接收广播; 否则就算有需要处理的广播那么也不会做出响应
            {
                //接收传入的参数
                int maxLen = intent.getIntExtra("maxLen",0);
                int curLen = intent.getIntExtra("curLen",0);

                seekBar.setMax(maxLen);
                seekBar.setProgress(curLen);

                //总时长  和 当前时长  00:00/00:00
                StringBuilder sBuilder = new StringBuilder();

                int m = curLen/1000/60;//当前时长的分钟数
                int s = curLen/1000%60;//当前时长的秒数

                int _m = maxLen/1000/60;//总时长的分钟数
                int _s = maxLen/1000%60;//总时长的秒数

                sBuilder.append(m/10).append(m%10)
                        .append(":")
                        .append(s/10).append(s%10)
                        .append("/")
                        .append(_m/10).append(_m%10)
                        .append(":")
                        .append(_s/10).append(_s%10);

                tv.setText(sBuilder.toString());
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
