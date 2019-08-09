package ru.geekbrains.projectandroid2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class CallBack extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewHierarchy = inflater.inflate(R.layout.calback_activity, container, false);
        Button buttonSend = viewHierarchy.findViewById(R.id.buttonSend);
        final EditText editTextAdd = viewHierarchy.findViewById(R.id.editTextAdd);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextAdd.setText("");
                Toast.makeText(getActivity(), R.string.sendToast,Toast.LENGTH_LONG).show();
            }
        });
        return viewHierarchy;
    }

}
