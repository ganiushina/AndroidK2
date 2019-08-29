package ru.geekbrains.projectandroid2.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.geekbrains.projectandroid2.LocationTrack;
import ru.geekbrains.projectandroid2.R;
import ru.geekbrains.projectandroid2.rest.OpenWeatherRepo;
import ru.geekbrains.projectandroid2.rest.entities.WeatherRequestRestModel;

public class Start extends Fragment  {

    private final static int permissionRequestCode = 100;
    private final static String MSG_NO_DATA = "No data";

    private LocationTrack locationTrack;

    private TextView mGpsInfo = null;
    private TextView mAddress = null;
    private TextView TemperatureTextView = null;
    private TextView txtViewPreference = null;
    private TextView detailsTxtView = null;
    private TextView updateTextView = null;
    private TextView weatherIconTxtView = null;
    private TextView textViewWelcome = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewStart = inflater.inflate(R.layout.fragment_start, container, false);
        initUi(viewStart);
        checkLocationPermission();
        return viewStart;
    }

    private void trackLocation() {
        locationTrack = new LocationTrack(getContext());
        if (locationTrack.canGetLocation()) {

            double longitude = locationTrack.getLongitude();
            double latitude = locationTrack.getLatitude();

            String longitudeLatitude = "Latitude: " + latitude + "\n" +
                    "Longitude: " + longitude;
            if (longitudeLatitude.equals("")) {
                longitudeLatitude = MSG_NO_DATA;
            }
            mGpsInfo.setText(longitudeLatitude);
            mAddress.setText(getAddressByLoc(longitude, latitude));
            Toast.makeText(getContext(), longitudeLatitude, Toast.LENGTH_SHORT).show();
        } else {
            locationTrack.showSettingsAlert();
        }
    }

    @Override
    public void onPause() {
        locationTrack.stopListener();
        super.onPause();
    }

    private void checkLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                trackLocation();
            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, permissionRequestCode);
            }
        }
    }

     @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == permissionRequestCode) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                trackLocation();
            } else {
                Toast.makeText(getContext(),
                        "Извините, апп без данного разрешения может работать неправильно",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initUi(View viewStart) {
        mGpsInfo = viewStart.findViewById(R.id.mGpsInfo);
        mAddress = viewStart.findViewById(R.id.mAddress);
        TemperatureTextView = viewStart.findViewById(R.id.TemperatureTextView);
        txtViewPreference = viewStart.findViewById(R.id.txtViewPreference);
        detailsTxtView = viewStart.findViewById(R.id.detailsTxtView);
        updateTextView = viewStart.findViewById(R.id.updateTextView);
        weatherIconTxtView = viewStart.findViewById(R.id.weatherIconTxtView);
        textViewWelcome = viewStart.findViewById(R.id.textViewWelcome);
    }

    /**
     * Get address string by location
     * */
    @SuppressLint("SetTextI18n")
    private String getAddressByLoc(double longitude, double latitude) {

        // Create geocoder
        final Geocoder geo = new Geocoder(getContext());

        // Try to get addresses list
        List<Address> list;
        try {
            list = geo.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
            return e.getLocalizedMessage();
        }
        // If list is empty, return "No data" string
        if (list.isEmpty()) return MSG_NO_DATA;

        // Get first element from List
        Address a = list.get(0);
        // Get a Postal Code
        final int index = a.getMaxAddressLineIndex();
        String postal = null;
        if (index >= 0) {
            postal = a.getAddressLine(index);
        }
        String cityName = list.get(0).getLocality();
        updateWeatherData(cityName);
        textViewWelcome.setText(getString(R.string.welcome) + " " + cityName);

        // Make address string
        StringBuilder builder = new StringBuilder();
        final String sep = ", ";
        builder.append(postal).append(sep)
                .append(a.getCountryName()).append(sep)
                .append(a.getAdminArea()).append(sep)
                .append(a.getThoroughfare()).append(sep)
                .append(a.getSubThoroughfare());
        return builder.toString();
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
        TemperatureTextView.setText(currentTextText);
    }

    private void setPlaceName(String name, String country) {
        String cityText = name.toUpperCase() + ", " + country;
        txtViewPreference.setText(cityText);
    }

    private void setDetails(String description, float humidity, float pressure)  {
        String detailsText = description.toUpperCase() + "\n"
                + "Humidity: " + humidity + "%" + "\n"
                + "Pressure: " + pressure + "hPa";
        detailsTxtView.setText(detailsText);
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
        weatherIconTxtView.setText(icon);
    }

    private void setUpdatedText(long dt) {
        DateFormat dateFormat = DateFormat.getDateTimeInstance();
        String updateOn = dateFormat.format(new Date(dt * 1000));
        String updatedText = "Last update: " + updateOn;
        updateTextView.setText(updatedText);
    }
}
