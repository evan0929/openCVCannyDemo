package com.popperevan.opencvtest;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DrawableUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import tech.huqi.smartopencv.SmartOpenCV;
import tech.huqi.smartopencv.core.preview.CameraConfiguration;

import static org.opencv.android.CameraBridgeViewBase.CAMERA_ID_BACK;
import static org.opencv.imgproc.Imgproc.LINE_4;

public class CameraActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2{
    private static final String TAG="CameraActivity====";
    private JavaCameraView javaCameraView;
    private Context mContext;
    private Mat mRgb,mRgbPre;
    private Mat result;
    private Button mCapture;
    private ImageView mCaptuerView;
    private ImageView mScanView,mCannyView;
    int x1 = 50;
    int y1 = 100;
    int y2 = 100;
    int x2=50, mCount = 1;
    private Handler mHandler;
    private Runnable mRunnable;
    private BaseLoaderCallback baseLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status){
                case LoaderCallbackInterface.SUCCESS:{
                    Log.e(TAG,"opecv 调用成功，可以打开预览");
                    javaCameraView.enableView();
                    mRgb = new Mat();
                    mRgbPre = new Mat();
                    result = new Mat();
                }
                break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        mContext=this;
        javaCameraView = (JavaCameraView) findViewById(R.id.javaCameraView);
        javaCameraView.setVisibility(SurfaceView.VISIBLE);
        javaCameraView.setCameraIndex(CAMERA_ID_BACK);
        javaCameraView.setCameraPermissionGranted();
        javaCameraView.setCvCameraViewListener(this);
        initSmartOpenCV();
        mCapture=findViewById(R.id.capture);
        mCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 1.调用opencv拍照 2。图片灰度 3。图片高斯模糊 4。图片边缘检测 5。图片下拉动画
            capture();
            }
        });
        mCaptuerView=findViewById(R.id.result_view);
        mScanView=findViewById(R.id.scan_view);
        mCannyView=findViewById(R.id.canny_view);
    }

    /**
     * 调用拍照储存
     */
    private void capture(){

        if(!mRgbPre.empty()&&mRgbPre!=null) {
            Log.e(TAG,"mRgbPre.empty===false");
            //关闭相机预览，关闭预览视图
            javaCameraView.disableView();
            javaCameraView.setVisibility(View.GONE);
            mCapture.setVisibility(View.GONE);
            //显示拍照图片
            mCaptuerView.setVisibility(View.VISIBLE);
            Bitmap bitmap = Bitmap.createBitmap(mRgbPre.width(), mRgbPre.height(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(mRgbPre,bitmap);
            mCaptuerView.setImageBitmap(bitmap);
            //灰度化
            mat2Gray(mRgbPre);
        }else{
            Log.e(TAG,"mRgbPre.empty===true");
        }
    }

    /**
     *
     *
     * @param mat
     * @return
     */
    public void mat2Gray(Mat mat) {
        Mat resultGray = new Mat();
        Mat resultBlur = new Mat();
        Mat resultCanny = new Mat();
        Mat resultContours = new Mat();
        List<MatOfPoint>contours=new ArrayList<>();

        // 灰度化
        Imgproc.cvtColor(mat, resultGray, Imgproc.COLOR_BGRA2GRAY, 1);

//        Utils.matToBitmap(grayMat, srcBitmap);
        //高斯模糊
        Imgproc.GaussianBlur(resultGray, resultBlur, new Size(3.0, 3.0), 5.0);
        //canny算子边缘检测
        Imgproc.Canny(resultBlur, resultCanny, 100.0, 200.0);
        //检测的边框
        Imgproc.findContours(resultCanny,contours,resultContours,3,2, new Point(0,0));
        //在原图进行描边
        if (contours.size()>0){
            Scalar scalar=new Scalar(155, 240, 250, 255);
            Imgproc.drawContours(mat,contours,-1,scalar,-1,LINE_4,resultContours,2,new Point(0,0));
        }
        //mat转换为bitmap
        final Bitmap bitmap = Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat,bitmap);
        //描边后的bitmap进行动画
        final Animation alphaAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.view_anim_translate);
        alphaAnim.setDuration(2000);
        alphaAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mScanView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mScanView.setVisibility(View.VISIBLE);
        mCannyView.setVisibility(View.VISIBLE);
        final ClipDrawable[] drawable = new ClipDrawable[2];
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final ClipDrawable imageDrawable = new ClipDrawable(new BitmapDrawable(getResources(),bitmap), Gravity.TOP | Gravity.START, ClipDrawable.VERTICAL);
                mCannyView.setImageDrawable(imageDrawable);
                drawable[0] = (ClipDrawable) mCannyView.getDrawable();
                drawable[1] = (ClipDrawable) mScanView.getDrawable();
            }
        });
