package org.haojun.represent;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StartActivity extends WearableActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private final SensorEventListener listener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[0];
            float z = event.values[0];
            float curr = (float) Math.sqrt((double) (x*x + y*y + z*z));
            float delta = prevSensorVal - curr;
            if (prevSensorVal == Float.POSITIVE_INFINITY) {
                prevSensorVal = curr;
                return;
            }
            if (Math.abs(delta) > 100) {
                Intent sendIntent = new Intent(getApplicationContext(), WatchToMobileService.class);
                sendIntent.putExtra("type", "zipCode");
                startService(sendIntent);
            }
            prevSensorVal = curr;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(listener, sensor);
    }

    private SensorManager sensorManager;
    private Sensor sensor;
    private float prevSensorVal = Float.POSITIVE_INFINITY;
}
