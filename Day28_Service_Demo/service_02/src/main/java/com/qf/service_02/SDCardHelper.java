package com.qf.service_02;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * sd卡的存取数据
 *
 * @author 樊成秀
 *
 *
 */
public class SDCardHelper {

    //路径
    public static final String PATH = Environment.getExternalStorageDirectory()+"/1621/img/";
    /**
     * 判断sd的状态
     *
     * @return
     */
    public static boolean isMounted()
    {
        //获取当前sd的状态
        String state = Environment.getExternalStorageState();

        return state.equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 判断sd卡的可用空间
     */
    public static boolean isAvailable()
    {
        if(isMounted())
        {
            //1, 实例化文件管理系统的状态对象  StatFs
            StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());

            long size = 0;

            if(Build.VERSION.SDK_INT > 18)
            {
                size = statFs.getFreeBytes();
            }else
            {
                //可用数据块   *  每个数据块的大小
                size = statFs.getFreeBlocks() *statFs.getBlockSize();//字节
            }

            //可用控件的大小必须要大于10MB,则表示可以使用
            //1KB = 1024B
            //1MB = 1024KB
            //1G = 1024MB
            if(size>10*1024*1024)
            {
                return true;
            }
        }
        return false;
    }

    /**
     * 向sd的公共目录中, 存入byte[] 数据
     * @param fileName   文件名称
     * @param data		 文件内容
     * @return
     */
    public static boolean saveBytePublicDir(String fileName,byte[] data)
    {
        try {

            //1, 判断当前SD卡是否可用
            if(isMounted())
            {
                //2, 判断当前缓存的目录是否存在
                File dir = new File(PATH);
                if(!dir.exists())
                {
                    dir.mkdirs();//级联创建
                }

                //3, 将byte类型的内容, 写入到fileName对应的文件中
                FileOutputStream fos = new FileOutputStream(new File(dir, fileName));

                fos.write(data);

                fos.close();

                return  true;

            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return false;
    }

    /**
     * 向sd的公共目录中, 存入Bitmap 数据
     * @param fileName
     * @param bitmap
     * @return
     */
    public static boolean saveBitmapPublicDir(String fileName,Bitmap bitmap)
    {
        BufferedOutputStream bos = null;
        try {

            if(isMounted())
            {
                //得到目录
                File dir = new File(PATH);
                if(!dir.exists())
                {
                    dir.mkdirs();
                }

                bos = new BufferedOutputStream(new FileOutputStream(new File(dir, fileName)));

                //将Bitmap写入指定的文件中(压缩处理)
                if(fileName!=null && (fileName.contains(".png") || fileName.contains(".PNG")))
                {
                    /**
                     * format   存储图片的格式
                     * quality	比例
                     * stream	输出流
                     */
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                }else
                {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                }

                return true;
            }
        } catch (Exception e) {
            // TODO: handle exception
        }

        return false;
    }
    /**
     * 向sd卡中读取数据
     * @param fileName  文件名称
     * @return byte[]
     */
    public static byte[] getBytePublicDir(String fileName)
    {
        BufferedInputStream bis = null;
        ByteArrayOutputStream baos =null;
        try {
            //1, 判断sd卡的状态
            if(isMounted())
            {
                baos = new ByteArrayOutputStream();

                File file = new File(PATH, fileName);

                if(file.exists())//判断该图片是否存在
                {
                    FileInputStream fis = new FileInputStream(file);

                    bis = new BufferedInputStream(fis);

                    byte[] buffer = new byte[1024];
                    int len = 0;

                    while ((len = bis.read(buffer))!=-1) {

                        baos.write(buffer, 0, len);
                        baos.flush();
                    }

                    return baos.toByteArray();
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }finally
        {
            if(bis!=null)
            {
                try {
                    bis.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if(baos!=null)
            {
                try {
                    baos.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }


        return null;
    }


    /**
     * 向sd的私有目录中,    存入byte[] 数据
     * @param context 	上下文对象
     * @param fileName  文件名称
     * @param data  	文件内容
     * @return
     */
    public static boolean saveBytePrivateDir(Context context, String fileName, byte[] data)
    {
        FileOutputStream fos = null;

        try {

            if(isMounted())
            {
                //context.getExternalCacheDir()  缓存目录
                //SD卡私有目录的根目录   context.getExternalFilesDir(null)
                File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName);

                fos = new FileOutputStream(file);

                fos.write(data);

                return true;
            }


        } catch (Exception e) {

            e.printStackTrace();
        }finally
        {
            if(fos!=null)
            {
                try {
                    fos.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        return false;
    }


    /**
     * 向sd的私有目录中, 存入Bitmap 数据
     * @param fileName
     * @param bitmap
     * @return
     */
    public static boolean saveBitmapPrivateDir(Context context,String fileName,Bitmap bitmap)
    {
        BufferedOutputStream bos = null;
        try {

            if(isMounted())
            {
                File file = new File(context.getExternalFilesDir(null), fileName);

                bos = new BufferedOutputStream(new FileOutputStream(file));

                //将Bitmap写入指定的文件中(压缩处理)
                if(fileName!=null && (fileName.contains(".png") || fileName.contains(".PNG")))
                {
                    /**
                     * format   存储图片的格式
                     * quality	比例
                     * stream	输出流
                     */
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                }else
                {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                }

                return true;
            }
        } catch (Exception e) {
            // TODO: handle exception
        }

        return false;
    }
    /**
     * 从sd卡的私有目录中读取内容
     * @param context     上下文对象
     * @param fileName		文件名称
     * @return		文件内容
     */
    public static byte[] getBytePrivateDir(Context context,String fileName)
    {
        BufferedInputStream bis = null;
        ByteArrayOutputStream baos =null;
        try {
            //1, 判断sd卡的状态
            if(isMounted())
            {
                baos = new ByteArrayOutputStream();

                File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName);

                if(file.exists())//判断该图片是否存在
                {
                    FileInputStream fis = new FileInputStream(file);

                    bis = new BufferedInputStream(fis);

                    byte[] buffer = new byte[1024];
                    int len = 0;

                    while ((len = bis.read(buffer))!=-1) {

                        baos.write(buffer, 0, len);
                        baos.flush();
                    }

                    return baos.toByteArray();
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }finally
        {
            if(bis!=null)
            {
                try {
                    bis.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if(baos!=null)
            {
                try {
                    baos.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }


        return null;
    }

    /**
     * 向sd卡缓存目录中 存入byte[] 数据
     * @param context	上下文对象
     * @param fileName	文件名称
     * @param data		文件内容
     * @return
     */
    public static boolean saveBytePrivateCache(Context context,String fileName,byte[] data)
    {
        BufferedOutputStream bos = null;

        try {

            //1, 判断当前sd卡是否可用
            if(isMounted())
            {
                //2, 路径
                //context.getExternalCacheDir()  缓存目录
                //storage/sdcard/Android/data/应用程序包名/cache/....
                File file = new File(context.getExternalCacheDir(), fileName);

                bos = new BufferedOutputStream(new FileOutputStream(file));

                bos.write(data,0,data.length);

                bos.flush();

                return true;
            }

        } catch (Exception e) {
            // TODO: handle exception
        }
        finally
        {
            if(bos!=null)
            {
                try {
                    bos.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        return false;
    }


    /**
     * 向sd的缓存目录中, 存入Bitmap 数据
     * @param fileName
     * @param bitmap
     * @return
     */
    public static boolean saveBitmapPrivateCache(Context context,String fileName,Bitmap bitmap)
    {
        BufferedOutputStream bos = null;
        try {

            if(isMounted())
            {
                File file = new File(context.getExternalCacheDir(), fileName);

                bos = new BufferedOutputStream(new FileOutputStream(file));

                //将Bitmap写入指定的文件中(压缩处理)
                if(fileName!=null && (fileName.contains(".png") || fileName.contains(".PNG")))
                {
                    /**
                     * format   存储图片的格式
                     * quality	比例
                     * stream	输出流
                     */
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                }else
                {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                }

                return true;
            }
        } catch (Exception e) {
            // TODO: handle exception
        }

        return false;
    }
    /**
     * 读取缓存目录中的内容
     * @param context    上下文对象
     * @param fileName   文件名称
     * @return			 文件的内容
     */
    public static byte[] getBytePrivateCache(Context context,String fileName)
    {
        BufferedInputStream bis = null;
        ByteArrayOutputStream baos =null;
        try {
            //1, 判断sd卡的状态
            if(isMounted())
            {
                baos = new ByteArrayOutputStream();

                File file = new File(context.getExternalCacheDir(), fileName);

                if(file.exists())//判断该图片是否存在
                {
                    FileInputStream fis = new FileInputStream(file);

                    bis = new BufferedInputStream(fis);

                    byte[] buffer = new byte[1024];
                    int len = 0;

                    while ((len = bis.read(buffer))!=-1) {

                        baos.write(buffer, 0, len);
                        baos.flush();
                    }

                    return baos.toByteArray();
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }finally
        {
            if(bis!=null)
            {
                try {
                    bis.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if(baos!=null)
            {
                try {
                    baos.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }


        return null;
    }

    /**
     * 清除公共目录中的所有缓存的文件
     */
    public static void clearPublicFile()
    {
        if(isMounted())
        {
            //得到路径
            File file = new File(PATH);

            if(file.exists())
            {
                File[] files = file.listFiles();//列出指定目录中的所有文件

                for(File f:files)
                {
                    f.delete();//删除文件
                }
            }
        }
    }


    /**
     * 返回文件的路径+文件的名称 的路径
     *
     */
    public static File getFile(String fileName)
    {
        File file = new File(PATH,fileName);

        return file;
    }


}

