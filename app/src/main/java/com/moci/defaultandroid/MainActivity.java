package com.moci.defaultandroid;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.fonts.FontStyle;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.moci.defaultandroid.util.mDialog;
import com.moci.defaultandroid.util.mImg;
import com.moci.defaultandroid.util.mLog;
import com.moci.defaultandroid.util.mTool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "main";
    public static final int GALLERY_CODE = 101;
    private mDialog dialog;
    private ProgressDialog mProgressDialog;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initWidgt();
    }

    private Button up;
    private Button down;
    private Button left;
    private Button right;
    private Button swcPic;
    private ImageView imageView;
    private TextView logPreview;
    private File file;

    private float x = 0;
    private float y = 0;
    protected void initData(){
        dialog = new mDialog(this);
        mHandler = new Handler();

        String fileName = mTool.getTime()+"_log.txt";
        String filePath = "/storage/emulated/0/defaultAndroid/";

        File tfile = new File(filePath);
        if (!tfile.exists()) {
            tfile.mkdir();
        }

        file = new File(filePath, fileName);

    }

    protected void initWidgt(){

        imageView = findViewById(R.id.imageView);
        logPreview = findViewById(R.id.logPreview);

        up = findViewById(R.id.up);
        down = findViewById(R.id.down);
        left = findViewById(R.id.left);
        right = findViewById(R.id.right);
        swcPic = findViewById(R.id.swcPic);

        x = imageView.getX();
        y = imageView.getY();

        logPreview.setMovementMethod(new ScrollingMovementMethod());
        up.setOnClickListener(this);
        down.setOnClickListener(this);
        left.setOnClickListener(this);
        right.setOnClickListener(this);
        swcPic.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.up:
                y = y-10;
                imageView.setY(y);
                break;
            case R.id.down:
                y = y+10;
                imageView.setY(y);
                break;
            case R.id.left:
                x = x-10;
                imageView.setX(x);
                break;
            case R.id.right:
                x = x+10;
                imageView.setX(x);
                break;
            case R.id.swcPic:
                getPicture();
                break;
        }

        wlog("x = "+x+" y = "+y);
    }
    String content = "";

    protected void wlog(String rawMsg){
        String msg = rawMsg+"\n";
        try {
            FileOutputStream fos = new FileOutputStream(file,true);
            fos.write(msg.getBytes());
            fos.close();

            InputStream instream = new FileInputStream(file);
            content = "";
            if (instream != null) {
                InputStreamReader inputreader
                        = new InputStreamReader(instream, "UTF-8");
                BufferedReader buffreader = new BufferedReader(inputreader);
                String line = "";
                //分行读取
                while ((line = buffreader.readLine()) != null) {
                    content += line + "\n";
                }
                instream.close();//关闭输入流
            }
            // 主线程设置log
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    logPreview.setText(content);
                    // 自动滚动到最后一行
                    int offset=logPreview.getLineCount()*logPreview.getLineHeight();
                    if(offset>logPreview.getHeight()){
                        logPreview.scrollTo(0,offset-logPreview.getHeight());
                    }
                }
            });
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    Bitmap bitmap;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK){
            return;
        }
        if(requestCode == 1){
            if (data != null) {
                //判断手机系统版本号
                if (Build.VERSION.SDK_INT >= 19) {
                    //4.4及以上系统使用这个方法处理图片
                    bitmap = mImg.handleImageOnKitKat(this, data);
                } else {
                    //4.4以下系统使用这个方法处理图片
                    bitmap = mImg.handleImageBeforeKitKat(this, data);
                }
            }
            imageView.setImageBitmap(bitmap);
        }
    }

    private void getPicture(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_GALLERY
        startActivityForResult(intent, 1);
    }
}
