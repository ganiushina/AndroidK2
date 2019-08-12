package ru.geekbrains.projectandroid2.Service;


import android.app.IntentService;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

import ru.geekbrains.projectandroid2.BuildConfig;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static ru.geekbrains.projectandroid2.MainActivity.BROADCAST_ACTION;


public class BackgroundService extends IntentService {

    public static final String EXTRA_KEY_OUT = "EXTRA_OUT";

    public BackgroundService() {
        super("background_service");
    }

    //Здесь начинается фоновый поток
    @Override
    protected void onHandleIntent(Intent intent) {

        final float[] temperature = new float[1];
        temperature[0] =  setTemper();
        String temp = Float.toString(temperature[0]);

        Intent broadcastIntent = new Intent(BROADCAST_ACTION);
        broadcastIntent.putExtra(EXTRA_KEY_OUT, temp);

        sendBroadcast(broadcastIntent);
    }

    public float setTemper(){
        final String cityName = "Samara";
        final float[] temperature = new float[1];
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
            final WeatherRequest weatherRequest = gson.fromJson(result, WeatherRequest.class);
            temperature[0] = weatherRequest.getMain().getTemp();

        } catch (MalformedURLException e) {
            Log.e(TAG, "Fail URI", e);
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return temperature[0] ;
    }

    private String getLines(BufferedReader in) {
        String lines = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            lines = in.lines().collect(Collectors.joining("\n"));
        }
        return lines;
    }

}
