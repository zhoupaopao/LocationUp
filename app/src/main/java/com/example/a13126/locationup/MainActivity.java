package com.example.a13126.locationup;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity {
    //        准备做个能够将定位写入文本里面的app，等到有无线网的时候上传。
//每半小时获取一次定位，这里可以启动一个服务，30分钟执行一次，将获取的经纬度存在文件当中，每一小时请求一下当前状态，是否是无线网状态，是的话就上传这个文件。


//类似年轮的app，需要每天添加一张图片，最好如果今天没有添加可以发送个广播推送一下。
//实现原理，添加一张图片，然后填上现在的体重，上传到后台，返回一个图片的url，最好这个图片能做一下水印，上面有体重，然后我把这个数据存放在本地，在列表中展示，如果sp里面存放不了那么多数据的话就存放在一个文件里面

    //需要重新安装一下eclipse，重新学习一下教程,根据招聘需求去加
    //左侧列表了解一下

    //制定一个属于自己的计划，每天做5个仰卧起坐，俯卧撑，早起睡觉喝杯水，晚上学习一会代码。以后体重app做完了可以加上体重，需要自定义日历

    private Button open_location;
    private LocationManager locationManager;
    private static final int BAIDU_READ_PHONE_STATE =100;
    private String provider;
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(getApplicationContext(),"没有权限,请手动开启定位权限",Toast.LENGTH_SHORT).show();
            // 申请一个（或多个）权限，并提供用于回调返回的获取码（用户定义）
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, BAIDU_READ_PHONE_STATE);

        }
        initView();

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
    @SuppressLint("MissingPermission")
    private void initView() {
        open_location=findViewById(R.id.open_location);
        open_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setClass(MainActivity.this,GaodeMap.class);
                startActivity(intent);
            }
        });
//        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        //获取当前可用的位置控制器
//        List<String> list = locationManager.getProviders(true);
//
//        if (list.contains(LocationManager.GPS_PROVIDER)) {
//            //是否为GPS位置控制器
//            provider = LocationManager.GPS_PROVIDER;
//        }
//        else if (list.contains(LocationManager.NETWORK_PROVIDER)) {
//            //是否为网络位置控制器
//            provider = LocationManager.NETWORK_PROVIDER;
//
//        } else {
//            Toast.makeText(this, "请检查网络或GPS是否打开",
//                    Toast.LENGTH_LONG).show();
//            return;
//        }
//        Location location = locationManager.getLastKnownLocation(provider);
//        if (location != null) {
//            //获取当前位置，这里只用到了经纬度
//            String string = "纬度为：" + location.getLatitude() + ",经度为："
//                    + location.getLongitude();
//            Log.i("initView: ", string);
//        }
//
////绑定定位事件，监听位置是否改变
////第一个参数为控制器类型第二个参数为监听位置变化的时间间隔（单位：毫秒）
////第三个参数为位置变化的间隔（单位：米）第四个参数为位置监听器
//        locationManager.requestLocationUpdates(provider, 2000, 2,
//                locationListener);
    }


    LocationListener locationListener = new LocationListener() {

        @Override
        public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderEnabled(String arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderDisabled(String arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onLocationChanged(Location arg0) {
            // TODO Auto-generated method stub
            // 更新当前经纬度
        }
    };
    //关闭时解除监听器
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

}
