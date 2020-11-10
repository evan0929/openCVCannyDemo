package com.popperevan.opencvtest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ClipDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity2 extends AppCompatActivity {
    private ImageView mONe,mTwo;
    private Button test;
    double multiple=0;
    Bitmap bitmap;
    Handler handler;
    private final Timer timer = new Timer();
    private TimerTask task;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main2);
        mONe=findViewById(R.id.img_one);
        mTwo=findViewById(R.id.img_two);
        mTwo.setVisibility(View.GONE);
        test=findViewById(R.id.test);
        bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.test);
        final int height=bitmap.getHeight();
        final int unit=height/40;

        Log.e("=============","height:"+height+"unit:"+unit);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (multiple<(height-unit)){
                    Log.e("=============","multiple_one:"+multiple);
                    multiple+=unit;
                    Log.e("=============","multiple_two:"+multiple);
                    if (multiple>=(height-unit)){
                        multiple=(height-unit);
                        Log.e("=============","multiple_3:"+multiple);
                    }
                    animation(multiple);
                }else {
                    Log.e("=============","multiple_4:"+multiple);
                    if (timer!=null){
                        timer.cancel();
                    }
                }
                super.handleMessage(msg);
            }
        };



        final ClipDrawable drawable = (ClipDrawable) mONe.getDrawable();
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                //如果该消息是本程序所发送的
                if (msg.what == 0x1233) {
                    //修改ClipDrawable的level值
                    drawable.setLevel(drawable.getLevel() + 200);
                }
            }
        };
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = 0x1233;
                //发送消息，通知应用修改ClipDrawable对象的level值
                handler.sendMessage(msg);
                //取消定时器
                if (drawable.getLevel() >= 10000) {
                    timer.cancel();
                }
            }
        }, 0, 50);



        task = new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        };
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timer.schedule(task, 0, 500);
                mONe.setImageBitmap(bitmap);
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    private void animation(double multiple){
        int height= (int) (multiple);
        Bitmap bmp=Bitmap.createBitmap(bitmap.getWidth(),height,Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(bmp);
        canvas.drawBitmap(bitmap,0,0,null);
        mTwo.setVisibility(View.VISIBLE);
        mTwo.setImageBitmap(bmp);
        Log.e("=============","heights:"+bmp.getHeight()+"width:"+bmp.getWidth());
    }
}