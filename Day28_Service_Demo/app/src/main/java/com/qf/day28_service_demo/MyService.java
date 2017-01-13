package com.qf.day28_service_demo;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 1, 获取传入的apkName, apkPath
 * 2, 根据apkPath 开启线程下载apk
 * 3, 实时发送通知, 显示下载的进度
 * 4, 将下载的apk存入sd卡中
 *
 */
public class MyService extends Service {

    private NotificationManager manager;

    @Override
    public void onCreate() {
        super.onCreate();

        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //1, 获取传入的apkName, apkPath
        final String apkName = intent.getStringExtra("apkName");
        final String apkPath = intent.getStringExtra("apkPath");

        //2,根据apkPath 开启线程下载apk
        new Thread(){
            @Override
            public void run() {
                try{
                    //构建通知
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(MyService.this);
                    builder.setSmallIcon(R.mipmap.ic_launcher);
                    builder.setContentTitle("下载提示");
                    builder.setContentText("正在下载");


                    //加载数据
                    URL url =  new URL(apkPath);

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    conn.setRequestMethod("GET");

                    conn.connect();

                    if (conn.getResponseCode() == 200)
                    {
                        //获取下载的总长度
                        int maxLen = conn.getContentLength();

                        //当前加载的长度
                        int curLen = 0;

                        BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());

                        //把获取的流, 存入sd卡中
                        //PATH + apkName
                        FileOutputStream fos = new FileOutputStream(SDCardHelper.getFile(apkName));

                        byte[] buffer = new byte[1024];
                        int len = 0;

                        while ((len = bis.read(buffer))!=-1)
                        {
                            curLen +=len;

                            //计算当前下载的进度  (百分百)
                            int progress  = curLen*100/maxLen;

                            //把当前下载的进度值, 通过通知发送出去
                            builder.setProgress(100,progress,false);

                            //发送通知
                            manager.notify(1,builder.build());

                           // Thread.sleep(2000);

                            //写出流
                            fos.write(buffer,0,len);
                        }

                        fos.flush();
                        fos.close();
                        bis.close();

                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                }

                //移除下载的通知
                manager.cancel(1);

                //发送下载成功的通知
                NotificationCompat.Builder builder = new NotificationCompat.Builder(MyService.this);
                builder.setSmallIcon(R.mipmap.ic_launcher);
                builder.setContentTitle("提示");
                builder.setContentText("下载完成");
                builder.setTicker("下载完成");

                manager.notify(2,builder.build());

                //发送下载完成的广播
                Intent intent=new Intent();
                intent.setAction("com.qf.downLoad");
                sendBroadcast(intent);

            }
        }.start();


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }
}
