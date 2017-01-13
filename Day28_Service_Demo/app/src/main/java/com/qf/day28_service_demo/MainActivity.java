package com.qf.day28_service_demo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button button;

    private String apkPath = "http://218.244.149.129:9010/download.php?apkid=13";
    private String apkName = "hello.apk";

    //初始化广播接收者
    private MyReceiver receiver = new MyReceiver();

    private boolean isDownLoad = false;

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            button.setText("安装");
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button  = (Button) findViewById(R.id.button);

            //判断:  下载的目录中, 是否改名称的apk, 如果有则显示"安装", 如果没有则显示"下载"
            byte[] data = SDCardHelper.getBytePublicDir(apkName);

            if (data!=null && data.length>0)
            {
                button.setText("安装");
            }
            else
            {
                button.setText("下载");
                isDownLoad = true;
        }

        //注册广播接收者  --  接收下载成功的广播
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.qf.downLoad");
        registerReceiver(receiver,filter);
    }

    //点击按钮 下载/安装 apk
    public  void clickButton(View view)
    {
        if (isDownLoad)
        {
            //下载 -- 开启服务下载apk
            Intent intent = new Intent(MainActivity.this,MyService.class);
            intent.putExtra("apkName",apkName);
            intent.putExtra("apkPath",apkPath);
            startService(intent);
        }
        else{
            //安装
            InstallAPKUtils.installApk(SDCardHelper.getFile(apkName),this);
        }
    }

    /**
     * 自定义的广播接收者
     */
    class MyReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            //给Handler 发送消息 , 改变按钮显示的内容
            handler.sendEmptyMessage(1);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
