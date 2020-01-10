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
}
