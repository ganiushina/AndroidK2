package ru.geekbrains.projectandroid2.Service;

public class WeatherRequest {

    private Weather[] weather;
    private Main main;
    private String name;

    public Weather[] getWeather() {
        return weather;
    }
    public void setWeather(Weather[] weather) {
        this.weather = weather;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

}
