package com.moci.defaultandroid.util;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.TypedValue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * @author : Cygnusmoci
 * @create : 2020-01-08 10:50
 * @description :通过handleImageOnKitKat方法返回由系统相册选择的图片
 */
public class mImg {
    private static String mImagePath = "";

    //4.4及以上系统使用这个方法处理图片
    @TargetApi(19)
    public static Bitmap handleImageOnKitKat(Context context, Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(context, uri)) {
            //如果是document类型的Uri,则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];  //解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(context, contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            //如果不是document类型的Uri,则使用普通方式处理
            imagePath = getImagePath(context, uri, null);
        }
        mImagePath = imagePath;
        return proccessImage(imagePath);
    }

    //4.4以下系统使用这个方法处理图片
    public static Bitmap handleImageBeforeKitKat(Context context, Intent data) {
        Uri uri = data.getData();
        mImagePath = getImagePath(context, uri, null);
        return proccessImage(mImagePath);
    }

    public static String getImagePath(Context context, Uri uri, String selection) {
        String path = null;
        //通过Uri和selection来获取真实的图片路径
        Cursor cursor = context.getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    //对bitmap进行质量压缩
    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {    //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    public static Bitmap decodeResource(final String name){
        InputStream inputStream = new InputStream() {
            @Override
            public int read() throws IOException {
                String fileName = "/res/drawable/";
                getClass().getResourceAsStream(fileName+name);
                return -1;
            }
        };
        Bitmap res = BitmapFactory.decodeStream(inputStream);
        return res;
    }

    /**
     * 解析bitmap资源，不会缩放
     * @param resources
     * @param id
     * @return
     */
    public static Bitmap decodeResource(Resources resources, int id) {
        TypedValue value = new TypedValue();
        resources.openRawResource(id, value);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inTargetDensity = value.density;
        return BitmapFactory.decodeResource(resources, id, opts);
    }

    public static Bitmap proccessImage(String srcPath){
        // 取得图片旋转角度
        int angle = readPictureDegree(srcPath);

        Bitmap bit = getImage(srcPath);

        Bitmap bitmap = rotaingImageView(angle, bit);

        return bitmap;
    }

    //传入图片路径，返回压缩后的bitmap
    public static Bitmap getImage(String srcPath) {
        if (TextUtils.isEmpty(srcPath))  //如果图片路径为空 直接返回
            return null;

        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);//此时返回bm为空

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;//这里设置高度为800f
        float ww = 480;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
    }

    /**
     * 读取照片旋转角度
     *
     * @param path 照片路径
     * @return 角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 旋转图片
     * @param angle 被旋转角度
     * @param bitmap 图片对象
     * @return 旋转后的图片
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        Bitmap returnBm = null;
        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bitmap;
        }
        if (bitmap != returnBm) {
            bitmap.recycle();
        }
        return returnBm;
    }

    /**
     * 处理非偶数长款图片
     * @param bitmap
     * @return
     */
    public static Bitmap getEvenWidthHeightBitmap(Bitmap bitmap){
        Bitmap returnBm = null;
        int inputWidth = bitmap.getWidth();
        int inputHeight = bitmap.getHeight();
        int relWidth = inputWidth;
        int relHeight = inputHeight;
        if(inputWidth % 2 == 1){
            relWidth = inputWidth- 1;
        }
        if(inputHeight % 2 == 1){
            relHeight = inputHeight - 1;
        }
        float scaleWidth = ((float) relWidth) / inputWidth;
        float scaleHeight = ((float) relHeight) / inputHeight;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth,scaleHeight);
        try {
            returnBm = Bitmap.createBitmap(bitmap, 0, 0, inputWidth, inputHeight, matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bitmap;
        }
        if (bitmap != returnBm) {
            bitmap.recycle();
        }
        return returnBm;
    }

    public static byte[] bitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imgBytes = baos.toByteArray();
        return imgBytes;
    }

    /**
     * RGB 解析
     * @param image
     * @return
     */
    public static byte[] getPixelsRGB(Bitmap image){
        int bytes = image.getByteCount();  //返回可用于储存此位图像素的最小字节数
        ByteBuffer buffer = ByteBuffer.allocate(bytes); //  使用allocate()静态方法创建字节缓冲区
        image.copyPixelsToBuffer(buffer); // 将位图的像素复制到指定的缓冲区
        byte[] rgba = buffer.array();
        byte[] pixels = new byte[(rgba.length / 4) * 3];
        int count = rgba.length / 4;
        //Bitmap像素点的色彩通道排列顺序是RGBA
        for (int i = 0; i < count; i++) {
            pixels[i * 3] = rgba[i * 4];        //R
            pixels[i * 3 + 1] = rgba[i * 4 + 1];    //G
            pixels[i * 3 + 2] = rgba[i * 4 + 2];       //B
        }
        return pixels;
    }

    /**
     * Gray 解析
     * @param bitmap
     * @return
     */
    public static byte[] getPixelsGray(Bitmap bitmap){
        if (bitmap == null)
            return null;

        byte[] ret = new byte[bitmap.getWidth() * bitmap.getHeight()];
        for (int j = 0; j < bitmap.getHeight(); ++j)
            for (int i = 0; i < bitmap.getWidth(); ++i) {
                int pixel = bitmap.getPixel(i, j);
                int red = ((pixel & 0x00FF0000) >> 16);
                int green = ((pixel & 0x0000FF00) >> 8);
                int blue = pixel & 0x000000FF;
                ret[j * bitmap.getWidth() + i] = (byte) ((299 * red + 587 * green + 114 * blue) / 1000);
            }
        return ret;
    }

    /**
     * RGBA 解析
     * @param image
     * @return
     */
    public static byte[] getPixelsRGBA(Bitmap image) {
        // calculate how many bytes our image consists of
        int bytes = image.getByteCount();

        ByteBuffer buffer = ByteBuffer.allocate(bytes); // Create a new buffer
        image.copyPixelsToBuffer(buffer); // Move the byte data to the buffer

        byte[] temp = buffer.array(); // Get the underlying array containing the data.

        byte[] pixels = new byte[temp.length]; // Allocate for RGBA

        // Copy pixels into place
        for (int i = 0; i < (temp.length / 4); i++) {
            pixels[i * 4 + 0] = temp[i * 4 + 0];       //R
            pixels[i * 4 + 1] = temp[i * 4 + 1];       //G
            pixels[i * 4 + 2] = temp[i * 4 + 2];       //B
            pixels[i * 4 + 3] = temp[i * 4 + 3];       //A
        }

        return pixels;
    }


    /**
     * bitmap解析YUV21
     * @param bitmap
     * @return
     */
    public static byte[] convertYUV21FromRGB(Bitmap bitmap,int rotation){
        bitmap = rotaingImageView(rotation, bitmap);
        bitmap = getEvenWidthHeightBitmap(bitmap);
        int inputWidth = bitmap.getWidth();
        int inputHeight = bitmap.getHeight();

        int[] argb = new int[inputWidth * inputHeight];

        bitmap.getPixels(argb, 0, inputWidth, 0, 0, inputWidth, inputHeight);

        byte[] yuv = new byte[inputWidth * inputHeight * 3 / 2];

        encodeYUV420SP(yuv, argb, inputWidth, inputHeight);

        bitmap.recycle();

        return yuv;

    }

    public static byte[] convertYUV21FromRGB(Bitmap bitmap){
        return  convertYUV21FromRGB(bitmap,0);
    }


    private static void encodeYUV420SP(byte[] yuv420sp, int[] argb, int width, int height) {
        final int frameSize = width * height;

        int yIndex = 0;
        int uvIndex = frameSize;
        int maxSize = yuv420sp.length;
        int a, R, G, B, Y, U, V;
        int index = 0;
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {

                a = (argb[index] & 0xff000000) >> 24; // a is not used obviously
                R = (argb[index] & 0xff0000) >> 16;
                G = (argb[index] & 0xff00) >> 8;
                B = (argb[index] & 0xff) >> 0;

                // well known RGB to YUV algorithm
                Y = ((66 * R + 129 * G + 25 * B + 128) >> 8) + 16;
                U = ((-38 * R - 74 * G + 112 * B + 128) >> 8) + 128;
                V = ((112 * R - 94 * G - 18 * B + 128) >> 8) + 128;

                // NV21 has a plane of Y and interleaved planes of VU each sampled by a factor of 2
                //    meaning for every 4 Y pixels there are 1 V and 1 U.  Note the sampling is every other
                //    pixel AND every other scanline.
                yuv420sp[yIndex++] = (byte) ((Y < 0) ? 0 : ((Y > 255) ? 255 : Y));


                if (j % 2 == 0 && index % 2 == 0) {
                    yuv420sp[uvIndex++] = (byte) ((V < 0) ? 0 : ((V > 255) ? 255 : V));
                    yuv420sp[uvIndex++] = (byte) ((U < 0) ? 0 : ((U > 255) ? 255 : U));
                }
                index++;
                if(uvIndex >= maxSize) return;
            }
        }
    }

    /**
     * BGR 解析
     * @param image
     * @return
     */
    public static byte[] getPixelsBGR(Bitmap image) {
        // calculate how many bytes our image consists of
        int bytes = image.getByteCount();

        ByteBuffer buffer = ByteBuffer.allocate(bytes); // Create a new buffer
        image.copyPixelsToBuffer(buffer); // Move the byte data to the buffer

        byte[] temp = buffer.array(); // Get the underlying array containing the data.

        byte[] pixels = new byte[(temp.length/4) * 3]; // Allocate for BGR

        // Copy pixels into place
        for (int i = 0; i < temp.length/4; i++) {

            pixels[i * 3] = temp[i * 4 + 2];		//B
            pixels[i * 3 + 1] = temp[i * 4 + 1]; 	//G
            pixels[i * 3 + 2] = temp[i * 4 ];		//R

        }

        return pixels;
    }

    /**
     * yuv生成Bitmap(rgb格式的)
     * @param data
     * @param width
     * @param height
     * @return
     */
    public static Bitmap getYUVBitmap(byte[] data,int width,int height){
        try {
            YuvImage image = new YuvImage(data, ImageFormat.NV21, width, height, null);
            if(image!=null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                image.compressToJpeg(new Rect(0, 0, width, height), 100, stream);
                Bitmap bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
                //TODO：此处可以对位图进行处理，如显示，保存等
                stream.close();
                return bmp;
            }
        }catch (IOException e){
            return null;
        }
        return null;
    }

    public static void write(byte[] mImage, File mImageFile){
        try {
            FileOutputStream fos = new FileOutputStream(mImageFile);

            if(mImage != null) fos.write(mImage);
            fos.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
