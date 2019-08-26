package ru.geekbrains.projectandroid2.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;


import ru.geekbrains.projectandroid2.R;
import ru.geekbrains.projectandroid2.Service.BackgroundService;

import static ru.geekbrains.projectandroid2.MainActivity.BROADCAST_ACTION;

public class Sensor extends Fragment implements SensorEventListener {

    private SensorManager mSensorManager;
    private android.hardware.Sensor sensorLight;
    private android.hardware.Sensor sensorTemperature;
    private android.hardware.Sensor sensorHumidity;
    private TextView textViewHudimity;
    private TextView textViewTemperature;
    private TextView textLight;
    private TextView textViewTemperatureFromInet;

    private TextView textViewInfo;

    private ImageView image_1 ;

    private ServiceFinishedReceiver receiver = new ServiceFinishedReceiver();

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
        textViewTemperatureFromInet = viewSensor.findViewById(R.id.textViewTemperatureFromInet);
        image_1 = viewSensor.findViewById(R.id.image_1);
        textViewInfo = viewSensor.findViewById(R.id.textViewInfo);
        Button button = viewSensor.findViewById(R.id.buttonLoad);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initDownLoadTask();
            }
        });
    }

    private void initDownLoadTask(){
        String URL1 = "https://img2.goodfon.ru/wallpaper/nbig/1/c4/samara-ladya-volga-povolzhe.jpg";
        new DownloadFilesTask(image_1).execute(URL1);    }

    private void getSensors() {
        mSensorManager = (SensorManager) Objects.requireNonNull(this.getActivity()).getSystemService(Activity.SENSOR_SERVICE);
        if (mSensorManager != null) {
            sensorLight = mSensorManager.getDefaultSensor(android.hardware.Sensor.TYPE_LIGHT);
            sensorTemperature = mSensorManager.getDefaultSensor(android.hardware.Sensor.TYPE_AMBIENT_TEMPERATURE);
            sensorHumidity = mSensorManager.getDefaultSensor(android.hardware.Sensor.TYPE_RELATIVE_HUMIDITY);
        }
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
        Objects.requireNonNull(getActivity()).registerReceiver(receiver, new IntentFilter(BROADCAST_ACTION));
        Intent intent = new Intent(getActivity(), BackgroundService.class);
        getActivity().startService(intent);

        if(this.getUserVisibleHint()) {
            this.registerSensorListener();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        this.unregisterSensorListener();
        Objects.requireNonNull(getActivity()).unregisterReceiver(receiver);
    }

    private SensorEventListener listenerLight = new SensorEventListener() {

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

    private SensorEventListener lisnerTemperature = new SensorEventListener() {
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

    private SensorEventListener listenerHudimity = new SensorEventListener() {
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
        if (sensorLight != null) {
            mSensorManager.registerListener(listenerLight, sensorLight,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (sensorTemperature != null) {
            mSensorManager.registerListener(lisnerTemperature, sensorTemperature,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (sensorHumidity != null) {
            mSensorManager.registerListener(listenerHudimity, sensorHumidity,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    private void unregisterSensorListener() {
        mSensorManager.unregisterListener(this);
    }

    private class ServiceFinishedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, final Intent intent) {
            Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String result = intent
                            .getStringExtra(BackgroundService.EXTRA_KEY_OUT);
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Temperature from openweathermap = ").append(result).append(" C").append("\n").append("=======================================").append("\n");
                    textViewTemperatureFromInet.setText(stringBuilder);
                }
            });
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class DownloadFilesTask  extends AsyncTask<String, Void, Bitmap> {

        ImageView imageView = null;

        private DownloadFilesTask(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            textViewInfo.setText("Загрузка начата");
        }

        protected Bitmap doInBackground(String... addresses) {
            Bitmap bitmap = null;
            InputStream in = null;
            try {
                URL url = new URL(addresses[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                in = conn.getInputStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
            textViewInfo.setText("Загрузка завершена");
        }
    }

}


