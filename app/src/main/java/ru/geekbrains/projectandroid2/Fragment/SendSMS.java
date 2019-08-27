package ru.geekbrains.projectandroid2.Fragment;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import ru.geekbrains.projectandroid2.R;

public class SendSMS extends Fragment {
    private final int permissionRequestCode = 123;
    private EditText editTextTo;
    private EditText txtMessage;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewSendSMS = inflater.inflate(R.layout.fragment_sms, container, false);
        editTextTo = viewSendSMS.findViewById(R.id.editTextTo);
        txtMessage = viewSendSMS.findViewById(R.id.editTextBody);
        Button buttonSendSMS = viewSendSMS.findViewById(R.id.buttonSendSMS);
        checkPermission();
        buttonSendSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNo = editTextTo.getText().toString();
                String message = txtMessage.getText().toString();
                sendSMS(phoneNo, message);
                editTextTo.setText("");
                txtMessage.setText("");
            }
        });
        return viewSendSMS;
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.RECEIVE_SMS)
                        != PackageManager.PERMISSION_GRANTED) {
            final String[] permissions = new String[]{Manifest.permission.RECEIVE_SMS};
            ActivityCompat.requestPermissions(getActivity(), permissions, permissionRequestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if(requestCode == permissionRequestCode) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Спасибо!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(),
                        "Извините, апп без данного разрешения может работать неправильно",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendSMS(String phoneNumber, String message)    {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";
        try {
            PendingIntent sentPI = PendingIntent.getBroadcast(getContext(), 0,
                    new Intent(SENT), 0);
            PendingIntent deliveredPI = PendingIntent.getBroadcast(getContext(), 0,
                    new Intent(DELIVERED), 0);
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
            Toast.makeText(getContext(), "SMS Sent Successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), "SMS Failed to Send, Please try again", Toast.LENGTH_SHORT).show();
        }
    }
}
