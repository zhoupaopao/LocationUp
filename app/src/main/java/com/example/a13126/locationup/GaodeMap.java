package com.example.a13126.locationup;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * Created by 13126 on 2018/5/26.
 */
//利用AlarmManger+Service+BarocastReceiver实现5s一次打印操作
public class GaodeMap extends Activity {
    Button gaode;
    Button stop_location;
    Button stop_service;
    //声明AMapLocationClient类对象
    public AMapLocationClient locationClient = null;
    private static final int BAIDU_READ_PHONE_STATE =100;
    //声明定位回调监听器
    private AMapLocationClientOption locationOption = new AMapLocationClientOption();
    Intent intent1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gaode);
        //请求文件读取写入权限
        checkPermission();
        initView();
    }

    private void initView() {
        gaode=findViewById(R.id.gaode);
        stop_location=findViewById(R.id.stop_location);
        stop_service=findViewById(R.id.stop_service);
        intent1 = new Intent(GaodeMap.this,HorizonService.class);
        locationClient = new AMapLocationClient(getApplicationContext());
        //设置定位参数
        locationClient.setLocationOption(getDefaultOption());
        // 设置定位监听
        locationClient.setLocationListener(locationListener);
        gaode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//初始化client


                locationClient.startLocation();
            }
        });
        stop_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //这边不是简单的关闭服务就行的，因为是广播一直在开启服务，然而系统广播是关不了的，只能关闭alarmManager
                stopService(intent1);
            }
        });
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
                //解析定位结果
                String result = Utils.getLocationStr(loc);
                Toast.makeText(GaodeMap.this,result,Toast.LENGTH_SHORT).show();
                Log.i("onLocationChanged: ", result);
                //定位成功开启服务
                intent1.putExtra("getLongitude",loc.getLongitude());
                intent1.putExtra("getLongitude",loc.getLongitude());
                startService(intent1);
            } else {
//                tvResult.setText("定位失败，loc is null");
                Toast.makeText(GaodeMap.this,"定位失败",Toast.LENGTH_SHORT).show();
                //重新定位
            }
        }
    };
    /**
     * 停止定位
     *
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private void stopLocation(){
        // 停止定位
        locationClient.stopLocation();
    }
    /**
     * 销毁定位
     *
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private void destroyLocation(){
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }
    private void checkPermission() {
        //检查权限（NEED_PERMISSION）是否被授权 PackageManager.PERMISSION_GRANTED表示同意授权
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PERMISSION_GRANTED|| ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PERMISSION_GRANTED) {
            //用户已经拒绝过一次，再次弹出权限申请对话框需要给用户一个解释
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission
                    .WRITE_EXTERNAL_STORAGE)) {
                //当用户拒绝过一次后，再次请求的时候需要给用户提醒
                Toast.makeText(this, "请开通相关权限，否则无法正常使用本应用！", Toast.LENGTH_SHORT).show();
            }
            //申请权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, BAIDU_READ_PHONE_STATE);

//            checkPermission();
        } else {
            Toast.makeText(this, "授权成功！", Toast.LENGTH_SHORT).show();
            Log.e("checkPermission", "checkPermission: 已经授权！");
        }
    }
    //用户点击拒绝权限后的事件
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PERMISSION_GRANTED) {
                    Toast.makeText(this, "" + "权限" + permissions[i] + "申请成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "" + "权限" + permissions[i] + "申请失败", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}
