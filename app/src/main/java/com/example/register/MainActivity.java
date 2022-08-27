package com.example.register;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Config;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import com.example.register.PearsonUtil;


public class MainActivity extends AppCompatActivity {
    // canvas
    private Canvas mCanvas;
    private Paint mPaint = new Paint();
    private Paint mPaintText = new Paint(Paint.UNDERLINE_TEXT_FLAG);
    private Rect mRect = new Rect();
    private Rect mBounds = new Rect();
    private int mColorRectangle;
    private int mColorAccent;
    private Bitmap mBitmap;
    private ImageView mImageView;
    private Button mbtn1;
    private static final int OFFSET = 120;
    private int mOffset = OFFSET;
    private static final int MULTIPLIER = 100;
    private int mColorBackground;

    // linechart
    private LineChart line;
    public List<Entry> list=new ArrayList<>();
    public LineDataSet lineDataSet;
    public LineData lineData;
    public List<Float> mlist = new ArrayList<>();
    public List<Integer> xxx = new ArrayList<>();
    public boolean flag = false; // 判断手指是否在按下状态

    // x,y location
    public float mx = 0.0f;
    public float my = 10f;


    private double threshold = 0.9;
    // progress bar define
    private CircleLoadingView circleloading;


    public EditText pox,poY,condition;
    public TextView txt1;

    private Timer timer = new Timer();
    private TimerTask task;
    private final MyHandler mhandler = new MyHandler(this);
    private int cnt = -1; //有效帧记录
    public boolean is_succeed = true; //判断序列是否合格
    public int register_cnt = 0; //记录注册进度，与angles数组相关

    // angles define
//    public double[] angles = {0,18,-18,36,-36,54,-54,72,-72};
    public double[] angles = {-90,-67.5,-45,-22.5,0,22.5,45,67.5,90};

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        pox = (EditText)findViewById(R.id.editText1);
        poY = (EditText)findViewById(R.id.editText2);
        condition = (EditText)findViewById(R.id.editText3);
        pox.setVisibility(View.INVISIBLE);
        poY.setVisibility(View.INVISIBLE);
        condition.setVisibility(View.INVISIBLE);
//        rpb = (RoundProgressBar)findViewById(R.id.roundProgressBar);
        circleloading = findViewById(R.id.circleloading);
//        circleloading.setAnimation(80, 5000);
//        circleloading.setVisibility(View.INVISIBLE);
        mColorBackground = ResourcesCompat.getColor(getResources(),
                R.color.colorBackground, null);
        mColorRectangle = ResourcesCompat.getColor(getResources(),
                R.color.colorRectangle, null);
        mColorAccent = ResourcesCompat.getColor(getResources(),
                R.color.purple_500, null);
        mPaint.setColor(mColorAccent);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(50);
        mPaintText.setColor(
                ResourcesCompat.getColor(getResources(),
                        R.color.purple_200, null)
        );
        mPaintText.setTextSize(70);
        mImageView = (ImageView) findViewById(R.id.myimageview);
//        DisplayMetrics dm2 = getResources().getDisplayMetrics();
//        System.out.println("heigth2 : " + dm2.heightPixels);
//        System.out.println("width2 : " + dm2.widthPixels);
//        x = dm2.widthPixels;
//        y = dm2.heightPixels;
        txt1 = (TextView) findViewById(R.id.Text1);
        txt1.setVisibility(View.INVISIBLE);
        mbtn1 = (Button) findViewById(R.id.btn1);
        line = (LineChart) findViewById(R.id.chart_line);
        line.setDrawGridBackground(false);
        line.getDescription().setEnabled(false);
        for(int i=0;i<angles.length;i++){
            writeTxtToStorage("Download/rawdata"+String.valueOf(i)+".txt","",false);

        }

        mbtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int vWidth = mImageView.getWidth();
                int vHeight = mImageView.getHeight();
                int halfWidth = vWidth / 2;
                int halfHeight = vHeight / 2;
                Log.e("1",String.valueOf(vWidth));
                Log.e("2",String.valueOf(vHeight));
                if (mOffset == OFFSET) {
                    mBitmap = Bitmap.createBitmap(vWidth, vHeight, Bitmap.Config.ARGB_8888);
                    mImageView.setImageBitmap(mBitmap);
                    mCanvas = new Canvas(mBitmap);
//                    mCanvas.drawColor(mColorBackground);
//                    mCanvas.drawText(getString(R.string.keep_tapping), 100, 100, mPaintText);
                    int[] tmp = find_array_ex_ey_sx_sy();
                    drawAL(tmp[0],tmp[1],tmp[2],tmp[3]);
                    mOffset += OFFSET;
                }
                txt1.setVisibility(View.VISIBLE);
                writeTxtToStorage("Download/rawdata"+String.valueOf(register_cnt)+".txt","start "+String.valueOf(angles[register_cnt])+"\n",false);
                //开始计时
//                startTime();



                view.invalidate();
            }
        });
        mImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                mx = event.getX();
                my = event.getY();
                try
                {
                    switch(event.getAction())
                    {
                        case MotionEvent.ACTION_DOWN:
                            pox.setText(""+mx);
                            poY.setText(""+my);
                            condition.setText("down");
                            Log.e("down","down");
                            //
                            startTime();
                            circleloading.setAnimation(100, 2000);
                            //
                            flag = false;

                            break;
                        case MotionEvent.ACTION_UP:
                            pox.setText(""+mx);
                            poY.setText(""+my);
                            condition.setText("up");
                            Log.e("up","upup");
                            timer.cancel();//注销计时器事件
                            ///
                            flag = false; ////
                            double score = PearsonUtil.getPearsonCorrelationScore(mlist,xxx);
                            Log.e("pearson",String.valueOf(score));
                            Log.e("cnt",String.valueOf(cnt));
                            int start_seq = find_seq_start(mlist);
                            //TODO:is_succeed
                            is_succeed = correct_is_succeed(start_seq,mlist.size());
                            int duration = mlist.size() - start_seq;
                            Log.e("debug","this");

                            //判断数据是否采集成功
                            if (!is_succeed) {
                                new AlertDialog.Builder(MainActivity.this).setTitle("Info")//设置对话框标题

                                        .setMessage("invalid data collection.\n please register again!")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {//添加确定按钮

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                                                list=new ArrayList<>();
                                                mlist = new ArrayList<>();
                                                xxx = new ArrayList<>();
                                                cnt = -1;
                                                writeTxtToStorage("Download/rawdata"+String.valueOf(register_cnt)+".txt","start "+String.valueOf(angles[register_cnt])+"\n",false);

                                            }
                                        }).setNegativeButton("Quit", new DialogInterface.OnClickListener() {//添加返回按钮

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {//响应事件
                                        list=new ArrayList<>();
                                        mlist = new ArrayList<>();
                                        xxx = new ArrayList<>();
                                        cnt = -1;
                                        writeTxtToStorage("Download/rawdata"+String.valueOf(register_cnt)+".txt","start "+String.valueOf(angles[register_cnt])+"\n",false);
                                    }

                                }).show();//在按键响应事件中显示此对话框
                            }else{
                                new AlertDialog.Builder(MainActivity.this).setTitle("Info")//设置对话框标题

                                        .setMessage(String.format("collect %d timestamps,registration success!",duration))
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {//添加确定按钮,换下一个角度

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件
                                                int vWidth = mImageView.getWidth();
                                                int vHeight = mImageView.getHeight();
                                                mBitmap = Bitmap.createBitmap(vWidth, vHeight, Bitmap.Config.ARGB_8888);
                                                mImageView.setImageBitmap(mBitmap);
                                                mCanvas = new Canvas(mBitmap);
//                                                drawAL(600,800,800,400);

                                                list=new ArrayList<>();
                                                mlist = new ArrayList<>();
                                                xxx = new ArrayList<>();
                                                cnt = -1;
                                                writeTxtToStorage("Download/rawdata"+String.valueOf(register_cnt)+".txt",String.valueOf(start_seq),true);
                                                register_cnt += 1;
                                                writeTxtToStorage("Download/rawdata"+String.valueOf(register_cnt)+".txt","start "+String.valueOf(angles[register_cnt])+"\n",false);
                                                int[] tmp = find_array_ex_ey_sx_sy();
                                                drawAL(tmp[0],tmp[1],tmp[2],tmp[3]);
                                            }
                                        }).show();//在按键响应事件中显示此对话框
                            }


                            break;
                        case MotionEvent.ACTION_MOVE:
                            pox.setText(""+mx);
                            poY.setText(""+my);
                            condition.setText("move");
