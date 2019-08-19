package ru.geekbrains.projectandroid2.Service;


import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import ru.geekbrains.projectandroid2.BuildConfig;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static ru.geekbrains.projectandroid2.MainActivity.BROADCAST_ACTION;


public class BackgroundService extends IntentService {

    public static final String EXTRA_KEY_OUT = "EXTRA_OUT";
    private float temperature = 0.0f;

    public BackgroundService() {
        super("background_service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        temperature =  setTemper();
        String temp = Float.toString(temperature);
        Intent broadcastIntent = new Intent(BROADCAST_ACTION);
        broadcastIntent.putExtra(EXTRA_KEY_OUT, temp);
        sendBroadcast(broadcastIntent);
    }

    public float setTemper(){
        final String cityName = "Samara";
        final String WEATHER_URL = String.format("https://api.openweathermap.org/data/2.5/weather?q=%s,ru&appid=", cityName);
        try {
            final URL uri = new URL(WEATHER_URL + BuildConfig.WEATHER_API_KEY);
            HttpsURLConnection urlConnection = null;
            urlConnection = (HttpsURLConnection) uri.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000);
            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream())); // читаем  данные в поток
            String result = getLines(in);
            Gson gson = new Gson();
            WeatherRequest weatherRequest = gson.fromJson(result, WeatherRequest.class);
            temperature = weatherRequest.getMain().getTemp();
        } catch (MalformedURLException e) {
            Log.e(TAG, "Fail URI", e);
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return temperature ;
    }

    private String getLines(BufferedReader in) {
        String line = null;
        try {
            line = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }

}
