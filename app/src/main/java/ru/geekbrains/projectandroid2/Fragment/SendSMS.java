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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.Objects;

import ru.geekbrains.projectandroid2.R;

public class SendSMS extends Fragment {
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;

    private EditText editTextTo;
    private EditText txtMessage;

    private String phoneNo;
    private String message;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewSendSMS = inflater.inflate(R.layout.fragment_sms, container, false);
        editTextTo = viewSendSMS.findViewById(R.id.editTextTo);
        txtMessage = viewSendSMS.findViewById(R.id.editTextBody);
        Button buttonSendSMS = viewSendSMS.findViewById(R.id.buttonSendSMS);
        buttonSendSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendSMSMessage();
                editTextTo.setText("");
                txtMessage.setText("");
            }
        });
        return viewSendSMS;
    }

    protected void sendSMSMessage() {
        phoneNo = editTextTo.getText().toString();
        message = txtMessage.getText().toString();

        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(Objects.requireNonNull(getActivity()),
                    Manifest.permission.SEND_SMS)) {
            } else {
                requestPermissions(new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_SEND_SMS) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNo, null, message, null, null);
                Toast.makeText(getContext(), "SMS sent.",
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(),
                        "SMS fail, please try again.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
