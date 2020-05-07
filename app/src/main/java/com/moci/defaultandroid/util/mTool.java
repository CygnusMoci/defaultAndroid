package com.moci.defaultandroid.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author : Cygnusmoci
 * @create : 2020-01-09 16:53
 * @description :
 */
public class mTool {
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
}
