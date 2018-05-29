package com.example.a13126.locationup;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 13126 on 2018/5/28.
 */

public class HorizonService extends Service{
    AlarmManager manager;
    PendingIntent pendingIntent1;
    int ii=0;
    //声明AMapLocationClient类对象
    public AMapLocationClient locationClient = null;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("打印时间", startId+"onStartCommand: ");

        //代表着开始服务
        //开启一个线程
        new Thread(new Runnable() {
            @Override
            public void run() {
//                Log.i("run: 打印时间", new Date().toString());
                locationClient = new AMapLocationClient(getApplicationContext());
                //设置定位参数
                locationClient.setLocationOption(getDefaultOption());
                // 设置定位监听
                locationClient.setLocationListener(locationListener);
                locationClient.startLocation();
            }
        }).start();
//        开启alarmManager
//        AlarmManager manager= (AlarmManager) getSystemService(ALARM_SERVICE);
        int five=5000;//这里设置间隔时间是5s
        long triggerAtTime= SystemClock.elapsedRealtime()+five;//获取的是系统启动后的时间
        Intent i=new Intent(this,AlarmReceiver.class);//跳转到这个广播接收器，让这个广播接收器运行这个服务
        PendingIntent pendingIntent=PendingIntent.getBroadcast(this,0,i,0);
        pendingIntent1=pendingIntent;


//        if(startId==5){
//            manager.cancel(pendingIntent);
//        }else{
            //我不需要手机关机运行，但是需要在休眠的时候也运行
            manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pendingIntent);
//        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        manager= (AlarmManager) getSystemService(ALARM_SERVICE);

        Log.i("打印时间", "onCreate: ");
    }
    /**
     * 默认的定位参数
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private AMapLocationClientOption getDefaultOption(){
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(4000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(true);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        return mOption;
    }
    /**
     * 定位监听
     */
    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation loc) {
            if (null != loc) {
                ii++;
                //解析定位结果
                String result = Utils.getLocationStr(loc);
                Toast.makeText(HorizonService.this,result,Toast.LENGTH_SHORT).show();
                Log.i("onLocationChanged: ", result);
                //定位成功
                double getLongitude= loc.getLongitude();//经度
                double getLatitude= loc.getLatitude();//纬度
                String city=loc.getCity();//城市
                SimpleDateFormat    formatter    =   new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                Date    curDate    =   new    Date(System.currentTimeMillis());//获取当前时间
                String    str    =    formatter.format(curDate);
                Log.i("onLocationChanged: ", str);
                //写入文件
                String message=getLongitude+","+getLatitude;
                String filePath = "/sdcard/location/";
                writeTxtToFile(message, filePath, "test1.txt");
//                if(ii==5){
//                    ii=0;
//                    //读取文件
//                    String res=loadFromSDFile("/sdcard/location/test1.txt");
//                    Log.i("resresres: ", res);
//                }
                //可以提交后删除
//                if(ii==2){
//                    //删除文件
//                    File file = new File("/sdcard/location/test1.txt");
//                    if(file.delete()){
//                        Toast.makeText(HorizonService.this,"删除成功",Toast.LENGTH_SHORT).show();
//                    }
//                }
                //销毁定位
                destroyLocation();
            } else {
//                tvResult.setText("定位失败，loc is null");
                Toast.makeText(HorizonService.this,"定位失败",Toast.LENGTH_SHORT).show();
                //重新定位
                locationClient.startLocation();
            }
        }
    };
    // 将字符串写入到文本文件中
    public void writeTxtToFile(String strcontent, String filePath, String fileName) {
        //生成文件夹之后，再生成文件，不然会出错
        makeFilePath(filePath, fileName);

        String strFilePath = filePath+fileName;
        // 每次写入时，都换行写
        String strContent = strcontent + "\r\n";
        try {
            File file = new File(strFilePath);
            if (!file.exists()) {
                Log.d("TestFile", "Create the file:" + strFilePath);
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
            raf.close();
        } catch (Exception e) {
            Log.e("TestFile", "Error on write File:" + e);
        }
    }
    // 生成文件
    public File makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    // 生成文件夹
    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            Log.i("error:", e+"");
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //stopservice会进入ondestory
        Log.i("打印时间", "onDestroy: ");
        manager.cancel(pendingIntent1);
    }
    private void destroyLocation(){
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
        }
    }
    private String loadFromSDFile(String fname) {
//        fname="/"+fname;
        String result=null;
        try {
            File f=new File(fname);
            int length=(int)f.length();
            byte[] buff=new byte[length];
            FileInputStream fin=new FileInputStream(f);
            fin.read(buff);
            fin.close();
            result=new String(buff,"UTF-8");
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(HorizonService.this,"没有找到指定文件",Toast.LENGTH_SHORT).show();
        }
        return result;
    }
}
