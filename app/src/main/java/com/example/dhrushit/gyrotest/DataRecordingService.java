package com.example.dhrushit.gyrotest;

import android.app.IntentService;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by dHRUSHIT on 4/3/2017.
 */

public class DataRecordingService extends IntentService implements SensorEventListener {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public DataRecordingService(String name) throws FileNotFoundException {
        super(name);
        file = new File(Environment.getExternalStorageDirectory(),"gyroLog.txt");
        fOut = openFileOutput(String.valueOf(file),  MODE_APPEND);
        mOutputStreamWriter = new OutputStreamWriter(fOut);
    }

    File file ;
    FileOutputStream fOut;
    OutputStreamWriter mOutputStreamWriter;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        try {
            mOutputStreamWriter.append(event.values[0]+ "\t" + event.values[1]+ "\t" + event.values[2]+ "\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
