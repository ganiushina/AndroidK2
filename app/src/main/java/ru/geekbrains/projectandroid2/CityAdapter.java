package ru.geekbrains.projectandroid2;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import java.util.List;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.CustomViewHolder> {

    private List<City> citys;
    public String strPrefer;

    private ItemCallback onItemTouchListener;

    private SharedPreferences defaultPrefs;

    class CustomViewHolder extends ViewHolder  {

        TextView cityName;

        private final String textKey = "text_key";

        CustomViewHolder(View view) {
            super(view);
            this.cityName = view.findViewById(R.id.cityName);
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        City clickedDataItem = citys.get(pos);
                        Toast.makeText(v.getContext(), v.getResources().getString(R.string.YouChoice) + " " + clickedDataItem.city, Toast.LENGTH_SHORT).show();
                        defaultPrefs = PreferenceManager.getDefaultSharedPreferences(v.getContext());
                        saveToPreference(defaultPrefs, clickedDataItem.city);
                        readFromPreference(defaultPrefs);
                        onItemTouchListener.updateTextView();
                    }
                }
            });
        }

        private void saveToPreference(SharedPreferences preferences, String cityNmae) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(textKey, cityNmae);
            editor.apply();
        }
        private void readFromPreference(SharedPreferences preferences) {
            strPrefer = preferences.getString(textKey, "");
        }
    }

    public CityAdapter(List<City> citys, ItemCallback onItemTouchListener) {
        this.citys = citys;
        this.onItemTouchListener = onItemTouchListener;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.city_list, parent, false);
        return new CustomViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        City city = citys.get(position);
        holder.cityName.setText(city.city);
    }

    @Override
    public int getItemCount() {
        return citys.size();
    }
}
