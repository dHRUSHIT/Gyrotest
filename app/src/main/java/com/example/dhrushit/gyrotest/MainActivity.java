package com.example.dhrushit.gyrotest;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

@TargetApi(Build.VERSION_CODES.N)
public class MainActivity extends AppCompatActivity implements SensorEventListener,View.OnClickListener {

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 4337;
    private SensorManager mSensorManager;
    private Sensor mAccelerator;
    private Sensor mGyroscope;

    String root = Environment.getExternalStorageDirectory().toString();
    String gyroFilename = "gyrodata.txt";
    String acclFilename = "accldata.txt";

    boolean newFileBool = false;

    TextView acc_x,acc_y,acc_z;
    TextView gyro_x,gyro_y,gyro_z;
    TextView gyroTextView,acclTextView;
    Button upBtn,downBtn,rightBtn,leftBtn;

    File folder = new File(root);
    File gyroFile;
    File acclFile;

    FileWriter gyroWriter,acclWriter;

    Calendar calendar;
    Date curTime;
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calendar = Calendar.getInstance();


        gyroTextView = (TextView) findViewById(R.id.gyroTextView);
        acclTextView = (TextView) findViewById(R.id.acclTextView);
        upBtn = (Button) findViewById(R.id.up_btn);
        downBtn = (Button) findViewById(R.id.down_btn);
        rightBtn = (Button) findViewById(R.id.right_btn);
        leftBtn = (Button) findViewById(R.id.left_btn);

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

//        getFileInstances();
    }

    private void getFileInstances() {
        if(!folder.exists()){
            folder.mkdirs();
        }

        gyroFile = new File(folder,gyroFilename);
        acclFile = new File(folder,acclFilename);

        if(!gyroFile.exists()){
            try {
                gyroFile.createNewFile();
                newFileBool = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(!acclFile.exists()){
            try {
                acclFile.createNewFile();
                newFileBool = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void addDeviceInfo() {
        String deviceInfo = "Device Information\n------------------\n";
        deviceInfo += "BOARD : " + Build.BOARD + "\nBRAND : " + Build.BRAND
                + "\nDEVICE : " + Build.DEVICE + "\nHARDWARE : " + Build.HARDWARE
                + "\nMANUFACTURER : " + Build.MANUFACTURER + "\nMODEL : " + Build.MODEL
                + "\nPRODUCT : " + Build.PRODUCT + "\nTYPE : " + Build.TYPE;
        try {
            gyroWriter.append(deviceInfo);
            acclWriter.append(deviceInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void checkAndGetPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCheck
                != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

        }else{
            getFileInstances();
            getWriterInstances(newFileBool);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getFileInstances();
            getWriterInstances(newFileBool);
        }
        return;
    }

    private void getWriterInstances(boolean isFileNew) {

        try {
            gyroWriter = new FileWriter(gyroFile.getAbsolutePath(),true);
            acclWriter = new FileWriter(acclFile.getAbsolutePath(),true);
            if(gyroWriter != null && acclWriter != null){
                Toast.makeText(this, "logging now", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "could not get FileWriter", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(isFileNew){
            newFileBool = false;
            addDeviceInfo();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        String s;
        s = sdf.format(new Date()) + "\t" + Float.toString(event.values[0]) + "\t" + Float.toString(event.values[1]) + "\t" + Float.toString(event.values[2]) + "\n";
        int type = event.sensor.getType();
        switch (type){
            case Sensor.TYPE_GYROSCOPE:
                writeToGyroLog(s);

                break;
            case Sensor.TYPE_ACCELEROMETER:
                writeToAcclLog(s);

                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        String string = "----------------------------------";
        switch (id){
            case R.id.left_btn:
                string += "left";
                break;
            case R.id.right_btn:
                string += "right";
                break;
            case R.id.up_btn:
                string += "up";
                break;
            case R.id.down_btn:
                string += "down";
                break;
        }
        string += "----------------------------------";

        writeToAcclLog(string);
        writeToGyroLog(string);
    }

    public void writeToGyroLog(String s){
        try {
            if(gyroWriter != null){
                gyroWriter.append(s);
                gyroWriter.flush();
            }
            gyroTextView.setText(s);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeToAcclLog(String s){
        try {
            if(acclWriter != null){
                acclWriter.append(s);
                acclWriter.flush();
            }
            acclTextView.setText(s);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
