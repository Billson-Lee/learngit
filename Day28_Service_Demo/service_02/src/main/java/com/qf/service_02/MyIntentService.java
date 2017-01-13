package com.qf.service_02;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;

public class MyIntentService extends IntentService {

    public MyIntentService() {
        super("MyIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //工作线程
        //1, 获取传入的path
        String path = intent.getStringExtra("path");

        //2,网络获取图片, 并且存入sd卡中
        Bitmap bitmap =  HttpUtils.getBitmap(path);
        if (bitmap!=null)
        {
            String fileName = path.substring(path.lastIndexOf("/")+1);

            // 存入sd卡中
            boolean isSave = SDCardHelper.saveBitmapPublicDir(fileName,bitmap);

            if (isSave) {
                //发送图片下载成功的广播
                Intent intent1 = new Intent();
                intent1.setAction("com.qf.imageDownLoad");
                intent1.putExtra("fileName",fileName);
                sendBroadcast(intent1);
            }

        }


    }
}