//
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                //如果该消息是本程序所发送的
                if (msg.what == 1) {
                    //修改ClipDrawable的level值
                    drawable[0].setLevel(drawable[0].getLevel() + 148);
                    drawable[1].setLevel(drawable[1].getLevel() + 148);
                }
            }
        };
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = 1;
                //发送消息，通知应用修改ClipDrawable对象的level值
                handler.sendMessage(msg);
                //取消定时器
                if (drawable[0].getLevel() >= 10000) {
                    timer.cancel();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mScanView.setVisibility(View.GONE);
                        }
                    });
                }
            }
        }, 0, 30);
    }

    /**
     * 初始化smartopencv 可不用
     */
    private void initSmartOpenCV() {
        SmartOpenCV.getInstance().init(javaCameraView, new CameraConfiguration.Builder()
                .debug(false)
                .cameraIndex(CAMERA_ID_BACK)      // 设置摄像头索引,主要用于多摄像头设备，优先级低于frontCamera
                .keepScreenOn(false) // 是否保持屏幕常亮
                .frontCamera(false)   // 是否使用前置摄像头
                .openCvDefaultDrawStrategy(false)      // 是否使用OpenCV默认的预览图像绘制策略
                .openCvDefaultPreviewCalculator(false) // 是否使用OpenCV默认的预览帧大小计算策略
                .landscape(false)     // 是否横屏显示
                .enableFpsMeter(true) // 开启预览帧率的显示
//                .usbCamera(false)     // 是否使用USB摄像头，当设备接入的是USB摄像头时将其设置为true
//                .maxFrameSize(400, 300)     // 设置预览帧的最大大小
//                .cvCameraViewListener(this) // 设置OpenCV回调监听器
//                .previewSizeCalculator(new IPreviewSizeCalculator() { // 自定义预览帧大小计算策略
//                    @Override
//                    public Size calculateCameraFrameSize(List<Size> supportedSizes, int surfaceWidth, int surfaceHeight) {
//                        // 若需要根据自己的具体业务场景改写览帧大小，覆写该方法逻辑
//                        ScreenPropertyBean bean=SysUtils.getAndroiodScreenProperty(mContext);
//                        return new Size(bean.getWidth(),bean.getHeight());
//                    }
//                })
//                .drawStrategy(new IDrawStrategy() { // 自定义绘制策略
//                    @Override
//                    public void drawBitmap(Canvas canvas, Bitmap frameBitmap, int surfaceWidth, int surfaceHeight) {
//                        // 若需根据自己的具体业务场景绘制预览帧图像，覆写该方法逻辑
//                    }
//                })
                .build());
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (javaCameraView != null)
            javaCameraView.disableView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()){
            Log.e(TAG,"opecv OpenCVLoader.initDebug is false");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION,this,baseLoaderCallback);
        }else{
            Log.e(TAG,"opecv OpenCVLoader.initDebug is true todo managerConnect");
            baseLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (javaCameraView != null)
            javaCameraView.disableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        Log.e(TAG,"onCameraViewStarted");

    }

    @Override
    public void onCameraViewStopped() {
        Log.e(TAG,"onCameraViewStopped");

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgbPre=inputFrame.rgba();
        return mRgbPre;
    }

}