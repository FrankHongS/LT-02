package com.hon.librarytest02.camera.camerax;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import androidx.camera.core.CameraSelector;

/**
 * Created by Frank_Hon on 7/28/2020.
 * E-mail: v-shhong@microsoft.com
 */
public class RotateSensor implements SensorEventListener {
    private final float[] mAccelerometerValues = new float[3];
    private final float[] mMagneticFieldValues = new float[3];
    private int mCurrentDegree = 0;

    public RotateSensor(Context context) {
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                System.arraycopy(event.values, 0, mAccelerometerValues, 0, 3);
                calculateDegree();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                System.arraycopy(event.values, 0, mMagneticFieldValues, 0, 3);
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void calculateDegree() {
        final float[] rValues = new float[9];
        final float[] sensorValues = new float[3];
        SensorManager.getRotationMatrix(rValues, null, mAccelerometerValues, mMagneticFieldValues);
        SensorManager.getOrientation(rValues, sensorValues);
        sensorValues[0] = (float) Math.toDegrees(sensorValues[0]);
        sensorValues[1] = (float) Math.toDegrees(sensorValues[1]);
        sensorValues[2] = (float) Math.toDegrees(sensorValues[2]);

        if (sensorValues[2] < -45 && sensorValues[2] > -135) {
            mCurrentDegree = 270;
        }
        if (sensorValues[2] > 45 && sensorValues[2] < 135) {
            mCurrentDegree = 90;
        }

        if (sensorValues[1] < -45) {
            mCurrentDegree = 0;
        }
        if (sensorValues[1] > 45) {
            mCurrentDegree = 180;
        }
    }

    public int getCurrentDegree(int lensFacing) {
        return lensFacing == CameraSelector.LENS_FACING_BACK ? mCurrentDegree : 360 - mCurrentDegree;
    }
}