//                            Log.e("move","move");
                            flag = true;
                            Log.v("cnt",String.valueOf(cnt));




                            break;
                        default:


                            break;

                    }
                    return true;
                }
                catch(Exception e)
                {
                    Log.v("touch", e.toString());
                    return false;
                }
            }
        });

//        get_line_chart();


    }

    private class MyHandler extends android.os.Handler{
        private WeakReference<MainActivity> mActivity;
        MyHandler(MainActivity activity){
            mActivity = new WeakReference<MainActivity>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            cnt ++;
//            Log.v("defaultcnt",String.valueOf(cnt));
            if(angles[register_cnt] <= 45 && angles[register_cnt] >=-45){
                list.add(new Entry(cnt,my));
            }
            else{
                list.add(new Entry(cnt,mx));
            }

            //获取电容屏
            String v = adb_get_matrix();
            //写入电容屏文件,以及x,y坐标值
            String xy_location = String.valueOf(mx)+" "+String.valueOf(my)+" ";
            writeTxtToStorage("Download/rawdata"+String.valueOf(register_cnt)+".txt",xy_location,true);
            writeTxtToStorage("Download/rawdata"+String.valueOf(register_cnt)+".txt",v+"\n",true);

            if(angles[register_cnt] <= 45 && angles[register_cnt] >=-45) {
                lineDataSet = new LineDataSet(list, "y");
            }else{
                lineDataSet = new LineDataSet(list, "x");
            }
            lineDataSet.setColor(R.color.purple_500);
//            lineDataSet.setCircleColor(R.color.purple_500);
            lineDataSet.setDrawCircles(false);

            lineData=new LineData(lineDataSet);
            // f(x+h)-2f(x)+f(x-h)/h^2 差分求导


            if(flag== true){
                if(angles[register_cnt] <= 45 && angles[register_cnt] >=-45){
                    mlist.add(my);
                }
                else{
                    mlist.add(mx);
                }

                xxx.add(cnt);
            }


            //set line chart style
            lineData.setValueTextSize(0f);
            line.setData(lineData);

            line.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            line.getXAxis().setDrawAxisLine(true);
            line.getXAxis().setDrawGridLines(false);
            line.getXAxis().setDrawLabels(false);
            line.getAxisRight().setEnabled(false);
            line.getLegend().setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
            line.notifyDataSetChanged();//对图表数据进行更新
            line.invalidate();          //对图表的显示更新




        }
    }
    public void startTime(){
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                Message message = mhandler.obtainMessage();
                message.what = 1;
                mhandler.sendMessage(message);
            }
        };
        timer.schedule(task, 10,60);
    }
    public void stopTime(){
        cnt = 0;
        timer.cancel();//注销计时器事件
    };


    /**
     * 向内存中写文件
     * @param mFileName
     * @param mContent
     * @param flag
     */
    public void writeTxtToStorage(String mFileName,String mContent,boolean flag){

        File file = new File( Environment.getExternalStorageDirectory().getPath()+ "/" + mFileName);
        Log.e("1", Environment.getExternalStorageDirectory().getPath()+ "/" + mFileName);
        FileOutputStream fos = null;
        try {
            if (!file.exists()) {

                File parentDir = new File(file.getParent());
                if (!parentDir.exists()) {
                    parentDir.mkdirs();
                    file.createNewFile();
                }
            }

            fos = new FileOutputStream(file, flag);
            fos.write(mContent.getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }
    public int[] find_array_ex_ey_sx_sy(){
        int[] a = {0,0,0,0};
        double myangle = Math.toRadians(angles[register_cnt]);
        double w = Math.cos(myangle) * 200;
        double h = Math.sin(myangle) * 200;
        // (700,600) is the center point of the arrow
        int cx = 700;
        int cy = 600;
        a[0] = cx - (int)Math.floor(h);
        a[1] = cy + (int)Math.floor(w);
        a[2] = cx + (int)Math.floor(h);
        a[3] = cy - (int)Math.floor(w);
        return a;
    }
    /**
     * 判断该序列是否采集成功
     * @param start
     * @param n
     */
    public boolean correct_is_succeed(int start , int n){
        boolean v = false;
        if(start == -1){
            return false;
        }
        if(n>50){
            v = false;

        }else if(n-start<20){
            v = false;
        }else{
            v = true;
        }
        return v;

    }
    /**
     * 找到序列起始点，返回有效帧数
     * @param ml
     */
    public int find_seq_start(List<Float> ml){
        int n = ml.size();
        List<Float> x_axis = new ArrayList<>();
        float s = 0.0f;
        for(int j=0;j<n;j++){
            x_axis.add(s);
            s = s+1.0f;
        }
        Log.e("length of list",String.valueOf(n));
        for(int i=0;i<n-2;i++){
            List<Float> tmpa = new ArrayList<>(ml.subList(i,n));
            List<Float> tmpb = new ArrayList<>(ml.subList(i+1,n));
            List<Float> tmpc = new ArrayList<>(ml.subList(i+2,n));
            double sa = Math.abs(PearsonUtil.getPearsonCorrelationScore(tmpa,x_axis.subList(i,n)));
            double sb = Math.abs(PearsonUtil.getPearsonCorrelationScore(tmpb,x_axis.subList(i+1,n)));
            double sc = Math.abs(PearsonUtil.getPearsonCorrelationScore(tmpc,x_axis.subList(i+2,n)));
            Log.e("pearson value",String.format("%f %f %f",sa,sb,sc));
            if(sa >= 0.93  && (sb >= sa && sc >=sb) || sa >= 0.955){
                Log.e("find start of seq!",String.format("timestamp %d",i));
                return i;

            }

        }
        return -1;
    }
    /**
     *
     */
    public String adb_get_matrix(){
//        String mcmd = "/system/bin/screencap -p /storage/emulated/0/Pictures/test.png";

        try {

            // Executes the command.
            Process process = Runtime.getRuntime().exec("cat /proc/capacitive_matrix");

            // Reads stdout.
            // NOTE: You can write to stdin of the command using
            //       process.getOutputStream().
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            int read;
            char[] buffer = new char[4096];
            StringBuffer output = new StringBuffer();
            while ((read = reader.read(buffer)) > -1) {
                output.append(buffer, 0, read);
            }
            reader.close();

            // Waits for the command to finish.
            process.waitFor();

            Log.e("11",output.toString());
            return output.toString();
        } catch (IOException e) {

            throw new RuntimeException(e);

        } catch (InterruptedException e) {

            throw new RuntimeException(e);
        }finally {

        }





//        p.destroy();
    }

    /**
     * 判断是否有root权限
     * @return
     */
    public static boolean is_root() {
        boolean res = false;

        try {
            if ((!new File("/system/bin/su").exists()) &&

                    (!new File("/system/xbin/su").exists())) {
                res = false;

            } else {
                res = true;

            }
            ;

        } catch (Exception e) {
        }

        return res;
    }

    /**
     * 初始随便画画
     */
    public void get_line_chart(){

        list.add(new Entry(0,0));
        list.add(new Entry(1,10));
//        list.add(new Entry(2,12));
//        list.add(new Entry(3,6));
//        list.add(new Entry(4,3));

        lineDataSet=new LineDataSet(list,"y");
        lineData=new LineData(lineDataSet);
        line.setData(lineData);
    }




    /**
     * 画箭头
     * @param sx 原点x
     * @param sy 原点y
     * @param ex 终点x
     * @param ey 终点y
     */
    public void drawAL(int sx, int sy, int ex, int ey)
    {
        double H = 8; // 箭头高度
        double L = 3.5; // 底边的一半
        int x3 = 0;
        int y3 = 0;
        int x4 = 0;
        int y4 = 0;
        double awrad = Math.atan(L / H); // 箭头角度
        double arraow_len = Math.sqrt(L * L + H * H); // 箭头的长度
        double[] arrXY_1 = rotateVec(ex - sx, ey - sy, awrad, true, arraow_len);
        double[] arrXY_2 = rotateVec(ex - sx, ey - sy, -awrad, true, arraow_len);
        double x_3 = ex - arrXY_1[0]; // (x3,y3)是第一端点
        double y_3 = ey - arrXY_1[1];
        double x_4 = ex - arrXY_2[0]; // (x4,y4)是第二端点
        double y_4 = ey - arrXY_2[1];
        Double X3 = new Double(x_3);
        x3 = X3.intValue();
        Double Y3 = new Double(y_3);
        y3 = Y3.intValue();
        Double X4 = new Double(x_4);
        x4 = X4.intValue();
        Double Y4 = new Double(y_4);
        y4 = Y4.intValue();
        // 画线
        mCanvas.drawLine(sx, sy, ex, ey,mPaint);
        Path triangle = new Path();
        triangle.moveTo(ex, ey);
        triangle.lineTo(x3, y3);
        triangle.lineTo(x4, y4);
        triangle.close();
        mCanvas.drawPath(triangle,mPaint);

    }

    /**
     * // 计算
     * @param px x分量
     * @param py y分量
     * @param ang 旋转角
     * @param isChLen 是否改变长度
     * @param newLen 新长度
     * @return
     */

    public double[] rotateVec(int px, int py, double ang, boolean isChLen, double newLen)
    {
        double mathstr[] = new double[2];
        // 矢量旋转函数，参数含义分别是x分量、y分量、旋转角、是否改变长度、新长度
        double vx = px * Math.cos(ang) - py * Math.sin(ang);
        double vy = px * Math.sin(ang) + py * Math.cos(ang);
        if (isChLen) {
            double d = Math.sqrt(vx * vx + vy * vy);
            vx = vx / d * newLen;
            vy = vy / d * newLen;
            mathstr[0] = vx;
            mathstr[1] = vy;
        }
        return mathstr;
    }

    /**
     * 画一条直线
     * @param fromX 起点x坐标
     * @param fromY	起点Y坐标
     * @param toX	终点X坐标
     * @param toY	终点Y坐标
     */
    public void drawLine(float fromX ,float fromY,float toX,float toY){
        Path linePath=new Path();
        linePath.moveTo(fromX, fromY);
        linePath.lineTo(toX, toY);
        linePath.close();
        mCanvas.drawPath(linePath, mPaint);
        ;
    }

    /**
     * canvas tutorial
     */
    public void draw_my_pic(){
        int vWidth = mImageView.getWidth();
        int vHeight = mImageView.getHeight();
        int halfWidth = vWidth / 2;
        int halfHeight = vHeight / 2;
        mBitmap = Bitmap.createBitmap(vWidth, vHeight, Bitmap.Config.ARGB_8888);
        mImageView.setImageBitmap(mBitmap);
        mCanvas = new Canvas(mBitmap);
        mCanvas.drawColor(mColorBackground);
        mCanvas.drawText(getString(R.string.keep_tapping), 100, 100, mPaintText);
        mOffset += OFFSET;
    }

    public void drawSomething(View view) {
    ;
    }

}