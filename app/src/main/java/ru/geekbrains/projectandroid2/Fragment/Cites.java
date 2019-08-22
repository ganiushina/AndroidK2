package ru.geekbrains.projectandroid2.Fragment;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.geekbrains.projectandroid2.City;
import ru.geekbrains.projectandroid2.CityAdapter;
import ru.geekbrains.projectandroid2.ItemCallback;
import ru.geekbrains.projectandroid2.R;
import ru.geekbrains.projectandroid2.database.CitiesTable;
import ru.geekbrains.projectandroid2.database.DatabaseHelper;
import ru.geekbrains.projectandroid2.database.DatabaseHelperWeather;
import ru.geekbrains.projectandroid2.database.WeatherTable;
import ru.geekbrains.projectandroid2.rest.OpenWeatherRepo;
import ru.geekbrains.projectandroid2.rest.entities.WeatherRequestRestModel;

import static ru.geekbrains.projectandroid2.database.WeatherTable.COLUMN_HUMIDITY;
import static ru.geekbrains.projectandroid2.database.WeatherTable.COLUMN_PRESSURE;
import static ru.geekbrains.projectandroid2.database.WeatherTable.COLUMN_WEATHER;
import static ru.geekbrains.projectandroid2.database.WeatherTable.getCityWeather;

public class Cites extends Fragment  implements ItemCallback {

    private List<City> cites = new ArrayList<>();
    private TextView textViewPreference;
    private TextView currentTemperatureTextView;
    private TextView detailsTextView;
    private TextView weatherIconTextView;
    private TextView updatedTextView;
    private EditText editTextCity;
    private CityAdapter mAdapter;

    private SQLiteDatabase database;
    private SQLiteDatabase databaseWeather;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View cityView = inflater.inflate(R.layout.activity_citys, container, false);
        initRecyclerViewAdapter(cityView);
        initDB();
        populateCityDetails();
        initFonts();
        return cityView;
    }

    private void initRecyclerViewAdapter(View cityView) {
        RecyclerView recyclerView = cityView.findViewById(R.id.recyclerView);
        textViewPreference = cityView.findViewById(R.id.textViewPreference);
        currentTemperatureTextView = cityView.findViewById(R.id.currentTemperatureTextView);
        detailsTextView = cityView.findViewById(R.id.detailsTextView);
        weatherIconTextView = cityView.findViewById(R.id.weatherIconTextView);
        editTextCity = cityView.findViewById(R.id.editTextCity);
        updatedTextView = cityView.findViewById(R.id.updatedTextView);
        Button buttonSave = cityView.findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CitiesTable.findCity(String.valueOf(editTextCity.getText()), database) == 0) {
                    CitiesTable.addCity(String.valueOf(editTextCity.getText()), database);
                    cites.add(new City(String.valueOf(editTextCity.getText())));
                    updateWeatherData(String.valueOf(editTextCity.getText()));
                    mAdapter.notifyDataSetChanged();
                }
                else {
                    Toast.makeText(getContext(), String.valueOf(editTextCity.getText()) + " " + getString(R.string.notEmpty),
                            Toast.LENGTH_SHORT).show();
                }
                editTextCity.setText("");
            }
        });
        mAdapter = new CityAdapter(cites, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }
    private void initFonts() {
        Typeface weatherFont = Typeface.createFromAsset(Objects.requireNonNull(getActivity()).getAssets(), "fonts/weather.ttf");
        weatherIconTextView.setTypeface(weatherFont);
    }
    private void initDB() {
        database = new DatabaseHelper(getContext()).getWritableDatabase();
        databaseWeather = new DatabaseHelperWeather(getContext()).getWritableDatabase();
    }


    private void populateCityDetails() {
        List<String> cityName;
        InputStream inputStream = this.getResources().openRawResource(R.raw.russia1);
        BufferedReader bR = new BufferedReader(
                new InputStreamReader(inputStream));
        String line = "";
        StringBuilder responseStrBuilder = new StringBuilder();
        while(true){
            try {
                if ((line = bR.readLine()) == null) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            responseStrBuilder.append(line);
        }
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            JSONArray mainObject = new JSONArray(responseStrBuilder.toString());
            for (int i = 0; i < mainObject.length(); i++) {
                JSONObject object = mainObject.getJSONObject(i);
                if (CitiesTable.findCity(object.getString("city"), database) == 0)
                    CitiesTable.addCity(object.getString("city"), database);
            }

            cityName = CitiesTable.getAllCities(database);
            for (int i = 0; i < cityName.size(); i++) {
                cites.add(new City(cityName.get(i)));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateTextView() {
        updateWeatherData(mAdapter.strPrefer);
    }

    private void updateWeatherData(String city) {
        OpenWeatherRepo.getSingleton().getAPI().loadWeather(city + ",ru",
                "70a56cee80c8d839beb4faa4e394ad87", "metric")
                .enqueue(new Callback<WeatherRequestRestModel>() {
                    @Override
                    public void onResponse(@NonNull Call<WeatherRequestRestModel> call,
                                           @NonNull Response<WeatherRequestRestModel> response) {
                        if (response.body() != null && response.isSuccessful()) {
                            renderWeather(response.body());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<WeatherRequestRestModel> call, @NonNull Throwable t) {
                        Toast.makeText(getContext(), getString(R.string.network_error),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void renderWeather(WeatherRequestRestModel model) {
        int city_id;
        city_id = CitiesTable.findCity(model.name, database);
        if (WeatherTable.findWeatherCity(city_id, databaseWeather) == 0)
            WeatherTable.addWeather(city_id, model.main.pressure, model.main.temp, model.main.humidity, databaseWeather);
        else
            WeatherTable.editWeather(city_id, model.main.pressure, model.main.temp, model.main.humidity, databaseWeather);
        setWeatherInfo(city_id);
        setPlaceName(model.name, model.sys.country);
    }

    private void setWeatherInfo(int city_id) {

        HashMap<String, String> weather = getCityWeather(city_id, databaseWeather);
        String weather_humidity = "";
        String weather_pressure = "";

        for (Map.Entry<String, String> item : weather.entrySet()) {
            if (item.getKey().equals(COLUMN_WEATHER))
                setCurrentTemp(item.getValue());
            if (item.getKey().equals(COLUMN_PRESSURE))
                weather_pressure = item.getValue();
            if (item.getKey().equals(COLUMN_HUMIDITY))
                weather_humidity = item.getValue();
        }
        if (!weather_humidity.isEmpty() || !weather_pressure.isEmpty()) {
            setDetails(weather_humidity, weather_pressure);
        }
    }


    private void setCurrentTemp(String temp) {
        float tempTemper = Float.parseFloat(temp);
        String currentTextText = String.format(Locale.getDefault(), "%.2f", tempTemper) + "\u2103";
        currentTemperatureTextView.setText(currentTextText);
    }

    private void setPlaceName(String name, String country) {
        String cityText = name.toUpperCase() + ", " + country;
        textViewPreference.setText(cityText);
    }

    private void setDetails(String humidity, String pressure)  {
        String detailsText =  "Humidity: " + humidity + "%" + "\n"
                + "Pressure: " + pressure + "hPa";
        detailsTextView.setText(detailsText);
    }


    private void setUpdatedText(long dt) {
        DateFormat dateFormat = DateFormat.getDateTimeInstance();
        String updateOn = dateFormat.format(new Date(dt * 1000));
        String updatedText = "Last update: " + updateOn;
        updatedTextView.setText(updatedText);
    }

}
