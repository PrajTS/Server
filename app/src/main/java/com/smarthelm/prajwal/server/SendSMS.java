package com.smarthelm.prajwal.server;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Prajwal on 26-10-2017.
 */

public class SendSMS extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            finish();
        }
// get data via the key
        String number = extras.getString("emC");
        String msg = extras.getString("msg");

        try {
        SmsManager smsManager = SmsManager.getDefault();
        //SmsManager.getSmsManagerForSubscriptionId(1).sendTextMessage("8317399805",null, msg,null,null);
        smsManager.sendTextMessage(number, null, msg.trim(), null, null);
        Log.e("msg1",msg.trim());
        Log.e("number1",number);
        Toast.makeText(getApplicationContext(), "SMS Sent!",
                Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "SMS failed, please try again later!",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        finish();

    }




    }

