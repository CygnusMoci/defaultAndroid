package com.moci.defaultandroid.util;

import android.media.MediaMetadataRetriever;
import android.util.Log;

import java.io.File;

/**
 * @author : Cygnusmoci
 * @create : 2020/3/20 6:05 PM
 * @description :
 */
public class InputVideo {
    public File videoFile =null;
    public byte[] video = null;
    public byte[][] videoArr = null;
    public int frame;
    public int height;
    public int width;
    public String decodeTime;
    public volatile static InputVideo instance = null;

    public static InputVideo getInstance() {
        if(instance == null){
            synchronized (InputVideo.class){
                if(instance == null) {
                    instance = new InputVideo();
                }
            }
        }
        return instance;
    }


    public byte[] getVideo(String path, int height, int width){
        this.videoFile = new File(path);
        this.video = FileUtil.getBytesFromFile(this.videoFile);

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        Long start = System.currentTimeMillis();
        retriever.setDataSource(path);
        Log.w("moci", "getVideo: "+(System.currentTimeMillis() - start)+"ms");
        // 取得视频的长度(单位为毫秒)
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        // 取得视频的长度(单位为秒)
        int seconds = Integer.valueOf(time) / 1000;
        int frameRate = 30;
        videoArr = new byte[frameRate*seconds][];
        Log.w("moci", "getVideo: "+videoArr.length);
        start = System.currentTimeMillis();
        for (int i = 0; i <videoArr.length; i++) {
            try {
                videoArr[i] = mImg.getPixelsYUV21(retriever.getFrameAtIndex(i));
            }catch (IllegalStateException e){
                Log.w("moci", "convertYUV21FromRGB: "+i);
                continue;
            }
        }
        frame = videoArr.length;
        this.decodeTime = (System.currentTimeMillis() - start)/1000+"";
        this.height = height;
        this.width = width;
        return this.video;
    }
}
