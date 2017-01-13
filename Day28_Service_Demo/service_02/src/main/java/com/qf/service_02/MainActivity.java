package com.qf.service_02;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {


    private ImageView iv;

    private String path = "http://photocdn.sohu.com/20161122/Img473841512.jpeg";

    private MyReceiver receiver = new MyReceiver();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv = (ImageView) findViewById(R.id.iv);

        //注册广播接收者
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.qf.imageDownLoad");
        registerReceiver(receiver,filter);

    }

    public void  downLoad(View v)
    {
        //开启服务, 下载图片, 存入sd卡
        Intent intent = new Intent(this,MyIntentService.class);
        intent.putExtra("path",path);
        startService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    /**
     * 自定义广播接收者
     */
    class  MyReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {

            String fileName = intent.getStringExtra("fileName");

            //根据名称, 从sd卡中获取图片
           byte[] data =  SDCardHelper.getBytePublicDir(fileName);

            if(data!=null && data.length>0)
            {
                Bitmap bitmap = BitmapFactory.decodeByteArray(data,0,data.length);

                iv.setImageBitmap(bitmap);
            }
        }
    }
}
