package com.example.dhrushit.gyrotest;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 4337;
    private SensorManager mSensorManager;
    private Sensor mAccelerator;
    private Sensor mGyroscope;

    String root = Environment.getExternalStorageDirectory().toString();
    String gyroFilename = "gyrodata.txt";
    String acclFilename = "accldata.txt";

    TextView acc_x,acc_y,acc_z;
    TextView gyro_x,gyro_y,gyro_z;
    TextView gyroTextView,acclTextView;

    File folder = new File(root);
    File gyroFile;
    File acclFile;

    FileWriter gyroWriter,acclWriter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gyroTextView = (TextView) findViewById(R.id.gyroTextView);
        acclTextView = (TextView) findViewById(R.id.acclTextView);

        /*acc_x = (TextView) findViewById(R.id.acc_x);
        acc_y = (TextView) findViewById(R.id.acc_y);
        acc_z = (TextView) findViewById(R.id.acc_z);
        gyro_x = (TextView) findViewById(R.id.gyro_x);
        gyro_y = (TextView) findViewById(R.id.gyro_y);
        gyro_z = (TextView) findViewById(R.id.gyro_z);*/

        checkAndGetPermission();

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerator = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mSensorManager.registerListener((SensorEventListener) this,mGyroscope,SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener((SensorEventListener) this,mAccelerator,SensorManager.SENSOR_DELAY_NORMAL);

        if(!folder.exists()){
            folder.mkdirs();
        }

        gyroFile = new File(folder,gyroFilename);
        acclFile = new File(folder,acclFilename);
    }

    private void checkAndGetPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck
                != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            try {
                gyroWriter = new FileWriter(gyroFile.getAbsolutePath(),true);
                acclWriter = new FileWriter(acclFile.getAbsolutePath(),true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        String s;
        s = Float.toString(event.values[0]) + "\t" + Float.toString(event.values[1]) + "\t" + Float.toString(event.values[2]) + "\n";
        int type = event.sensor.getType();
        switch (type){
            case Sensor.TYPE_GYROSCOPE:
                try {
                    if(gyroWriter != null){
                        gyroWriter.append(s);
                        gyroWriter.flush();
                    }
                    gyroTextView.setText(s);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
            case Sensor.TYPE_ACCELEROMETER:
                try {
                    if(acclWriter != null){
                        acclWriter.append(s);
                        acclWriter.flush();
                    }
                    acclTextView.setText(s);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
