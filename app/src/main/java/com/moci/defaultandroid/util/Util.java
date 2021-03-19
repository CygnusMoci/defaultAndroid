package com.moci.defaultandroid.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author : Cygnusmoci
 * @create : 2020-01-09 16:53
 * @description :
 */
public class Util {
    public static byte[] img;
    /**
     * 获取时间
     * @return
     */
    public static String getTime(){
        SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd_HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        return str;
    }

    /**
     * 剪切子串
     * @param str 父串
     * @param subStr 子串
     * @param time 次数
     * @return
     */
    public static String subString(String str,String subStr,int time){
        StringBuffer stb = new StringBuffer(str);
        int index;
        for (int i = 0; i < time; i++) {
            index = stb.lastIndexOf(subStr);
            stb = new StringBuffer(stb.substring(index));
        }
        stb = new StringBuffer(stb.substring(subStr.length()));
        return stb.toString();
    }

    /**
     * 读取assets里的二进制文件
     * @param context
     * @param file
     * @return
     */
    public static byte[] readAssets(Context context, String file){
        try {
            InputStream is = context.getAssets().open(file);
            int lenght = is.available();
            byte[] buffer = new byte[lenght];
            is.read(buffer);
            is.close();
            return buffer;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获得dir中所有文件的实例
     * @param dirName
     * @return
     */
    public static File[] getfiles(String dirName){
        String dirPath = "/sdcard/"+dirName+"/";
        File file = new File(dirPath);
        if (!file.exists()) {
            file.mkdir();
        }

        File files[] = new File(dirPath).listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return true;
            }
        });

        return files;
    }

    /**
     * 保存bitmap 以RGBA格式
     * @param bitmap
     * @param bitmapFileName
     * @param dirName
     * @return
     */
    public static String saveBitmap(Bitmap bitmap, String bitmapFileName, String dirName){
        String dirPath = "/sdcard/"+dirName+"/";
        File file = new File(dirPath);
        if (!file.exists()) {
            file.mkdir();
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(dirPath + bitmapFileName);
            boolean successful = bitmap.compress(
                    Bitmap.CompressFormat.JPEG,100, fos);
            fos.close();
            if (successful){
                return dirPath + bitmapFileName;
            }
            else
                return "fail";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "-1";
    }

    /**
     * file转bitmap
     * @param file
     * @return
     */
    public static Bitmap file2Btimap(File file){
        Bitmap res = BitmapFactory.decodeFile(file.toString());
        return res;
    }
}
