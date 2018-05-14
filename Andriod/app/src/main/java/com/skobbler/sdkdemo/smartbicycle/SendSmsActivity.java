package com.skobbler.sdkdemo.smartbicycle;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.skobbler.sdkdemo.R;

import static com.skobbler.sdkdemo.R.id;

public class SendSmsActivity extends ActionBarActivity {

    Button sendSMSBtn;
    EditText toPhoneNumberET;
    //EditText smsMessageET;
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Name = "nameKey";
    SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_sms);
        sendSMSBtn = (Button) findViewById(id.sendSMSBtn);
        toPhoneNumberET = (EditText) findViewById(id.toPhoneNumberET);
      // smsMessageET = (EditText) findViewById(id.smsMessageET);
        //Intent i=getIntent();
        //String toPhoneNumber=i.getStringExtra("phoneno");
        //String smsMessage=i.getStringExtra("sms");
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        if (sharedpreferences.contains(Name)) {
            toPhoneNumberET.setText(sharedpreferences.getString(Name,""));
        }
        sendSMSBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String toPhoneNumber = toPhoneNumberET.getText().toString();
                SharedPreferences.Editor editor = sharedpreferences.edit();

                editor.putString(Name,toPhoneNumber);
                editor.commit();
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + toPhoneNumber));
                startActivity(callIntent);
            }

        });
     //   sendSMSBtn.setOnClickListener(new View.OnClickListener() {
     //       public void onClick(View view) {
     //           sendSMS();
     //       }
     //   });


    }
/*
    protected void sendSMS() {
        String toPhoneNumber = toPhoneNumberET.getText().toString();
        String smsMessage = smsMessageET.getText().toString();
       String toPhoneNumber = "7259467276";
        String smsMessage = "Activate";
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(toPhoneNumber, null, smsMessage, null, null);
            Toast.makeText(getApplicationContext(),"SMS Sent",
                    Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "Sending SMS failed.",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}

*/
}