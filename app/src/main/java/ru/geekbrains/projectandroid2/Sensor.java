package ru.geekbrains.projectandroid2;

import android.app.Activity;
import android.content.Intent;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class Sensor extends Fragment implements SensorEventListener {

    private SensorManager mSensorManager;
    private android.hardware.Sensor sensorLight;
    private android.hardware.Sensor sensorTemperature;
    private android.hardware.Sensor sensorHumidity;
    private TextView textViewHudimity;
    private TextView textViewTemperature;
    private TextView textLight;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewSensor = inflater.inflate(R.layout.activity_sensors, container, false);
        initViews(viewSensor);
        getSensors();
        return viewSensor;
    }

    private void initViews(View viewSensor) {
        textViewHudimity = viewSensor.findViewById(R.id.textViewHudimity);
        textViewTemperature = viewSensor.findViewById(R.id.textViewTemperature);
        textLight = viewSensor.findViewById(R.id.textLight);
    }

    private void getSensors() {
        mSensorManager = (SensorManager) this.getActivity().getSystemService(Activity.SENSOR_SERVICE);
        sensorLight = mSensorManager.getDefaultSensor(android.hardware.Sensor.TYPE_LIGHT);
        sensorTemperature = mSensorManager.getDefaultSensor(android.hardware.Sensor.TYPE_AMBIENT_TEMPERATURE);
        sensorHumidity = mSensorManager.getDefaultSensor(android.hardware.Sensor.TYPE_RELATIVE_HUMIDITY);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0], y = event.values[1];
    }

    @Override
    public void onAccuracyChanged(android.hardware.Sensor sensor, int i) {

    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if(mSensorManager == null) {
            return;
        }
        if(menuVisible) {
            this.registerSensorListener();
        } else {
            this.unregisterSensorListener();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if(this.getUserVisibleHint()) {
            this.registerSensorListener();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        this.unregisterSensorListener();
    }

    SensorEventListener listenerLight = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy) {}

        @Override
        public void onSensorChanged(SensorEvent event) {
            showLightSensors(event);
        }
    };

    private void showLightSensors(SensorEvent event){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Light Sensor value = ").append(event.values[0])
                .append("\n").append("=======================================").append("\n");
        textLight.setText(stringBuilder);
    }

    SensorEventListener lisnerTemperature = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy) {}

        @Override
        public void onSensorChanged(SensorEvent event) {
            showTemperatureSensors(event);
        }
    };

    private void showTemperatureSensors(SensorEvent event){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Temperature Sensor value = ").append(event.values[0])
                .append("\n").append("=======================================").append("\n");
        textViewTemperature.setText(stringBuilder);
    }

    SensorEventListener listenerHudimity = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy) {}

        @Override
        public void onSensorChanged(SensorEvent event) {
            showHudimitySensors(event);
        }
    };

    private void showHudimitySensors(SensorEvent event) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Hudimity Sensor value = ").append(event.values[0])
                .append("\n").append("=======================================").append("\n");
        textViewHudimity.setText(stringBuilder);
    }

    private void registerSensorListener() {
        mSensorManager.registerListener(listenerLight, sensorLight,
                SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(lisnerTemperature, sensorTemperature,
                SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(listenerHudimity, sensorHumidity,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void unregisterSensorListener() {
        mSensorManager.unregisterListener(this);
    }
}
