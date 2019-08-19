package ru.geekbrains.projectandroid2.Fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.geekbrains.projectandroid2.City;
import ru.geekbrains.projectandroid2.CityAdapter;
import ru.geekbrains.projectandroid2.ItemCallback;
import ru.geekbrains.projectandroid2.R;
import ru.geekbrains.projectandroid2.rest.OpenWeatherRepo;
import ru.geekbrains.projectandroid2.rest.entities.WeatherRequestRestModel;

public class Cites extends Fragment  implements ItemCallback {

    private List<City> cites = new ArrayList<>();
    private TextView textViewPreference;
    private TextView currentTemperatureTextView;
    private TextView detailsTextView;
    private TextView weatherIconTextView;
    private TextView updatedTextView;
    private CityAdapter mAdapter;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View cityView = inflater.inflate(R.layout.activity_citys, container, false);
        initRecyclerViewAdapter(cityView);
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
        updatedTextView = cityView.findViewById(R.id.updatedTextView);
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

    private void populateCityDetails() {
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
                cites.add(new City(object.getString("region"), object.getString("city")));
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
        setPlaceName(model.name, model.sys.country);
        setDetails(model.weather[0].description, model.main.humidity, model.main.pressure);
        setCurrentTemp(model.main.temp);
        setUpdatedText(model.dt);
        setWeatherIcon(model.weather[0].id,
                model.sys.sunrise * 1000,
                model.sys.sunset * 1000);
    }

    private void setCurrentTemp(float temp) {
        String currentTextText = String.format(Locale.getDefault(), "%.2f", temp) + "\u2103";
        currentTemperatureTextView.setText(currentTextText);
    }

    private void setPlaceName(String name, String country) {
        String cityText = name.toUpperCase() + ", " + country;
        textViewPreference.setText(cityText);
    }

    private void setDetails(String description, float humidity, float pressure)  {
        String detailsText = description.toUpperCase() + "\n"
                + "Humidity: " + humidity + "%" + "\n"
                + "Pressure: " + pressure + "hPa";
        detailsTextView.setText(detailsText);
    }

    private void setWeatherIcon(int actualId, long sunrise, long sunset) {
        int id = actualId / 100;
        String icon = "";

        if(actualId == 800) {
            long currentTime = new Date().getTime();
            if(currentTime >= sunrise && currentTime < sunset) {
                icon = "\u2600";
            } else {
                icon = getString(R.string.weather_clear_night);
            }
        } else {
            switch (id) {
                case 2: {
                    icon = getString(R.string.weather_thunder);
                    break;
                }
                case 3: {
                    icon = getString(R.string.weather_drizzle);
                    break;
                }
                case 5: {
                    icon = getString(R.string.weather_rainy);
                    break;
                }
                case 6: {
                    icon = getString(R.string.weather_snowy);
                    break;
                }
                case 7: {
                    icon = getString(R.string.weather_foggy);
                    break;
                }
                case 8: {
                    icon = "\u2601";
                    break;
                }
            }
        }
        weatherIconTextView.setText(icon);
    }

    private void setUpdatedText(long dt) {
        DateFormat dateFormat = DateFormat.getDateTimeInstance();
        String updateOn = dateFormat.format(new Date(dt * 1000));
        String updatedText = "Last update: " + updateOn;
        updatedTextView.setText(updatedText);
    }

}
