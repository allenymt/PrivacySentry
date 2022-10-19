package com.yl.lib.privacysentry.location;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.yl.lib.privacysentry.R;

import java.io.IOException;
import java.util.List;

/**
 * @author yulun
 * @since 2022-06-13 15:05
 */
public class LocationTestActivity extends AppCompatActivity {

    private Handler mHandler = new Handler(Looper.myLooper());
    private TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_test);

        textView = findViewById(R.id.location_text);
        // 判断当前是否拥有使用GPS的权限
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
        ) {
            // 申请权限
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                    },
                    100);
        } else {

        }

        findViewById(R.id.location).setOnClickListener(v -> {
            getLocation();
        });

        findViewById(R.id.last_location).setOnClickListener(v -> {
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            Location l = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            printLocation(l, LocationManager.NETWORK_PROVIDER + "last");
        });
    }

    private void doNextLocation() {
        textView.setText("location is null ,doNextLocation");
        mHandler.postDelayed(locationRunnable, 500);
    }

    private void getLocation() {
        // 获取当前位置管理器
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        // 启动位置请求
        // LocationManager.GPS_PROVIDER GPS定位
        // LocationManager.NETWORK_PROVIDER 网络定位
        // LocationManager.PASSIVE_PROVIDER 被动接受定位信息
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1000, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    printLocation(location, LocationManager.GPS_PROVIDER);
                }
            });
            mHandler.postDelayed(locationRunnable, 200);

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1000, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    printLocation(location, LocationManager.NETWORK_PROVIDER);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printLocation(Location location, String provider) {
        if (location == null) {
            return;
        }
        double lat = location.getLatitude();
        double lng = location.getLongitude();

        List<Address> list = null;
        Geocoder gd = new Geocoder(this);

        try {
            list = gd.getFromLocation(lat, lng, 2);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 城市转换
        String cityHome = "provider is " + provider + "x y is" + location.getLatitude() + "__" + location.getLongitude();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Address address = list.get(i);
                cityHome = cityHome + "__" + address.getLocality();
            }
        }

        textView.setText(cityHome);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private Runnable locationRunnable = new Runnable() {
        @Override
        public void run() {
            if (ActivityCompat.checkSelfPermission(LocationTestActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(LocationTestActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            Location lGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lGps != null) {
                printLocation(lGps, LocationManager.GPS_PROVIDER);
            }

            Location lNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (lNet != null) {
                printLocation(lNet, LocationManager.NETWORK_PROVIDER);
            }

            if (lGps == null && lNet == null) {
                doNextLocation();

            }
        }
    };


}
