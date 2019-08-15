package ru.geekbrains.projectandroid2.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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
import java.util.ArrayList;
import java.util.List;

import ru.geekbrains.projectandroid2.City;
import ru.geekbrains.projectandroid2.CityAdapter;
import ru.geekbrains.projectandroid2.R;

public class Citys extends Fragment   {

    private List<City> citys = new ArrayList<>();
    private CityAdapter mAdapter;
    private TextView textViewPreference;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View cityView = inflater.inflate(R.layout.activity_citys, container, false);
        initRecyclerViewAdapter(cityView);
        populateCityDetails();
        return cityView;
    }

    private void initRecyclerViewAdapter(View cityView) {
        RecyclerView recyclerView = cityView.findViewById(R.id.recyclerView);
        mAdapter = new CityAdapter(citys);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        textViewPreference = cityView.findViewById(R.id.textViewPreference);
        Button button = cityView.findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                textViewPreference.setText(getString(R.string.YouChoice) + " " + mAdapter.strPrefer);
            }
        });
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
                citys.add(new City(object.getString("region"), object.getString("city")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
